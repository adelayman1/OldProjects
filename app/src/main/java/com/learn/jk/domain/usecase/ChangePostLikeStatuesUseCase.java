package com.learn.jk.domain.usecase;

import com.learn.jk.data.repository.PostRepository;
import com.learn.jk.data.repository.UserRepository;

public class ChangePostLikeStatuesUseCase {
    UserRepository userRepository = new UserRepository();
    private PostRepository postRepository = PostRepository.getInstance();

    public void invoke(String postKey, Boolean isLiked) {
        if (isLiked) {
            postRepository.unlike(postKey, userRepository.getUid());
        } else {
            postRepository.like(postKey, userRepository.getUid());
        }
    }
}
