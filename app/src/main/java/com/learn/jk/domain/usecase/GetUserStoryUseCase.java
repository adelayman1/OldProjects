package com.learn.jk.domain.usecase;

import com.learn.jk.data.model.UserStoriesModel;
import com.learn.jk.data.repository.StoryRepository;
import com.learn.jk.data.repository.UserRepository;
import io.reactivex.rxjava3.core.Single;

public class GetUserStoryUseCase {
    UserRepository userRepository = new UserRepository();
    StoryRepository storyRepository = new StoryRepository();

    public Single<UserStoriesModel> invoke(String userUID) {
        return  storyRepository.getStoriesOfUser(userUID).flatMap(myStories -> {
            return  userRepository.getUser(userUID).map(userModel -> new UserStoriesModel(myStories, userModel)).onErrorReturn(throwable -> null);
        }).onErrorReturn(throwable -> null);
    }
}
