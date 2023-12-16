package com.learn.jk.data.repository;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.learn.jk.data.model.CommentModel;
import com.learn.jk.data.model.PostModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleEmitter;
import io.reactivex.rxjava3.core.SingleOnSubscribe;

public class PostRepository {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    public static PostRepository instance;

    public static PostRepository getInstance() {
        if (instance != null)
            return instance;

        instance = new PostRepository();
        return instance;
    }

    public Single<List<PostModel>> getUserPosts(String userUID, String myUID, boolean isFollowing) {

        return Single.create(new SingleOnSubscribe<List<PostModel>>() {
            @Override
            public void subscribe(@io.reactivex.rxjava3.annotations.NonNull SingleEmitter<List<PostModel>> emitter) throws Throwable {
                List<PostModel> tempPostsArray = new ArrayList<>();
                reference.child("Posts").orderByChild("uid").equalTo(userUID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        tempPostsArray.clear();
                        for (DataSnapshot item : snapshot.getChildren()) {
                            PostModel postModel = item.getValue(PostModel.class);
                            if (postModel.getUid().equals(myUID))
                                tempPostsArray.add(postModel);
                            else {
                                if (postModel.getVisible().equals("general"))
                                    tempPostsArray.add(postModel);
                                else if (postModel.getVisible().equals("followers")) {
                                    if (isFollowing) {
                                        tempPostsArray.add(postModel);
                                    }
                                }

                            }
                        }
                        emitter.onSuccess(tempPostsArray);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        emitter.onError(error.toException());
                    }
                });
            }
        });
    }
    public Single<List<String>> getUserPhotos(String userUID, String myUID, boolean isFollowing) {
        return Single.create(new SingleOnSubscribe<List<String>>() {
            @Override
            public void subscribe(@io.reactivex.rxjava3.annotations.NonNull SingleEmitter<List<String>> emitter) throws Throwable {
                List<String> tempPhotosArray = new ArrayList<>();
                reference.child("Posts").orderByChild("uid").equalTo(userUID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        tempPhotosArray.clear();
                        for (DataSnapshot item : snapshot.getChildren()) {
                            PostModel postModel = item.getValue(PostModel.class);
                            if (postModel.getUid().equals(myUID))
                                if (postModel.getType().equals("photo"))
                                    tempPhotosArray.add(postModel.getImage());
                                else {
                                    if (postModel.getVisible().equals("general"))
                                        if (postModel.getType().equals("photo"))
                                            tempPhotosArray.add(postModel.getImage());
                                        else if (postModel.getVisible().equals("followers")) {
                                            if (isFollowing) {

                                                if (postModel.getType().equals("photo"))
                                                    tempPhotosArray.add(postModel.getImage());
                                            }
                                        }

                                }
                        }
                        emitter.onSuccess(tempPhotosArray);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        emitter.onError(error.toException());
                    }
                });

            }
        });
    }
    public Single<PostModel> getPost(String postId) {
        return Single.create(new SingleOnSubscribe<PostModel>() {
            @Override
            public void subscribe(@io.reactivex.rxjava3.annotations.NonNull SingleEmitter<PostModel> emitter) throws Throwable {
                reference.child("Posts").child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        PostModel postModel = snapshot.getValue(PostModel.class);
                        emitter.onSuccess(postModel);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        emitter.onError(error.toException());
                    }
                });
            }
        });
    }
    public Single<List<PostModel>> getFollowingPosts(List<String> followingList, String uid) {
        return Single.create(new SingleOnSubscribe<List<PostModel>>() {
            @Override
            public void subscribe(@io.reactivex.rxjava3.annotations.NonNull SingleEmitter<List<PostModel>> emitter) throws Throwable {
                List<PostModel> postsArray = new ArrayList<>();
                reference.child("Posts").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        postsArray.clear();
                        for (DataSnapshot item : snapshot.getChildren()) {
                            PostModel post = item.getValue(PostModel.class);
                            if (followingList.contains(post.getUid())) {
                                postsArray.add(0, post);
                            } else if (post.getVisible().equals("general") || post.getUid().equals(uid)) {
                                postsArray.add(post);
                            }
                        }
                        emitter.onSuccess(postsArray);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        emitter.onError(error.toException());
                    }
                });

            }
        });
    }
