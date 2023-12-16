package com.learn.jk.presentation.profileScreen.editScreen;

import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.learn.jk.databinding.FragmentAccountTypeBinding;
import com.valdesekamdem.library.mdtoast.MDToast;

public class AccountTypeFragment extends Fragment {
    private String type = "";
    FragmentAccountTypeBinding binding;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EditViewModel viewModel = ViewModelProviders.of(requireActivity()).get(EditViewModel.class);

        binding.etWebsiteURL.setVisibility(View.GONE);
        binding.ivAcceptTypeAccountChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!type.isEmpty()) {
                    if (type.equals("web")) {
                        if (!binding.etWebsiteURL.getText().toString().isEmpty()) {
                            if (Patterns.WEB_URL.matcher(binding.etWebsiteURL.getText().toString()).matches()) {
                                viewModel.changeAccountType(type, binding.etWebsiteURL.getText().toString());
                            } else {

                                binding.etWebsiteURL.setError("please insert true url");
                            }
                        } else {
                            binding.etWebsiteURL.setError("this cannot be null");
                        }
                    } else
                        viewModel.changeAccountType(type);
                } else {
                    MDToast.makeText(getContext(), "please change data first", MDToast.LENGTH_SHORT, MDToast.TYPE_INFO).show();
                }
            }
        });
        binding.radioNormal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    type = "normal";
                }
            }
        });
        binding.radioPrivate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    type = "private";
                }
            }
        });
        binding.radioWeb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    type = "web";
                    binding.etWebsiteURL.setVisibility(View.VISIBLE);
                } else {
                    binding.etWebsiteURL.setVisibility(View.GONE);
                }
            }
        });

        viewModel.isAccountChangedSuccess.observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean)
                    MDToast.makeText(getContext(), "success", MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show();
                else
                    MDToast.makeText(getContext(), "error", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         binding = FragmentAccountTypeBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }
}