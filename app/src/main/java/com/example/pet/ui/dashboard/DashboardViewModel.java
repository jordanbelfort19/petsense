package com.example.pet.ui.dashboard;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.pet.data.Pet;
import com.example.pet.data.PetRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds dashboard state and supports switching between pets.
 */
public class DashboardViewModel extends ViewModel {

    private final PetRepository petRepository = new PetRepository();

    private final MutableLiveData<String> userName = new MutableLiveData<>("Sarah");

    // Multi-pet state
    private final MutableLiveData<List<Pet>> pets =
            new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Pet> selectedPet = new MutableLiveData<>();

    // Existing dashboard pet fields, updated when the selected pet changes
    private final MutableLiveData<String> petName = new MutableLiveData<>("Max");
    private final MutableLiveData<String> petBreedAge =
            new MutableLiveData<>("Golden Retriever · 2 Years");
    private final MutableLiveData<Boolean> petIsMale = new MutableLiveData<>(true);

    private final MutableLiveData<Integer> healthScore = new MutableLiveData<>(86);
    private final MutableLiveData<String> healthStatus = new MutableLiveData<>("Good");

    private final MutableLiveData<String> sleepValue = new MutableLiveData<>("8h 20m");
    private final MutableLiveData<String> activityValue = new MutableLiveData<>("2h 45m");
    private final MutableLiveData<String> feedingValue = new MutableLiveData<>("2 / 2 meals");
    private final MutableLiveData<String> waterValue = new MutableLiveData<>("1.8 L");

    private final MutableLiveData<String> careReminderDetail =
            new MutableLiveData<>("Next preventive task · in 3 weeks");

    // Kept from your original ViewModel
    private final MutableLiveData<String> healthInsight =
            new MutableLiveData<>(
                    "Max is maintaining a healthy activity and sleep pattern today."
            );

    public DashboardViewModel() {
        // Demo pet for the dashboard before/without Firestore data
        Pet demoPet = new Pet(
                "Max", "Dog", "Golden Retriever", "2 Years", null, true, "24 kg", null
        );

        List<Pet> initialPets = new ArrayList<>();
        initialPets.add(demoPet);
        pets.setValue(initialPets);
        selectPet(demoPet);

        refreshPetsFromRepository();
    }

    private void refreshPetsFromRepository() {
        try {
            petRepository.loadPets(new PetRepository.PetsCallback() {
                @Override
                public void onSuccess(List<Pet> loadedPets) {
                    if (loadedPets == null || loadedPets.isEmpty()) {
                        return;
                    }

                    pets.setValue(loadedPets);
                    selectPet(loadedPets.get(0));
                }

                @Override
                public void onError(Exception e) {
                    // Keep the demo pet visible if loading fails.
                }
            });
        } catch (IllegalStateException notSignedIn) {
            // Keep the demo pet visible if no user is signed in.
        }
    }

    // Getters
    public LiveData<String> getUserName() { return userName; }

    public LiveData<List<Pet>> getPets() { return pets; }
    public LiveData<Pet> getSelectedPet() { return selectedPet; }

    public LiveData<String> getPetName() { return petName; }
    public LiveData<String> getPetBreedAge() { return petBreedAge; }
    public LiveData<Boolean> getPetIsMale() { return petIsMale; }

    public LiveData<Integer> getHealthScore() { return healthScore; }
    public LiveData<String> getHealthStatus() { return healthStatus; }

    public LiveData<String> getSleepValue() { return sleepValue; }
    public LiveData<String> getActivityValue() { return activityValue; }
    public LiveData<String> getFeedingValue() { return feedingValue; }
    public LiveData<String> getWaterValue() { return waterValue; }

    public LiveData<String> getCareReminderDetail() {
        return careReminderDetail;
    }

    public LiveData<String> getHealthInsight() {
        return healthInsight;
    }

    // Actions

    /** Switches the active pet. */
    public void selectPet(@NonNull Pet pet) {
        selectedPet.setValue(pet);
        petName.setValue(pet.getName());
        petBreedAge.setValue(pet.breedAgeLine());
        petIsMale.setValue(pet.isMale());

        // TODO: Load health/activity/sleep/feeding values for this pet
        // when those values are stored separately per pet.
    }

    /** Saves a new pet, adds it to the list, and selects it. */
    public void addPet(@NonNull Pet pet, @NonNull Runnable onDone) {
        try {
            petRepository.addPet(pet, new PetRepository.SavePetCallback() {
                @Override
                public void onSuccess(Pet savedPet) {
                    List<Pet> current = pets.getValue() != null
                            ? pets.getValue()
                            : new ArrayList<>();

                    List<Pet> updated = new ArrayList<>(current);
                    updated.add(savedPet);
                    pets.setValue(updated);

                    selectPet(savedPet);
                    onDone.run();
                }

                @Override
                public void onError(Exception e) {
                    // Local fallback if Firestore saving fails.
                    addPetLocallyOnly(pet);
                    onDone.run();
                }
            });
        } catch (IllegalStateException notSignedIn) {
            addPetLocallyOnly(pet);
            onDone.run();
        }
    }

    private void addPetLocallyOnly(@NonNull Pet pet) {
        List<Pet> current = pets.getValue() != null
                ? pets.getValue()
                : new ArrayList<>();

        List<Pet> updated = new ArrayList<>(current);
        updated.add(pet);
        pets.setValue(updated);

        selectPet(pet);
    }

    public void updatePet(@NonNull Pet pet, @NonNull Runnable onDone) {
        try {
            petRepository.updatePet(pet, new PetRepository.SavePetCallback() {
                @Override
                public void onSuccess(Pet savedPet) {
                    updatePetLocallyOnly(savedPet);
                    onDone.run();
                }

                @Override
                public void onError(Exception e) {
                    updatePetLocallyOnly(pet);
                    onDone.run();
                }
            });
        } catch (IllegalStateException notSignedIn) {
            updatePetLocallyOnly(pet);
            onDone.run();
        }
    }

    public void updatePetLocallyOnly(@NonNull Pet pet) {
        List<Pet> current = pets.getValue() != null ? pets.getValue() : new ArrayList<>();
        List<Pet> updated = new ArrayList<>();
        for (Pet p : current) {
            if (p.getId() != null && p.getId().equals(pet.getId())) {
                updated.add(pet);
            } else if (p.getName().equals(pet.getName())) { // Fallback if no ID
                updated.add(pet);
            } else {
                updated.add(p);
            }
        }
        pets.setValue(updated);
        selectPet(pet);
    }

    public void updateHealthScore(int score, @NonNull String status) {
        healthScore.setValue(score);
        healthStatus.setValue(status);
    }
}