
package com.codeminders.ardrone;

public abstract class DroneCommand
{
    protected static final int    MIN_PRIORITY           = 0;
    protected static final int    HIGH_PRIORITY          = 50;
    protected static final int    VERY_HIGH_PRIORITY     = 90;
    protected static final int    MAX_PRIORITY           = 100;
    protected static final long   DEFAULT_STICKY_RATE_MS = 100;

    protected static final String LAND_TAKEOFF_CATEGORY  = "takeoffland";

    private int                   sticky_counter         = 0;

    public abstract int getPriority();

    public String getCategory()
    {
        return null;
    }

    public boolean isSticky()
    {
        return false;
    }

    /**
     * For sticky packets indicates how many times it has been sent.
     * 
     * @return current value
     */
    public int incrementStickyCounter()
    {
        return ++sticky_counter;
    }

    public int getStickyCounter()
    {
        return sticky_counter;
    }

    /**
     * For sticky packets indicates delay between sending repeated packets;
     */
    public long getStickyRate()
    {
        return DEFAULT_STICKY_RATE_MS;
    }

    /**
     * This method is used to check if 2 commands are are relaceable by each
     * other.
     * 
     * @param cmd
     * @return true if they are replaceable
     */
    public boolean replaces(DroneCommand cmd)
    {
        if(equals(cmd))
        {
            return true;
        } else
        {
            String c = getCategory();
            return c != null && c.equals(cmd.getCategory());
        }
    }

}
