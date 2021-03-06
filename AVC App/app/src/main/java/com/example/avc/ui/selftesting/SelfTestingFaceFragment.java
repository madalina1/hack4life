package com.example.avc.ui.selftesting;

import androidx.camera.core.CameraX;
import androidx.camera.core.CaptureConfig;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageAnalysisConfig;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureConfig;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.OptionsBundle;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.camera.core.UseCase;
import androidx.camera.core.impl.utils.executor.CameraXExecutors;
import androidx.constraintlayout.solver.widgets.Analyzer;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.avc.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.google.firebase.ml.vision.common.FirebaseVisionPoint;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;

public class SelfTestingFaceFragment extends Fragment {

    private SelfTestingFaceViewModel mViewModel;
    private int REQUEST_CODE_PERMISSIONS = 42;

    private TextureView  viewFinder;
    private int rotation = 0;
    Preview preview;
    ImageAnalysis analyzerUseCase;

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

    @SuppressLint("RestrictedApi")
    private void startCamera() {
//        Rational rational = new Rational(1,1);
        @SuppressLint("RestrictedApi") PreviewConfig previewConfig = new PreviewConfig.Builder().setLensFacing(CameraX.LensFacing.FRONT).build();
        preview = new Preview(previewConfig);

        preview.setOnPreviewOutputUpdateListener(output -> {
            ViewGroup parent = (ViewGroup) viewFinder.getParent();
            parent.removeView(viewFinder);
            parent.addView(viewFinder, 0);
            SurfaceTexture surfaceTexture = output.getSurfaceTexture();
            viewFinder.setSurfaceTexture(surfaceTexture);

            updateTransform();
        });

        // Setup image analysis pipeline that computes average pixel luminance
        ImageAnalysisConfig analyzerConfig = new ImageAnalysisConfig.Builder().setImageReaderMode(ImageAnalysis.ImageReaderMode.ACQUIRE_LATEST_IMAGE).setImageQueueDepth(20).build();

        // Build the image analysis use case and instantiate our analyzer
        analyzerUseCase = new ImageAnalysis(analyzerConfig);

        analyzerUseCase.setAnalyzer(CameraXExecutors.highPriorityExecutor(),new SmilingSymmetryAnalyzer());


        // Bind use cases to lifecycle
        // If Android Studio complains about "this" being not a LifecycleOwner
        // try rebuilding the project or updating the appcompat dependency to
        // version 1.1.0 or higher.
//        CameraX.bindToLifecycle(this, preview);
        CameraX.bindToLifecycle(this, analyzerUseCase);

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
                Toast.makeText(getContext(), "Permisiunile pentru locatie, camera, SMS apel telefonic nu au fost acordate de utilizator.", Toast.LENGTH_SHORT).show();
                getActivity().finish();
            } else {
                //start Camera
                viewFinder.post(this::startCamera);
            }
        }
    }


    private class SmilingSymmetryAnalyzer implements ImageAnalysis.Analyzer {
        private long lastAnalyzedTimestamp = 0L;
        private FirebaseVisionFaceDetector detector;
        private Boolean detectorInitialised = false;
        private Boolean isAvc = false;
        private Boolean smileAttempted = false;
        double minimumSmileAngle = 60.0;
        double maxAngleDiff = 20.0;

        public SmilingSymmetryAnalyzer(){
            initDetector();
            detectorInitialised = true;
        }

        @Override
        public void analyze(ImageProxy image, int rotationDegrees) {
//            if (image.getImage().getHardwareBuffer().describeContents())
//            BitmapFactory.decodeStream()
//            image.getImage()
            FirebaseVisionImage img = FirebaseVisionImage.fromMediaImage(Objects.requireNonNull(image.getImage()), degreesToFirebaseRotation(rotationDegrees));
            Task<List<FirebaseVisionFace>> result =
                    detector.detectInImage(img)
                            .addOnSuccessListener(
                                    new OnSuccessListener<List<FirebaseVisionFace>>() {
                                        @Override
                                        public void onSuccess(List<FirebaseVisionFace> faces) {
                                            // Task completed successfully
                                            if (faces.size() > 0){
                                                FirebaseVisionFace face = faces.get(0);

                                                FirebaseVisionPoint mouthLeft = face.getLandmark(FirebaseVisionFaceLandmark.MOUTH_LEFT).getPosition();
                                                FirebaseVisionPoint mouthRight = face.getLandmark(FirebaseVisionFaceLandmark.MOUTH_RIGHT).getPosition();
                                                FirebaseVisionPoint noseBase = face.getLandmark(FirebaseVisionFaceLandmark.NOSE_BASE).getPosition();
                                                FirebaseVisionPoint mouthBottom = face.getLandmark(FirebaseVisionFaceLandmark.MOUTH_BOTTOM).getPosition();

                                                double rightAngle = angleBetween(mouthBottom,noseBase, mouthRight);
                                                double leftAngle = angleBetween(noseBase, mouthBottom, mouthRight);

                                                smileAttempted = rightAngle <= minimumSmileAngle || leftAngle <= minimumSmileAngle;
                                                isAvc = Math.abs(rightAngle - leftAngle) >= maxAngleDiff;

                                                if (smileAttempted && isAvc)
                                                {
                                                    //todo signal somehow
                                                    return;
                                                }
                                            }

                                        }
                                    })
                            .addOnFailureListener(
                                    new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Task failed with an exception
                                            // ...
                                            int a = 0;
                                            a += 1;
                                        }
                                    });

        }
        private int degreesToFirebaseRotation(int degrees) {
            switch (degrees) {
                case 0:
                    return FirebaseVisionImageMetadata.ROTATION_0;
                case 90:
                    return FirebaseVisionImageMetadata.ROTATION_90;
                case 180:
                    return FirebaseVisionImageMetadata.ROTATION_180;
                case 270:
                    return FirebaseVisionImageMetadata.ROTATION_270;
                default:
                    throw new IllegalArgumentException(
                            "Rotation must be 0, 90, 180, or 270.");
            }
        }
        private void initDetector(){
            FirebaseVisionFaceDetectorOptions highAccuracyOpts =
                new FirebaseVisionFaceDetectorOptions.Builder()
                        .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
                        .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
                        .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
                        .build();

            detector = FirebaseVision.getInstance()
                .getVisionFaceDetector(highAccuracyOpts);

        }

        private double angleBetween(FirebaseVisionPoint p0, FirebaseVisionPoint p1, FirebaseVisionPoint p2) {
            //angle between [p0 p1] and [p0 p2]
            return Math.toDegrees(Math.atan2(p1.getX() - p0.getX(),p1.getY() - p0.getY())-
                    Math.atan2(p2.getX()- p0.getX(),p2.getY()- p0.getY()));
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        MediaPlayer faceSound = MediaPlayer.create(getContext(), R.raw.face);
        faceSound.start();
    }
}
