package com.codeminders.ardrone.commands;

import com.codeminders.ardrone.DroneCommand;

public class QuitCommand extends DroneCommand
{
    @Override
    public int getPriority()
    {
        return MAX_PRIORITY;
    }
}
