package com.codeminders.ardrone;

import com.codeminders.ardrone.data.DataDecoder;

public abstract class NavDataDecoder extends DataDecoder {

    private ARDrone drone;
      
    public NavDataDecoder(ARDrone drone) {
        this.drone = drone;
    }

    public void notifyDroneWithDecodedNavdata(NavData navdata) {
       drone.navDataReceived(navdata);
    }

}
