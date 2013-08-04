
package com.codeminders.ardrone.controllers;

import java.io.IOException;

/**
 * Base abstract class for supported controllers
 * 
 * @author lord
 * 
 */
public abstract class Controller
{
    
    public abstract void close() throws IOException;
    
    public abstract String getName();
    
    public abstract String getManufacturerString();
    
    public abstract String getProductString();
   
    public abstract GameControllerState read() throws IOException;

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append(getName() + " [");
        builder.append("manufacturer=");
        builder.append(getManufacturerString());
        builder.append(", product=");
        builder.append(getProductString());
        builder.append("]");
        return builder.toString();
    }

}