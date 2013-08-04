package com.codeminders.ardrone.version;

import java.io.IOException;

public interface DroneVersionReader {

    String readDroneVersion() throws IOException;

}
