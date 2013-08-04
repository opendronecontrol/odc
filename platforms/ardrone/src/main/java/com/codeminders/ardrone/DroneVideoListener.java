
package com.codeminders.ardrone;

public interface DroneVideoListener
{
    void frameReceived(int startX, int startY, int w, int h, int[] rgbArray, int offset, int scansize);
}
