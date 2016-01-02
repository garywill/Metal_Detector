package com.stolzatrub.metaldetector;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * This class handles the Magnetic Sensor
 */
public class MagneticSensor implements SensorEventListener //Implementing Listener to react to value changes
{
   private SensorManager mSensorManager; //Sensormanager
   private Sensor mSensor; // Magnetic Sensor

    /*
    * Constructor, initializes the Sensor. Might throw Exception, if sensor does not exist (?)
    */
    public MagneticSensor(Context pContext) throws Exception
    {
        mSensorManager = (SensorManager) pContext.getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        ;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {
        ;
    } //Unused

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
