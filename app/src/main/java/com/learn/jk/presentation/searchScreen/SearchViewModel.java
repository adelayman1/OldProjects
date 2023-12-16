package com.learn.jk.presentation.searchScreen;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.learn.jk.data.repository.PostRepository;
import com.learn.jk.data.repository.Repository;
import com.learn.jk.data.repository.UserRepository;
import com.learn.jk.data.model.PostModel;
import com.learn.jk.data.model.UserModel;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;

public class SearchViewModel extends ViewModel {
    private Repository repository = Repository.getInstance();
    private UserRepository userRepository = UserRepository.getInstance();
    private PostRepository postRepository = PostRepository.getInstance();
    public MutableLiveData<List<UserModel>> usersSearchResult = new MutableLiveData<>();
    public MutableLiveData<List<PostModel>> tagsSearchResult = new MutableLiveData<>();
    public MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public void searchUsers(String userName) {
        userRepository.searchWithUserName(userName).subscribeWith(new DisposableSingleObserver<List<UserModel>>() {
            @Override
            public void onSuccess(@NonNull List<UserModel> userModels) {
                usersSearchResult.postValue(userModels);
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }
        });
    }
    /**
     * @param tag add tag without # letter
     */
    public void searchTags(String tag) {
        List<PostModel> tempPosts = new ArrayList<>();
       /* String[] tag = tag.split("#");//TODO THAT
        if (!tag.contains("#")) {// add it in view
            searchResult.setValue(null);
            return;
        }*/
       //        ValueEventListener postListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                mPostsList.clear();
//                for (int i = 0; i < mPostsUidList.size(); i++) {
//                    if (snapshot.child(mPostsUidList.get(i)).exists()) {
//                        PostModel item = snapshot.child(mPostsUidList.get(i)).getValue(PostModel.class);
//                        mPostsList.add(item);
//                    }
//                }
//                searchResult.setValue(mPostsList);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        };
//        repository.getTagByName(tag[1]).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                mPostsUidList.clear();
//                if(snapshot.exists()) {
//                    numOfPostsInTag.postValue(String.valueOf(snapshot.getChildrenCount()));
//                    for (DataSnapshot item : snapshot.getChildren()) {
//                        String itemModel = item.getKey();
//                        mPostsUidList.add(itemModel);
//                    }
//                }else {
//                    searchResult.setValue(null);
//                    return;
//                }
//                repository.getPosts().addListenerForSingleValueEvent(postListener);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
        if(tag.contains("#"))
        {
            tag=tag.split("#")[1];
        }
        postRepository.getUIDsInTag(tag).subscribeWith(new DisposableSingleObserver<List<String>>() {
                    @Override
                    public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull List<String> strings) {
                        tempPosts.clear();
                        strings.forEach(new Consumer<String>() {
                            @Override
                            public void accept(String s) {
                                postRepository.getPost(s).subscribeWith(new DisposableSingleObserver<PostModel>() {
                                    @Override
                                    public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull PostModel postModel) {
                                        tempPosts.add(postModel);
                                        tagsSearchResult.postValue(tempPosts);
                                    }

                                    @Override
                                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                                        tagsSearchResult.postValue(null);
                                    }
                                });
                            }
                        });
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        tagsSearchResult.postValue(null);
                    }
                });
    }
}
