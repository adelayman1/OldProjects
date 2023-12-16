package com.learn.jk.presentation.homeScreen;

import android.view.View;

import com.learn.jk.data.model.PostModel;
import com.learn.jk.data.model.UserModel;

public interface IPostAdapterItemClickListener {
    void onRecycleViewItemClick(View view, PostModel model,Boolean isLike);
    void onCommentClickListener(PostModel model, UserModel user);
    void iconAvatarOnClick(String uid);
    void tagClickListener(String tagName);

    void share(String key);
}
