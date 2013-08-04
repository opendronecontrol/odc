
package com.codeminders.ardrone.controllers.decoders;

import java.io.IOException;
import java.util.BitSet;

import com.codeminders.ardrone.controllers.GameControllerState;
import com.codeminders.ardrone.controllers.ControllerData;

/**
 * "Afterglow" controller for PS3 state decodder
 * 
 * @author lord
 * 
 */
public class AfterGlowControllerDecoder implements ControllerStateDecoder
{
  
    private int joystickCoordConv(byte b)
    {
        int v = b < 0 ? b + 256 : b;
        return v - 128;
    }

    @Override
    public GameControllerState decodeState(ControllerData data) throws IOException
    {
        byte[] buf = data.getBuffer();

        BitSet bs = new BitSet(13);
        for(int i = 0; i < 8; i++)
        {
            if((1 & (buf[0] >> i)) == 1)
                bs.set(i);
        }
        for(int i = 0; i < 5; i++)
        {
            if((1 & (buf[1] >> i)) == 1)
                bs.set(8 + i);
        }

        int i = 0;
        boolean square = bs.get(i++);
        boolean cross = bs.get(i++);
        boolean circle = bs.get(i++);
        boolean triangle = bs.get(i++);
        boolean L1 = bs.get(i++);
        boolean R1 = bs.get(i++);
        boolean L2 = bs.get(i++);
        boolean R2 = bs.get(i++);
        boolean select = bs.get(i++);
        boolean start = bs.get(i++);
        boolean leftJoystickPress = bs.get(i++);
        boolean rightJoystickPress = bs.get(i++);
        boolean PS = bs.get(i++);

        int leftJoystickX = joystickCoordConv(buf[3]);
        int leftJoystickY = joystickCoordConv(buf[4]);
        int rightJoystickX = joystickCoordConv(buf[5]);
        int rightJoystickY = joystickCoordConv(buf[6]);

        // TODO: decode HAT switch
        int hatSwitchLeftRight = 0;
        int hatSwitchUpDown = 0;

        GameControllerState res = new GameControllerState(square, cross, circle, triangle, L1, R1, L2, R2, select, start,
                leftJoystickPress, rightJoystickPress, PS, hatSwitchLeftRight, hatSwitchUpDown, leftJoystickX,
                leftJoystickY, rightJoystickX, rightJoystickY);


        return res;
    }
}
