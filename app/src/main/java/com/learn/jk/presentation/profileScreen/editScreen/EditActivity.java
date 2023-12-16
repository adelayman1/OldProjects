package com.learn.jk.presentation.profileScreen.editScreen;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.learn.jk.R;
import com.learn.jk.databinding.ActivityEditBinding;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EditActivity extends AppCompatActivity {
    private String dateSpinner = "";
    private byte[] image = null;
    private String gender = "";
    private int RESULT_LOAD_IMAGE = 1;
    private ActivityEditBinding binding;
    private List<String> mSpinnerList;
    private EditViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        viewModel = ViewModelProviders.of(this).get(EditViewModel.class);
        mSpinnerList = new ArrayList<>();
        mSpinnerList.add("Show my birthday");
        mSpinnerList.add("Show month and day only");
        mSpinnerList.add("don't show my birthday");
        setCurrentFragment(new AccountTypeFragment());
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Loading...");
        progressDialog.setCanceledOnTouchOutside(false);
        binding.ivAcceptChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String country = binding.etChangeCountry.getText().toString();
                String firstName = binding.etChangeFirstName.getText().toString();
                String lastName = binding.etChangeLastName.getText().toString();
                String whatYouLove = binding.etChangeWhatLove.getText().toString();
                String day = binding.etDay.getText().toString();
                String month = binding.etMonth.getText().toString();
                String year = binding.etYear.getText().toString();
                String hint = binding.etChangeHint.getText().toString();
                if (country.trim().isEmpty() && firstName.trim().isEmpty() && lastName.trim().isEmpty() && whatYouLove.trim().isEmpty() && gender.trim().isEmpty() && day.trim().isEmpty() && month.trim().isEmpty() && year.trim().isEmpty() && dateSpinner.trim().isEmpty() && image == null && hint.trim().isEmpty()) {
                    MDToast.makeText(EditActivity.this, "please change any data", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                    return;
                }
                if (!firstName.isEmpty() && lastName.isEmpty() || !lastName.isEmpty() && firstName.isEmpty())
                    MDToast.makeText(EditActivity.this, "please insert all name", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                else {
                    viewModel.sendDate(country, firstName, lastName, whatYouLove, gender, day, month, year, dateSpinner, image, hint);
                }
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
        viewModel.isSuccess.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                viewModel.isLoading.postValue(false);
                if (aBoolean) {
                    MDToast.makeText(EditActivity.this, "success", MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show();
                    finish();
                } else {
                    MDToast.makeText(EditActivity.this, "error", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                }
            }
        });

        binding.ivChangeUserAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(EditActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(EditActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
                } else {
                    Intent imageIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(imageIntent, RESULT_LOAD_IMAGE);
                }
            }
        });
        binding.radioChangeRadioMale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    gender = "male";
                }
            }
        });
        binding.radioChangeRadioFemale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    gender = "female";
                }
            }
        });
        //
        binding.spChangeBirthdayViewOptions.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mSpinnerList));
        binding.spChangeBirthdayViewOptions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                dateSpinner = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        viewModel.image.observe(this, new Observer<Bitmap>() {
            @Override
            public void onChanged(Bitmap bitmap) {
                binding.ivChangeUserAvatar.setImageBitmap(bitmap);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();

            try {
                Bitmap bitmapImage = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmapImage.compress(Bitmap.CompressFormat.WEBP, 50, stream);
                image = stream.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void setCurrentFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().
                replace(R.id.flEditAccountType, fragment).
                commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent imageIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(imageIntent, RESULT_LOAD_IMAGE);
            } else {
                MDToast.makeText(this, "please agree permission", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                ActivityCompat.requestPermissions(EditActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
            }
        }
    }
}