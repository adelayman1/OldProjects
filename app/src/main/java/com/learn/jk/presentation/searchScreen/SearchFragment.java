package com.learn.jk.presentation.searchScreen;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.learn.jk.data.model.PostModel;
import com.learn.jk.data.model.UserModel;
import com.learn.jk.presentation.commentScreen.CommentActivity;
import com.learn.jk.presentation.homeScreen.IPostAdapterItemClickListener;
import com.learn.jk.presentation.homeScreen.PostAdapter;
import com.learn.jk.presentation.profileScreen.ProfileActivity;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment implements SearchAdapterItemClickListener, IPostAdapterItemClickListener {
    private UsersAdapter accountsAdapter;
    private PostAdapter postAdapter;
    private List<UserModel> mUsers;
    private List<PostModel> mPosts;
    private SearchViewModel viewModel;
    private com.learn.jk.databinding.FragmentSearchBinding binding;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = ViewModelProviders.of(requireActivity()).get(SearchViewModel.class);
        //to check if it view in bottom navigation or no
        if (getArguments() != null) {
            //view back btn
            binding.lnBack.setVisibility(View.VISIBLE);
            viewModel.searchTags(getArguments().getString("tagName"));
            binding.lnSearchBodyItems.setVisibility(View.GONE);
            binding.tvSearchToViewResult.setVisibility(View.GONE);
            binding.lnLoading.setVisibility(View.VISIBLE);
            binding.progressSearch.setVisibility(View.VISIBLE);
            binding.chipTag.setChecked(true);
            binding.chipAccount.setEnabled(false);
        } else {
            // gide back btn
            binding.lnBack.setVisibility(View.GONE);
            binding.lnLoading.setVisibility(View.VISIBLE);
            binding.tvSearchToViewResult.setVisibility(View.VISIBLE);
            binding.progressSearch.setVisibility(View.GONE);
            binding.lnTagNum.setVisibility(View.GONE);
            binding.lnSearchBodyItems.setVisibility(View.GONE);
            binding.chipAccount.setChecked(true);
            binding.chipAccount.setEnabled(true);
        }
        mUsers = new ArrayList<UserModel>();
        mPosts = new ArrayList<PostModel>();
        accountsAdapter = new UsersAdapter(mUsers, this);
        postAdapter = new PostAdapter(mPosts, this, this.getLifecycle());

        binding.rcSearch.setAdapter(accountsAdapter);
        binding.rcSearch.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        binding.etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    if (!binding.etSearch.getText().toString().isEmpty()) {
                        InputMethodManager manager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        manager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                        viewModel.isLoading.postValue(true);
                        if (binding.chipAccount.isChecked())
                            viewModel.searchUsers(binding.etSearch.getText().toString());
                        else
                            viewModel.searchTags(binding.etSearch.getText().toString());

                    } else {
                        binding.etSearch.setError("please insert text");
                    }
                    return true;
                }
                return false;
            }
        });

        viewModel.usersSearchResult.observe(getViewLifecycleOwner(), new Observer<List<UserModel>>() {
            @Override
            public void onChanged(List<UserModel> userModels) {
                viewModel.isLoading.postValue(false);
                if (userModels != null && userModels.size() > 0) {
                    binding.lnLoading.setVisibility(View.GONE);
                    binding.lnSearchBodyItems.setVisibility(View.VISIBLE);
                    binding.rcSearch.setVisibility(View.VISIBLE);
                    mUsers.clear();
                    mUsers.addAll(userModels);
                    accountsAdapter.notifyDataSetChanged();
                    binding.rcSearch.setAdapter(accountsAdapter);
                    binding.lnTagNum.setVisibility(View.GONE);
                } else {
                    binding.lnLoading.setVisibility(View.VISIBLE);
                    binding.tvSearchToViewResult.setText("No result");
                    binding.tvSearchToViewResult.setVisibility(View.VISIBLE);
                    binding.lnSearchBodyItems.setVisibility(View.GONE);
                    binding.rcSearch.setVisibility(View.GONE);
                }
            }
        });
        viewModel.tagsSearchResult.observe(getViewLifecycleOwner(), new Observer<List<PostModel>>() {
            @Override
            public void onChanged(List<PostModel> postModels) {
                viewModel.isLoading.postValue(false);
                if (postModels != null && postModels.size() > 0) {
                    binding.tvTagPostsNum.setText(postModels.size() + " posts");
                    binding.lnLoading.setVisibility(View.GONE);
                    binding.lnSearchBodyItems.setVisibility(View.VISIBLE);
                    binding.rcSearch.setVisibility(View.VISIBLE);
                    mPosts.clear();
                    mPosts.addAll(postModels);
                    postAdapter.notifyDataSetChanged();
                    binding.rcSearch.setAdapter(postAdapter);
                    binding.lnTagNum.setVisibility(View.VISIBLE);
                } else {
                    binding.lnLoading.setVisibility(View.VISIBLE);
                    binding.lnTagNum.setVisibility(View.GONE);
                    binding.tvSearchToViewResult.setText("No result");
                    binding.tvSearchToViewResult.setVisibility(View.VISIBLE);
                    binding.lnSearchBodyItems.setVisibility(View.GONE);
                    binding.rcSearch.setVisibility(View.GONE);
                }
            }
        });
        viewModel.isLoading.observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    binding.lnSearchBodyItems.setVisibility(View.GONE);
                    binding.chipGroup.setVisibility(View.GONE);
                    binding.tvSearchToViewResult.setVisibility(View.GONE);
                    binding.lnLoading.setVisibility(View.VISIBLE);
                    binding.progressSearch.setVisibility(View.VISIBLE);
                } else {
                    binding.progressSearch.setVisibility(View.GONE);
                    binding.chipGroup.setVisibility(View.VISIBLE);
                }
            }
        });
        binding.chipAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!binding.etSearch.getText().toString().trim().isEmpty()) {
                    viewModel.searchUsers(binding.etSearch.getText().toString());
                    viewModel.isLoading.postValue(true);
                } else {
                    binding.etSearch.setError("please insert text");
                }
            }
        });
        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().popBackStack();
            }
        });
        binding.chipTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!binding.etSearch.getText().toString().trim().isEmpty()) {
                    viewModel.searchTags(binding.etSearch.getText().toString());
                    viewModel.isLoading.postValue(true);
                } else {
                    binding.etSearch.setError("please insert text");
                }
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = com.learn.jk.databinding.FragmentSearchBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void itemClickListener(String uid) {
        Intent intent = new Intent(getContext(), ProfileActivity.class);
        intent.putExtra("uid", uid);
        startActivity(intent);
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
    public void onRecycleViewItemClick(View view, PostModel model, Boolean isLike) {

    }

    @Override
    public void onCommentClickListener(PostModel postModel, UserModel user) {
        Intent intent = new Intent(getContext(), CommentActivity.class);
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

    }
}