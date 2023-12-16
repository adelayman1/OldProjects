package com.learn.jk.presentation.addPostScreen;

import android.graphics.Bitmap;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.learn.jk.data.repository.PostRepository;
import com.learn.jk.domain.usecase.AddImagePostUseCase;

import java.io.ByteArrayOutputStream;
import java.util.List;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;

public class AddPostViewModel extends ViewModel {
    public MutableLiveData<Boolean> isSuccess = new MutableLiveData<>();
    public MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public PostRepository postRepository = PostRepository.getInstance();
    public AddImagePostUseCase addImagePostUseCase = new AddImagePostUseCase();

    public void AddPost(String hint, Bitmap image, String visible, int commentEnable, List<String> hashTags) {
        isLoading.postValue(true);
       /*  Date date = new Date();
        long time = date.getTime();
        String uid = repository.getUid();
        HashMap<String, Object> data = new HashMap<>();
        data.put("uid", uid);
        data.put("time", time);//TODO:GET TIME
        data.put("hint", hint);
        data.put("type", "photo");
        data.put("visible", visible);
        data.put("commentEnable", commentEnable);
        StorageReference child = repository.uploadPhoto();
        child.putBytes(image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                child.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        data.put("image", uri.toString());
                        repository.sendPost(uid, data).observeForever(new Observer<HashMap<String, Object>>() {
                            @Override
                            public void onChanged(HashMap<String, Object> stringObjectHashMap) {
                                Task<Void> listener = (Task<Void>) stringObjectHashMap.get("listener");

                                listener.addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        if (hashTags.size() > 0) {
                                            for (String tag : hashTags) {
                                                repository.addHashTag((String) stringObjectHashMap.get("key"), tag);
                                            }
                                            isSuccess.postValue(true);
                                        } else {

                                            isSuccess.postValue(true);
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        isSuccess.postValue(false);
                                    }
                                });
                            }
                        });


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        isSuccess.postValue(false);
                    }
                });


            }
        });*/
        //TODO:USER UID

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.WEBP, 50, stream);
        addImagePostUseCase.invoke(hint, stream.toByteArray(), visible, commentEnable, hashTags).subscribeWith(new DisposableSingleObserver<Boolean>() {
            @Override
            public void onSuccess(@NonNull Boolean aBoolean) {
                isLoading.postValue(false);
                isSuccess.postValue(aBoolean);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                isLoading.postValue(false);
                isSuccess.postValue(false);
            }
        });
    }

    public void AddPost(String hint, String video, String visible, int commentEnable, List<String> hashTags) {
        isLoading.postValue(true);
    /*    Date date = new Date();
        long time = date.getTime();
        String uid = repository.getUid();
        HashMap<String, Object> data = new HashMap<>();
        data.put("uid", uid);
        data.put("time", time);//TODO:GET TIME
        data.put("hint", hint);
        data.put("visible", visible);
        data.put("commentEnable", commentEnable);
        data.put("type", "video");
        data.put("video", video);
        repository.sendPost(uid, data).observeForever(new Observer<HashMap<String, Object>>() {
            @Override
            public void onChanged(HashMap<String, Object> stringObjectHashMap) {
                Task<Void> listener = (Task<Void>) stringObjectHashMap.get("listener");
                listener.addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if (hashTags.size() > 0) {
                            for (String tag : hashTags) {
                                repository.addHashTag((String) stringObjectHashMap.get("key"), tag);
                            }

                            isSuccess.postValue(true);
                        } else {

                            isSuccess.postValue(true);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        isSuccess.postValue(false);
                    }
                });
            }
        });
*/
        postRepository.addVideoPost(FirebaseAuth.getInstance().getUid(), hint, video, visible, commentEnable, hashTags).subscribeWith(new DisposableSingleObserver<Boolean>() {
            @Override
            public void onSuccess(@NonNull Boolean aBoolean) {
                isLoading.postValue(false);
                isSuccess.postValue(aBoolean);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                isLoading.postValue(false);
                isSuccess.postValue(false);
            }
        });
        ;
    }
}
