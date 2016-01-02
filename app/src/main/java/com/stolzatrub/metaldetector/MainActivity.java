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
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private Button startbutton;
    /*
    * Will run, when App starts up
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Initalize startbutton
        startbutton = (Button) findViewById(R.id.startbutton);
        startbutton.setOnClickListener(new View.OnClickListener()
        {
            //OnClick Eventhandler. No Support Lambda Expressions :(
            @Override
            public void onClick(View v)
            {
                TextView outputtext = (TextView) findViewById(R.id.outputtext);
                outputtext.setText("It works");
            }
        });

        //Create Magnetic Sensor, catch Error
        try
        {
            MagneticSensor msensor = new MagneticSensor(this);
        }
        catch(Exception e) //Errorhandling
        {
            AlertDialog.Builder fehler = new AlertDialog.Builder(this);
            fehler.setMessage(e.toString());
            fehler.setPositiveButton("OK",null);
            fehler.show();
        }

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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
