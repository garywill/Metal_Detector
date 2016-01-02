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
    //These variables store sample information
    private int takensamples = 0;
    private double[] samplearr = new double[25];
    private boolean samplestate = true; //Collect Samples?
    private double average = 0.00; //Average

    //These variables are used in the detecting algorithm
    private double deviation = 0.00;

    //These variables handle the sensor
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
        //Calculate Microtesla from Sensor
        double magnitude = Math.sqrt(Math.pow(event.values[0],2)+Math.pow(event.values[1],2)+Math.pow(event.values[2],2));

        //Calculate Average?
        if(samplestate)
        {
            calculateAverage(magnitude);
        }
        else
        {
            //searchForAnomalies(magnitude);
        }

        //Testoutput
        outputtext.setText("Magnitude:"+magnitude+"\nAverage:"+average+"\nTaken Samples: "+takensamples+
                "\nDeviation:"+ deviation); // <- Experimental Code
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) //Unused
    {
        ;
    }

    /*
    * Calculate the average of 25 samples to calibrate the searching algorithm. May increase samplesize later
    */
    public void calculateAverage(double sample)
    {
        if(takensamples >= samplearr.length)
        {
            samplestate = false;
            for(double x : samplearr)
            {
                average += x;
            }
            average = average/samplearr.length;
        }
        else
        {
            samplearr[takensamples] = sample;
            takensamples++;
        }
    }

    /*
    * Recalibrate and start calculating the average again on next SensorEvent
    */
    public void recalibrateSensor()
    {
        samplestate = true;
        takensamples = 0;
        average = 0.00;
    }

    /*
    * This method compares sample data and looks for anomalies (high values, low values). This indicates the
    * presence of ferromagnetic material or interfering fields (powerlines)
    */
    public void searchForAnomalies(double sample)
    {
        //Calculate the deviation
        deviation = sample - average;
        int devceiled =  (int) Math.abs(deviation);
        //TODO
        if(devceiled > 5 )
        {
            if(devceiled > 10)
            {
                if(devceiled > 15)
                {
                    //That's a clear anomaly
                    //But let's check if it's only a sampling error (take a few testsamples maybe 5)
                }
                //That is most certainly an anomaly
                //Take a few testsamples to verify maybe 10
            }
            //This could be a misreading, as the sensors are quite sensitive (maybe the user changed the angle of the device)
            //Take further steps to verify
        }

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
