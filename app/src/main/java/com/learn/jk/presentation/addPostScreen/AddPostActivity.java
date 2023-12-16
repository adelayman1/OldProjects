package com.learn.jk.presentation.addPostScreen;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.learn.jk.R;
import com.learn.jk.databinding.ActivityAddPostBinding;
import com.learn.jk.databinding.DialogBinding;
import com.valdesekamdem.library.mdtoast.MDToast;
import com.volokh.danylo.hashtaghelper.HashTagHelper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddPostActivity extends AppCompatActivity {
    private HashTagHelper mTextHashTagHelper;
    private AddPostViewModel viewModel;
    private int RESULT_LOAD_IMAGE = 1;
    private ActivityAddPostBinding binding;
    private Bitmap image = null;
    private String video = null;
    private int commentsEnable = 1;
    private String postVisible = "general";
    private int RESULT_CROP_IMAGE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddPostBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        viewModel = ViewModelProviders.of(this).get(AddPostViewModel.class);
        mTextHashTagHelper = HashTagHelper.Creator.create(getResources().getColor(R.color.blue), new HashTagHelper.OnHashTagClickListener() {
            @Override
            public void onHashTagClicked(String hashTag) {

            }
        }, '_');
        binding.lnPostSettings.setVisibility(View.GONE);
        binding.switchDisableComments.setChecked(false);
        binding.switchFriendsOnly.setChecked(false);
        mTextHashTagHelper.handle(binding.etPostText);


        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Uploading...");
        progressDialog.setCanceledOnTouchOutside(false);
        binding.btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if ((image != null || video != null) && !binding.etPostText.getText().toString().trim().isEmpty()) {

                    progressDialog.show();
                    if (image == null)
                        viewModel.AddPost(binding.etPostText.getText().toString(), video, postVisible, commentsEnable, mTextHashTagHelper.getAllHashTags());
                    else
                        viewModel.AddPost(binding.etPostText.getText().toString(), image, postVisible, commentsEnable, mTextHashTagHelper.getAllHashTags());
                } else {
                    MDToast.makeText(AddPostActivity.this, "please insert text and image or video", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                }
            }
        });
        binding.ivSettingPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.lnPostSettings.getVisibility() == View.GONE)
                    binding.lnPostSettings.setVisibility(View.VISIBLE);
                else
                    binding.lnPostSettings.setVisibility(View.GONE);
            }
        });
        binding.switchDisableComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commentsEnable = binding.switchDisableComments.isChecked() ? 0 : 1;
            }
        });
        binding.switchFriendsOnly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postVisible = binding.switchFriendsOnly.isChecked() ? "friends" : "general";
            }
        });
        binding.ivAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(AddPostActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(AddPostActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
                } else {
                    Intent imageIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(imageIntent, RESULT_LOAD_IMAGE);
                }
            }
        });
        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        binding.ivAddVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogBinding dialogBinding = DialogBinding.inflate(LayoutInflater.from(AddPostActivity.this));
                dialogBinding.etDialogSecond.setVisibility(View.GONE);
                dialogBinding.lnDialogText.setVisibility(View.GONE);
                dialogBinding.lnDialogEditTexts.setVisibility(View.VISIBLE);
                dialogBinding.etDialogFirst.setHint("Enter video id");
                AlertDialog dialog = new AlertDialog.Builder(AddPostActivity.this)
                        .setView(dialogBinding.getRoot())
                        .create();
                dialogBinding.btnPositiveDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!isYouTubeLink(dialogBinding.etDialogFirst.getText().toString()))
                            MDToast.makeText(AddPostActivity.this, "please insert true youtube url", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                        else
                            dialog.hide();
                    }
                });
                dialogBinding.btnNavigateDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.hide();
                    }
                });
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

            }
        });
        viewModel.isSuccess.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                progressDialog.dismiss();
                if (aBoolean) {
                    MDToast.makeText(AddPostActivity.this, "Post added successful", MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show();
                    finish();
                } else
                    MDToast.makeText(AddPostActivity.this, "Error when add post", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
//            try {
//                Intent imageIntent = new Intent("com.android.camera.action.CROP");
//                imageIntent.putExtra("crop","true");
//                imageIntent.setDataAndType(selectedImage,"image/*");
//                imageIntent.putExtra("aspectX",1);
//                imageIntent.putExtra("aspectY",1);
//                imageIntent.putExtra("outputX",265);
//                imageIntent.putExtra("outputY",265);
//                imageIntent.putExtra("return-data",true);
//                startActivityForResult(Intent.createChooser(imageIntent, "Select Picture"), RESULT_CROP_IMAGE);
//            } catch (ActivityNotFoundException e) {
            try {
                Bitmap bitmap = uriToBitmap(selectedImage);
                binding.ivViewImageAdded.setVisibility(View.VISIBLE);
                binding.ivViewImageAdded.setImageBitmap(bitmap);
                image = bitmap;
                video = null;
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
    }

    Bitmap uriToBitmap(Uri uri) throws IOException {
        return MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
    }

    private boolean isYouTubeLink(String youTubeUrl) {
        String pattern = "https?://(?:[0-9A-Z-]+\\.)?(?:youtu\\.be/|youtube\\.com\\S*[^\\w\\-\\s])([\\w\\-]{11})(?=[^\\w\\-]|$)(?![?=&+%\\w]*(?:['\"][^<>]*>|</a>))[?=&+%\\w]*";

        Pattern compiledPattern = Pattern.compile(pattern,
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = compiledPattern.matcher(youTubeUrl);
        if (matcher.find()) {
            video = matcher.group(1);
            binding.ivViewImageAdded.setVisibility(View.GONE);
            image = null;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            Intent imageIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(imageIntent, RESULT_LOAD_IMAGE);
        } else {
            MDToast.makeText(this, "please agree permission", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
            ActivityCompat.requestPermissions(AddPostActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        }
    }

}