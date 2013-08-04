package com.codeminders.ardrone.commands;

public class RefCommand extends ATCommand
{
    protected int value;

    protected RefCommand()
    {
        value |= (1 << 18) | (1 << 20)  | (1 << 22)  | (1 << 24)  | (1 << 28) ;
    }

    @Override
    protected String getID()
    {
        return "REF";
    }

    @Override
    protected Object[] getParameters()
    {
        return new Object[] { value };
    }
}
