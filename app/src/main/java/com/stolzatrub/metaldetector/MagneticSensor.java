package com.stolzatrub.metaldetector;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Created by Florian on 01.01.2016.
 */
public class MagneticSensor implements SensorEventListener
{
   private SensorManager mSensorManager;
   private Sensor mSensor;

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
    }

    public void register()
    {
        mSensorManager.registerListener(this,mSensor,SensorManager.SENSOR_DELAY_NORMAL);
    }
    public void unregister()
    {
        mSensorManager.unregisterListener(this);
    }

}
