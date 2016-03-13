package com.stolzatrub.metaldetector;


import android.app.AlertDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    //
    private Button startbutton;
    private boolean on = false;
    private MagneticSensor msensor;
    private RotationHandler rhandler;

    /*
    * Will run, when App starts up
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Create Magnetic Sensor, catch Error
        try
        {
            msensor = new MagneticSensor(this,(TextView) findViewById(R.id.outputtext), (TextView) findViewById(R.id.advancedtex)); // <- Experimental Code
            rhandler = new RotationHandler(this, (TextView) findViewById(R.id.vectortext),msensor); //<- Experimental Rotationhandler
        }
        catch(Exception e) //Errorhandling
        {
            AlertDialog.Builder error= new AlertDialog.Builder(this);
            error.setMessage(e.toString());
            error.setPositiveButton("OK",null);
            error.show();
        }

        //Initalize startbutton
        startbutton = (Button) findViewById(R.id.startbutton);
        startbutton.setOnClickListener(new View.OnClickListener()
        {
            //OnClick Eventhandler, Experimental Code
            @Override
            public void onClick(View v)
            {
                if(on)
                {
                    ImageView searchgif = (ImageView) findViewById(R.id.imageView);
                    searchgif.setImageResource(R.drawable.nosearch);
                    msensor.unregister();
                    startbutton.setText("Start Metal Detection");
                    on = false;

                    rhandler.unregister(); //<- Experimental Code
                }
                else
                {
                    ImageView searchgif = (ImageView) findViewById(R.id.imageView);
                    msensor.register();
                    searchgif.setImageResource(R.drawable.search);
                    startbutton.setText("Stop Metal Detection");
                    on = true;

                    rhandler.register(); //<- Experimental Code
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { //Unused
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { //Unused
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId())
        {
            case R.id.recalibrate:
                msensor.recalibrateSensor();
                return true;
            case R.id.mode:
                msensor.toggleAdvanced();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
        /*
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        */
        //return super.onOptionsItemSelected(item);
    }
}
