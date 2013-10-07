
/** Modified 2012 by Tim Wood
*    Minor changes to work with ARDrone 2.0
*/



package com.codeminders.ardrone;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.awt.image.BufferedImage;


import com.codeminders.ardrone.commands.ConfigureCommand;
import com.codeminders.ardrone.commands.ControlCommand;
import com.codeminders.ardrone.commands.EmergencyCommand;
import com.codeminders.ardrone.commands.FlatTrimCommand;
import com.codeminders.ardrone.commands.HoverCommand;
import com.codeminders.ardrone.commands.KeepAliveCommand;
import com.codeminders.ardrone.commands.LandCommand;
import com.codeminders.ardrone.commands.MoveCommand;
import com.codeminders.ardrone.commands.PlayAnimationCommand;
import com.codeminders.ardrone.commands.PlayLEDCommand;
import com.codeminders.ardrone.commands.QuitCommand;
import com.codeminders.ardrone.commands.TakeOffCommand;
import com.codeminders.ardrone.data.ARDroneDataReader;
import com.codeminders.ardrone.data.ChannelProcessor;
import com.codeminders.ardrone.data.decoder.ardrone10.ARDrone10NavDataDecoder;
// import com.codeminders.ardrone.data.decoder.ardrone20.ARDrone20NavDataDecoder;
import com.codeminders.ardrone.data.decoder.ardrone10.ARDrone10VideoDataDecoder;
import com.codeminders.ardrone.data.decoder.ardrone20.ARDrone20VideoDataDecoder;
// import com.codeminders.ardrone.decoder.TestH264DataDecoder;
import com.codeminders.ardrone.data.logger.ARDroneDataReaderAndLogWrapper;
import com.codeminders.ardrone.data.logger.DataLogger;
import com.codeminders.ardrone.data.navdata.FlyingState;
import com.codeminders.ardrone.data.navdata.Mode;
import com.codeminders.ardrone.data.reader.LigthUDPDataReader;
import com.codeminders.ardrone.data.reader.TCPDataRader;
import com.codeminders.ardrone.data.reader.UDPDataReader;
import com.codeminders.ardrone.version.DroneVersionReader;
import com.codeminders.ardrone.version.ftp.DroneFTPversionReader;

public class ARDrone
{
    public enum State
    {
        DISCONNECTED, CONNECTING, BOOTSTRAP, DEMO, ERROR, TAKING_OFF, LANDING
    }

    public enum VideoChannel
    {
        HORIZONTAL_ONLY, VERTICAL_ONLY, VERTICAL_IN_HORIZONTAL, HORIZONTAL_IN_VERTICAL
    }

    public enum Animation
    {
        PHI_M30_DEG(0), PHI_30_DEG(1), THETA_M30_DEG(2), THETA_30_DEG(3), THETA_20DEG_YAW_200DEG(4), THETA_20DEG_YAW_M200DEG(
                5), TURNAROUND(6), TURNAROUND_GODOWN(7), YAW_SHAKE(8), YAW_DANCE(9), PHI_DANCE(10), THETA_DANCE(11), VZ_DANCE(
                12), WAVE(13), PHI_THETA_MIXED(14), DOUBLE_PHI_THETA_MIXED(15), ANIM_MAYDAY(16);

        private int value;

        private Animation(int value)
        {
            this.value = value;
        }

        public int getValue()
        {
            return value;
        }
    }

    public enum LED
    {
        BLINK_GREEN_RED(0), BLINK_GREEN(1), BLINK_RED(2), BLINK_ORANGE(3), SNAKE_GREEN_RED(4), FIRE(5), STANDARD(6), RED(
                7), GREEN(8), RED_SNAKE(9), BLANK(10), RIGHT_MISSILE(11), LEFT_MISSILE(12), DOUBLE_MISSILE(13), FRONT_LEFT_GREEN_OTHERS_RED(
                14), FRONT_RIGHT_GREEN_OTHERS_RED(15), REAR_RIGHT_GREEN_OTHERS_RED(16), REAR_LEFT_GREEN_OTHERS_RED(17), LEFT_GREEN_RIGHT_RED(
                18), LEFT_RED_RIGHT_GREEN(19), BLINK_STANDARD(20);

        private int value;

