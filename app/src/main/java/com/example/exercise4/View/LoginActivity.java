package com.example.exercise4.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.exercise4.R;

import java.net.InetAddress;

/**
 * The login activity for the application. Get an IP and Port from the user, and if valid will call
 * to the JoystickActivity.
 */
public class LoginActivity extends AppCompatActivity {

    /***
     * Overriding the Activity's onCreate method. Set the contentView as the xml layout file.
     * @param savedInstanceState Bundle - used by the inherited class.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

    }


    /**
     * Used by the connect button on the layout.
     * Get the strings currently typed into the IP and Port TextBoxes, validate them by trying to
     * parse them (IP as an IP address, Port as an Integer), and if valid creates a new instance of
     * JoystickActivity, send it the ip and port given and start it.
     * @param view View
     */
    public void connectCommand(View view) {
        String ipString = ((EditText)findViewById(R.id.ipText)).getText().toString();
        String portString = ((EditText)findViewById(R.id.portText)).getText().toString();
        try {
            InetAddress.getByName(ipString);
            try {
                Integer.parseInt(portString);
                Intent intent = new Intent(this, JoystickActivity.class);
                intent.putExtra("ip", ipString);
                intent.putExtra("port", portString);
                startActivity(intent);
            } catch (Exception e) {
                alert("Port given is not an Integer.");
            }
        } catch (Exception e) {
            alert("IP given is not a valid IP address.");
        }
    }

    /**
     * Creates an AlertDialog on the LoginActivity view.
     * Is used in case of exception while parsing the IP or the Port, and is given a message
     * accordingly. The Close button is given null listener and therefore will do nothing except
     * closing the Dialog.
     * @param msg String - the message which will be shown on the AlertDialog
     */
    public void alert(String msg) {
        new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage(msg)
                    .setNeutralButton("Close",null)
                    .show();
    }
}