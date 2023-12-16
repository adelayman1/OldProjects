package com.learn.jk.data.repository;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.learn.jk.data.model.UserModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleEmitter;
import io.reactivex.rxjava3.core.SingleOnSubscribe;

public class UserRepository {
    String uid;
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    public static UserRepository instance;

    public static UserRepository getInstance() {
        if (instance != null)
            return instance;

        instance = new UserRepository();
        return instance;
    }

    public Single<Boolean> isLogin() {
        return Single.create(new SingleOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@io.reactivex.rxjava3.annotations.NonNull SingleEmitter<Boolean> emitter) throws Throwable {
                Boolean userExist = FirebaseAuth.getInstance().getCurrentUser() != null;
                emitter.onSuccess(userExist);
            }
        });
    }

    public synchronized String getUid() {
        if (uid == null) uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        return uid;
    }

    public Single<List<UserModel>> searchWithUserName(String userName) {
        return Single.create(new SingleOnSubscribe<List<UserModel>>() {
            @Override
            public void subscribe(@io.reactivex.rxjava3.annotations.NonNull SingleEmitter<List<UserModel>> emitter) throws Throwable {
                List<UserModel> tempUsers = new ArrayList<>();
                reference.child("User").orderByChild("name").equalTo(userName).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        tempUsers.clear();
                        for (DataSnapshot item : snapshot.getChildren()) {
                            UserModel user = item.getValue(UserModel.class);
                            tempUsers.add(user);
                        }
                        emitter.onSuccess(tempUsers);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        emitter.onError(error.toException());
                    }
                });
            }
        });
    }

    public Single<UserModel> getUser(String uid) {
        return Single.create(new SingleOnSubscribe<UserModel>() {
            @Override
            public void subscribe(@io.reactivex.rxjava3.annotations.NonNull SingleEmitter<UserModel> emitter) throws Throwable {
                reference.child("User").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (emitter.isDisposed())
                            return;
                        if (!snapshot.exists()) {
                            emitter.onError(new Exception("no Profile"));
                            return;
                        }
                        UserModel model = snapshot.getValue(UserModel.class);
                        emitter.onSuccess(model);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        emitter.onError(error.toException());
                    }
                });
            }
        });
    }

    public void follow(String myUid, String uid) {
        reference.child("Followers").child(uid).child(myUid).setValue("t");
        reference.child("Following").child(myUid).child(uid).setValue("t");
    }

    public void unFollow(String myUid, String uid) {
        reference.child("Followers").child(uid).child(myUid).removeValue();
        reference.child("Following").child(myUid).child(uid).removeValue();
    }

    public Single<List<String>> getFollowing(String myUid) {
        return Single.create(new SingleOnSubscribe<List<String>>() {
            @Override
            public void subscribe(@io.reactivex.rxjava3.annotations.NonNull SingleEmitter<List<String>> emitter) throws Throwable {

                List<String> followingList = new ArrayList<>();
                reference.child("Following").child(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        followingList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            followingList.add(dataSnapshot.getKey());
                        }
                        emitter.onSuccess(followingList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        emitter.onError(error.toException());
                    }
                });
            }
        });
    }

    public Single<String> getNumOfFollowers(String myUid) {
        return Single.create(new SingleOnSubscribe<String>() {
            @Override
            public void subscribe(@io.reactivex.rxjava3.annotations.NonNull SingleEmitter<String> emitter) throws Throwable {
                reference.child("Followers").child(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        emitter.onSuccess(String.valueOf(snapshot.getChildrenCount()));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        emitter.onError(error.toException());
                    }
                });
            }
        });
    }

    public Single<List<String>> getFollowersUIDs(String uid) {
        List<String> followingList = new ArrayList<>();
        return Single.create(new SingleOnSubscribe<List<String>>() {
            @Override
            public void subscribe(@io.reactivex.rxjava3.annotations.NonNull SingleEmitter<List<String>> emitter) throws Throwable {
                reference.child("Followers").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (emitter.isDisposed()) {
                            return;
                        }
                        followingList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            followingList.add(dataSnapshot.getKey());
                        }
                        emitter.onSuccess(followingList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        emitter.onError(error.toException());
                    }
                });
            }
        });
    }

    public Single<Boolean> isFollowing(String myUid, String userUID) {
        return Single.create(new SingleOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@io.reactivex.rxjava3.annotations.NonNull SingleEmitter<Boolean> emitter) throws Throwable {
                reference.child("Following").child(myUid).child(userUID).addListenerForSingleValueEvent(new ValueEventListener() {
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

    public Single<Boolean> deleteInformation(String myUid, String itemKey) {
        return Single.create(new SingleOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@io.reactivex.rxjava3.annotations.NonNull SingleEmitter<Boolean> emitter) throws Throwable {
                reference.child("User").child(uid).child("information").child(itemKey).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
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

    public Single<Boolean> addInformation(String myUid, String itemKey, String value) {
        return Single.create(new SingleOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@io.reactivex.rxjava3.annotations.NonNull SingleEmitter<Boolean> emitter) throws Throwable {
                reference.child("User").child(uid).child("information").child(itemKey).setValue(value).addOnSuccessListener(new OnSuccessListener<Void>() {
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

    public Single<HashMap<String, String>> getInformation(String uid) {
        return Single.create(new SingleOnSubscribe<HashMap<String, String>>() {
            @Override
            public void subscribe(@io.reactivex.rxjava3.annotations.NonNull SingleEmitter<HashMap<String, String>> emitter) throws Throwable {
                HashMap<String, String> informationMap = new HashMap<>();
                reference.child("User").child(uid).child("information").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        informationMap.clear();
                        for (DataSnapshot item : snapshot.getChildren()) {
                            String title = item.getKey();
                            String text = item.getValue(String.class);
                            informationMap.put(title, text);
                        }
                        emitter.onSuccess(informationMap);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        emitter.onError(error.toException());
                    }
                });

            }
        });
    }

    public Single<Boolean> changeUserName(String myUid, String name) {
        return Single.create(new SingleOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@io.reactivex.rxjava3.annotations.NonNull SingleEmitter<Boolean> emitter) throws Throwable {
                reference.child("User").child(myUid).child("name").setValue(name).addOnSuccessListener(new OnSuccessListener<Void>() {
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

    public Single<Boolean> changeUserCountry(String myUid, String country) {
        return Single.create(new SingleOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@io.reactivex.rxjava3.annotations.NonNull SingleEmitter<Boolean> emitter) throws Throwable {
                reference.child("User").child(myUid).child("country").setValue(country).addOnSuccessListener(new OnSuccessListener<Void>() {
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

    public Single<Boolean> changeUserWhatLove(String myUid, String whatLove) {
        return Single.create(new SingleOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@io.reactivex.rxjava3.annotations.NonNull SingleEmitter<Boolean> emitter) throws Throwable {
                reference.child("User").child(myUid).child("whatLove").setValue(whatLove).addOnSuccessListener(new OnSuccessListener<Void>() {
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

    public Single<Boolean> changeUserGender(String myUid, String gender) {
        return Single.create(new SingleOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@io.reactivex.rxjava3.annotations.NonNull SingleEmitter<Boolean> emitter) throws Throwable {
                reference.child("User").child(myUid).child("gender").setValue(gender).addOnSuccessListener(new OnSuccessListener<Void>() {
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

    public Single<Boolean> changeUserDateOfDay(String myUid, String day) {
        return Single.create(new SingleOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@io.reactivex.rxjava3.annotations.NonNull SingleEmitter<Boolean> emitter) throws Throwable {
                reference.child("User").child(myUid).child("day").setValue(day).addOnSuccessListener(new OnSuccessListener<Void>() {
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

    public Single<Boolean> changeUserDateOfMonth(String myUid, String month) {
        return Single.create(new SingleOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@io.reactivex.rxjava3.annotations.NonNull SingleEmitter<Boolean> emitter) throws Throwable {
                reference.child("User").child(myUid).child("month").setValue(month).addOnSuccessListener(new OnSuccessListener<Void>() {
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

    public Single<Boolean> changeUserDateOfYear(String myUid, String year) {
        return Single.create(new SingleOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@io.reactivex.rxjava3.annotations.NonNull SingleEmitter<Boolean> emitter) throws Throwable {
                reference.child("User").child(myUid).child("year").setValue(year).addOnSuccessListener(new OnSuccessListener<Void>() {
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

    public Single<Boolean> changeAccountDateVisibility(String myUid, String dateVisibility) {
        return Single.create(new SingleOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@io.reactivex.rxjava3.annotations.NonNull SingleEmitter<Boolean> emitter) throws Throwable {
                reference.child("User").child(myUid).child("dateVisibility").setValue(dateVisibility).addOnSuccessListener(new OnSuccessListener<Void>() {
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

    public Single<Boolean> changeUserHint(String myUid, String hint) {
        return Single.create(new SingleOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@io.reactivex.rxjava3.annotations.NonNull SingleEmitter<Boolean> emitter) throws Throwable {
                reference.child("User").child(myUid).child("hint").setValue(hint).addOnSuccessListener(new OnSuccessListener<Void>() {
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

    public Single<Boolean> changeAccountType(String myUid, String accountType) {
        return Single.create(new SingleOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@io.reactivex.rxjava3.annotations.NonNull SingleEmitter<Boolean> emitter) throws Throwable {
                reference.child("User").child(myUid).child("accountType").setValue(accountType).addOnSuccessListener(new OnSuccessListener<Void>() {
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

    public Single<Boolean> changeAccountTypeToWebsite(String myUid, String accountType, String url) {
        return Single.create(new SingleOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@io.reactivex.rxjava3.annotations.NonNull SingleEmitter<Boolean> emitter) throws Throwable {
                reference.child("User").child(myUid).child("accountType").setValue(accountType).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        reference.child("User").child(myUid).child("websiteURL").setValue(url).addOnSuccessListener(new OnSuccessListener<Void>() {
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
        });
    }

    public Single<Boolean> changeUserPhoto(String uid, byte[] image) {
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
                                reference.child("User").child(uid).child("photo").setValue(uri.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        emitter.onSuccess(task.isSuccessful());
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
    public Single<Boolean> changeUserData(String userUID,HashMap<String,Object> userData) {
        return Single.create(new SingleOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@io.reactivex.rxjava3.annotations.NonNull SingleEmitter<Boolean> emitter) throws Throwable {
                DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("User").child(userUID);
                userData.put("key",userReference.getKey());
                userReference.setValue(userData).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        emitter.onSuccess(true);
                    }
                });
            }
        });
    }
}
