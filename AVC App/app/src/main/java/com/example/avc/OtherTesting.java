package com.example.avc;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
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

    public void onFaceRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        Button nextFaceButton = findViewById(R.id.otherFaceButton);
        nextFaceButton.setEnabled(true);
        nextFaceButton.setTextColor(getColor(R.color.buttonColor));
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_face_yes:
                if (checked)
                    break;
            case R.id.radio_face_no:
                if (checked)
                    break;
            case R.id.radio_face_idk:
                if (checked)
                    break;
        }
    }

    public void onArmsRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        Button nextArmsButton = (Button) findViewById(R.id.otherArmsButton);
        nextArmsButton.setEnabled(true);
        nextArmsButton.setTextColor(getColor(R.color.buttonColor));

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_arms_yes:
                if (checked)
                    break;
            case R.id.radio_arms_no:
                if (checked)
                    break;
            case R.id.radio_arms_idk:
                if (checked)
                    break;
        }
    }

    public void onSpeechRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        Button nextSpeechButton = (Button) findViewById(R.id.otherSpeechButton);
        nextSpeechButton.setEnabled(true);
        nextSpeechButton.setTextColor(getColor(R.color.buttonColor));
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_speech_yes:
                if (checked)
                    break;
            case R.id.radio_speech_no:
                if (checked)
                    break;
            case R.id.radio_speech_idk:
                if (checked)
                    break;
        }
    }
}
