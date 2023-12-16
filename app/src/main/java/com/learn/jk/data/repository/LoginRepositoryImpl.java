package com.learn.jk.data.repository;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.learn.jk.domain.repository.LoginRepository;

import java.util.HashMap;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleEmitter;
import io.reactivex.rxjava3.core.SingleOnSubscribe;

//TODO TRUE
//TODO TRUE
//TODO TRUE
public class LoginRepositoryImpl implements LoginRepository {

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    public static LoginRepositoryImpl instance;

    public static LoginRepositoryImpl getInstance() {
        if (instance != null)
            return instance;

        instance = new LoginRepositoryImpl();
        return instance;
    }

    @Override
    public Single<Boolean> forgetPassword(String email) {
        return Single.create(new SingleOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@io.reactivex.rxjava3.annotations.NonNull SingleEmitter<Boolean> emitter) throws Throwable {
                FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
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

    //TODO THAT
    public synchronized boolean isLogin() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    @Override
    public Single<Boolean> signIn(String email, String password) {
        return Single.create(new SingleOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@io.reactivex.rxjava3.annotations.NonNull SingleEmitter<Boolean> emitter) throws Throwable {
                mAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
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

    @Override
    public Single<FirebaseUser> createAccountWithEmailAndPassword(String email, String password) {
        return Single.create(new SingleOnSubscribe<FirebaseUser>() {
            @Override
            public void subscribe(@io.reactivex.rxjava3.annotations.NonNull SingleEmitter<FirebaseUser> emitter) throws Throwable {
                mAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        emitter.onSuccess(authResult.getUser());
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

    public Single<Boolean> signUp(String email, String password, String first, String last, final String date, boolean isMale, String day, String month, String year) {
        return Single.create(new SingleOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@io.reactivex.rxjava3.annotations.NonNull SingleEmitter<Boolean> emitter) throws Throwable {
                mAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("User").child(authResult.getUser().getUid());
                        HashMap<String, Object> userData = new HashMap<>();
                        userData.put("date", date);
                        userData.put("day", day);
                        userData.put("month", month);
                        userData.put("year", year);
                        userData.put("name", first + " " + last);
                        userData.put("hint", "no hint");
                        userData.put("email", email);
                        userData.put("photo", "noPhoto");
                        userData.put("key", userReference.getKey());
                        userData.put("gender", isMale ? "Male" : "Female");
                        userData.put("whatLove", "not Added");
                        userData.put("verified", false);
                        userData.put("accountType", "normal");
                        userData.put("websiteURL", "");
                        userData.put("dateVisibility", "Show my birthday");
                        userReference.setValue(userData).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                emitter.onSuccess(true);
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
}
