package com.learn.jk.presentation.loginScreen;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.learn.jk.data.repository.LoginRepositoryImpl;
import com.learn.jk.domain.usecase.SignInUseCase;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;

public class LoginViewModel extends ViewModel {
    private LoginRepositoryImpl repository = LoginRepositoryImpl.getInstance();
    private SignInUseCase signInUseCase = new SignInUseCase();
    public MutableLiveData<Boolean> loginSuccess = new MutableLiveData<>();
    public MutableLiveData<Boolean> forgetMessageSend = new MutableLiveData<>();
    public MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public void Login(String email, String password) {
        try {
            signInUseCase.invoke(email, password).subscribeWith(new DisposableSingleObserver<Boolean>() {
                @Override
                public void onSuccess(@NonNull Boolean aBoolean) {
                    loginSuccess.postValue(true);
                }

                @Override
                public void onError(@NonNull Throwable e) {
                    Log.d("Login error:", e.getMessage());
                    loginSuccess.postValue(false);
                }
            });
        } catch (Exception e) {
            loginSuccess.postValue(false);
        }
    }

    public void sendForgetMessage(String email) {
        repository.forgetPassword(email).subscribeWith(new DisposableSingleObserver<Boolean>() {
            @Override
            public void onSuccess(@NonNull Boolean aBoolean) {
                forgetMessageSend.postValue(true);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.d("Login error:", e.getMessage());
                forgetMessageSend.postValue(false);
            }
        });
    }

}
