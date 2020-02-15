package com.example.avc;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.avc.ui.selftesting.SelfTestingArmsFragment;
import com.example.avc.ui.selftesting.SelfTestingFaceFragment;
import com.example.avc.ui.selftesting.SelfTestingSpeechFragment;
import com.example.avc.ui.selftesting.SelfTestingTimeFragment;

import static com.example.avc.MainActivity.setWindowFlag;

public class SelfTesting extends AppCompatActivity {
    private Boolean backPressed;

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
        getSupportFragmentManager().beginTransaction().replace(R.id.container, SelfTestingTimeFragment.newInstance()).commitNow();
    }

}
