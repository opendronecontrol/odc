package com.codeminders.ardrone.commands;

public class LandCommand extends RefCommand
{
    public LandCommand()
    {
        // 9th bit set to 0
    }

    @Override
    public int getPriority()
    {
        return HIGH_PRIORITY;
    }
    
    public boolean isSticky()
    {
        return true;
    }
    
    public String getCategory()
    {
        return LAND_TAKEOFF_CATEGORY;
    }

}
