package com.learn.jk.presentation.signUpScreen;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.learn.jk.domain.usecase.SignUpUseCase;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;

public class SignUpViewModel extends ViewModel {
    private SignUpUseCase signUpUseCase = new SignUpUseCase();
    public MutableLiveData<Boolean> viewFirstLayout = new MutableLiveData<>();
    public MutableLiveData<Boolean> isSuccess = new MutableLiveData<>();
    public MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public void SignUp(final String email, String password, final String first, final String last, final String date, boolean isMale, String day, String month, String year) {
        try {
            signUpUseCase.invoke(email, password, first, last, date, isMale, day, month, year).subscribe(new DisposableSingleObserver<Boolean>() {
                @Override
                public void onSuccess(@NonNull Boolean aBoolean) {
                    isSuccess.postValue(true);
                }

                @Override
                public void onError(@NonNull Throwable e) {
                    Log.d("SignUp error:", e.getMessage());
                    isSuccess.postValue(false);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void changeLayoutVisibility(boolean statues) {
        viewFirstLayout.postValue(statues);
    }


}