        private LED(int value)
        {
            this.value = value;
        }

        public int getValue()
        {
            return value;
        }
    }

    public enum ConfigOption
    {
        ACCS_OFFSET("control:accs_offset"), ACCS_GAINS("control:accs_gains"), GYROS_OFFSET("control:gyros_offset"), GYROS_GAINS(
                "control:gyros_gains"), GYROS110_OFFSET("control:gyros110_offset"), GYROS110_GAINS(
                "control:gyros110_gains"), GYRO_OFFSET_THR_X("control:gyro_offset_thr_x"), GYRO_OFFSET_THR_Y(
                "control:gyro_offset_thr_y"), GYRO_OFFSET_THR_Z("control:gyro_offset_thr_z"), PWM_REF_GYROS(
                "control:pwm_ref_gyros"), CONTROL_LEVEL("control:control_level"), SHIELD_ENABLE("control:shield_enable"), EULER_ANGLE_MAX(
                "control:euler_angle_max"), ALTITUDE_MAX("control:altitude_max"), ALTITUDE_MIN("control:altitude_min"), CONTROL_TRIM_Z(
                "control:control_trim_z"), CONTROL_IPHONE_TILT("control:control_iphone_tilt"), CONTROL_VZ_MAX(
                "control:control_vz_max"), CONTROL_YAW("control:control_yaw"), OUTDOOR("control:outdoor"), FLIGHT_WITHOUT_SHELL(
                "control:flight_without_shell"), BRUSHLESS("control:brushless"), AUTONOMOUS_FLIGHT(
                "control:autonomous_flight"), MANUAL_TRIM("control:manual_trim"), INDOOR_EULER_ANGLE_MAX(
                "control:indoor_euler_angle_max"), INDOOR_CONTROL_VZ_MAX("control:indoor_control_vz_max"), INDOOR_CONTROL_YAW(
                "control:indoor_control_yaw"), OUTDOOR_EULER_ANGLE_MAX("control:outdoor_euler_angle_max"), OUTDOOR_CONTROL_VZ_MAX(
                "control:outdoor_control_vz_max"), OUTDOOR_CONTROL_YAW("outdoor_control:control_yaw");

        private String value;

        private ConfigOption(String value)
        {
            this.value = value;
        }

        public String getValue()
        {
            return value;
        }
    }

    private Logger                          log               = Logger.getLogger(getClass().getName());

    private static final int                CMD_QUEUE_SIZE    = 64;
    private State                           state             = State.DISCONNECTED;
    private Object                          state_mutex       = new Object();

    private static final int                NAVDATA_PORT      = 5554;
    private static final int                VIDEO_PORT        = 5555;
    private static final int                CONTROL_PORT      = 5559;
    
    private static final int                NAVDATA_BUFFER_SIZE = 1024; //4096;
    private static final int                VIDEO_BUFFER_SIZE = 100 * 1024;

    final static byte[]                     DEFAULT_DRONE_IP  = { (byte) 192, (byte) 168, (byte) 1, (byte) 1 };

    private static final int                DEFAULT_DRONE_VERSION = 1;

    private InetAddress                     drone_addr;
    private DatagramSocket                  cmd_socket;

    private CommandQueue                    cmd_queue         = new CommandQueue(CMD_QUEUE_SIZE);

    private ChannelProcessor                drone_nav_channel_processor;
    private ChannelProcessor                drone_video_channel_processor;

    private CommandSender                   cmd_sender;

    private Thread                          cmd_sending_thread;


    private boolean                         combinedYawMode   = true;

    private boolean                         emergencyMode     = true;
    private Object                          emergency_mutex   = new Object();

    private List<DroneStatusChangeListener> status_listeners  = new LinkedList<DroneStatusChangeListener>();
    private List<DroneVideoListener>        image_listeners   = new LinkedList<DroneVideoListener>();
    private List<NavDataListener>           navdata_listeners = new LinkedList<NavDataListener>();

    private int                             navDataReconnectTimeout = 1000; // 1 second
    private int                             videoReconnectTimeout   = 1000; // 1 second

    private VideoDataDecoder                ext_video_data_decoder;
    private NavDataDecoder                  ext_nav_data_decoder;
    
