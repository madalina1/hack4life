package com.example.avc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.Toast;

import com.example.avc.ui.selftesting.SelfTestingArmsFragment;
import com.example.avc.ui.selftesting.SelfTestingFaceFragment;
import com.example.avc.ui.selftesting.SelfTestingSpeechFragment;
import com.example.avc.ui.selftesting.SelfTestingSymptomsFragment;
import com.example.avc.ui.selftesting.SelfTestingTimeFragment;

import static com.example.avc.MainActivity.setWindowFlag;

public class SelfTesting extends AppCompatActivity {
    private Boolean backPressed;

    private short faceResult=-1;
    private short armsResult=-1;
    private short speechResult=-1;
    public boolean faceNumbness=false;
    public boolean headache=false;
    public boolean puking=false;
    public boolean balance=false;
    public boolean vision=false;
    public boolean confusion=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.self_testing_activity);
        this.backPressed = false;
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, SelfTestingFaceFragment.newInstance())
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

    public void goToSelfTestingArms(View view) {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, SelfTestingArmsFragment.newInstance()).commitNow();
    }

    public void goToSelfTestingSpeech(View view) {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, SelfTestingSpeechFragment.newInstance()).commitNow();
    }

    public void goToSelfTestingTime(View view) {
        if(view.getId() == R.id.selfSymptomsButton)
            getSupportFragmentManager().beginTransaction().replace(R.id.container, SelfTestingTimeFragment.newInstance()).commitNow();
        else{
            short unknown = 0;
            if(this.getFaceResult() == 0)
                unknown++;
            if(this.getArmsResult() == 0)
                unknown++;
            if(this.getSpeechResult() == 0)
                unknown++;
            if(this.getFaceResult() == 1 || this.getArmsResult() == 1 || this.getSpeechResult() ==1 || unknown>=2){
                getSupportFragmentManager().beginTransaction().replace(R.id.container, SelfTestingTimeFragment.newInstance()).commitNow();
            }
            else{
                getSupportFragmentManager().beginTransaction().replace(R.id.container, SelfTestingSymptomsFragment.newInstance()).commitNow();
            }
        }
    }


    public void onSelfTestingCheckboxClicked(View view) {
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

    public void callEmergency(View view) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:112112"));
        startActivity(intent);
    }

    public short getFaceResult() {
        return faceResult;
    }

    public void setFaceResult(short faceResult) {
        this.faceResult = faceResult;
    }

    public short getArmsResult() {
        return armsResult;
    }

    public void setArmsResult(short armsResult) {
        this.armsResult = armsResult;
    }

    public short getSpeechResult() {
        return speechResult;
    }

    public void setSpeechResult(short speechResult) {
        this.speechResult = speechResult;
    }
}
