package com.stolzatrub.metaldetector;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.TextView;

/**
 * This class monitors the angle the device and interrupts the main application if necessary
 */
public class RotationHandler implements SensorEventListener {
    private SensorManager rSensorManager;
    private Sensor aSensor;
    private Sensor mSensor;
    private TextView outputtext;

    float[] mGravity; //Placeholder
    float[] mGeomagnetic; //Placeholder
    public RotationHandler(Context pContext,TextView outputtext) throws Exception
    {
        rSensorManager = (SensorManager) pContext.getSystemService(Context.SENSOR_SERVICE);
        aSensor = rSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensor = rSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        this.outputtext = outputtext; // <- Experimental Code
    }



    @Override
    public void onSensorChanged(SensorEvent event) {
        /* Placeholder Code for Rotationhandling */
        /*
         * The first Value is not important
         * The second Value describes the vertical rotation
         * The third Value describes the horizontal rotation
         */
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            mGravity = event.values;
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            mGeomagnetic = event.values;
        if (mGravity != null && mGeomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                outputtext.setText("" + orientation[0] + "\n" + orientation[1] + "\n" + orientation[2]);
            }
        }
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
        rSensorManager.registerListener(this, aSensor, SensorManager.SENSOR_DELAY_NORMAL);
        rSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }
    public void unregister()
    {
        rSensorManager.unregisterListener(this);
    }
}
