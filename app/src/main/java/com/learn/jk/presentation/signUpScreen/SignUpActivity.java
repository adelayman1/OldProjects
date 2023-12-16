package com.learn.jk.presentation.signUpScreen;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.learn.jk.databinding.ActivitySignUpBinding;
import com.learn.jk.presentation.loginScreen.LoginActivity;
import com.learn.jk.presentation.mainScreen.BaseActivity;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.util.ArrayList;
import java.util.Calendar;

public class SignUpActivity extends AppCompatActivity {
    private SignUpViewModel viewModel;
    private boolean isMale = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySignUpBinding binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        viewModel = ViewModelProviders.of(this).get(SignUpViewModel.class);
        ProgressDialog progressDialog = new ProgressDialog(SignUpActivity.this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Signing Up...");
        progressDialog.setCanceledOnTouchOutside(false);
        viewModel.viewFirstLayout.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    binding.lnFirstSignUp.setVisibility(View.VISIBLE);
                    binding.lnFinishSignUp.setVisibility(View.GONE);
                } else {

                    binding.lnFirstSignUp.setVisibility(View.GONE);
                    binding.lnFinishSignUp.setVisibility(View.VISIBLE);
                }
            }
        });
        viewModel.isSuccess.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                viewModel.isLoading.postValue(false);
                if (aBoolean) {
                    MDToast.makeText(SignUpActivity.this, "Success", MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show();
                    startActivity(new Intent(SignUpActivity.this, BaseActivity.class));
                    finish();
                } else
                    MDToast.makeText(SignUpActivity.this, "error", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
            }
        });
        viewModel.isLoading.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean)
                    progressDialog.show();
                else
                    progressDialog.dismiss();
            }
        });
        binding.btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isEmpty(binding.etFirstNameSignUp)) {
                    binding.etFirstNameSignUp.setError("that cannot be empty");
                    return;
                }
                if (isEmpty(binding.etLastNameSignUp)) {
                    binding.etLastNameSignUp.setError("that cannot be empty");
                    return;
                }
                if (isEmpty(binding.spinnerDay)) {
                    binding.spinnerDay.setError("that cannot be empty");
                    return;
                }
                if (Calendar.getInstance().get(Calendar.YEAR) - Integer.parseInt(binding.spinnerYear.getSelectedItem().toString()) <= 7) {
                    MDToast.makeText(SignUpActivity.this, "you should be bigger than 7 years", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                    return;
                }
                viewModel.changeLayoutVisibility(false);
            }
        });
        binding.radioMale.setChecked(true);
        binding.radioMale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isMale = b;
            }
        });

        ArrayList<String> years = new ArrayList<String>();
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = thisYear; i >= 1970; i--) {
            years.add(Integer.toString(i));
        }
        String[] months = new String[]{"January", "February",
                "March", "April", "May", "June", "July", "August", "September",
                "October", "November", "December"};

        ArrayAdapter<String> yearsAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, years);
        binding.spinnerYear.setAdapter(yearsAdapter);
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, months);
        binding.spinnerMonth.setAdapter(monthAdapter);
        binding.btnFinishSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEmpty(binding.etEmailSignUp)) {
                    binding.etEmailSignUp.setError("that cannot be empty");
                    return;
                }
                if (isEmpty(binding.etPasswordSignUp)) {
                    binding.etPasswordSignUp.setError("that cannot be empty");
                    return;
                }
                String date = binding.spinnerYear.getSelectedItem() + "/" + binding.spinnerMonth.getSelectedItem() + "/" + binding.spinnerDay.getText().toString();
                String day = binding.spinnerDay.getText().toString();
                String month = (String) binding.spinnerMonth.getSelectedItem();
                String year = (String) binding.spinnerYear.getSelectedItem();
                viewModel.isLoading.postValue(true);
                viewModel.SignUp(binding.etEmailSignUp.getText().toString(), binding.etPasswordSignUp.getText().toString(), binding.etFirstNameSignUp.getText().toString(), binding.etLastNameSignUp.getText().toString(), date, isMale, day, month, year);
            }
        });
        binding.tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().isEmpty();
    }
}