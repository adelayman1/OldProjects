package com.learn.jk.data.repository;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.learn.jk.data.model.StoryModel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleEmitter;
import io.reactivex.rxjava3.core.SingleOnSubscribe;
import omari.hamza.storyview.model.MyStory;

public class StoryRepository {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    public static StoryRepository instance;

    public static StoryRepository getInstance() {
        if (instance != null)
            return instance;

        instance = new StoryRepository();
        return instance;
    }

    /**
     * @param uid   uid of the user added the story
     * @param image image bytes of storyImage
     * @return the {@link LiveData} for this operation
     */
    public Single<Boolean> addStory(String uid, byte[] image) {
         return Single.create(new SingleOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@io.reactivex.rxjava3.annotations.NonNull SingleEmitter<Boolean> emitter) throws Throwable {
                DatabaseReference storyReference = reference.child("Story").child(uid).push();
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference().child("images").child(UUID.randomUUID().toString() + UUID.randomUUID().toString());
                storageRef.putBytes(image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                storyReference.setValue(uri.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        emitter.onSuccess(true);
                                    }
                                });
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
     * @param uid a user uid we will get his stories
     */
    //TODO getStory
    public Single<ArrayList<MyStory>> getStoriesOfUser(String uid) {
        return Single.create(new SingleOnSubscribe<ArrayList<MyStory>>() {
            @Override
            public void subscribe(@io.reactivex.rxjava3.annotations.NonNull SingleEmitter<ArrayList<MyStory>> emitter) throws Throwable {
                ArrayList<MyStory> tempStories = new ArrayList<>();
                reference.child("Story").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        tempStories.clear();
                        for (DataSnapshot item : snapshot.getChildren()) {
                            String image = item.getValue(String.class);
                            tempStories.add(new MyStory(image, null, null));
                        }
                        emitter.onSuccess(tempStories);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        emitter.onError(error.toException());
                    }
                });
            }
        });
    }


    public Single<List<StoryModel>> getFollowingStories(List<String> followingList) {
        return Single.create(new SingleOnSubscribe<List<StoryModel>>() {
            @Override
            public void subscribe(@io.reactivex.rxjava3.annotations.NonNull SingleEmitter<List<StoryModel>> emitter) throws Throwable {

                List<StoryModel> tempStories = new ArrayList<>();
                reference.child("Story").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        tempStories.clear();
                        for (int i = 0; i < followingList.size(); i++) {
                            if (snapshot.child(followingList.get(i)).exists()) {
                                tempStories.add(new StoryModel(snapshot.getChildrenCount(), followingList.get(i)));
                            }
                        }
                        emitter.onSuccess(tempStories);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        emitter.onError(error.toException());
                    }
                });
            }
        });
    }

}
