package com.example.avc;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.Toast;

import com.example.avc.ui.othertesting.OtherTestingSymptomsFragment;
import com.example.avc.ui.othertesting.OtherTestingTimeFragment;
import com.example.avc.ui.selftesting.SelfTestingArmsFragment;
import com.example.avc.ui.selftesting.SelfTestingFaceFragment;
import com.example.avc.ui.selftesting.SelfTestingSpeechFragment;
import com.example.avc.ui.selftesting.SelfTestingTimeFragment;

import static com.example.avc.MainActivity.setWindowFlag;

public class SelfTesting extends AppCompatActivity {
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
}
