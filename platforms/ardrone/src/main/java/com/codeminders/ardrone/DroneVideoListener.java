
package com.codeminders.ardrone;

import java.awt.image.BufferedImage;

public interface DroneVideoListener
{
    void frameReceived(int startX, int startY, int w, int h, int[] rgbArray, int offset, int scansize);
    void frameReceived(BufferedImage bi);
}