    private DroneVersionReader              versionReader;
    private int                             versionNum;

    private int coolItBuddy = 0;

    public ARDrone() throws UnknownHostException
    {
        this(InetAddress.getByAddress(DEFAULT_DRONE_IP), 1000, 1000);
    }

    public ARDrone(InetAddress drone_addr, int navDataReconnectTimeout, int videoReconnectTimeout)
    {
        this.drone_addr = drone_addr;
        this.navDataReconnectTimeout = navDataReconnectTimeout;
        this.videoReconnectTimeout = videoReconnectTimeout;
        
        this.versionReader = new DroneFTPversionReader(drone_addr);
    }

    public void addImageListener(DroneVideoListener l)
    {
        synchronized(image_listeners)
        {
            image_listeners.add(l);
        }
    }

    public void removeImageListener(DroneVideoListener l)
    {
        synchronized(image_listeners)
        {
            image_listeners.remove(l);
        }
    }

    public void clearImageListeners()
    {
        synchronized(image_listeners)
        {
            image_listeners.clear();
        }
    }

    public void addStatusChangeListener(DroneStatusChangeListener l)
    {
        synchronized(status_listeners)
        {
            status_listeners.add(l);
        }
    }

    public void removeStatusChangeListener(DroneStatusChangeListener l)
    {
        synchronized(status_listeners)
        {
            status_listeners.remove(l);
        }
    }

    public void clearStatusChangeListeners()
    {
        synchronized(status_listeners)
        {
            status_listeners.clear();
        }
    }

    public void addNavDataListener(NavDataListener l)
    {
        synchronized(navdata_listeners)
        {
            navdata_listeners.add(l);
        }
    }

    public void removeNavDataListener(NavDataListener l)
    {
        synchronized(navdata_listeners)
        {
            navdata_listeners.remove(l);
        }
    }

    public void clearNavDataListeners()
    {
        synchronized(navdata_listeners)
        {
            navdata_listeners.clear();
        }
    }

    private void changeState(State newstate) throws IOException
    {
        if(newstate == State.ERROR)
            changeToErrorState(null);

        synchronized(state_mutex)
        {
            if(state != newstate)
            {
                log.fine("State changed from " + state + " to " + newstate);
                state = newstate;

                // We automatically switch to DEMO from bootstrap
                if(state == State.BOOTSTRAP)
                    sendDemoNavigationData();

                state_mutex.notifyAll();
            }
        }

        if(newstate == State.DEMO)
        {
            synchronized(status_listeners)
            {
                for(DroneStatusChangeListener l : status_listeners)
                    l.ready();
            }
        }
    }

    public void changeToErrorState(Exception ex)
    {
        synchronized(state_mutex)
        {
            try
            {
                if(state != State.DISCONNECTED)
                    doDisconnect();
            } catch(IOException e)
            {
                // Ignoring exceptions on disconnection
            }
            log.log(Level.FINE ,"State changed from " + state + " to " + State.ERROR + " with exception ", ex);
            state = State.ERROR;
            state_mutex.notifyAll();
        }
    }

    public void clearEmergencySignal() throws IOException
    {
        synchronized(emergency_mutex)
        {
            if(isEmergencyMode())
                cmd_queue.add(new EmergencyCommand());
        }
    }
    
    /**
     * Initiate drone connection procedure.
     * 
     * @throws IOException
     */
    public void connect() throws IOException
    {
        connect(null, null);
    }

