package com.stolzatrub.metaldetector;

/**
 * Created by Florian on 14.03.2016.
 */
public class SensorException extends Exception
{
    //Parameterless Constructor
    public SensorException() {}

    //Constructor that accepts a message
    public SensorException(String message)
    {
        super(message);
    }
}
