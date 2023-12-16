package com.learn.jk.presentation.homeScreen;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.learn.jk.data.model.PostModel;
import com.learn.jk.data.model.StoryModel;
import com.learn.jk.data.model.UserStoriesModel;
import com.learn.jk.data.repository.PostRepository;
import com.learn.jk.data.repository.StoryRepository;
import com.learn.jk.data.repository.UserRepository;
import com.learn.jk.domain.usecase.ChangePostLikeStatuesUseCase;
import com.learn.jk.domain.usecase.GetPostsUseCase;
import com.learn.jk.domain.usecase.GetStoriesUseCase;
import com.learn.jk.domain.usecase.GetUserStoryUseCase;

import java.util.List;

public class HomeViewModel extends ViewModel {
    private PostRepository postRepository = PostRepository.getInstance();
    private StoryRepository storyRepository = StoryRepository.getInstance();
    private UserRepository userRepository = UserRepository.getInstance();
    private GetPostsUseCase getPostsUseCase = new GetPostsUseCase();
    private GetStoriesUseCase getStoriesUseCase = new GetStoriesUseCase();
    private GetUserStoryUseCase getUserStoryUseCase = new GetUserStoryUseCase();
    private ChangePostLikeStatuesUseCase changePostLikeStatuesUseCase = new ChangePostLikeStatuesUseCase();
    private String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    public MutableLiveData<List<PostModel>> posts;
    public MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    public MutableLiveData<List<StoryModel>> mStories;
    public MutableLiveData<List<String>> mFollowing;
    public MutableLiveData<UserStoriesModel> storiesOfUser = new MutableLiveData<>();

    public void changeLikeStatues(String postKey, Boolean isLike) {
        changePostLikeStatuesUseCase.invoke(postKey,isLike);
    }

    public String getUID() {
        return userRepository.getUid();
    }

//
//    public LiveData<List<String>> getFollowing() {
//        if (mFollowing == null) {
//            isLoading.postValue(true);
//            mFollowing = new MutableLiveData<>();
//            loadFollowingList();
//        }
//        return mFollowing;
//    }
//
//    public void loadFollowingList() {
//        userRepository.getFollowing(uid).subscribeWith(new DisposableSingleObserver<List<String>>() {
//            @Override
//            public void onSuccess(@NonNull List<String> strings) {
//                mFollowing.postValue(strings);
//            }
//
//            @Override
//            public void onError(@NonNull Throwable e) {
//
//            }
//        });
//    }

    public LiveData<List<PostModel>> getPosts() {
        if (posts == null) {
            isLoading.postValue(true);
            posts = new MutableLiveData<>();
            loadPosts();
        }
        return posts;
    }

    public LiveData<List<StoryModel>> getStories() {
        if (mStories == null) {
            mStories = new MutableLiveData<>();
            loadStories();
        }
        return mStories;
    }

    public void loadStories() {
        getStoriesUseCase.invoke().subscribe(stories -> {
            mStories.postValue(stories);
        }, error -> {

        });
    }

    public void loadPosts() {
        getPostsUseCase.invoke().subscribe(userPosts -> {
            posts.postValue(userPosts);
            isLoading.postValue(true);
        }, error -> {

        });
    }

    public void getUserStories(String uid) {
        getUserStoryUseCase.invoke(uid).subscribe(userStoriesModel -> {
            storiesOfUser.postValue(userStoriesModel);
        }, error -> {

        });
    }

    public void refresh() {
//        userRepository.getFollowing(uid).subscribeWith(new DisposableSingleObserver<List<String>>() {
//            @Override
//            public void onSuccess(@NonNull List<String> strings) {
//                loadPosts();
//                loadStories();
//            }
//
//            @Override
//            public void onError(@NonNull Throwable e) {
//
//            }
//        });
    }
}
