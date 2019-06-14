package com.example.exercise4;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.net.UnknownHostException;

public class JoystickActivity extends AppCompatActivity {

    private FlightGearClient fg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final JoystickView joystick = new JoystickView(this);
        setContentView(joystick);
        fg = null;
        try {
            fg = new FlightGearClient("10.0.2.2","5402");
            joystick.setOnMoveListener(new JoystickView.OnMoveListener() {
                @Override
                public void onMove(double angle, int length) {
                    double normX = ((double)length * Math.cos(angle)) / 100;
                    double normY = ((double)length * Math.sin(angle)) / 100;
                    // Apparently the cos/sin functions can't handle well some values.
                    if (normX == 6.123233995736766E-17) {
                        normX = 0;
                    }
                    if (normY == 1.2246467991473532E-16) {
                        normY = 0;
                    }
                    fg.setAileron(normX);
                    fg.setElevator(normY);
                    Log.i("JoystickActivity", "angle="+angle+", length="+length
                            +", cos(angle)="+Math.cos(angle)+", sin(angle)="+Math.sin(angle));
                    Log.i("JoystickActivity", "Joystick was moved to " + normX + "," + normY);
                }
            });
        } catch (UnknownHostException e) {
            Log.e("TCP","Failed creating the FlightGearClient instance");
        }
    }

    public void sendParams(View view) {
        double aileron = 0;
        double elevator = 0;
        fg.setAileron(aileron);
        fg.setElevator(elevator);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fg.stop();
    }
}
