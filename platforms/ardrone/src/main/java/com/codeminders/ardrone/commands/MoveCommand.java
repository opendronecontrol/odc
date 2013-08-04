package com.codeminders.ardrone.commands;

public class MoveCommand extends PCMDCommand
{
    public MoveCommand(boolean combined_yaw_enabled,
                       float   left_right_tilt,
                       float   front_back_tilt,
                       float   vertical_speed,
                       float   angular_speed)
    {
        super(false);
        this.combined_yaw_enabled = combined_yaw_enabled;
        this.left_right_tilt      = left_right_tilt;
        this.front_back_tilt      = front_back_tilt;
        this.vertical_speed       = vertical_speed;
        this.angular_speed        = angular_speed;
    }
}
