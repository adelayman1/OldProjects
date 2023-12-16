package com.learn.jk.data.repository;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleEmitter;
import io.reactivex.rxjava3.core.SingleOnSubscribe;

public class MainRepository {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    public static MainRepository instance;

    public static MainRepository getInstance() {
        if (instance != null)
            return instance;

        instance = new MainRepository();
        return instance;
    }
//TODO ADD DISIPOLE AND ON ERROR


    public Single<String> getAppVersion() {

        return Single.create(new SingleOnSubscribe<String>() {
            @Override
            public void subscribe(@io.reactivex.rxjava3.annotations.NonNull SingleEmitter<String> emitter) throws Throwable {
                reference.child("Global").child("AppVersion").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        emitter.onSuccess(snapshot.getValue(String.class));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        emitter.onError(error.toException());
                    }
                });
            }
        });
    }

    public Single<String> getAdminMessages() {

        return Single.create(new SingleOnSubscribe<String>() {
            @Override
            public void subscribe(@io.reactivex.rxjava3.annotations.NonNull SingleEmitter<String> emitter) throws Throwable {
                reference.child("Global").child("Messages").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists())
                            emitter.onSuccess(snapshot.getValue(String.class));
                        else
                            emitter.onSuccess(null);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        emitter.onError(error.toException());
                    }
                });
            }
        });
    }

    public Single<Boolean> isAppStop() {

        return Single.create(new SingleOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@io.reactivex.rxjava3.annotations.NonNull SingleEmitter<Boolean> emitter) throws Throwable {
                reference.child("Global").child("stop").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (emitter.isDisposed())
                            return;
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
}