    public void connect(DataLogger videoLogger, DataLogger navdataLogger) throws IOException
    {
        try
        {
            int version = DEFAULT_DRONE_VERSION;
            try {
                String versionStr = versionReader.readDroneVersion();
                log.log(Level.FINER, "Drone version string: " + versionStr);
                System.out.println("ARDrone version: " + versionStr);
                version = Integer.parseInt(versionStr.substring(0, versionStr.indexOf('.')));
            } catch (NumberFormatException e) {
                log.log(Level.SEVERE, "Failed to discover drone version. Using configuration for drone version: " + version, e);
            }
            versionNum = version;
            
            cmd_socket = new DatagramSocket();

            cmd_sender = new CommandSender(cmd_queue, this, drone_addr, cmd_socket);
            cmd_sending_thread = new Thread(cmd_sender);
            cmd_sending_thread.setName("Command Sender");
            cmd_sending_thread.start();
            
            enableVideo();
            // disableVideo();
            enableAutomaticVideoBitrate();

            NavDataDecoder nav_data_decoder = (null == ext_nav_data_decoder) ?
                    getNavDecoder(version) //new  ARDrone10NavDataDecoder(this, NAVDATA_BUFFER_SIZE)
                    :
                    ext_nav_data_decoder;
                    
            ARDroneDataReader nav_data_reader = (null == navdataLogger) ? 
                    new LigthUDPDataReader(drone_addr, NAVDATA_PORT, navDataReconnectTimeout) 
                    :
                    new ARDroneDataReaderAndLogWrapper(new LigthUDPDataReader(drone_addr, NAVDATA_PORT, navDataReconnectTimeout), navdataLogger);
                    
            drone_nav_channel_processor = new ChannelProcessor(nav_data_reader, nav_data_decoder);
            
            VideoDataDecoder video_data_decoder = (null == ext_video_data_decoder) ? 
                    getVideoDecoder(version)
                    :
                    ext_video_data_decoder;
            ARDroneDataReader video_data_reader =  (null == videoLogger) ?  
                    getVideoReader(version)
                    :
                    new ARDroneDataReaderAndLogWrapper(new UDPDataReader(drone_addr, VIDEO_PORT, videoReconnectTimeout), videoLogger);
            
            if (null != video_data_reader && null != video_data_decoder) {
                drone_video_channel_processor = new ChannelProcessor(video_data_reader, video_data_decoder);
            }

            changeState(State.CONNECTING);

        } catch(IOException ex)
        {
            changeToErrorState(ex);
            throw ex;
        }
    }

    private NavDataDecoder getNavDecoder(int version) throws IOException {
        switch (version) {
            case 1:
                return   new ARDrone10NavDataDecoder(this, NAVDATA_BUFFER_SIZE);
            case 2:
                return   new ARDrone10NavDataDecoder(this, NAVDATA_BUFFER_SIZE);
            default:
                return   new ARDrone10NavDataDecoder(this, NAVDATA_BUFFER_SIZE);
        }
      
    }
    
    private VideoDataDecoder getVideoDecoder(int version) throws IOException {
        switch (version) {
            case 1:
                return   new ARDrone10VideoDataDecoder(this, VIDEO_BUFFER_SIZE);
            case 2:
                try{
                    disableAutomaticVideoBitrate(); // test this
                    return   new ARDrone20VideoDataDecoder(this);
                } catch (Exception e){
                    System.out.println(e);
                    return null;
                }
            default:
                return   new ARDrone10VideoDataDecoder(this, VIDEO_BUFFER_SIZE);
        }
      
    }
    
    private ARDroneDataReader getVideoReader(int version) throws IOException {
        switch (version) {
            case 1:
                return new LigthUDPDataReader(drone_addr, VIDEO_PORT, videoReconnectTimeout);
            case 2:
                return new TCPDataRader(drone_addr, VIDEO_PORT, videoReconnectTimeout);
            default:
                return new LigthUDPDataReader(drone_addr, VIDEO_PORT, videoReconnectTimeout);
            }
    }

    public void disableAutomaticVideoBitrate() throws IOException
    {
        cmd_queue.add(new ConfigureCommand("video:bitrate_control_mode", "0"));
    }

    public void disconnect() throws IOException
    {
        try
        {
            doDisconnect();
        } finally
        {
            changeState(State.DISCONNECTED);
        }
    }

    private void doDisconnect() throws IOException
    {
        if(cmd_queue != null)
            cmd_queue.add(new QuitCommand());

        if(drone_nav_channel_processor != null)
            drone_nav_channel_processor.finish();

        if(drone_video_channel_processor != null)
            drone_video_channel_processor.finish();

        if(cmd_socket != null)
            cmd_socket.close();

        // Only the following method can throw an exception.
        // We call it last, to ensure it won't prevent other
        // cleanup operations from being completed
        // control_socket.close();
    }

