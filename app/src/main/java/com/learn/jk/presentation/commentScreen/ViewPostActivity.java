package com.learn.jk.presentation.commentScreen;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.learn.jk.R;
import com.learn.jk.data.model.PostModel;
import com.learn.jk.data.model.UserModel;
import com.learn.jk.databinding.ActivityViewPostBinding;
import com.learn.jk.presentation.profileScreen.ProfileActivity;
import com.learn.jk.presentation.searchScreen.SearchFragment;
import com.squareup.picasso.Picasso;
import com.volokh.danylo.hashtaghelper.HashTagHelper;

import static com.learn.jk.data.GLOBAL.getTimeAgo;

public class ViewPostActivity extends AppCompatActivity {
    CommentViewModel viewModel;
    HashTagHelper mTextHashTagHelper;
    PostModel post = null;
    UserModel user = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityViewPostBinding binding = ActivityViewPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel = ViewModelProviders.of(this).get(CommentViewModel.class);
        if (getIntent().getStringExtra("postID") != null) {
            String postID = getIntent().getStringExtra("postID");
            viewModel.getPost(postID);
            viewModel.likeListener(postID);
        }
        binding.ivSharePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, "https://wk.com/post/" + getIntent().getStringExtra("postID"));
                shareIntent.setType("text/plain");
                startActivity(shareIntent);
            }
        });

        viewModel.likeNum.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                binding.tvLikePost.setText(s);
            }
        });
        viewModel.isLike.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    binding.lnLikePost.setTag("Yes");
                    binding.ivLikePost.setImageResource(R.drawable.ic_baseline_favorite_24);
                } else {
                    binding.lnLikePost.setTag("No");
                    binding.ivLikePost.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                }
            }
        });
        viewModel.post.observe(this, new Observer<PostModel>() {
            @Override
            public void onChanged(PostModel postModel) {
                if (postModel == null)
                    return;
                post = postModel;
                viewModel.getUserData(postModel.getUid());
            }
        });
        viewModel.user.observe(this, new Observer<UserModel>() {
            @Override
            public void onChanged(UserModel userModel) {
                if (userModel == null)
                    return;
                user = userModel;
                mTextHashTagHelper = HashTagHelper.Creator.create(Color.parseColor("#72a9e7"), new HashTagHelper.OnHashTagClickListener() {
                    @Override
                    public void onHashTagClicked(String hashTag) {
                        SearchFragment searchFragment = new SearchFragment();
                        Bundle bundle = new Bundle();

                        bundle.putString("tagName", "#" + hashTag);
                        searchFragment.setArguments(bundle);
                        getSupportFragmentManager().beginTransaction().add(android.R.id.content, searchFragment).addToBackStack(null).commit();

                    }
                }, '_');
                if (userModel.isVerified())
                    binding.ivAccountVerified.setVisibility(View.VISIBLE);
                else
                    binding.ivAccountVerified.setVisibility(View.GONE);
                binding.progressViewPost.setVisibility(View.GONE);
                binding.svPostScroll.setVisibility(View.VISIBLE);
                mTextHashTagHelper.handle(binding.tvHintPost);
                binding.tvUserNamePost.setText(userModel.getName());
                binding.tvTimePost.setText(getTimeAgo(post.getTime())); //TODO:GET DIFFERENT BETWEEN TWO TIMES
                binding.tvHintPost.setText(post.getHint());

                if (post.getType().equals("video")) {
                    binding.videoPost.setVisibility(View.VISIBLE);
                    binding.ivPost.setVisibility(View.GONE);
                } else {
                    binding.videoPost.setVisibility(View.GONE);
                    binding.ivPost.setVisibility(View.VISIBLE);
                    Picasso.get().load(post.getImage()).error(R.drawable.ic_round_error_outline_24).into(binding.ivPost);
                }
                if (post.getCommentEnable() == 1) {
                    binding.lnCommentPost.setVisibility(View.VISIBLE);
                } else {
                    binding.lnCommentPost.setVisibility(View.GONE);
                }
                if (!userModel.getPhoto().equals("noPhoto"))
                    Picasso.get().load(userModel.getPhoto()).into(binding.ivUserImagePost);
                else
                    binding.ivUserImagePost.setImageResource(R.drawable.ic_baseline_fiber_manual_record_24);

                viewModel.likeListener(post.getKey());
            }

        });


        binding.lnLikePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (post != null)
                    viewModel.changeLikeStatues(post.getKey(), binding.tvLikePost.getTag() == "Yes");
            }
        });
        binding.lnCommentPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (post != null && user != null) {
                    Intent intent = new Intent(ViewPostActivity.this, CommentActivity.class);
                    intent.putExtra("postModel", post);
                    intent.putExtra("name", user.getName());
                    intent.putExtra("verified", user.isVerified());
                    intent.putExtra("avatar", user.getPhoto());
                    startActivity(intent);
                }
            }
        });
        binding.ivUserImagePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (post != null) {
                    Intent intent = new Intent(ViewPostActivity.this, ProfileActivity.class);
                    intent.putExtra("uid", post.getUid());
                    startActivity(intent);
                }
            }
        });
    }
}