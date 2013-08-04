
package com.codeminders.ardrone.data.decoder.ardrone10.navdata;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import java.util.logging.Logger;

import com.codeminders.ardrone.NavData;
import com.codeminders.ardrone.data.navdata.ControlAlgorithm;
import com.codeminders.ardrone.data.navdata.CtrlState;
import com.codeminders.ardrone.data.navdata.FlyingState;
import com.codeminders.ardrone.data.navdata.Mode;
import com.codeminders.ardrone.data.navdata.NavDataFormatException;
import com.codeminders.ardrone.data.navdata.NavDataTag;
import com.codeminders.ardrone.data.navdata.vision.Dimension;
import com.codeminders.ardrone.data.navdata.vision.Point;
import com.codeminders.ardrone.data.navdata.vision.VisionTag;
import com.codeminders.ardrone.data.navdata.vision.VisionTag.VisionTagType;

public class ARDrone10NavData implements NavData
{

    private static final Logger log = Logger.getLogger(ARDrone10NavData.class.getName());

    protected Mode             mode;

    // state flags
    protected boolean          flying;
    protected boolean          videoEnabled;
    protected boolean          visionEnabled;
    protected ControlAlgorithm controlAlgorithm;
    protected boolean          altitudeControlActive;
    protected boolean          userFeedbackOn;              // /TODO better
                                                             // name
    protected boolean          controlReceived;
    protected boolean          trimReceived;
    protected boolean          trimRunning;
    protected boolean          trimSucceeded;
    protected boolean          navDataDemoOnly;
    protected boolean          navDataBootstrap;
    protected boolean          motorsDown;
    protected boolean          gyrometersDown;
    protected boolean          batteryTooLow;
    protected boolean          batteryTooHigh;
    protected boolean          timerElapsed;
    protected boolean          notEnoughPower;
    protected boolean          angelsOutOufRange;
    protected boolean          tooMuchWind;
    protected boolean          ultrasonicSensorDeaf;
    protected boolean          cutoutSystemDetected;
    protected boolean          PICVersionNumberOK;
    protected boolean          ATCodedThreadOn;
    protected boolean          navDataThreadOn;
    protected boolean          videoThreadOn;
    protected boolean          acquisitionThreadOn;
    protected boolean          controlWatchdogDelayed;
    protected boolean          ADCWatchdogDelayed;
    protected boolean          communicationProblemOccurred;
    protected boolean          emergency;

    // Common nav data
    protected int              sequence;

    // Demo nav data
    protected CtrlState        ctrl_state;
    protected int              battery;
    protected float            altitude;
    protected float            pitch;
    protected float            roll;
    protected float            yaw;
    protected float            vx;
    protected float            vy;
    protected float            vz;

    // Vision tags data
    protected List<VisionTag>  vision_tags;

    @Override
    public float getAltitude()
    {
        return altitude;
    }

    @Override
    public int getBattery()
    {
        return battery;
    }

    @Override
    public ControlAlgorithm getControlAlgorithm()
    {
        return controlAlgorithm;
    }

    @Override
    public CtrlState getControlState()
    {
        return ctrl_state;
    }

    @Override
    public float getLongitude()
    {
        return vy;
    }

    @Override
    public Mode getMode()
    {
        return mode;
    }

    @Override
    public float getPitch()
    {
        return pitch;
    }

    @Override
    public float getRoll()
    {
        return roll;
    }

    @Override
    public int getSequence()
    {
        return sequence;
    }

    @Override
    public float getVx()
    {
        return vx;
    }

    @Override
    public float getVz()
    {
        return vz;
    }

    @Override
    public float getYaw()
    {
        return yaw;
    }

    @Override
    public boolean isAcquisitionThreadOn()
    {
        return acquisitionThreadOn;
    }

    @Override
    public boolean isADCWatchdogDelayed()
    {
        return ADCWatchdogDelayed;
    }

    @Override
    public boolean isAltitudeControlActive()
    {
        return altitudeControlActive;
    }

    @Override
    public boolean isAngelsOutOufRange()
    {
        return angelsOutOufRange;
    }

    @Override
    public boolean isATCodedThreadOn()
    {
        return ATCodedThreadOn;
    }

    @Override
    public boolean isBatteryTooHigh()
    {
        return batteryTooHigh;
    }

    @Override
    public boolean isBatteryTooLow()
    {
        return batteryTooLow;
    }