    /**
     * Enables the automatic bitrate control of the video stream. Enabling this
     * configuration will reduce the bandwith used by the video stream under bad
     * Wi-Fi conditions, reducing the commands latency. Note : Before enabling
     * this config, make sure that your video decoder is able to handle the
     * variable bitrate mode !
     * 
     * @throws IOException
     */
    public void enableAutomaticVideoBitrate() throws IOException
    {
        setConfigOption("video:bitrate_control_mode", "1");
    }
    public void enableVideo() throws IOException
    {
        setConfigOption("general:video_enable", "TRUE");
    }
    public void disableVideo() throws IOException
    {
        setConfigOption("general:video_enable", "FALSE");
    }
    

    public void hover() throws IOException
    {
        cmd_queue.add(new HoverCommand());
    }

    public boolean isCombinedYawMode()
    {
        return combinedYawMode;
    }

    public boolean isEmergencyMode()
    {
        return emergencyMode;
    }

    public void land() throws IOException
    {
        // TODO: Review of possible race condition
        cmd_queue.add(new LandCommand());
        changeState(State.LANDING);
    }

    /**
     * Move the drone
     * 
     * @param left_right_tilt The left-right tilt (aka. "drone roll" or phi
     *            angle) argument is a percentage of the maximum inclination as
     *            configured here. A negative value makes the drone tilt to its
     *            left, thus flying leftward. A positive value makes the drone
     *            tilt to its right, thus flying rightward.
     * @param front_back_tilt The front-back tilt (aka. "drone pitch" or theta
     *            angle) argument is a percentage of the maximum inclination as
     *            configured here. A negative value makes the drone lower its
     *            nose, thus flying frontward. A positive value makes the drone
     *            raise its nose, thus flying backward. The drone translation
     *            speed in the horizontal plane depends on the environment and
     *            cannot be determined. With roll or pitch values set to 0, the
     *            drone will stay horizontal but continue sliding in the air
     *            because of its inertia. Only the air resistance will then make
     *            it stop.
     * @param vertical_speed The vertical speed (aka. "gaz") argument is a
     *            percentage of the maximum vertical speed as defined here. A
     *            positive value makes the drone rise in the air. A negative
     *            value makes it go down.
     * @param angular_speed The angular speed argument is a percentage of the
     *            maximum angular speed as defined here. A positive value makes
     *            the drone spin right; a negative value makes it spin left.
     * @throws IOException
     */
    public void move(float left_right_tilt, float front_back_tilt, float vertical_speed, float angular_speed)
            throws IOException
    {
        cmd_queue.add(new MoveCommand(combinedYawMode, left_right_tilt, front_back_tilt, vertical_speed, angular_speed));
    }

    // Callback used by receiver
    protected void navDataReceived(NavData nd)
    {
        if(nd.isBatteryTooLow() || nd.isNotEnoughPower())
        {
            //if(coolItBuddy++ % 20 == 0) log.severe("Battery pb " + nd.toString());
            if(coolItBuddy++ % 60 == 0) System.out.println("Battery is getting low: " + nd.getBattery() );
        }

        synchronized(emergency_mutex)
        {
            emergencyMode = nd.isEmergency();
        }

        try
        {
            synchronized(state_mutex)
            {
                if(state != State.CONNECTING && nd.isControlReceived())
                {
                    log.fine("Control received! ACK!");
                    cmd_queue.add(new ControlCommand(5, 0));
                }

                if(state == State.TAKING_OFF && nd.getFlyingState() == FlyingState.FLYING)
                {
                    log.fine("Take off success");
                    cmd_queue.clear(); // Maybe we should just remove
                                       // LAND/TAKEOFF comand
                                       // instead of nuking the whole queue?
                    changeState(State.DEMO);
                } else if(state == State.LANDING && nd.getFlyingState() == FlyingState.LANDED)
                {
                    log.fine("Landing success");
                    cmd_queue.clear(); // Maybe we should just remove
                                       // LAND/TAKEOFF comand
                                       // instead of nuking the whole queue?
                    changeState(State.DEMO);
                } else if(state != State.BOOTSTRAP && nd.getMode() == Mode.BOOTSTRAP)
                {
                    changeState(State.BOOTSTRAP);
                } else if(state == State.BOOTSTRAP && nd.getMode() == Mode.DEMO)
                {
                    changeState(State.DEMO);
                } else if(state == State.CONNECTING && nd.getMode() == Mode.DEMO)
                {
                    changeState(State.DEMO);
                }

                if(state != State.CONNECTING && nd.isCommunicationProblemOccurred())
                {
                    // 50ms communications watchdog has been triggered
                    cmd_queue.add(new KeepAliveCommand());
                }

            }
        } catch(IOException e)
        {
            log.log(Level.SEVERE, "Error changing the state", e);
        }

        if(state == State.DEMO)
        {
            synchronized(navdata_listeners)
            {
                for(NavDataListener l : navdata_listeners)
                    l.navDataReceived(nd);
            }
        }
    }

