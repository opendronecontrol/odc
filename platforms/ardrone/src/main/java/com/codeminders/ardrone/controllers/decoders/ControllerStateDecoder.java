package com.codeminders.ardrone.controllers.decoders;

import java.io.IOException;

import com.codeminders.ardrone.controllers.GameControllerState;
import com.codeminders.ardrone.controllers.ControllerData;

public interface ControllerStateDecoder {
    
    public GameControllerState decodeState(ControllerData data) throws IOException;
}
