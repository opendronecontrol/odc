
package com.codeminders.ardrone;

import java.io.IOException;
import java.net.*;

import java.util.logging.Logger;

import com.codeminders.ardrone.commands.*;

public class CommandSender implements Runnable
{
    private static final int CMD_PORT = 5556;

    private CommandQueue     cmd_queue;
    private ARDrone          drone;
    private InetAddress      drone_addr;
    private DatagramSocket   cmd_socket;
    private int              sequence = 1;

    private Logger           log      = Logger.getLogger(getClass().getName());

    public CommandSender(CommandQueue cmd_queue, ARDrone drone, InetAddress drone_addr, DatagramSocket cmd_socket)
    {
        this.cmd_queue = cmd_queue;
        this.drone = drone;
        this.drone_addr = drone_addr;
        this.cmd_socket = cmd_socket;
    }

    @Override
    public void run()
    {
        while(true)
        {
            try
            {
                DroneCommand c = cmd_queue.take();
                if(c instanceof QuitCommand)
                {
                    // Terminating
                    break;
                }

                if(c instanceof ATCommand)
                {
                    ATCommand cmd = (ATCommand) c;
                    //if(!(c instanceof KeepAliveCommand) && !(c instanceof MoveCommand) && !(c instanceof HoverCommand) && c.getStickyCounter()==0)
                    log.fine("Q[" + cmd_queue.size() + "]Sending AT command " + c);
                    byte[] pdata = cmd.getPacket(sequence++); // TODO: pass
                                                              // sequence number
                    DatagramPacket p = new DatagramPacket(pdata, pdata.length, drone_addr, CMD_PORT);
                    cmd_socket.send(p);
                }
            } catch(InterruptedException e)
            {
                // ignoring
            } catch(IOException e)
            {
                drone.changeToErrorState(e);
                break;
            }
        }
    }

}