    public void playAnimation(int animation_no, int duration) throws IOException
    {
        if( versionNum == 1) cmd_queue.add(new PlayAnimationCommand(animation_no, duration));
        else cmd_queue.add(new ConfigureCommand("control:flight_anim", animation_no + "," + duration));
    }

    public void playAnimation(Animation animation, int duration) throws IOException
    {
        cmd_queue.add(new PlayAnimationCommand(animation.getValue(), duration));
    }

    public void playLED(int animation_no, float freq, int duration) throws IOException
    {
        cmd_queue.add(new PlayLEDCommand(animation_no, freq, duration));
    }

    public void playLED(LED animation, float freq, int duration) throws IOException
    {
        cmd_queue.add(new PlayLEDCommand(animation.getValue(), freq, duration));
    }

    public void selectVideoChannel(VideoChannel c) throws IOException
    {
        /*
         * Current implementation supports 4 different channels : -
         * ARDRONE_VIDEO_CHANNEL_HORI - ARDRONE_VIDEO_CHANNEL_VERT -
         * ARDRONE_VIDEO_CHANNEL_LARGE_HORI_SMALL_VERT -
         * ARDRONE_VIDEO_CHANNEL_LARGE_VERT_SMALL_HORI
         * 
         * AT command example : AT*CONFIG=605,"video:video_channel","2"
         */

        String s;
        switch(c)
        {
        case HORIZONTAL_ONLY: // ARDRONE_VIDEO_CHANNEL_HORI
            s = "0";
            break;

        case VERTICAL_ONLY: // ARDRONE_VIDEO_CHANNEL_VERT
            s = "1";
            break;

        case VERTICAL_IN_HORIZONTAL: // ARDRONE_VIDEO_CHANNEL_LARGE_HORI_SMALL_VERT
            s = "2";
            break;

        case HORIZONTAL_IN_VERTICAL: // ARDRONE_VIDEO_CHANNEL_LARGE_VERT_SMALL_HORI
            s = "3";
            break;
        default:
            assert (false);
            return;
        }

        cmd_queue.add(new ConfigureCommand("video:video_channel", s));
    }

    public void sendAllNavigationData() throws IOException
    {
        setConfigOption("general:navdata_demo", "FALSE");
    }

    public void sendDemoNavigationData() throws IOException
    {
        setConfigOption("general:navdata_demo", "TRUE");
    }

    public void sendEmergencySignal() throws IOException
    {
        synchronized(emergency_mutex)
        {
            if(!isEmergencyMode())
                cmd_queue.add(new EmergencyCommand());
        }
    }

    public void setCombinedYawMode(boolean combinedYawMode)
    {
        this.combinedYawMode = combinedYawMode;
    }

    public void setConfigOption(String name, String value) throws IOException
    {
        cmd_queue.add(new ConfigureCommand(name, value));
    }

    public void setConfigOption(ConfigOption option, String value) throws IOException
    {
        cmd_queue.add(new ConfigureCommand(option.getValue(), value));
    }

    public void takeOff() throws IOException
    {
        // TODO: review for possible race condition
    	if (!isEmergencyMode()) {
	        cmd_queue.add(new TakeOffCommand());
	        changeState(State.TAKING_OFF);
    	}
    }

    public void trim() throws IOException
    {
        cmd_queue.add(new FlatTrimCommand());
    }

    // Callback used by VideoReciver
    protected void videoFrameReceived(int startX, int startY, int w, int h, int[] rgbArray, int offset, int scansize)
    {
        synchronized(image_listeners)
        {
            for(DroneVideoListener l : image_listeners)
                l.frameReceived(startX, startY, w, h, rgbArray, offset, scansize);
        }
    }
    protected void videoFrameReceived(BufferedImage bi)
    {
        synchronized(image_listeners)
        {
            for(DroneVideoListener l : image_listeners)
                l.frameReceived(bi);
        }
    }

