package com.learn.jk.presentation.profileScreen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.learn.jk.data.model.UserModel;
import com.learn.jk.databinding.ActivityFollowersBinding;
import com.learn.jk.presentation.searchScreen.SearchAdapterItemClickListener;
import com.learn.jk.presentation.searchScreen.UsersAdapter;

import java.util.ArrayList;
import java.util.List;

public class FollowersActivity extends AppCompatActivity implements SearchAdapterItemClickListener {
    private UsersAdapter usersAdapter;
    private List<UserModel> mUsers;
    private ProfileViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityFollowersBinding binding = ActivityFollowersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.rcFollowers.setVisibility(View.GONE);
        binding.tvNoFollowers.setVisibility(View.GONE);
        binding.progressFollowers.setVisibility(View.VISIBLE);
        viewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);
        mUsers = new ArrayList<UserModel>();
        usersAdapter = new UsersAdapter(mUsers, this);
        binding.rcFollowers.setAdapter(usersAdapter);
        binding.rcFollowers.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        viewModel.getFollowersUserList().observe(this, new Observer<List<UserModel>>() {
            @Override
            public void onChanged(List<UserModel> userModels) {
                binding.progressFollowers.setVisibility(View.GONE);
                if (binding.swipeRefresh.isRefreshing()) {
                    binding.swipeRefresh.setRefreshing(false);
                }
                if (userModels.size() > 0) {
                    mUsers.clear();
                    mUsers.addAll(userModels);
                    binding.rcFollowers.setVisibility(View.VISIBLE);
                    binding.tvNoFollowers.setVisibility(View.GONE);
                    usersAdapter.notifyDataSetChanged();
                } else {
                    binding.rcFollowers.setVisibility(View.GONE);
                    binding.tvNoFollowers.setVisibility(View.VISIBLE);
                }
            }
        });
        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        binding.swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                viewModel.loadFollowersUserList();
            }
        });
    }

    @Override
    public void itemClickListener(String uid) {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("uid", uid);
        startActivity(intent);
        finish();
    }
}