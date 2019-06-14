package com.example.exercise4;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.net.InetAddress;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void connectCommand(View view) {
        String ipString = ((EditText)findViewById(R.id.ipText)).getText().toString();
        String portString = ((EditText)findViewById(R.id.portText)).getText().toString();
        try {
            InetAddress.getByName(ipString);
            Integer.parseInt(portString);
            Intent intent = new Intent(this, JoystickActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            Log.e("TCP", "C: Error", e);
            alert(this, e);
        }
    }

    public static void alert(Context context, Exception e) {
        new AlertDialog.Builder(context)
                    .setTitle("Error")
                    .setMessage(e.toString())
                    .setNeutralButton("Close",null)
                    .show();
    }
}