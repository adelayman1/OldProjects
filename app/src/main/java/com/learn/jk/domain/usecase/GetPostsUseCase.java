package com.learn.jk.domain.usecase;

import com.learn.jk.data.model.PostModel;
import com.learn.jk.data.repository.PostRepository;
import com.learn.jk.data.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Single;

public class GetPostsUseCase {
    UserRepository userRepository = new UserRepository();
    private PostRepository postRepository = PostRepository.getInstance();

    public Single<List<PostModel>> invoke() {
        String uid = userRepository.getUid();
        return userRepository.getFollowing(uid).flatMap(followingList -> postRepository.getFollowingPosts(followingList, uid)).onErrorReturn(throwable -> new ArrayList<PostModel>());
    }
}
