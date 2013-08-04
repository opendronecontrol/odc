
package com.codeminders.ardrone;

public interface DroneStatusChangeListener
{

    /**
     * This method is called whenever the drone changes from BOOTSTRAP or
     * ERROR modes to DEMO mode. Could be used for user-supplied initialization
     */
    void ready();

}
