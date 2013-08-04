package com.codeminders.ardrone.commands;

public class ControlCommand extends ATCommand
{
    protected int arg1;
    protected int arg2;

    public ControlCommand(int arg1, int arg2)
    {
        this.arg1 = arg1;
        this.arg2 = arg2;
    }

    @Override
    protected String getID()
    {
        return "CTRL";
    }

    @Override
    protected Object[] getParameters()
    {
        return new Object[] { arg1, arg2 };
    }
}
