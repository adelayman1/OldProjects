package com.learn.jk.presentation.loginScreen;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.learn.jk.databinding.ActivityLoginBinding;
import com.learn.jk.databinding.DialogBinding;
import com.learn.jk.presentation.mainScreen.BaseActivity;
import com.learn.jk.presentation.signUpScreen.SignUpActivity;
import com.valdesekamdem.library.mdtoast.MDToast;

public class LoginActivity extends AppCompatActivity {
    private AlertDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityLoginBinding binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        LoginViewModel viewModel = ViewModelProviders.of(this).get(LoginViewModel.class);
        ProgressDialog progressDialog=new ProgressDialog(LoginActivity.this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Loading...");
        progressDialog.setCanceledOnTouchOutside(false);
        DialogBinding dialogBinding = DialogBinding.inflate(LayoutInflater.from(LoginActivity.this));
        dialogBinding.etDialogSecond.setVisibility(View.GONE);
        dialogBinding.lnDialogText.setVisibility(View.GONE);
        dialogBinding.lnDialogEditTexts.setVisibility(View.VISIBLE);
        dialogBinding.etDialogFirst.setHint("Enter email");
        dialog = new AlertDialog.Builder(LoginActivity.this)
                .setView(dialogBinding.getRoot())
                .create();

        dialogBinding.btnPositiveDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isEmpty(dialogBinding.etDialogFirst)){
                    viewModel.sendForgetMessage(dialogBinding.etDialogFirst.getText().toString());
                }
            }
        });
        viewModel.isLoading.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean)
                    progressDialog.show();
                else
                    progressDialog.dismiss();
            }
        });
        dialogBinding.btnNavigateDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.hide();
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEmpty(binding.etEmailLogin)) {
                    binding.etEmailLogin.setError("that cannot be empty");
                    return;
                }
                if (isEmpty(binding.etPasswordLogin)) {
                    binding.etPasswordLogin.setError("that cannot be empty");
                    return;
                }
                viewModel.isLoading.postValue(true);
                viewModel.Login(binding.etEmailLogin.getText().toString(), binding.etPasswordLogin.getText().toString());

            }
        });
        viewModel.loginSuccess.observe(LoginActivity.this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                System.out.println("testttttt");
               viewModel.isLoading.postValue(false);
                if (aBoolean) {
                    MDToast.makeText(LoginActivity.this, "Success", MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show();
                    startActivity(new Intent(LoginActivity.this, BaseActivity.class));
                    finish();
                }else
                    MDToast.makeText(LoginActivity.this, "error", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
            }
        });
        binding.btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
                finish();
            }
        });
        viewModel.forgetMessageSend.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                dialog.hide();
                if(aBoolean){
                    MDToast.makeText(LoginActivity.this, "rest code sent in your email", Toast.LENGTH_LONG,MDToast.TYPE_SUCCESS).show();
                }else {
                    MDToast.makeText(LoginActivity.this, "error", Toast.LENGTH_LONG,MDToast.TYPE_ERROR).show();
                }
            }
        });

        binding.tvForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
            }
        });
    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().isEmpty();
    }
}
