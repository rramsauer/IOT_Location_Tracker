package com.rramsauer.iotlocatiotrackerapp.ui.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.rramsauer.iotlocatiotrackerapp.databinding.FragmentInformationBinding;
import com.rramsauer.iotlocatiotrackerapp.util.log.Logger;


public class InformationFragment extends Fragment {
    private FragmentInformationBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Logger.d("CLASS BLEFragment", "onCreateView");
        binding = FragmentInformationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        dataInitialize();

        return root;
    }

    private void dataInitialize() {
        Logger.d("CLASS BLEFragment", "Initialize date ...");


    }
}