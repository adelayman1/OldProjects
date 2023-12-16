package com.learn.jk.presentation.commentScreen;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.learn.jk.data.repository.PostRepository;
import com.learn.jk.data.repository.UserRepository;
import com.learn.jk.data.model.CommentModel;
import com.learn.jk.data.model.PostModel;
import com.learn.jk.data.model.UserModel;

import java.util.List;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.observers.DisposableObserver;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;

public class CommentViewModel extends ViewModel {
    private PostRepository postRepository = PostRepository.getInstance();
    private UserRepository userRepository = UserRepository.getInstance();
    private String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    public MutableLiveData<Boolean> isLike = new MutableLiveData<>();
    public MutableLiveData<String> likeNum = new MutableLiveData<>();
    public MutableLiveData<List<CommentModel>> comments;
    public MutableLiveData<PostModel> post = new MutableLiveData<>();
    public MutableLiveData<UserModel> user = new MutableLiveData<>();

    public MutableLiveData<List<CommentModel>> getComments(String postKey) {
        if(comments==null)
        {
            comments=new MutableLiveData<>();
            loadComments(postKey);
        }
        return comments;
    }
    public void loadComments(String postKey) {
        postRepository.getComments(postKey).subscribeWith(new DisposableSingleObserver<List<CommentModel>>() {
            @Override
            public void onSuccess(@NonNull List<CommentModel> commentModels) {
                comments.postValue(commentModels);
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }
        });
    }

    public void changeLikeStatues(String key, Boolean isLike) {
        if (isLike) {
            postRepository.unlike(key, uid);
        } else {
            postRepository.like(key, uid);
        }
    }

    public void likeListener(String postKey) {
        postRepository.getLikesNumber(postKey).subscribeWith(new DisposableObserver<String>() {
            @Override
            public void onNext(@NonNull String s) {
                likeNum.postValue(s);
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
        postRepository.isLike(postKey, uid).subscribeWith(new DisposableSingleObserver<Boolean>() {
            @Override
            public void onSuccess(@NonNull Boolean aBoolean) {
                isLike.postValue(aBoolean);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                isLike.postValue(false);
            }
        });
    }

    public void addComment(String postKey, String text) {
        postRepository.sendComments(postKey, text, uid);
    }

    public void getPost(String postID) {
        postRepository.getPost(postID).subscribeWith(new DisposableSingleObserver<PostModel>() {
            @Override
            public void onSuccess(@NonNull PostModel postModel) {
                post.postValue(postModel);
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }
        });
    }
    public void getUserData(String userUid) {
        userRepository.getUser(userUid).subscribeWith(new DisposableSingleObserver<UserModel>() {
            @Override
            public void onSuccess(@NonNull UserModel userModel) {
                user.postValue(userModel);
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }
        });
    }
}
