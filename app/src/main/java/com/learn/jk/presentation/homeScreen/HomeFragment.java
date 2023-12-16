package com.learn.jk.presentation.homeScreen;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.learn.jk.data.model.PostModel;
import com.learn.jk.data.model.StoryModel;
import com.learn.jk.data.model.UserModel;
import com.learn.jk.data.model.UserStoriesModel;
import com.learn.jk.databinding.FragmentHomeBinding;
import com.learn.jk.presentation.addPostScreen.AddPostActivity;
import com.learn.jk.presentation.commentScreen.CommentActivity;
import com.learn.jk.presentation.profileScreen.ProfileActivity;
import com.learn.jk.presentation.searchScreen.SearchFragment;

import java.util.ArrayList;
import java.util.List;

import omari.hamza.storyview.StoryView;
import omari.hamza.storyview.callback.StoryClickListeners;

public class HomeFragment extends Fragment implements IPostAdapterItemClickListener, IStoryItemClickListener {
    private PostAdapter postAdapter;
    private StoryAdapter storyAdapter;
    private HomeViewModel viewModel;
    private List<PostModel> mPosts;
    private List<StoryModel> mStories;
    private String storyUserUID;
    private FragmentHomeBinding binding;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = ViewModelProviders.of(requireActivity()).get(HomeViewModel.class);
        binding.btnAddPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), AddPostActivity.class));

            }
        });
        binding.ivMyProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ProfileActivity.class);
                intent.putExtra("uid", viewModel.getUID());
                startActivity(intent);
            }
        });
        //TODO REFRESH
        binding.swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                viewModel.refresh();
            }
        });
        mPosts = new ArrayList<>();
        mStories = new ArrayList<>();
        postAdapter = new PostAdapter(mPosts, this, this.getLifecycle());
        storyAdapter = new StoryAdapter(mStories, this);
        binding.rcPosts.setHasFixedSize(true);
        binding.rcStory.setHasFixedSize(true);
        binding.rcPosts.setAdapter(postAdapter);
        binding.rcPosts.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        binding.rcStory.setAdapter(storyAdapter);
        binding.rcStory.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        viewModel.getPosts().observe(requireActivity(), new Observer<List<PostModel>>() {
            @Override
            public void onChanged(List<PostModel> postModels) {
                mPosts.clear();
                mPosts.addAll(postModels);
                postAdapter.notifyDataSetChanged();
                if (binding.swipeRefresh.isRefreshing()) {
                    binding.swipeRefresh.setRefreshing(false);
                }
            }
        });
        viewModel.getStories().observe(requireActivity(), new Observer<List<StoryModel>>() {
                    @Override
                    public void onChanged(List<StoryModel> storyModels) {
                        mStories.clear();
                        mStories.addAll(storyModels);
                        storyAdapter.notifyDataSetChanged();
                    }
                });
        viewModel.storiesOfUser.observe(getViewLifecycleOwner(), new Observer<UserStoriesModel>() {
            @Override
            public void onChanged(UserStoriesModel userStories) {
                new StoryView.Builder(getParentFragmentManager())
                        .setStoriesList(userStories.getStories())
                        .setTitleText(userStories.getUser().getName())
                        .setStoryDuration(3500)
                        .setTitleLogoUrl(userStories.getUser().getPhoto())
                        .setStoryClickListeners(new StoryClickListeners() {
                            @Override
                            public void onDescriptionClickListener(int position) {
                            }

                            @Override
                            public void onTitleIconClickListener(int position) {
                                if (storyUserUID != null) {
                                    iconAvatarOnClick(storyUserUID);
                                }
                            }
                        })
                        .build()
                        .show();

            }
        });
        viewModel.isLoading.observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    binding.progressbarHome.setVisibility(View.VISIBLE);
                    binding.svHomeScroll.setVisibility(View.GONE);
                } else {
                    binding.progressbarHome.setVisibility(View.GONE);
                    binding.svHomeScroll.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onRecycleViewItemClick(View view, PostModel model, Boolean isLike) {
        viewModel.changeLikeStatues(model.getKey(), isLike);
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
        Intent intent = new Intent(getContext(), ProfileActivity.class);
        intent.putExtra("uid", uid);
        startActivity(intent);
    }

    @Override
    public void tagClickListener(String tagName) {
        SearchFragment searchFragment = new SearchFragment();
        Bundle bundle = new Bundle();
        bundle.putString("tagName", "#" + tagName);
        searchFragment.setArguments(bundle);
        getParentFragmentManager().beginTransaction().add(android.R.id.content, searchFragment).addToBackStack(null).commit();

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
    public void storyOnClick(String uid) {
        storyUserUID = uid;
        viewModel.getUserStories(uid);
    }
}