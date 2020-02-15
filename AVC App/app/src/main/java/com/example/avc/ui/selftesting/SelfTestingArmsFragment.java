package com.example.avc.ui.selftesting;

import androidx.camera.core.CameraX;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.avc.R;
import com.example.avc.SelfTesting;

import static com.google.common.primitives.Floats.max;
import static java.lang.StrictMath.abs;

public class SelfTestingArmsFragment extends Fragment {

    private SelfTestingArmsViewModel mViewModel;
    private int REQUEST_CODE_PERMISSIONS = 42;


    private SensorManager sensorManager;
    private Sensor sensor;
    private float leftPressure=0.0f;
    private float rightPressure=0.0f;
    private float maxDifference=0.0f;

    public static SelfTestingArmsFragment newInstance() {
        return new SelfTestingArmsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.self_testing_arms_fragment, container, false);

        rootView.findViewById(R.id.push_button_left).setOnTouchListener((v, event) -> {
            leftPressure = event.getPressure();
            return false;
        });

        rootView.findViewById(R.id.push_button_right).setOnTouchListener((v, event) -> {
            rightPressure = event.getPressure();
            return false;
        });

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(SelfTestingArmsViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(getContext(),Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getContext(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "Permisiunile pentru camera, SMS apel telefonic nu au fost acordate de utilizator.", Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        MediaPlayer faceSound = MediaPlayer.create(getContext(), R.raw.arms);
        faceSound.start();


        Handler handler = new Handler();
        handler.postDelayed(() -> {
        }, 4000);


        float lowThreshHold = 0.2f;

        float unsureThreshHold = 0.2f;
        float sureThreshHold = 5.0f;



        new CountDownTimer(6000, 1000) {

            public void onTick(long millisUntilFinished) {
                maxDifference = max(maxDifference, abs(leftPressure - rightPressure));
            }

            public void onFinish() {
                short no = -1;
                short maybe = 0;
                short yes = 1;

                if(maxDifference <= unsureThreshHold ){
                    ((SelfTesting) getActivity()).setArmsResult(no);
                }
                else if(maxDifference < sureThreshHold){
                    ((SelfTesting) getActivity()).setArmsResult(maybe);
                }
                else{
                    ((SelfTesting) getActivity()).setArmsResult(yes);
                }

                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, SelfTestingSpeechFragment.newInstance()).commitNow();
            }
        }.start();
    }

}
