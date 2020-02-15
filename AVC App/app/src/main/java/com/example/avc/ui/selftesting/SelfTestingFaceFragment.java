package com.example.avc.ui.selftesting;

import androidx.camera.core.AspectRatio;
import androidx.camera.core.CameraInfoUnavailableException;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.Rational;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.avc.R;

import java.util.concurrent.TimeUnit;

public class SelfTestingFaceFragment extends Fragment {

    private SelfTestingFaceViewModel mViewModel;
    private int REQUEST_CODE_PERMISSIONS = 42;

    private TextureView  viewFinder;
    private int rotation = 0;

    public static SelfTestingFaceFragment newInstance() {
        return new SelfTestingFaceFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.self_testing_face_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewFinder = getView().findViewById(R.id.self_face_view_finder);
        System.out.println(viewFinder);

        if (ContextCompat.checkSelfPermission(getContext(),Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getContext(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.CAMERA,Manifest.permission.SEND_SMS,Manifest.permission.CALL_PHONE}, REQUEST_CODE_PERMISSIONS);
        }
        else{
            //start Camera
            viewFinder.post(this::startCamera);
        }
        viewFinder.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> updateTransform());



        super.onViewCreated(view, savedInstanceState);
    }

    private void startCamera() {
//        Rational rational = new Rational(1,1);
        @SuppressLint("RestrictedApi") PreviewConfig previewConfig = new PreviewConfig.Builder().setLensFacing(CameraX.LensFacing.FRONT).build();
        Preview preview = new Preview(previewConfig);

        preview.setOnPreviewOutputUpdateListener(output -> {
            ViewGroup parent = (ViewGroup) viewFinder.getParent();
            parent.removeView(viewFinder);
            parent.addView(viewFinder, 0);
            SurfaceTexture surfaceTexture = output.getSurfaceTexture();
            viewFinder.setSurfaceTexture(surfaceTexture);

            updateTransform();
        });

        CameraX.bindToLifecycle( this, preview);
    }

    private void updateTransform() {
        Matrix matrix = new Matrix();

        //Find the center
        float centerX = viewFinder.getWidth() / 2f;
        float centerY = viewFinder.getHeight() / 2f;

        //Get correct rotation
        rotation = viewFinder.getDisplay().getRotation();

        matrix.postRotate((float)-rotation, centerX, centerY);

        float bufferRatio = 1.0f;

        int scaledWidth;
        int scaledHeight;
        // Match longest sides together -- i.e. apply center-crop transformation
        if (viewFinder.getWidth() > viewFinder.getHeight()) {
            scaledHeight = viewFinder.getWidth();
            scaledWidth = Math.round(viewFinder.getWidth() * bufferRatio);
        } else {
            scaledHeight = viewFinder.getHeight();
            scaledWidth = Math.round(viewFinder.getHeight() * bufferRatio);
        }

        // Compute the relative scale value
        float xScale = scaledWidth / (float)viewFinder.getWidth();
        float yScale = scaledHeight / (float)viewFinder.getHeight();

        // Scale input buffers to fill the view finder
        matrix.preScale(xScale, yScale, centerX, centerY);


        viewFinder.setTransform(matrix);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(SelfTestingFaceViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(getContext(),Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getContext(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "Permisiunile pentru camera, SMS apel telefonic nu au fost acordate de utilizator.", Toast.LENGTH_SHORT).show();
                getActivity().finish();
            } else {
                //start Camera
                viewFinder.post(this::startCamera);
            }
        }
    }


    private class SmilingSymmetryAnalyzer implements ImageAnalysis.Analyzer {
        private long lastAnalyzedTimestamp = 0L;

        @Override
        public void analyze(ImageProxy image, int rotationDegrees) {

        }
    }

}
