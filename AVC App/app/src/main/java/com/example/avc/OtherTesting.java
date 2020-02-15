package com.example.avc;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Trace;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
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
    public short faceResult=-1;
    public short armsResult=-1;
    public short speechResult=-1;
    public boolean faceNumbness=false;
    public boolean headache=false;
    public boolean puking=false;
    public boolean balance=false;
    public boolean vision=false;
    public boolean confusion=false;

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
        if(view.getId() == R.id.otherSymptomsButton)
            getSupportFragmentManager().beginTransaction().replace(R.id.container, OtherTestingTimeFragment.newInstance()).commitNow();
        else{
            short unknown = 0;
            if(this.faceResult == 0)
                unknown++;
            if(this.armsResult== 0)
                unknown++;
            if(this.speechResult== 0)
                unknown++;
            if(this.faceResult == 1 || this.armsResult == 1 || this.speechResult ==1 || unknown>=2){
                getSupportFragmentManager().beginTransaction().replace(R.id.container, OtherTestingTimeFragment.newInstance()).commitNow();
            }
            else{
                getSupportFragmentManager().beginTransaction().replace(R.id.container, OtherTestingSymptomsFragment.newInstance()).commitNow();
            }
        }
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
                    this.faceResult = 1;
                    break;
            case R.id.radio_face_no:
                if (checked)
                    this.faceResult = -1;
                    break;
            case R.id.radio_face_idk:
                if (checked)
                    this.faceResult = 0;
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
                    this.armsResult = 1;
                    break;
            case R.id.radio_arms_no:
                if (checked)
                    this.armsResult = -1;
                    break;
            case R.id.radio_arms_idk:
                if (checked)
                    this.armsResult = 0;
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
                    this.speechResult = 1;
                    break;
            case R.id.radio_speech_no:
                if (checked)
                    this.speechResult = -1;
                    break;
            case R.id.radio_speech_idk:
                if (checked)
                    this.speechResult = 0;
                    break;
        }
    }

    public void onOtherTestingCheckboxClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.checkbox_symptoms_face:
                if(checked)
                    this.faceNumbness = true;
                else
                    this.faceNumbness = false;
                break;
            case R.id.checkbox_symptoms_headache:
                if(checked)
                    this.headache = true;
                else
                    this.headache = false;
                break;
            case R.id.checkbox_symptoms_equilibrium:
                if(checked)
                    this.balance = true;
                else
                    this.balance = false;
                break;
            case R.id.checkbox_symptoms_eyes:
                if(checked)
                    this.vision = true;
                else
                    this.vision = false;
                break;
            case R.id.checkbox_symptoms_speech:
                if(checked)
                    this.confusion = true;
                else
                    this.confusion = false;
                break;
            case R.id.checkbox_symptoms_vomit:
                if(checked)
                    this.puking = true;
                else
                    this.puking = false;
                break;
        }
    }
}
