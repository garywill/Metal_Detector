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
    private int sensitivity = 5; //default = 5 , lower = 10

    //These variables handle the sensor
    private SensorManager mSensorManager; //Sensormanager
    private Sensor mSensor; // Magnetic Sensor

    TextView outputtext; // <- Experimental Code

    private boolean advancedmode = false;
    TextView advancedoutputtext;

    /*
    * Constructor, initializes the Sensor. Might throw Exception, if sensor does not exist (?)
    */
    public MagneticSensor(Context pContext, TextView outputtext, TextView advancedoutputtext) throws Exception
    {
        mSensorManager = (SensorManager) pContext.getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if(mSensor == null)
        {
            throw new SensorException("No Magnetic Sensor available!");
        }
        this.outputtext = outputtext; // <- Experimental Code
        this.advancedoutputtext = advancedoutputtext;
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
            outputtext.setText("Recalibrating");
        }
        else
        {
            percentage = (int) searchForAnomalies(magnitude);
            outputtext.setText("Likelihood of Metal: "+percentage+"%");
        }

        //Testoutput
        //outputtext.setText("Likelihood of Metal: "+percentage+"%");
        if(advancedmode) {
        advancedoutputtext.setText("Magnitude:"+magnitude+"\nAverage:"+average+"\nTaken Samples: "+takensamples+
                "\nDeviation:"+ deviation); // <- Experimental Code

        }
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
        if(devceiled > (sensitivity*3))
        {
            if(stage != 1)
            {
                stage = 1;
                streakcounter = 0;
            }
        }
        else if(devceiled > (sensitivity*2))
        {
            if(stage != 2)
            {
                stage = 2;
                streakcounter = 0;
            }
        }
        else if (devceiled > (sensitivity))
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
        double divisor = (double) (stage*10)/100;
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

    public void toggleAdvanced()
    {
        if(advancedmode)
        {
            advancedmode = false;
            advancedoutputtext.setText("");
        }
        else
        {
            advancedmode = true;
        }
    }

    public void setSensitivity(boolean lower)
    {
        if(lower)
        {
            sensitivity = 10;
        }
        else
        {
            sensitivity = 5;
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
