package com.example.exercise4.ViewModel;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import androidx.appcompat.app.AlertDialog;
import com.example.exercise4.Model.FlightGearClient;
import com.example.exercise4.Model.TCPClient.OnExceptionListener;
import com.example.exercise4.View.JoystickView.OnMoveListener;

/**
 * The ViewModel class for the MVVM architecture. Serves as a model for the JoystickActivity class,
 * and serves as View for the FlightGearClient class (which serve as the model).
 * Implements OnMoveListener to serve as a listener for the JoystickView,
 * Implements OnExceptionListener to serve as a listener for the FlightGearClient.
 * Implements OnClickListener to serve as a listener for AlertDialog's close button.
 */
public class ViewModel implements OnMoveListener, OnExceptionListener, OnClickListener {

    private static ViewModel instance;
    private FlightGearClient model;
    private Activity view;

    /***
     * The constructor for the ViewModel class. Set the view to the JoystickActivity given to him
     * as parameter, and creates a new instance of FlightGearClient with given IP and Port
     * parameters as the model.
     * @param joystickActivity Activity - will serve as the view
     * @param ip String - used for the model
     * @param port String - used for the model
     */
    public ViewModel(Activity joystickActivity, String ip, String port) {
        this.view = joystickActivity;
        model = new FlightGearClient(ip, port);
        model.setExceptionListener(this);
    }

    /***
     * Get the normalized x and y parameters from given angle and length and send them to the model.
     * @param angle double - the current angle of the joystick handle, calculated in radians.
     * @param length int - the current distance of the joystick handle from them middle
     *               as a percent (0 - 100)
     */
    @Override
    public void onMove(double angle, int length) {
        // Apparently the cos/sin functions can't handle well PI or PI/2 values:
        double normX = Math.abs(angle) == Math.PI / 2 ?
                0.0 : ((double)length * Math.cos(angle)) / 100;
        double normY = Math.abs(angle) == Math.PI  ?
                0.0 :((double)length * Math.sin(angle)) / 100;
        model.setAileron(normX);
        model.setElevator(normY);
    }

    /***
     * Creates a new Alert Dialog on the view, describing the exception which occurred.
     * @param e Exception
     */
    @Override
    public void onException(Exception e) {
        new AlertDialog.Builder(view)
                .setTitle("An Exception Occurred:")
                .setMessage(e.toString())
                .setNeutralButton("Close", this)
                .show();
    }

    /**
     * Close the current Activity. Called by the AlertDialog's close button.
     * @param dialogInterface DialogInterface - NOT USED
     * @param i int - NOT USED
     */
    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        view.finish();
    }

    /***
     * Send the model a disconnect command. Used by the view when closing the JoystickActivity.
     */
    public void disconnect() {
        model.disconnect();
    }

    public void destroyInstance() {
        instance = null;
    }
}