    @Override
    public boolean isCommunicationProblemOccurred()
    {
        return communicationProblemOccurred;
    }

    @Override
    public boolean isControlReceived()
    {
        return controlReceived;
    }

    @Override
    public boolean isControlWatchdogDelayed()
    {
        return controlWatchdogDelayed;
    }

    @Override
    public boolean isCutoutSystemDetected()
    {
        return cutoutSystemDetected;
    }

    @Override
    public boolean isEmergency()
    {
        return emergency;
    }

    @Override
    public boolean isFlying()
    {
        return flying;
    }

    @Override
    public boolean isGyrometersDown()
    {
        return gyrometersDown;
    }

    @Override
    public boolean isMotorsDown()
    {
        return motorsDown;
    }

    @Override
    public boolean isNavDataBootstrap()
    {
        return navDataBootstrap;
    }

    @Override
    public boolean isNavDataDemoOnly()
    {
        return navDataDemoOnly;
    }

    @Override
    public boolean isNavDataThreadOn()
    {
        return navDataThreadOn;
    }

    @Override
    public boolean isNotEnoughPower()
    {
        return notEnoughPower;
    }

    @Override
    public boolean isPICVersionNumberOK()
    {
        return PICVersionNumberOK;
    }

    @Override
    public boolean isTimerElapsed()
    {
        return timerElapsed;
    }

    @Override
    public boolean isTooMuchWind()
    {
        return tooMuchWind;
    }

    @Override
    public boolean isTrimReceived()
    {
        return trimReceived;
    }

    @Override
    public boolean isTrimRunning()
    {
        return trimRunning;
    }

    @Override
    public boolean isTrimSucceeded()
    {
        return trimSucceeded;
    }

    @Override
    public boolean isUltrasonicSensorDeaf()
    {
        return ultrasonicSensorDeaf;
    }

    @Override
    public boolean isUserFeedbackOn()
    {
        return userFeedbackOn;
    }

    @Override
    public boolean isVideoEnabled()
    {
        return videoEnabled;
    }

    @Override
    public boolean isVideoThreadOn()
    {
        return videoThreadOn;
    }

    @Override
    public boolean isVisionEnabled()
    {
        return visionEnabled;
    }

    @Override
    public FlyingState getFlyingState()
    {
        return FlyingState.fromControlState(ctrl_state);
    }

    @Override
    public List<VisionTag> getVisionTags()
    {
        return vision_tags;
    }

    public void setVisionTags(List<VisionTag> vision_tags)
    {
        this.vision_tags = vision_tags;
    }

    public static NavData createFromData(ByteBuffer buf, int len) throws NavDataFormatException
    {
        log.fine("Parsing navdata len=" + len);
        
        if (ByteOrder.LITTLE_ENDIAN != buf.order()) {
            buf.order(ByteOrder.LITTLE_ENDIAN);
        }
        
        ARDrone10NavData data = new ARDrone10NavData();
        data.mode = Mode.BOOTSTRAP; // Assume we are in bootstrap

        //int offset = 0;

        int header = buf.getInt(0);
        
        if(header != 0x55667788)
            throw new NavDataFormatException("Error parsing NavData");
       // offset += 4;

        int state = buf.getInt(4);
        //offset += 4;

        parseState(data, state);

        data.sequence = buf.getInt(8);
        //offset += 4;

        // int vision_flag = buf.getInt(offset);
        //offset += 4;
        
        int offset = 16;

        // Read options
        while(offset < len)
        {
            int option_tag = buf.getShort(offset);
            offset += 2;
            int option_len = buf.getShort(offset);
            offset += 2;

            if(option_len == 0)
                throw new NavDataFormatException("Zero-len option with tag " + option_tag);

            // log.fine("At offset " + (offset - 4) + " found option " +
            // option_tag + " with len=" + option_len);

            if(option_tag == NavDataTag.NAVDATA_DEMO_TAG.getValue())
            {
                parseDemoNavData(data, buf, offset);
                data.mode = Mode.DEMO;
            } else if(option_tag == NavDataTag.NAVDATA_CKS_TAG.getValue())
            {
                // this is last tag. We do not unpack it yet, but we gracefully
                // exit if it has been encountered.
                break;
            } else if(option_tag == NavDataTag.NAVDATA_VISION_DETECT_TAG.getValue())
            {
                List<VisionTag> vtags = parseVisionTags(data, buf, offset);
                if(vtags != null)
                    data.setVisionTags(vtags);
            } else
            {
                //log.warning("Skipping unknown NavData option with tag=" + option_tag);
            }
            offset = offset + option_len - 4;
        }

        // TODO: calculate checksum
        log.fine("Got Nav data. mode " + data.mode);

        return data;
    }

