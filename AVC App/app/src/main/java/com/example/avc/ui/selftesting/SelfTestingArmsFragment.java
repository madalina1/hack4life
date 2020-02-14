package com.example.avc.ui.selftesting;

import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.avc.R;

public class SelfTestingArmsFragment extends Fragment {

    private SelfTestingArmsViewModel mViewModel;

    public static SelfTestingArmsFragment newInstance() {
        return new SelfTestingArmsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.self_testing_arms_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(SelfTestingArmsViewModel.class);
        // TODO: Use the ViewModel
    }


}
