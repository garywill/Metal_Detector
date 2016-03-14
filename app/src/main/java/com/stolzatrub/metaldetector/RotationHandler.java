package com.stolzatrub.metaldetector;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.TextView;

/**
 * This class monitors the angle the device and interrupts the main application if necessary
 */
public class RotationHandler implements SensorEventListener {
    private Context pContext = null;

    private SensorManager rSensorManager;
    private Sensor aSensor;
    private Sensor mSensor;
    private TextView outputtext;

    private double verticalAveragearr[] = new double[25];
    private double horizontalAveragearr[] = new double[25];
    private double verticalAverage = 0.00;
    private double horizontalAverage = 0.00;
    private int samplesize = 0;
    private boolean calibrated = false;
    private double vertical = 0.00;
    private double horizontal = 0.00;
    private MagneticSensor mhandler = null;

    private boolean warning = false;

    float[] mGravity;
    float[] mGeomagnetic;
    public RotationHandler(Context pContext,TextView outputtext, MagneticSensor mhandler) throws Exception
    {
        rSensorManager = (SensorManager) pContext.getSystemService(Context.SENSOR_SERVICE);
        aSensor = rSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensor = rSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if(mSensor == null || aSensor == null)
        {
            throw new SensorException("No Magnetic Sensor/Accelerometer available");
        }
        this.outputtext = outputtext; // <- Experimental Code
        this.mhandler = mhandler;
        this.pContext = pContext;
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
                if(calibrated)
                {
                    vertical = Math.abs((Math.abs(orientation[1]) - Math.abs(verticalAverage)));
                    horizontal = Math.abs((Math.abs(orientation[2]) - Math.abs(horizontalAverage)));
                    if(vertical > 0.6)
                    {
                        recalibrate();
                        mhandler.recalibrateSensor();
                        showWarning();
                    }
                    if(horizontal > 1.5)
                    {
                        recalibrate();
                        mhandler.recalibrateSensor();
                        showWarning();
                    }
                }
                else
                {
                    calibrate(orientation[1], orientation[2]);
                }
                //outputtext.setText("" + calibrated + "\n" + orientation[1] + "\n" + verticalAverage);
            }
        }
    }

    public void recalibrate()
    {
        calibrated = false;
        verticalAverage = 0.00;
        horizontalAverage = 0.00;
        samplesize = 0;
    }

    public void calibrate(double vertical, double horizontal)
    {
        if (samplesize >= verticalAveragearr.length)
        {
            calibrated = true;
            for(double x : verticalAveragearr)
            {
                verticalAverage += x;
            }
            verticalAverage = verticalAverage / verticalAveragearr.length;
            for(double x : horizontalAveragearr)
            {
                horizontalAverage= x;
            }
            horizontalAverage = horizontalAverage / verticalAveragearr.length;
        }
        else
        {
            verticalAveragearr[samplesize] = vertical;
            horizontalAveragearr[samplesize] = horizontal;
            samplesize++;
        }
    }

    public void showWarning()
    {
        if(!warning)
        {
            AlertDialog.Builder warningmess= new AlertDialog.Builder(pContext);
            warningmess.setMessage("It seems like you have rotated or tilted your phone.\nThis can cause false readings, therefore a recalibration is needed" +
                    "!\nYou can also use this for fast recalibration.");
            warningmess.setPositiveButton("Don't show again", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    recalibrate();
                    mhandler.recalibrateSensor();
                }
            });
            warningmess.show();
            warning = true;
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
