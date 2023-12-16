package com.learn.jk.domain.repository;

import com.google.firebase.auth.FirebaseUser;

import io.reactivex.rxjava3.core.Single;

public interface LoginRepository {
    public Single<Boolean> forgetPassword(String email);
    public Single<Boolean> signIn(String email, String password);
    public Single<FirebaseUser> createAccountWithEmailAndPassword(String email, String password);
}
