package com.learn.jk.presentation.profileScreen;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.learn.jk.data.repository.PostRepository;
import com.learn.jk.data.repository.Repository;
import com.learn.jk.data.repository.StoryRepository;
import com.learn.jk.data.repository.UserRepository;
import com.learn.jk.data.model.PostModel;
import com.learn.jk.data.model.UserModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;

public class ProfileViewModel extends ViewModel {
    public Repository repository = Repository.getInstance();
    public UserRepository userRepository = UserRepository.getInstance();
    public PostRepository postRepository = PostRepository.getInstance();
    public StoryRepository storyRepository = StoryRepository.getInstance();
    public String myUid = repository.getUid();
    public MutableLiveData<Boolean> isStoryUploadSuccess;
    public MutableLiveData<UserModel> user;
    public MutableLiveData<Boolean> isInfoChangesSuccess=new MutableLiveData<>();
    public MutableLiveData<List<UserModel>> mFollowers;
    public MutableLiveData<Boolean> isFollow;
    public MutableLiveData<String> followersNum;
    public MutableLiveData<List<PostModel>> posts;
    public MutableLiveData<List<String>> photos;
    public MutableLiveData<Boolean> isProfileLoading =new MutableLiveData<>();
    public MutableLiveData<HashMap<String, String>> mInformation;
    public String getMyUid() {
        return myUid;
    }

    //    public void getData(String uid, Boolean isMProfile) {
//
//        repository.getUser(uid).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (!snapshot.exists() || !snapshot.child("accountType").exists()) {
//                    isProfileExist.postValue(false);
//                    return;
//                }
//                isProfileExist.postValue(true);
//                UserModel model = snapshot.getValue(UserModel.class);
//                String strBirthday;
//                if (model != null) {
//                    //true
//                    if (model.getWhatLove() != null)
//                        whatLove.postValue(model.getWhatLove());
//                    else
//                        whatLove.postValue("not Added");
//
//
//                    String dateType = model.getDateVisibility();
//                    switch (dateType) {
//                        case "Show my birthday":
//                            strBirthday = model.getMonth() + " " + model.getDay() + "," + model.getYear();
//                            birthday.postValue(strBirthday);
//                            break;
//                        case "Show month and day only":
//                            strBirthday = model.getMonth() + " " + model.getDay();
//                            birthday.postValue(strBirthday);
//                            break;
//                        case "don't show my birthday":
//                            birthday.postValue(null);
//                            break;
//                        default:
//                            birthday.postValue(null);
//                            break;
//                    }
//
//
//                    name.setValue(model.getName());
//                    hint.postValue(model.getHint());
//                    avatar.setValue(model.getPhoto());
//                    verified.postValue(model.isVerified());
//                    if (!isMProfile)
//                        accountType.postValue(model.getAccountType());
//                    if (!model.getWebsiteURL().trim().isEmpty())
//                        website.setValue(model.getWebsiteURL());
//                } else {
//                    name.setValue(" ");
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//        followersNum = userRepository.getNumOfFollowers(myUid);
//    }
    public LiveData<UserModel> getUserData(String uid) {
        if (user ==null) {
            user=new MutableLiveData<>();
            loadUserData(uid);
        }
        return user;
    }
    public LiveData<String> getFollowersNum(String uid) {
        if (followersNum ==null) {
            followersNum=new MutableLiveData<>();
            loadUserFollowersNum(uid);
        }
        return followersNum;
    }

