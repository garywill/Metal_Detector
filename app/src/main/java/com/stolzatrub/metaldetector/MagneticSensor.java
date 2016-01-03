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
    private int stage = 0;
    private int streakcounter = 0;

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
        int percentage = 0;

        //Calculate Average?
        if(samplestate)
        {
            calculateAverage(magnitude);
        }
        else
        {
            percentage = (int) searchForAnomalies(magnitude);
        }

        //Testoutput
        outputtext.setText("Magnitude:"+magnitude+"\nAverage:"+average+"\nTaken Samples: "+takensamples+
                "\nDeviation:"+ deviation+"\nLikelihood of Metal: "+percentage+"%"); // <- Experimental Code
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
    public double searchForAnomalies(double sample)
    {
        //Calculate the deviation
        deviation = sample - average;
        int devceiled =  (int) Math.abs(deviation);

        //Check for anomaly
        if(devceiled > 15)
        {
            if(stage != 1)
            {
                stage = 1;
                streakcounter = 0;
            }
        }
        else if(devceiled > 10)
        {
            if(stage != 2)
            {
                stage = 2;
                streakcounter = 0;
            }
        }
        else if (devceiled > 5)
        {
            if(stage != 1)
            {
                stage = 1;
                streakcounter = 0;
            }
        }
        else
        {
            stage = 0;
            streakcounter = 0;
            return 0.00;
        }
        streakcounter++;

        //Calculate likelihood
        double divisor = (double) (stage*5)/100;
        double percentage = streakcounter/divisor;
        if (percentage > 100)
        {
            return 100.00;
        }
        else
        {
            return percentage;
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
