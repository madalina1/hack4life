package com.example.avc.ui.selftesting;

import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.avc.R;
import com.example.avc.SelfTesting;

import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

import info.debatty.java.stringsimilarity.*;

public class SelfTestingSpeechFragment extends Fragment implements RecognitionListener {
    private SpeechRecognizer speech = null;
    private SelfTestingSpeechViewModel mViewModel;
    private Intent recognizerIntent;
    private String LOG_TAG = "SelfTestingSpeechFragment";
    private static final int REQUEST_RECORD_PERMISSION = 100;

    public static SelfTestingSpeechFragment newInstance() {
        return new SelfTestingSpeechFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.self_testing_speech_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(SelfTestingSpeechViewModel.class);
        // TODO: Use the ViewModel
    }

    private void listen(){
        speech = SpeechRecognizer.createSpeechRecognizer(getContext());
        Log.i(LOG_TAG, "isRecognitionAvailable: " + SpeechRecognizer.isRecognitionAvailable(getContext()));
        speech.setRecognitionListener((RecognitionListener) this);

        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
                "ro_RO");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);

        speech.startListening(recognizerIntent);
    }

    @Override
    public void onStart() {
        super.onStart();
        MediaPlayer speechSound = MediaPlayer.create(getContext(), R.raw.speech);
        speechSound.start();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                listen();
                requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_PERMISSION);
            }
        }, 5000);
    }

    @Override
    public void onDestroy() {
        Log.i(LOG_TAG, "destroy");
        speech.destroy();
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_RECORD_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    speech.startListening(recognizerIntent);
                } else {
                    Toast.makeText(getContext(), "Permission Denied!", Toast
                            .LENGTH_SHORT).show();
                }
        }
    }

    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onBufferReceived(byte[] buffer)
    {

    }

    @Override
    public void onEndOfSpeech()
    {
        Log.d(LOG_TAG, "onEndOfSpeech");
        speech.stopListening();
    }

    @Override
    public void onError(int error)
    {
        String errorMessage = getErrorText(error);

        Log.d(LOG_TAG, "error = " + errorMessage);
        speech.stopListening();
    }

    @Override
    public void onEvent(int eventType, Bundle params)
    {

    }

    @Override
    public void onPartialResults(Bundle partialResults)
    {
        Log.d(LOG_TAG, "onPartialResults" + partialResults);
    }

    @Override
    public void onReadyForSpeech(Bundle params)
    {
        Log.d(LOG_TAG, "onReadyForSpeech");
    }

    @Override
    public void onResults(Bundle results)
    {
        //Log.d(TAG, "onResults"); //$NON-NLS-1$
        ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

        String text = "Ana are mere";
        Levenshtein l = new Levenshtein();
        ArrayList<Double> arrayList = new ArrayList<Double>();

        for (String result : matches) {
//            Toast.makeText(getContext(), result + "\n" + l.distance(result, text), Toast.LENGTH_SHORT).show();
            arrayList.add((Double) l.distance(result, text));
        }

        int minIndex = arrayList.indexOf(Collections.min(arrayList));
        Toast.makeText(getContext(), arrayList.get(minIndex).toString(), Toast.LENGTH_SHORT).show();

        Double unsureThreshHold = 10.0;
        Double sureThreshHold = 13.0;
        short no = -1;
        short maybe = 0;
        short yes = 1;

        if(arrayList.get(minIndex) <= unsureThreshHold ){
            ((SelfTesting) getActivity()).setSpeechResult(no);
        }
        else if(arrayList.get(minIndex) < sureThreshHold){
            ((SelfTesting) getActivity()).setSpeechResult(maybe);
        }
        else{
            ((SelfTesting) getActivity()).setSpeechResult(yes);
        }

        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, SelfTestingTimeFragment.newInstance()).commitNow();
    }

    @Override
    public void onRmsChanged(float rmsdB)
    {
        if(rmsdB<=5.0f){
            this.onEndOfSpeech();
        }
    }

    public static String getErrorText(int errorCode) {
        String message;
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "Client side error";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "Insufficient permissions";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "Network error";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "Network timeout";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "No match";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "RecognitionService busy";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "error from server";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "No speech input";
                break;
            default:
                message = "Didn't understand, please try again.";
                break;
        }
        return message;
    }
}