    private static List<VisionTag> parseVisionTags(NavData data, ByteBuffer buf, int offset) throws NavDataFormatException
    {
        int nb_detected = buf.getInt(offset);
        offset += 4;

        if(nb_detected!=0)
            log.fine("" + nb_detected + " vision tags detected");

        if(nb_detected == 0)
            return null;

        assert (nb_detected > 0);
        List<VisionTag> res = new ArrayList<VisionTag>(nb_detected);
        for(int i = 0; i < nb_detected; i++)
        {
            int type = buf.getInt(offset + 4 * i);
            int xc = buf.getInt(offset + 4 * i + 1 * 4 * 4);
            int yc = buf.getInt(offset + 4 * i + 2 * 4 * 4);
            int width = buf.getInt(offset + 4 * i + 3 * 4 * 4);
            int height = buf.getInt(offset + 4 * i + 4 * 4 * 4);
            int dist = buf.getInt(offset + 4 * i + 5 * 4 * 4);

            VisionTag vt = new VisionTag(VisionTagType.fromInt(type), new Point(xc, yc), new Dimension(width, height),
                    dist);
            log.fine("Vision#" + i + " " + vt.toString());
            res.add(vt);
        }

        return res;
    }

    private static void parseDemoNavData(ARDrone10NavData data, ByteBuffer buf, int offset) throws NavDataFormatException
    {
        data.ctrl_state = CtrlState.fromInt(buf.getInt(offset) >> 16);
        log.fine("Ctrl State " + data.ctrl_state);

        offset += 4;
        data.battery = buf.getInt(offset);
        offset += 4;
        data.pitch = buf.getFloat(offset) / 1000;
        offset += 4;
        data.roll = buf.getFloat(offset) / 1000;
        offset += 4;
        data.yaw = buf.getFloat(offset) / 1000;
        offset += 4;
        data.altitude = ((float) buf.getInt(offset)) / 1000;
        offset += 4;
        data.vx = buf.getFloat(offset);
        offset += 4;
        data.vy = buf.getFloat(offset);
        offset += 4;
        data.vz = buf.getFloat(offset);
        offset += 4;
    }

    private static void parseState(ARDrone10NavData data, int state)
    {
        data.flying = (state & 1) != 0;
        data.videoEnabled = (state & (1 << 1)) != 0;
        data.visionEnabled = (state & (1 << 2)) != 0;
        data.controlAlgorithm = (state & (1 << 3)) != 0 ? ControlAlgorithm.ANGULAR_SPEED_CONTROL
                : ControlAlgorithm.EULER_ANGELS_CONTROL;
        data.altitudeControlActive = (state & (1 << 4)) != 0;
        data.userFeedbackOn = (state & (1 << 5)) != 0;
        data.controlReceived = (state & (1 << 6)) != 0;
        data.trimReceived = (state & (1 << 7)) != 0;
        data.trimRunning = (state & (1 << 8)) != 0;
        data.trimSucceeded = (state & (1 << 9)) != 0;
        data.navDataDemoOnly = (state & (1 << 10)) != 0;
        data.navDataBootstrap = (state & (1 << 11)) != 0;
        data.motorsDown = (state & (1 << 12)) != 0;
        //ARDRONE_COM_LOST_MASK       = 1U << 13, /*!< Communication Lost : (1) com problem, (0) Com is ok */
        data.gyrometersDown = (state & (1 << 14)) != 0;
        data.batteryTooLow = (state & (1 << 15)) != 0;
        data.batteryTooHigh = (state & (1 << 16)) != 0;
        data.timerElapsed = (state & (1 << 17)) != 0;
        data.notEnoughPower = (state & (1 << 18)) != 0;
        data.angelsOutOufRange = (state & (1 << 19)) != 0;
        data.tooMuchWind = (state & (1 << 20)) != 0;
        data.ultrasonicSensorDeaf = (state & (1 << 21)) != 0;
        data.cutoutSystemDetected = (state & (1 << 22)) != 0;
        data.PICVersionNumberOK = (state & (1 << 23)) != 0;
        data.ATCodedThreadOn = (state & (1 << 24)) != 0;
        data.navDataThreadOn = (state & (1 << 25)) != 0;
        data.videoThreadOn = (state & (1 << 26)) != 0;
        data.acquisitionThreadOn = (state & (1 << 27)) != 0;
        data.controlWatchdogDelayed = (state & (1 << 28)) != 0;
        data.ADCWatchdogDelayed = (state & (1 << 29)) != 0;
        data.communicationProblemOccurred = (state & (1 << 30)) != 0;
        data.emergency = (state & (1 << 31)) != 0;
    }

