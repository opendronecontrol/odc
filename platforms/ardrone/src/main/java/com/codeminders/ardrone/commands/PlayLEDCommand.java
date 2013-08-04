package com.codeminders.ardrone.commands;

public class PlayLEDCommand extends ATCommand
{
    protected int   animation_no;
    protected float frequency;
    protected int   duration;

    public PlayLEDCommand(int animation_no, float frequency, int duration)
    {
        this.animation_no = animation_no;
        this.frequency    = frequency;
        this.duration     = duration;
    }

    @Override
    protected String getID()
    {
        return "LED";
    }

    @Override
    protected Object[] getParameters()
    {
        return new Object[] { animation_no, frequency, duration };
    }
}
