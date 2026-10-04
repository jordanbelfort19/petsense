
package com.example.pet; // Replace with your actual package name

import androidx.lifecycle.MutableLiveData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthRepository {
    private final FirebaseAuth firebaseAuth;

    public AuthRepository() {
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public MutableLiveData<FirebaseUser> login(String email, String password) {
        MutableLiveData<FirebaseUser> userLiveData = new MutableLiveData<>();

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        userLiveData.setValue(firebaseAuth.getCurrentUser());
                    } else {
                        // In a real app, handle exceptions properly
                        userLiveData.setValue(null);
                    }
                });
        return userLiveData;
    }

    public FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }
}
//com.example.pet
//├── data                          ← new package
//        │   ├── Pet.java
//│   └── PetRepository.java
//├── ui
//│   ├── dashboard                 ← already exists
//│   │   ├── DashboardViewModel.java   (overwrite existing)
//│   │   └── HealthScoreView.java      (already there, don't touch)
//        │   └── pets                      ← new package
//        │       ├── PetAdapter.java
//        │       ├── PetSelectorBottomSheet.java
//        │       └── AddPetActivity.java
//        ├── AuthRepository.java           (unchanged)
//├── AuthViewModel.java            (unchanged)
//├── LoginActivity.java            (unchanged)
//└── MainActivity.java             (overwrite existing)
//res/layout/
//        ├── activity_main.xml             (overwrite existing)
//├── activity_add_pet.xml          ← new
//        ├── item_pet_row.xml              ← new
//        └── bottom_sheet_pet_selector.xml ← new