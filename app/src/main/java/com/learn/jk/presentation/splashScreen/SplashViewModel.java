package com.learn.jk.presentation.splashScreen;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.learn.jk.data.GLOBAL;
import com.learn.jk.data.repository.MainRepository;
import com.learn.jk.data.repository.UserRepository;

import java.util.List;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;

public class SplashViewModel extends ViewModel {
    private MainRepository mainRepository = MainRepository.getInstance();
    private UserRepository userRepository = UserRepository.getInstance();
    public MutableLiveData<Boolean> isLogin;

    public LiveData<Boolean> getIsLogin() {
        if(isLogin==null) {
            isLogin = new MutableLiveData<>();
            checkIsLogin();
        }
        return isLogin;
    }
    public void checkIsLogin() {
        userRepository.isLogin().subscribeWith(new DisposableSingleObserver<Boolean>() {
            @Override
            public void onSuccess(@NonNull Boolean aBoolean) {
                isLogin.postValue(aBoolean);
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }
        });
    }
}
