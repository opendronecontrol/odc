package com.codeminders.ardrone.commands;

public class ConfigureCommand extends ATCommand
{
    protected String name;
    protected String value;

    public ConfigureCommand(String name, String value)
    {
        this.name  = name;
        this.value = value;
    }

    @Override
    protected String getID()
    {
        return "CONFIG";
    }

    @Override
    protected Object[] getParameters()
    {
        return new Object[] { name, value };
    }
}
