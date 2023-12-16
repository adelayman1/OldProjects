package com.learn.jk.presentation.profileScreen.editScreen;

import android.graphics.Bitmap;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.learn.jk.data.repository.Repository;
import com.learn.jk.data.repository.UserRepository;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;

public class EditViewModel extends ViewModel {
    private Repository repository = Repository.getInstance();
    private UserRepository userRepository = UserRepository.getInstance();
    public MutableLiveData<Boolean> isSuccess = new MutableLiveData<>();
    public MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    public MutableLiveData<Boolean> isAccountChangedSuccess = new MutableLiveData<>();
    public MutableLiveData<Bitmap> image = new MutableLiveData<>();
    String myUid = repository.getUid();
    
    public void sendDate(String country, String firstName, String lastName, String whatYouLove, String gender, String day, String month, String year, String dateVisibility, byte[] image, String hint) {
        //inject this
        //TODO ISSUCCSES HER WITH RXJAVA
        isLoading.postValue(true);
        if (!country.trim().isEmpty()) {
            userRepository.changeUserCountry(myUid, country).subscribeWith(new DisposableSingleObserver<Boolean>() {
            @Override
            public void onSuccess(@NonNull Boolean aBoolean) {
                isSuccess.postValue(true);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                  isSuccess.postValue(false);
            }
        });
        }
        if (!firstName.trim().isEmpty() && !lastName.trim().isEmpty()) {
            userRepository.changeUserName(myUid, firstName + " " + lastName).subscribeWith(new DisposableSingleObserver<Boolean>() {
            @Override
            public void onSuccess(@NonNull Boolean aBoolean) {
                  isSuccess.postValue(true);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                  isSuccess.postValue(false);
            }
        });;
        }
        if (!whatYouLove.trim().isEmpty()) {
            userRepository.changeUserWhatLove(myUid, whatYouLove).subscribeWith(new DisposableSingleObserver<Boolean>() {
            @Override
            public void onSuccess(@NonNull Boolean aBoolean) {
                  isSuccess.postValue(true);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                  isSuccess.postValue(false);
            }
        });;
        }
        if (!gender.trim().isEmpty()) {
            userRepository.changeUserGender(myUid, gender).subscribeWith(new DisposableSingleObserver<Boolean>() {
            @Override
            public void onSuccess(@NonNull Boolean aBoolean) {
                  isSuccess.postValue(true);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                  isSuccess.postValue(false);
            }
        });;
        }
        if (!day.trim().isEmpty()) {
            userRepository.changeUserDateOfDay(myUid, day).subscribeWith(new DisposableSingleObserver<Boolean>() {
            @Override
            public void onSuccess(@NonNull Boolean aBoolean) {
                  isSuccess.postValue(true);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                  isSuccess.postValue(false);
            }
        });;

        }
        if (!month.trim().isEmpty()) {
            userRepository.changeUserDateOfMonth(myUid, month).subscribeWith(new DisposableSingleObserver<Boolean>() {
            @Override
            public void onSuccess(@NonNull Boolean aBoolean) {
                  isSuccess.postValue(true);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                  isSuccess.postValue(false);
            }
        });;

        }
        if (!year.trim().isEmpty()) {
            userRepository.changeUserDateOfYear(myUid, year).subscribeWith(new DisposableSingleObserver<Boolean>() {
            @Override
            public void onSuccess(@NonNull Boolean aBoolean) {
                  isSuccess.postValue(true);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                  isSuccess.postValue(false);
            }
        });;
        }
        if (!dateVisibility.trim().isEmpty()) {
            userRepository.changeAccountDateVisibility(myUid, dateVisibility).subscribeWith(new DisposableSingleObserver<Boolean>() {
            @Override
            public void onSuccess(@NonNull Boolean aBoolean) {
                  isSuccess.postValue(true);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                  isSuccess.postValue(false);
            }
        });;
        }
        if (!hint.trim().isEmpty()) {
            userRepository.changeUserHint(myUid, hint).subscribeWith(new DisposableSingleObserver<Boolean>() {
            @Override
            public void onSuccess(@NonNull Boolean aBoolean) {
                  isSuccess.postValue(true);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                  isSuccess.postValue(false);
            }
        });;

        }
        if (image != null) {
            userRepository.changeUserPhoto(myUid, image).subscribeWith(new DisposableSingleObserver<Boolean>() {
            @Override
            public void onSuccess(@NonNull Boolean aBoolean) {
                  isSuccess.postValue(true);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                  isSuccess.postValue(false);
            }
        });;
        }
    }


    public void changeAccountType(String type) {
        userRepository.changeAccountType(myUid, type).subscribeWith(new DisposableSingleObserver<Boolean>() {
            @Override
            public void onSuccess(@NonNull Boolean aBoolean) {
                isAccountChangedSuccess.postValue(true);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                isAccountChangedSuccess.postValue(false);
            }
        });
    }


    //This change to website
    public void changeAccountType(String type, String url) {
        userRepository.changeAccountTypeToWebsite(myUid, type, url).subscribeWith(new DisposableSingleObserver<Boolean>() {
            @Override
            public void onSuccess(@NonNull Boolean aBoolean) {
                isAccountChangedSuccess.postValue(true);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                isAccountChangedSuccess.postValue(false);
            }
        });;
    }
}
