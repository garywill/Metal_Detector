package com.stolzatrub.metaldetector;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;

/**
 * Created by Florian on 01.01.2016.
 */
public class MagneticSensor
{
   private SensorManager mSensorManager;
   private Sensor mSensor;

    public MagneticSensor(Context pContext) throws Exception
    {
        mSensorManager = (SensorManager) pContext.getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

}
