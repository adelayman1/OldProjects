package com.learn.jk.domain.usecase;

import com.learn.jk.data.repository.LoginRepositoryImpl;
import com.learn.jk.domain.repository.LoginRepository;

import io.reactivex.rxjava3.core.Single;

public class SignInUseCase {
    LoginRepositoryImpl repository = new LoginRepositoryImpl();
    public Single<Boolean> invoke(String email, String password) throws Exception {
        if(email.isEmpty()){
            throw(new Exception("null data"));
        }
        if(password.isEmpty()){
            throw(new Exception("null data"));
        }
        return repository.signIn(email, password);
    }
}
