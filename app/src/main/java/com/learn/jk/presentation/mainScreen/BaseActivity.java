package com.learn.jk.presentation.mainScreen;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationBarView;
import com.learn.jk.R;
import com.learn.jk.databinding.ActivityBaseBinding;
import com.learn.jk.presentation.searchScreen.SearchFragment;
import com.learn.jk.presentation.homeScreen.HomeFragment;
import com.learn.jk.presentation.messageScreen.ChatsFragment;
import com.learn.jk.presentation.settingScreen.SettingFragment;
import com.learn.jk.presentation.splashScreen.SplashActivity;
import com.valdesekamdem.library.mdtoast.MDToast;

public class BaseActivity extends AppCompatActivity {
    HomeFragment homeFragment = new HomeFragment();
    SearchFragment searchFragment = new SearchFragment();
    ChatsFragment chatsFragment = new ChatsFragment();
    SettingFragment settingFragment = new SettingFragment();
    Fragment currentFragment=new HomeFragment();
    ActivityBaseBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         binding = ActivityBaseBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        checkPermission();
        setCurrentFragment(currentFragment);

        binding.bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if(item.getItemId()==R.id.homeItem) {
                    currentFragment = homeFragment;
                    setCurrentFragment(currentFragment);
                }
                else if(item.getItemId()==R.id.searchItem) {
                    currentFragment = searchFragment;
                    setCurrentFragment(currentFragment);
                }
                else if(item.getItemId()==R.id.chatItem) {
                    currentFragment = chatsFragment;
                    setCurrentFragment(currentFragment);
                }
                return true;
            }
        });

    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                MDToast.makeText(this, "please agree permission", MDToast.LENGTH_SHORT,MDToast.TYPE_ERROR).show();
                checkPermission();
            }
        }
    }
    private void setCurrentFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().
        replace(R.id.mainFragment, fragment).
        commit();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("fragment",binding.bottomNavigationView.getSelectedItemId());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int selectedId=savedInstanceState.getInt("fragment");
        binding.bottomNavigationView.setSelectedItemId(selectedId);
    }
}