package com.learn.jk.domain.usecase;

import com.learn.jk.data.model.PostModel;
import com.learn.jk.data.repository.PostRepository;
import com.learn.jk.data.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Single;

public class AddImagePostUseCase {
    UserRepository userRepository = new UserRepository();
    private PostRepository postRepository = PostRepository.getInstance();

    public Single<Boolean> invoke(String hint, byte[] image, String visible, int commentEnable, List<String> hashTags) {
        return postRepository.addPost(userRepository.getUid(),hint,"photo",visible,commentEnable,hashTags).flatMap(postKey -> postRepository.addImageToPost(postKey, image)).onErrorReturn(throwable -> false);
    }
}
