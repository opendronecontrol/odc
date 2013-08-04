package com.codeminders.ardrone.commands;

public class PCMDCommand extends ATCommand
{
    protected boolean hover;
    protected boolean combined_yaw_enabled;
    protected float   left_right_tilt;
    protected float   front_back_tilt;
    protected float   vertical_speed;
    protected float   angular_speed;


    protected PCMDCommand(boolean hover)
    {
        this.hover = hover;
    }

    @Override
    protected String getID()
    {
        return "PCMD";
    }

    @Override
    protected Object[] getParameters()
    {
        if(hover)
            return new Object[] { 0, 0f, 0f, 0f, 0f };

//        int mode = combined_yaw_enabled ? 1 : 0;
        int mode = 1;
        if(combined_yaw_enabled)
            mode |= (1<<1);

        return new Object[] { mode, left_right_tilt, front_back_tilt, vertical_speed, angular_speed };
    }
}
