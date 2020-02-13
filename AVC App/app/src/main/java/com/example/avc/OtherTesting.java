package com.example.avc;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.avc.ui.othertesting.OtherTestingArmsFragment;
import com.example.avc.ui.othertesting.OtherTestingFaceFragment;
import com.example.avc.ui.othertesting.OtherTestingSpeechFragment;
import com.example.avc.ui.othertesting.OtherTestingSymptomsFragment;
import com.example.avc.ui.othertesting.OtherTestingTimeFragment;

import static com.example.avc.MainActivity.setWindowFlag;

public class OtherTesting extends AppCompatActivity {
    private Boolean backPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.other_testing_activity);
        this.backPressed = false;
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, OtherTestingFaceFragment.newInstance())
                    .commitNow();
        }

        if (Build.VERSION.SDK_INT >= 28) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    @Override
    public void onBackPressed() {
        if (!this.backPressed) {
            this.backPressed = true;
            Toast.makeText(getApplicationContext(), "Apăsați încă o dată pentru a ieși", Toast.LENGTH_SHORT).show();
        }
        else{
            this.backPressed = false;
            super.onBackPressed();
        }
    }

    public void goToOtherTestingArms(View view) {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, OtherTestingArmsFragment.newInstance()).commitNow();
    }
    public void goToOtherTestingSpeech(View view) {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, OtherTestingSpeechFragment.newInstance()).commitNow();
    }

    public void goToOtherTestingTime(View view) {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, OtherTestingTimeFragment.newInstance()).commitNow();
    }

    public void goToOtherTestingSymptoms(View view) {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, OtherTestingSymptomsFragment.newInstance()).commitNow();
    }
}