    /**
     * Wait for drone to switch to demo mode. Throw exception if this not
     * succeeded within given timeout. Should be called right after connect().
     * 
     * This is a convenience function. Another way to achieve the same result is
     * using status change callback.
     * 
     * @param how_long
     * @throws IOException
     */
    public void waitForReady(long how_long) throws IOException
    {
        long since = System.currentTimeMillis();
        synchronized(state_mutex)
        {
            while(true)
            {
                if(state == State.DEMO)
                {
                    return; // OK! We are now connected
                } else if(state == State.ERROR || state == State.DISCONNECTED)
                {
                    throw new IOException("Connection Error");
                } else if((System.currentTimeMillis() - since) >= how_long)
                {
                    try
                    {
                        disconnect();
                    } catch(IOException e)
                    {
                    }
                    // Timeout, too late
                    throw new IOException("Timeout connecting to ARDrone");
                } 
                

                long p = Math.min(how_long - (System.currentTimeMillis() - since), how_long);
                if(p > 0)
                {
                    try
                    {
                        state_mutex.wait(p);
                    } catch(InterruptedException e)
                    {
                        // Ignore
                    }
                }
            }
        }
    }
    public void pauseNavData() {
      if (null != drone_nav_channel_processor) {
          drone_nav_channel_processor.pause();
      }   
    }
    
    public void resumeNavData() {
        if (null != drone_nav_channel_processor) {
            drone_nav_channel_processor.resume();
        }   
    }
    
    public void pauseVideo() {
        if (null != drone_video_channel_processor) {
            drone_video_channel_processor.pause();
        }   
    }
    
    public void resumeVideo() {
        if (null != drone_video_channel_processor) {
            drone_video_channel_processor.resume();
        } 
    }

    public State getState() {
        return state;
    }

    public void setExternalVideoDataDecoder(VideoDataDecoder ext_video_data_decoder) {
        this.ext_video_data_decoder = ext_video_data_decoder;
    }
    
    public void setExternalVideoDataDecoder(NavDataDecoder ext_nav_data_decoder) {
        this.ext_nav_data_decoder = ext_nav_data_decoder;
    }
    /**
     * Read Drone version.
     * @return Drone version string e.g. "1.10.10". null - if version can't be obtained
     */
    public String getDroneVersion() {
        try {
            return versionReader.readDroneVersion();
        } catch (IOException e) {
           log.log(Level.SEVERE, "Failed to read drone version.", e);
        }
        return null;
    }
    /**
     * Reads drone configuration content. Please execute this method only when drone is connected, 
     * otherwise method will stick on waiting data from drone control port.
     * @return current values of Drone configuration, information about software, motors, tilt limitation etc.
     * Please see ARDrone Developers guide (Section 8.1 Reading the drone configuration) for full list of parameters. 
     * null if any error occurred
     */
    public synchronized String readDroneConfiguration() {
       
        String ret = null;
        synchronized (this) {
            Socket socket = null;
            try {
                socket = new Socket(drone_addr.getHostAddress(), CONTROL_PORT);
                
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int readCount;
                InputStream  in =  socket.getInputStream();
                cmd_queue.add(new ControlCommand(4, 0));
                boolean continueReading = true;
                while(continueReading && ((readCount = in.read(buffer)) > 0))
                {
                    bos.write(buffer, 0, readCount);
                    try {
                        Thread.sleep(100); // TODO: figure out something more complex. This code is required in order to give drone time to send content
                    } catch (InterruptedException e) {
                        log.log(Level.SEVERE, "Interrupted", e);
                    }
                    continueReading = in.available() > 0;
                }
                bos.close();

                ret = new String(bos.toByteArray(), "ASCII");
            } catch (IOException ex) {
                log.log(Level.SEVERE, "Error. Fialed to read drone configuration", ex);
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    log.log(Level.SEVERE, "Error. Close Drone TCP controll chanel", e);
                }
            }
        }
        
        return ret;
    }
    
}