    public void printState()
    {
        StringBuffer sb = new StringBuffer();

        sb.append("IsFlying: " + this.isFlying() + "\n");
        sb.append("IsVideoEnabled: " + this.isVideoEnabled() + "\n");
        sb.append("IsVisionEnabled: " + this.isVisionEnabled() + "\n");
        sb.append("controlAlgo: " + this.getControlAlgorithm() + "\n");
        sb.append("AltitudeControlActive: " + this.isAltitudeControlActive() + "\n");
        sb.append("IsUserFeedbackOn: " + this.isUserFeedbackOn() + "\n");
        sb.append("ControlReceived: " + this.isVideoEnabled() + "\n");
        sb.append("IsTrimReceived: " + this.isTrimReceived() + "\n");
        sb.append("IsTrimRunning: " + this.isTrimRunning() + "\n");
        sb.append("IsTrimSucceeded: " + this.isTrimSucceeded() + "\n");
        sb.append("IsNavthisDemoOnly: " + this.isNavDataDemoOnly() + "\n");
        sb.append("IsNavthisBootstrap: " + this.isNavDataBootstrap() + "\n");
        sb.append("IsMotorsDown: " + this.isMotorsDown() + "\n");
        sb.append("IsGyrometersDown: " + this.isGyrometersDown() + "\n");
        sb.append("IsBatteryLow: " + this.isBatteryTooLow() + "\n");
        sb.append("IsBatteryHigh: " + this.isBatteryTooHigh() + "\n");
        sb.append("IsTimerElapsed: " + this.isTimerElapsed() + "\n");
        sb.append("isNotEnoughPower: " + this.isNotEnoughPower() + "\n");
        sb.append("isAngelsOutOufRange: " + this.isAngelsOutOufRange() + "\n");
        sb.append("isTooMuchWind: " + this.isTooMuchWind() + "\n");
        sb.append("isUltrasonicSensorDeaf: " + this.isUltrasonicSensorDeaf() + "\n");
        sb.append("isCutoutSystemDetected: " + this.isCutoutSystemDetected() + "\n");
        sb.append("isPICVersionNumberOK: " + this.isPICVersionNumberOK() + "\n");
        sb.append("isATCodedThreadOn: " + this.isATCodedThreadOn() + "\n");
        sb.append("isNavthisThreadOn: " + this.isNavDataThreadOn() + "\n");
        sb.append("isVideoThreadOn: " + this.isVideoThreadOn() + "\n");
        sb.append("isAcquisitionThreadOn: " + this.isAcquisitionThreadOn() + "\n");
        sb.append("isControlWatchdogDelayed: " + this.isControlWatchdogDelayed() + "\n");
        sb.append("isADCWatchdogDelayed: " + this.isADCWatchdogDelayed() + "\n");
        sb.append("isCommunicationProblemOccurred: " + this.isCommunicationProblemOccurred() + "\n");
        sb.append("IsEmergency: " + this.isEmergency() + "\n");
        sb.append("CtrlState: " + this.getControlState() + "\n");
        sb.append("Battery: " + this.getBattery() + "\n");
        sb.append("Altidtude: " + this.getAltitude() + "\n");
        sb.append("Pitch: " + this.getPitch() + "\n");
        sb.append("Roll: " + this.getRoll() + "\n");
        sb.append("Yaw: " + this.getYaw() + "\n");
        sb.append("X velocity: " + this.getVx() + "\n");
        sb.append("Y velocity: " + this.getLongitude() + "\n");
        sb.append("Z velocity: " + this.getVz() + "\n");
        sb.append("Vision Tags: " + this.getVisionTags() + "\n");

        log.fine("State: " + sb.toString());
    }
}
