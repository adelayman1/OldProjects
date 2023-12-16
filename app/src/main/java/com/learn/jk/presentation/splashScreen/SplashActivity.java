package com.learn.jk.presentation.splashScreen;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.learn.jk.data.GLOBAL;
import com.learn.jk.databinding.DialogBinding;
import com.learn.jk.databinding.SplashMainBinding;
import com.learn.jk.presentation.commentScreen.ViewPostActivity;
import com.learn.jk.presentation.loginScreen.LoginActivity;
import com.learn.jk.presentation.mainScreen.BaseActivity;
import com.learn.jk.presentation.profileScreen.ProfileActivity;
import com.valdesekamdem.library.mdtoast.MDToast;

public class SplashActivity extends AppCompatActivity {
    private SplashViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SplashMainBinding binding = SplashMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel = ViewModelProviders.of(this).get(SplashViewModel.class);
        viewModel.getIsLogin().observe(SplashActivity.this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    if (getIntent().getData() != null) {
                        String link = getIntent().getData().toString();
                        if (link.contains("profile")) {
                            String profileID = link.split("https://wk.com/profile/")[1];
                            Intent intent = new Intent(SplashActivity.this, ProfileActivity.class);
                            intent.putExtra("uid", profileID);
                            startActivity(intent);
                            finish();
                        } else if (link.contains("post")) {
                            String postID = link.split("https://wk.com/post/")[1];
                            Intent intent = new Intent(SplashActivity.this, ViewPostActivity.class);
                            intent.putExtra("postID", postID);
                            startActivity(intent);
                            finish();
                        } else {
                            MDToast.makeText(SplashActivity.this, "link you opened not true", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                        }
                    } else {
                        startActivity(new Intent(SplashActivity.this, BaseActivity.class));
                        finish();
                    }
                } else {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                }
            }
        });
    }

}