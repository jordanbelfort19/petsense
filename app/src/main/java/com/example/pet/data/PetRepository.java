package com.example.pet.data;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Persists pet profiles to Cloud Firestore, under:
 *   users/{uid}/pets/{petId}
 *
 * SETUP THIS REPO ASSUMES (check these against your project before relying on it):
 *  1. The Firebase Firestore Android SDK is added in your app-level build.gradle:
 *       implementation platform('com.google.firebase:firebase-bom:33.1.0')
 *       implementation 'com.google.firebase:firebase-firestore'
 *       implementation 'com.google.firebase:firebase-auth'
 *     If your project only has Firebase Authentication set up (which the presence of
 *     AuthRepository/AuthViewModel suggests) and not Firestore, add the Firestore
 *     dependency and re-sync Gradle before this class will compile/run.
 *  2. A signed-in FirebaseAuth user exists (your existing LoginActivity flow) — pets are
 *     scoped per-user via FirebaseAuth.getInstance().getCurrentUser().getUid().
 *  3. Firestore security rules allow a signed-in user to read/write their own
 *     users/{uid}/pets subcollection, e.g.:
 *       match /users/{uid}/pets/{petId} {
 *         allow read, write: if request.auth != null && request.auth.uid == uid;
 *       }
 *
 * If Firestore isn't wired up yet in your project, do NOT delete this class — instead
 * treat DashboardViewModel's in-memory pet list as the working demo data source until
 * you add the dependency above, then flip DashboardViewModel to call this repository.
 */
public class PetRepository {

    private final FirebaseFirestore firestore;
    private final FirebaseAuth auth;

    public PetRepository() {
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    public interface PetsCallback {
        void onSuccess(List<Pet> pets);
        void onError(Exception e);
    }

    public interface SavePetCallback {
        void onSuccess(Pet savedPet);
        void onError(Exception e);
    }

    private CollectionReference petsCollection() {
        String uid = currentUid();
        return firestore.collection("users").document(uid).collection("pets");
    }

    private String currentUid() {
        if (auth.getCurrentUser() == null) {
            throw new IllegalStateException(
                    "PetRepository used with no signed-in user — call this after your existing login flow completes.");
        }
        return auth.getCurrentUser().getUid();
    }

    public void loadPets(@NonNull PetsCallback callback) {
        petsCollection().get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot snapshot) {
                        List<Pet> pets = new ArrayList<>();
                        snapshot.forEach(doc -> {
                            Pet pet = doc.toObject(Pet.class);
                            pet.setId(doc.getId());
                            pets.add(pet);
                        });
                        callback.onSuccess(pets);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.onError(e);
                    }
                });
    }

    public void addPet(@NonNull Pet pet, @NonNull SavePetCallback callback) {
        DocumentReference ref = petsCollection().document(); // pre-generates an id
        pet.setId(ref.getId());
        ref.set(pet)
                .addOnSuccessListener(unused -> callback.onSuccess(pet))
                .addOnFailureListener(callback::onError);
    }

    public void updatePet(@NonNull Pet pet, @NonNull SavePetCallback callback) {
        if (pet.getId() == null) {
            addPet(pet, callback);
            return;
        }
        petsCollection().document(pet.getId()).set(pet)
                .addOnSuccessListener(unused -> callback.onSuccess(pet))
                .addOnFailureListener(callback::onError);
    }
}
