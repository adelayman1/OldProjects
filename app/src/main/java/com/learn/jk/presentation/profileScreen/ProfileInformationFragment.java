package com.learn.jk.presentation.profileScreen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.learn.jk.databinding.FragmentProfileInformationBinding;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.util.HashMap;
import java.util.Map;


public class ProfileInformationFragment extends Fragment implements IInformationItemListener {
    private Map<String, String> items;
    private InformationAdapter adapter;
    private ProfileViewModel viewModel;
    private String profileUID;
    FragmentProfileInformationBinding binding;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = ViewModelProviders.of(requireActivity()).get(ProfileViewModel.class);
        items = new HashMap<>();
        profileUID = getArguments().getString("uid");
        adapter = new InformationAdapter(items, this);
        binding.rcAllInformation.setAdapter(adapter);
        binding.rcAllInformation.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        viewModel.isInfoChangesSuccess.observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean)
                    MDToast.makeText(getContext(), "successful", MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show();
                else
                    MDToast.makeText(getContext(), "Error", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();

            }
        });
        viewModel.getAllInfo(profileUID).observe(getViewLifecycleOwner(), new Observer<HashMap<String, String>>() {
            @Override
            public void onChanged(HashMap<String, String> map) {
                items.clear();
                items.putAll(map);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileInformationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void OnDeleteClickListener(String itemID) {
        viewModel.deleteInfoItem(itemID);
    }

    @Override
    public boolean isMyProfile() {
        return viewModel.getMyUid().equals(profileUID);
    }
}