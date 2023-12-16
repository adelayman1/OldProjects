package com.learn.jk.domain.usecase;

import com.learn.jk.data.model.PostModel;
import com.learn.jk.data.model.StoryModel;
import com.learn.jk.data.repository.PostRepository;
import com.learn.jk.data.repository.StoryRepository;
import com.learn.jk.data.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Single;

public class GetStoriesUseCase {
    UserRepository userRepository = new UserRepository();
    private StoryRepository storyRepository = StoryRepository.getInstance();

    public Single<List<StoryModel>> invoke() {
        String uid = userRepository.getUid();
        return userRepository.getFollowing(uid).flatMap(followingList -> storyRepository.getFollowingStories(followingList)).onErrorReturn(throwable -> new ArrayList<StoryModel>());
    }
}
