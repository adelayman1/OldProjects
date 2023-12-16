package com.learn.jk.presentation.profileScreen;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.loader.content.CursorLoader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.learn.jk.R;
import com.learn.jk.Utils.DateAndTimeUtils;
import com.learn.jk.data.model.PostModel;
import com.learn.jk.data.model.UserModel;
import com.learn.jk.databinding.ActivityProfileBinding;
import com.learn.jk.databinding.DialogBinding;
import com.learn.jk.presentation.addPostScreen.AddPostActivity;
import com.learn.jk.presentation.commentScreen.CommentActivity;
import com.learn.jk.presentation.homeScreen.IPostAdapterItemClickListener;
import com.learn.jk.presentation.homeScreen.PostAdapter;
import com.learn.jk.presentation.messageScreen.ChatActivity;
import com.learn.jk.presentation.profileScreen.editScreen.EditActivity;
import com.learn.jk.presentation.searchScreen.SearchFragment;
import com.passiondroid.imageeditorlib.ImageEditor;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity implements IPostAdapterItemClickListener {
    private PhotosAdapter photosAdapter;
    private PostAdapter postAdapter;
    private List<String> mPhotos;
    private List<PostModel> mPosts;
    private ProfileViewModel viewModel;
    private boolean isFollow;
    private String profileId;
    private int RESULT_LOAD_IMAGE = 1;
    private ProgressDialog progressDialog;
    private String type;
    private String websiteURL = "";
    private String name = "";
    private String avatar = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityProfileBinding binding = ActivityProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        viewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);
        mPhotos = new ArrayList<>();
        mPosts = new ArrayList<>();
        postAdapter = new PostAdapter(mPosts, this, this.getLifecycle());
        binding.rcPostsProfile.setAdapter(postAdapter);
        binding.rcPostsProfile.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        photosAdapter = new PhotosAdapter(mPhotos, this);
        binding.rcPhotosPublished.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        binding.rcPhotosPublished.setAdapter(photosAdapter);
        binding.rcPhotosPublished.setHasFixedSize(true);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Loading....");
        progressDialog.setCanceledOnTouchOutside(false);
        binding.ivVerified.setVisibility(View.GONE);
        profileId = getIntent().getStringExtra("uid");
        if (viewModel.isMyProfile(profileId)) {
            binding.btnEditProfile.setVisibility(View.VISIBLE);
            binding.lnWebsite.setVisibility(View.GONE);
            binding.lnTypesOFAccount.setVisibility(View.GONE);
            binding.tvAddInfo.setVisibility(View.VISIBLE);
        } else {
            binding.btnEditProfile.setVisibility(View.GONE);
            binding.tvAddInfo.setVisibility(View.GONE);
        }
        viewModel.getIsFollowing(profileId).observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                isFollow = aBoolean;
                if (!viewModel.isMyProfile(profileId)) {
                    if (aBoolean) {
                        binding.btnFollow.setVisibility(View.GONE);
                        binding.lnFollow.setVisibility(View.VISIBLE);
                        binding.btnUnFollow.setVisibility(View.VISIBLE);
                        binding.lnWebsite.setVisibility(View.GONE);
                    } else {
                        binding.btnUnFollow.setVisibility(View.GONE);
                        binding.btnFollow.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        viewModel.getFollowersNum(profileId).observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                binding.tvFollowersProfile.setText(s + " followers");
            }
        });
        viewModel.getUserData(profileId).observe(this, new Observer<UserModel>() {
            @Override
            public void onChanged(UserModel userModel) {
                //profile not exist
                if (userModel == null) {
                    viewModel.isProfileLoading.postValue(false);
                    binding.svProfile.setVisibility(View.GONE);
                    binding.tvNoProfile.setVisibility(View.VISIBLE);
                    return;
                }
                binding.tvNoProfile.setVisibility(View.GONE);
                name = userModel.getName();
                binding.ivVerified.setVisibility(userModel.isVerified()?View.VISIBLE:View.GONE);
                avatar = userModel.getPhoto();
                binding.tvUserNameProfile.setText(userModel.getName());
                binding.tvHintProfile.setText(userModel.getHint());
                Glide.with(getApplicationContext()).load(userModel.getPhoto()).into(binding.ivUserAvatar);
                binding.tvHintProfile.setText(userModel.getHint());
                if (!viewModel.isMyProfile(profileId)) {
                    if (userModel.getAccountType().equals("normal")) {
                        binding.lnFollow.setVisibility(View.VISIBLE);
                        binding.lnWebsite.setVisibility(View.GONE);
                    } else if (userModel.getAccountType().equals("web")) {
                        binding.lnFollow.setVisibility(View.GONE);
                        binding.lnWebsite.setVisibility(View.VISIBLE);
                        websiteURL = userModel.getWebsiteURL();
                    } else if (userModel.getAccountType().equals("private")) {
                        if (!viewModel.isMyProfile(profileId)) {
                            binding.svProfile.setVisibility(View.GONE);
                            binding.tvNoData.setVisibility(View.GONE);
                            binding.tvPrivateAccount.setVisibility(View.VISIBLE);
                        }
                    }
                }
                binding.tvWhatLoveProfile.setText("what love : " + userModel.getWhatLove());
                String date = DateAndTimeUtils.getDateType(userModel.getDateVisibility(), userModel.getDay(), userModel.getMonth(), userModel.getYear());
                binding.tvBirthdayProfile.setText(date == null ? "birthday is hidden" : date);
                binding.tvWhatLoveProfile.setText("what love : " + userModel.getWhatLove());
                viewModel.isProfileLoading.postValue(false);
            }
        });
        viewModel.getIsFollowing(profileId).observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                viewModel.getPosts(profileId, aBoolean).observe(ProfileActivity.this, new Observer<List<PostModel>>() {
                    @Override
                    public void onChanged(List<PostModel> postModels) {
                        if (postModels.size() == 0) {
                            binding.tvNoData.setVisibility(View.VISIBLE);
                            return;
                        }
                        binding.tvNoData.setVisibility(View.GONE);
                        mPosts.clear();
                        mPosts.addAll(postModels);
                        postAdapter.notifyDataSetChanged();
                    }
                });
                viewModel.getPhotos(profileId, aBoolean).observe(ProfileActivity.this, new Observer<List<String>>() {
                    @Override
                    public void onChanged(List<String> strings) {

                        binding.tvNumOfPhotosProfile.setText(String.valueOf(strings.size()));
                        if (strings.size() == 0)
                            return;
                        mPhotos.clear();
                        mPhotos.addAll(strings);
                        photosAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
        viewModel.isProfileLoading.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    binding.progressbarProfile.setVisibility(View.VISIBLE);
                    binding.tvNoProfile.setVisibility(View.GONE);
                    binding.svProfile.setVisibility(View.GONE);
                    binding.tvNoData.setVisibility(View.GONE);
                } else {
                    binding.progressbarProfile.setVisibility(View.GONE);
                    binding.svProfile.setVisibility(View.VISIBLE);
                }
            }
        });

        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        binding.tvFollowersProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewModel.isMyProfile(profileId))
                    startActivity(new Intent(ProfileActivity.this, FollowersActivity.class));
            }
        });
        binding.btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.changeFollow(profileId, isFollow);
            }
        });
        binding.btnAddPostProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, AddPostActivity.class));
            }
        });
        binding.btnOpenWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(websiteURL));
                startActivity(webIntent);
            }
        });
        binding.tvAddInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogBinding dialogBinding = DialogBinding.inflate(LayoutInflater.from(ProfileActivity.this));
                dialogBinding.lnDialogText.setVisibility(View.GONE);
                dialogBinding.lnDialogEditTexts.setVisibility(View.VISIBLE);
                dialogBinding.etDialogFirst.setHint("Enter information title");
                dialogBinding.etDialogSecond.setHint("Enter information value");
                AlertDialog dialog = new AlertDialog.Builder(ProfileActivity.this)
                        .setView(dialogBinding.getRoot())
                        .create();
                dialogBinding.btnPositiveDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (dialogBinding.etDialogFirst.getText().toString().trim().isEmpty() || dialogBinding.etDialogSecond.getText().toString().trim().isEmpty()) {
                            MDToast.makeText(ProfileActivity.this, "please insert all fields", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                        } else {
                            viewModel.addInfo(dialogBinding.etDialogFirst.getText().toString(), dialogBinding.etDialogSecond.getText().toString());
                            dialog.hide();
                        }
                    }
                });
                dialogBinding.btnNavigateDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.hide();
                    }
                });
                dialog.getWindow().

                        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        binding.lnShareProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, "https://wk.com/profile/" + profileId);
                shareIntent.setType("text/plain");
                startActivity(shareIntent);
            }
        });
        binding.btnUnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.changeFollow(profileId, isFollow);
            }
        });
        binding.btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, EditActivity.class));
            }
        });
        binding.tvStatuesProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(ProfileActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
                } else {
                    Intent i = new Intent();
                    i.setType("image/*");
                    i.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(i, "Select Picture"), RESULT_LOAD_IMAGE);

                }

            }
        });
        binding.btnMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, ChatActivity.class);
                intent.putExtra("uid", profileId);
                intent.putExtra("image", avatar);
                intent.putExtra("name", name);
                startActivity(intent);
            }
        });
        binding.lnMoreInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProfileInformationFragment fragment = new ProfileInformationFragment();
                Bundle bundle = new Bundle();
                bundle.putString("uid", profileId);
                fragment.setArguments(bundle);
                binding.flInformation.setVisibility(View.VISIBLE);
                getSupportFragmentManager().beginTransaction().
                        replace(R.id.flInformation, fragment)
                        .commit();
                binding.lnMoreInformation.setVisibility(View.GONE);
            }
        });


        binding.swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                viewModel.loadIsFollowing(profileId);
            }
        });
    }

    @Override
    public void onRecycleViewItemClick(View view, PostModel model, Boolean isLike) {
        viewModel.changeLikeStatues(model, isLike);
    }

    @Override
    public void onCommentClickListener(PostModel postModel, UserModel user) {
        Intent intent = new Intent(ProfileActivity.this, CommentActivity.class);
        intent.putExtra("postModel", postModel);
        intent.putExtra("name", user.getName());
        intent.putExtra("verified", user.isVerified());
        intent.putExtra("avatar", user.getPhoto());
        startActivity(intent);
    }

    @Override
    public void iconAvatarOnClick(String uid) {

    }

    @Override
    public void tagClickListener(String tagName) {
        SearchFragment searchFragment = new SearchFragment();
        Bundle bundle = new Bundle();
        bundle.putString("tagName", "#" + tagName);
        searchFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().add(android.R.id.content, searchFragment).addToBackStack(null).commit();
    }

    @Override
    public void share(String key) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, "https://wk.com/post/" + key);
        shareIntent.setType("text/plain");
        startActivity(shareIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK) {
            if (data != null) {
                Uri selectedImage = data.getData();
                String path = "";
                if (Build.VERSION.SDK_INT >= 17 && Build.VERSION.SDK_INT <= 18) {
                    path = getRealPathFromURI_API11to18(selectedImage);
                } else {
                    path = getRealPathFromURI_API19(selectedImage);
                }
                new ImageEditor.Builder(this, path).disable(ImageEditor.EDITOR_TEXT).open();

            }
        } else if (requestCode == ImageEditor.RC_IMAGE_EDITOR) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                String editedImagePath = data.getStringExtra(ImageEditor.EXTRA_EDITED_PATH);
                Uri imageURI = Uri.fromFile(new File(editedImagePath));
                try {
                    Bitmap bitmapImage = MediaStore.Images.Media.getBitmap(getContentResolver(), imageURI);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmapImage.compress(Bitmap.CompressFormat.WEBP, 50, stream);
                    byte[] imageByte = stream.toByteArray();
                    viewModel.addStory(imageByte);
                    progressDialog.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        }
    }

    @SuppressLint("NewApi")
    private String getRealPathFromURI_API19(Uri uri) {
        String filePath = "";
        String wholeID = DocumentsContract.getDocumentId(uri);

        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];

        String[] column = {MediaStore.Images.Media.DATA};

        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = getApplicationContext().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, new String[]{id}, null);

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;
    }


    private String getRealPathFromURI_API11to18(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        String result = null;

        CursorLoader cursorLoader = new CursorLoader(
                this,
                contentUri, proj, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();

        if (cursor != null) {
            int column_index =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            result = cursor.getString(column_index);
        }
        return result;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                MDToast.makeText(this, "please agree permission", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
            }
        }
    }

}
