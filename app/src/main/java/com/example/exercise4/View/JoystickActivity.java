package com.example.exercise4.View;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import com.example.exercise4.ViewModel.ViewModel;

/***
 * The JoystickActivity class contains a single JoystickView whom onMoveListener is bound to
 * the ViewModel.
 */
public class JoystickActivity extends AppCompatActivity {

    ViewModel vm;

    /***
     * Overriding the Activity's onCreate method, creating the JoystickView and the ViewModel
     * and binding the ViewModel (implements onMoveListener) to the JoystickView.
     * @param savedInstanceState Bundle used by the Activity class.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        vm = new ViewModel(this,
                intent.getStringExtra("ip"),
                intent.getStringExtra("port"));
        // Created the JoystickView here and not on a Layout because I had some issues binding the
        // onMoveListener using DataBinding.
        JoystickView joystick = new JoystickView(this);
        joystick.setOnMoveListener(vm);
        setContentView(joystick);
    }

    /***
     * Overriding the Activity's onDestroy method, sending disconnect command to the ViewModel.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        vm.disconnect();
        vm.destroyInstance();
    }
}