/*
TODO
    public LiveData<List<PostModel>> searchPosts(String postText) {

    }
*/




    //tag without #
    public Single<List<String>> getUIDsInTag(String tag) {  return Single.create(new SingleOnSubscribe<List<String>>() {
        @Override
        public void subscribe(@io.reactivex.rxjava3.annotations.NonNull SingleEmitter<List<String>> emitter) throws Throwable {
            List<String> tempPostsUIDs = new ArrayList<>();
            reference.child("Tags").child(tag).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(!snapshot.exists()){
                        emitter.onError(new Exception("no data"));
                        return;
                    }
                    tempPostsUIDs.clear();
                    for (DataSnapshot item:snapshot.getChildren()) {
                        String userUID = item.getKey();
                        tempPostsUIDs.add(userUID);
                    }
                    emitter.onSuccess(tempPostsUIDs);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    emitter.onError(error.toException());
                }
            });
        }
    });
    }
    /**
     * this code add hash tag to firebase
     *
     * @param postKey a key of post contains hashTags
     * @param hashTag a hashTag in post
     */
    public void addHashTag(String postKey, String hashTag) {
        reference.child("Tags").child(hashTag).child(postKey).setValue("true"); // add hashTag to db
    }
    //TODO WITH RXjava
    public Single<Boolean> addVideoPost(String uid, String hint, String video, String visible, int commentEnable, List<String> hashTags) {

        return Single.create(new SingleOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@io.reactivex.rxjava3.annotations.NonNull SingleEmitter<Boolean> emitter) throws Throwable {
                Date date = new Date();
                DatabaseReference postReference = reference.child("Posts").push();
                HashMap<String, Object> data = new HashMap<>();
                data.put("uid", uid);
                data.put("time", date.getTime());
                data.put("hint", hint);
                data.put("key", postReference.getKey());
                data.put("type", "video");
                data.put("visible", visible);
                data.put("video", video);
                data.put("commentEnable", commentEnable);
                postReference.setValue(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if (hashTags.size() > 0) {
                            for (String tag : hashTags) {
                                addHashTag(postReference.getKey(), tag);
                            }
                            emitter.onSuccess(true);
                        } else {
                            emitter.onSuccess(true);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        emitter.onError(e);
                    }
                });
            }
        });
    }
    public Single<String> addPost(String uid,String hint,String type,String visible,int commentEnable,List<String> hashTags) {
       return Single.create(new SingleOnSubscribe<String>() {
           @Override
           public void subscribe(@io.reactivex.rxjava3.annotations.NonNull SingleEmitter<String> emitter) throws Throwable {
               Date date = new Date();
               DatabaseReference postReference = reference.child("Posts").push();
               HashMap<String, Object> data = new HashMap<>();
               data.put("key", postReference.getKey());
               data.put("uid", uid);
               data.put("time", date.getTime());//TODO:GET TIME
               data.put("hint", hint);
               data.put("type", type);
               data.put("visible", visible);
               data.put("commentEnable", commentEnable);
               postReference.setValue(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                   @Override
                   public void onSuccess(Void aVoid) {
                       if (hashTags.size() > 0) {
                           for (String tag : hashTags) {
                               addHashTag(postReference.getKey(), tag);
                           }
                           emitter.onSuccess(postReference.getKey());
                       } else {
                           emitter.onSuccess(postReference.getKey());
                       }
                   }
               }).addOnFailureListener(new OnFailureListener() {
                   @Override
                   public void onFailure(@NonNull Exception e) {
                       emitter.onError(e);
                   }
               });
           }
       });
    }
    public Single<Boolean> addImagePost(String uid, String hint, byte[] image, String visible, int commentEnable, List<String> hashTags) {

        return Single.create(new SingleOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@io.reactivex.rxjava3.annotations.NonNull SingleEmitter<Boolean> emitter) throws Throwable {
                Date date = new Date();
                DatabaseReference postReference = reference.child("Posts").push();
                HashMap<String, Object> data = new HashMap<>();
                data.put("key", postReference.getKey());
                data.put("uid", uid);
                data.put("time", date.getTime());//TODO:GET TIME
                data.put("hint", hint);
                data.put("type", "photo");
                data.put("visible", visible);
                data.put("commentEnable", commentEnable);
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference().child("images").child(UUID.randomUUID().toString() + UUID.randomUUID().toString());
                storageRef.putBytes(image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                data.put("image", uri.toString());
                                postReference.setValue(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        if (hashTags.size() > 0) {
                                            for (String tag : hashTags) {
                                                addHashTag(postReference.getKey(), tag);
                                            }
                                            emitter.onSuccess(true);
                                        } else {
                                            emitter.onSuccess(true);
                                        }
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                emitter.onError(e);
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        emitter.onError(e);
                    }
                });
            }
        });
    }
    public Single<Boolean> addImageToPost(String postKey,byte[] image) {

        return Single.create(new SingleOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@io.reactivex.rxjava3.annotations.NonNull SingleEmitter<Boolean> emitter) throws Throwable {
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference().child("images").child(UUID.randomUUID().toString() + UUID.randomUUID().toString());
                storageRef.putBytes(image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                FirebaseDatabase.getInstance().getReference().child("Posts").child(postKey).child("image").setValue(uri.toString());
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                emitter.onError(e);
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        emitter.onError(e);
                    }
                });
            }
        });
    }

    /**
     * @param postKey     the key of post user commented to it
     * @param userUid     uid of user
     * @param commentText text of comments
     * @return the {@link Task} for this operation
     */
    public Single<Boolean> sendComments(String postKey, String commentText, String userUid) {
        return Single.create(new SingleOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@io.reactivex.rxjava3.annotations.NonNull SingleEmitter<Boolean> emitter) throws Throwable {
                HashMap<String, String> data = new HashMap<>();
                data.put("commentText", commentText);
                data.put("userUID", userUid);
                DatabaseReference commentReference = reference.child("Comments").child(postKey).push();
                data.put("key", commentReference.getKey());
                commentReference.setValue(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        emitter.onSuccess(true);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                       emitter.onError(e);
                    }
                });
            }
        });
    }
    public Single<List<CommentModel>> getComments(String PostKey) {
        return Single.create(new SingleOnSubscribe<List<CommentModel>>() {
            @Override
            public void subscribe(@io.reactivex.rxjava3.annotations.NonNull SingleEmitter<List<CommentModel>> emitter) throws Throwable {
                List<CommentModel> commentModelList = new ArrayList<>();
                reference.child("Comments").child(PostKey).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        commentModelList.clear();
                        for (DataSnapshot item : snapshot.getChildren()) {
                            CommentModel model = item.getValue(CommentModel.class);
                            commentModelList.add(model);
                        }
                        emitter.onSuccess(commentModelList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        emitter.onError(error.toException());
                    }
                });
            }
        });
    }

    public Observable<String> getLikesNumber(String key) {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@io.reactivex.rxjava3.annotations.NonNull ObservableEmitter<String> emitter) throws Throwable {
                reference.child("Likes").child(key).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        emitter.onNext(String.valueOf(snapshot.getChildrenCount()));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        emitter.onError(error.toException());
                    }
                });
            }
        });
    }
    public Single<Boolean> isLike(String postKey, String uid) {
        return Single.create(new SingleOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@io.reactivex.rxjava3.annotations.NonNull SingleEmitter<Boolean> emitter) throws Throwable {
                reference.child("Likes").child(postKey).child(uid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        emitter.onSuccess(snapshot.exists());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        emitter.onError(error.toException());
                    }
                });

            }
        });
    }
    public void like(String key, String uid) {
        reference.child("Likes").child(key).child(uid).setValue("true");
    }
    public void unlike(String key, String uid) {
        reference.child("Likes").child(key).child(uid).removeValue();
    }
}
