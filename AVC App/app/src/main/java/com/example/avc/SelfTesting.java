package com.example.avc;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.avc.ui.selftesting.SelfTestingFragment;

public class SelfTesting extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.self_testing_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, SelfTestingFragment.newInstance())
                    .commitNow();
        }
    }
}
