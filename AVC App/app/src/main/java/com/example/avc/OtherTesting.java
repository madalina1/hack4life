package com.example.avc;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.avc.ui.othertesting.OtherTestingFragment;

public class OtherTesting extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.other_testing_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, OtherTestingFragment.newInstance())
                    .commitNow();
        }
    }
}
