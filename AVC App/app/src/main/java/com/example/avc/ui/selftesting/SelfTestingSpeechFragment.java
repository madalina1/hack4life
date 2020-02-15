package com.example.avc.ui.selftesting;

import androidx.lifecycle.ViewModelProviders;

import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.avc.R;

public class SelfTestingSpeechFragment extends Fragment {

    private SelfTestingSpeechViewModel mViewModel;

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

    @Override
    public void onStart() {
        super.onStart();
        MediaPlayer faceSound = MediaPlayer.create(getContext(), R.raw.speech);
        faceSound.start();
    }

}