    public void loadUserData(String uid) {
        userRepository.getUser(uid).subscribeWith(new DisposableSingleObserver<UserModel>() {
            @Override
            public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull UserModel userModel) {
                user.postValue(userModel);
            }

            @Override
            public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                user.postValue(null);
            }
        });
    }
    public void loadUserFollowersNum(String uid) {
        userRepository.getNumOfFollowers(uid).subscribeWith(new DisposableSingleObserver<String>() {
            @Override
            public void onSuccess(@NonNull String s) {
                followersNum.postValue(s);
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }
        });
    }


    public LiveData<List<PostModel>> getPosts(String uid, boolean isFollowing) {
        if (posts == null) {
            posts = new MutableLiveData<>();
            loadPosts(uid, isFollowing);
        }
        return posts;
    }

    public LiveData<List<String>> getPhotos(String uid, boolean isFollowing) {
        if (photos == null) {
            photos = new MutableLiveData<>();
            loadPhotos(uid, isFollowing);
        }
        return photos;
    }

    public void loadPosts(String uid, boolean isFollowing) {
        postRepository.getUserPosts(uid, myUid, isFollowing).subscribeWith(new DisposableSingleObserver<List<PostModel>>() {
            @Override
            public void onSuccess(@NonNull List<PostModel> postModels) {
                posts.postValue(postModels);
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }
        });
    }

    public void loadPhotos(String uid, boolean isFollowing) {
        postRepository.getUserPhotos(uid, myUid, isFollowing).subscribeWith(new DisposableSingleObserver<List<String>>() {
            @Override
            public void onSuccess(@NonNull List<String> strings) {
                photos.postValue(strings);
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }
        });
    }

    public LiveData<Boolean> getIsFollowing(String uid){
        if(isFollow==null){
            isFollow=new MutableLiveData<>();
            isProfileLoading.postValue(true);
            loadIsFollowing(uid);
        }
        return isFollow;
    }
    public void loadIsFollowing(String uid) {
        userRepository.isFollowing(myUid, uid).subscribeWith(new DisposableSingleObserver<Boolean>() {
            @Override
            public void onSuccess(@NonNull Boolean aBoolean) {
                isFollow.postValue(aBoolean);
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }
        });

    }

    public void changeFollow(String userUid, Boolean isFollower) {
        if (isFollower) {
            userRepository.unFollow(myUid, userUid);
            isFollow.postValue(false);
        } else {
            userRepository.follow(myUid, userUid);
            isFollow.postValue(true);
        }
    }

    public void changeLikeStatues(PostModel postModel, Boolean isLike) {
        if (isLike) {
            postRepository.unlike(postModel.getKey(), myUid);
        } else {
            postRepository.like(postModel.getKey(), myUid);//TODO:repository.getUid().getValue()
        }
    }


    //done
    public LiveData<HashMap<String, String>> getAllInfo(String userUid) {
        if (mInformation == null) {
            mInformation = new MutableLiveData<>();
            loadAllInfo(userUid);
        }
        return mInformation;
    }

    void addInfo(String title, String value) {
        userRepository.addInformation(myUid, title, value).subscribeWith(new DisposableSingleObserver<Boolean>() {
            @Override
            public void onSuccess(@NonNull Boolean aBoolean) {
                isInfoChangesSuccess.postValue(true);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                isInfoChangesSuccess.postValue(false);
            }
        });
    }

    void loadAllInfo(String userUid) {
        //TODO اعادة تسمية ألأسامي re trype names corecct of vars
        userRepository.getInformation(userUid).subscribeWith(new DisposableSingleObserver<HashMap<String, String>>() {
            @Override
            public void onSuccess(@NonNull HashMap<String, String> map) {
                mInformation.postValue(map);
            }

            @Override
            public void onError(@NonNull Throwable e) {
            }
        });
    }

    void deleteInfoItem(String key) {
        userRepository.deleteInformation(myUid, key).subscribeWith(new DisposableSingleObserver<Boolean>() {
            @Override
            public void onSuccess(@NonNull Boolean aBoolean) {
                isInfoChangesSuccess.postValue(true);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                isInfoChangesSuccess.postValue(false);
            }
        });
    }

    /*
    TODO:here
     */
    void addStory(byte[] image) {
        String uid = repository.getUid();
        storyRepository.addStory(uid, image).subscribeWith(new DisposableSingleObserver<Boolean>() {
            @Override
            public void onSuccess(@NonNull Boolean aBoolean) {
                isStoryUploadSuccess.postValue(aBoolean);
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }
        });
    }
    ///


    public Boolean isMyProfile(String profileUID) {
        return profileUID.equals(myUid);
    }


    public LiveData<List<UserModel>> getFollowersUserList() {
        if (mFollowers == null) {
            mFollowers = new MutableLiveData<>();
            loadFollowersUserList();
        }
        return mFollowers;
    }

    public void loadFollowersUserList() {
        List<UserModel> mUsers = new ArrayList<>();
        userRepository.getFollowersUIDs(myUid).subscribeWith(new DisposableSingleObserver<List<String>>() {
            @Override
            public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull List<String> strings) {
                mUsers.clear();
                strings.forEach(new Consumer<String>() {
                    @Override
                    public void accept(String s) {
                        userRepository.getUser(s).subscribeWith(new DisposableSingleObserver<UserModel>() {
                            @Override
                            public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull UserModel userModel) {
                                mUsers.add(userModel);
                                mFollowers.postValue(mUsers);
                            }

                            @Override
                            public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {

                            }
                        });
                    }
                });
            }

            @Override
            public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {

            }
        });
    }
    /*
        public void getFollowersList(List<String> userUIDs) {
            List<UserModel> mUsers = new ArrayList<>();
            repository.getUsers().addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    mUsers.clear();
                    for (String item : userUIDs) {
                        UserModel user = snapshot.child(item).getValue(UserModel.class);
                        mUsers.add(user);
                    }
                    mFollowers.postValue(mUsers);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }*/
}
