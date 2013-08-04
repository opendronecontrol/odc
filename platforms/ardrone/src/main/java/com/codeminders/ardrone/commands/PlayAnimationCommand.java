package com.codeminders.ardrone.commands;

public class PlayAnimationCommand extends ATCommand
{
    protected int   animation_no;
    protected int   duration;

    public PlayAnimationCommand(int animation_no, int duration)
    {
        this.animation_no = animation_no;
        this.duration     = duration;
    }

    @Override
    protected String getID()
    {
        return "ANIM";
    }

    @Override
    protected Object[] getParameters()
    {
        return new Object[] { animation_no, duration };
    }
}
