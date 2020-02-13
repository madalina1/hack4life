package com.example.avc.ui.othertesting;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.avc.R;

public class OtherTestingSymptomsFragment extends Fragment {

    private OtherTestingSymptomsViewModel mViewModel;

    public static OtherTestingSymptomsFragment newInstance() {
        return new OtherTestingSymptomsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.other_testing_symptoms_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(OtherTestingSymptomsViewModel.class);
        // TODO: Use the ViewModel
    }

}
