package com.learn.jk.presentation.commentScreen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.learn.jk.R;
import com.learn.jk.data.GLOBAL;
import com.learn.jk.data.model.CommentModel;
import com.learn.jk.data.model.PostModel;
import com.learn.jk.databinding.ActivityCommentBinding;
import com.learn.jk.presentation.profileScreen.ProfileActivity;
import com.learn.jk.presentation.searchScreen.SearchFragment;
import com.squareup.picasso.Picasso;
import com.volokh.danylo.hashtaghelper.HashTagHelper;

import java.util.ArrayList;
import java.util.List;

public class CommentActivity extends AppCompatActivity {
    private CommentViewModel viewModel;
    private ActivityCommentBinding binding;
    private List<CommentModel> mComments;
    private CommentsAdapter adapter;
    private PostModel postModel;
    private HashTagHelper mTextHashTagHelper;
    private String userName;
    private String userAvatar;
    private boolean isVerified;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCommentBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        viewModel = ViewModelProviders.of(this).get(CommentViewModel.class);
        postModel = (PostModel) getIntent().getSerializableExtra("postModel");
        userName = getIntent().getStringExtra("name");
        userAvatar = getIntent().getStringExtra("avatar");
        isVerified = getIntent().getBooleanExtra("verified", false);
        mTextHashTagHelper = HashTagHelper.Creator.create(getResources().getColor(R.color.blue), new HashTagHelper.OnHashTagClickListener() {
            @Override
            public void onHashTagClicked(String hashTag) {
                SearchFragment searchFragment = new SearchFragment();
                Bundle bundle = new Bundle();
                bundle.putString("tagName", "#" + hashTag);
                searchFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().add(android.R.id.content, searchFragment).addToBackStack(null).commit();
            }
        }, '_');
        binding.tvUserNamePost.setText(userName);
        binding.ivAccountVerifiedComment.setVisibility(isVerified ? View.VISIBLE : View.GONE);
        if (!userAvatar.equals("noPhoto"))
            Picasso.get().load(userAvatar).error(R.drawable.ic_round_error_outline_24).into(binding.ivUserImagePost);
        else
            binding.ivUserImagePost.setImageResource(R.drawable.ic_baseline_fiber_manual_record_24);
        binding.tvTimePost.setText(GLOBAL.getTimeAgo(Long.parseLong(String.valueOf(postModel.getTime()))));
        binding.tvHintPost.setText(postModel.getHint());

        mComments = new ArrayList<>();
        adapter = new CommentsAdapter(mComments);
        binding.rcComments.setAdapter(adapter);
        binding.rcComments.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        viewModel.getComments(postModel.getKey()).observe(this, new Observer<List<CommentModel>>() {
            @Override
            public void onChanged(List<CommentModel> commentModels) {
                if (binding.swipeRefresh.isRefreshing()) {
                    binding.swipeRefresh.setRefreshing(false);
                }
                mComments.clear();
                mComments.addAll(commentModels);
                adapter.notifyDataSetChanged();
                binding.progressBarComment.setVisibility(View.GONE);
                binding.lnPost.setVisibility(View.VISIBLE);
            }
        });;
        viewModel.likeListener(postModel.getKey());

        if (postModel.getType().equals("video")) {
            binding.videoPost.setVisibility(View.VISIBLE);
            binding.ivPost.setVisibility(View.GONE);
            //TODO:VIDEO

        } else {
            binding.videoPost.setVisibility(View.GONE);
            binding.ivPost.setVisibility(View.VISIBLE);
            Picasso.get().load(postModel.getImage()).into(binding.ivPost);
        }
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
        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        binding.lnLikePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.changeLikeStatues(postModel.getKey(), binding.lnLikePost.getTag() == "Yes");
            }
        });
        binding.ivUserImagePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CommentActivity.this, ProfileActivity.class).putExtra("uid", postModel.getUid()));
            }
        });
        binding.ivSendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!binding.etComment.getText().toString().trim().isEmpty())
                    viewModel.addComment(postModel.getKey(), binding.etComment.getText().toString());
                binding.etComment.setText("");
            }
        });
        binding.swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                viewModel.loadComments(postModel.getKey());
                viewModel.likeListener(postModel.getKey());
            }
        });
    }
}