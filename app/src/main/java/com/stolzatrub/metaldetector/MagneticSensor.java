package com.stolzatrub.metaldetector;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.TextView;

/**
 * This class handles the Magnetic Sensor
 */
public class MagneticSensor implements SensorEventListener //Implementing Listener to react to value changes
{
    private SensorManager mSensorManager; //Sensormanager
    private Sensor mSensor; // Magnetic Sensor
    TextView outputtext; // <- Experimental Code

    /*
    * Constructor, initializes the Sensor. Might throw Exception, if sensor does not exist (?)
    */
    public MagneticSensor(Context pContext, TextView outputtext) throws Exception
    {
        mSensorManager = (SensorManager) pContext.getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        this.outputtext = outputtext; // <- Experimental Code
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        double magnitude = Math.sqrt(Math.pow(event.values[0],2)+Math.pow(event.values[1],2)+Math.pow(event.values[2],2));
        outputtext.setText(""+magnitude); // <- Experimental Code
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) //Unused
    {
        ;
    }
    /*
    * (Un)registering Method
    */
    public void register()
    {
        mSensorManager.registerListener(this,mSensor,SensorManager.SENSOR_DELAY_NORMAL);
    }
    public void unregister()
    {
        mSensorManager.unregisterListener(this);
    }

}
