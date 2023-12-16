package com.learn.jk.domain.usecase;

import com.google.firebase.auth.FirebaseUser;
import com.learn.jk.data.repository.LoginRepositoryImpl;
import com.learn.jk.data.repository.UserRepository;

import java.util.HashMap;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.functions.Function;

public class SignUpUseCase {
    LoginRepositoryImpl loginRepository = new LoginRepositoryImpl();
    UserRepository userRepository = new UserRepository();

    public Single<Boolean> invoke(String email, String password, String firstName, String lastName, final String date, boolean isMale, String day, String month, String year) throws Exception {
        if (email.isEmpty()) {
            throw (new Exception("null data"));
        }
        if (password.isEmpty()) {
            throw (new Exception("null data"));
        }
        return loginRepository.createAccountWithEmailAndPassword(email, password).flatMap(user -> {
            HashMap<String, Object> userData = new HashMap<>();
            userData.put("date", date);
            userData.put("day", day);
            userData.put("month", month);
            userData.put("year", year);
            userData.put("name", firstName + " " + lastName);
            userData.put("hint", "no hint");
            userData.put("email", email);
            userData.put("photo", "noPhoto");
            userData.put("gender", isMale ? "Male" : "Female");
            userData.put("whatLove", "not Added");
            userData.put("verified", false);
            userData.put("accountType", "normal");
            userData.put("websiteURL", "");
            userData.put("dateVisibility", "Show my birthday");
            return userRepository.changeUserData(userRepository.getUid(), userData);
        });
    }
}
