package com.example.pet;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseUser;

public class AuthViewModel extends ViewModel {

    private final AuthRepository authRepository;

    // Initialize it immediately
    private final MutableLiveData<FirebaseUser> userLiveData =
            new MutableLiveData<>();

    private final MutableLiveData<Boolean> isLoading =
            new MutableLiveData<>(false);

    private final MutableLiveData<String> errorMessage =
            new MutableLiveData<>();

    public AuthViewModel() {
        authRepository = new AuthRepository();
    }

    public void login(String email, String password) {

        if (email == null || email.trim().isEmpty()
                || password == null || password.trim().isEmpty()) {

            errorMessage.setValue("Email and password cannot be empty");
            return;
        }

        isLoading.setValue(true);

        LiveData<FirebaseUser> repositoryResult =
                authRepository.login(email, password);

        repositoryResult.observeForever(user -> {

            isLoading.setValue(false);

            if (user != null) {
                userLiveData.setValue(user);
            } else {
                errorMessage.setValue(
                        "Login failed. Check your credentials."
                );
            }
        });
    }

    public LiveData<FirebaseUser> getUserLiveData() {
        return userLiveData;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public FirebaseUser getCurrentUser() {
        return authRepository.getCurrentUser();
    }
}