package com.learn.jk.presentation.settingScreen;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.learn.jk.databinding.FragmentSettingBinding;

public class SettingFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentSettingBinding binding=FragmentSettingBinding.inflate(inflater,container,false);

        return binding.getRoot();
    }
}