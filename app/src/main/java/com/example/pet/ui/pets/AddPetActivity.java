package com.example.pet.ui.pets;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pet.R;
import com.example.pet.databinding.ActivityAddPetBinding;

/**
 * Standalone Activity for creating a new pet profile.
 *
 * On save, this Activity does NOT write to Firestore itself — it only validates the
 * form and hands the field values back to MainActivity via Intent extras, which then
 * calls DashboardViewModel.addPet(...). That keeps pet persistence in one place
 * (the ViewModel/Repository) instead of duplicating it here and risking the
 * dashboard's pet list going out of sync with what was actually saved.
 */
public class AddPetActivity extends AppCompatActivity {

    public static final String EXTRA_PET_NAME = "extra_pet_name";
    public static final String EXTRA_PET_SPECIES = "extra_pet_species";
    public static final String EXTRA_PET_BREED = "extra_pet_breed";
    public static final String EXTRA_PET_AGE = "extra_pet_age";
    public static final String EXTRA_PET_NOTES = "extra_pet_notes";
    public static final String EXTRA_PET_WEIGHT = "extra_pet_weight";
    public static final String EXTRA_PET_PHOTO_URL = "extra_pet_photo_url";

    private ActivityAddPetBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddPetBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setUpSpeciesDropdown();
        binding.btnAddPetBack.setOnClickListener(v -> finish());
        binding.btnSavePet.setOnClickListener(v -> onSaveClicked());

        preFillData();
    }

    private void preFillData() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_PET_NAME)) {
            binding.inputName.setText(intent.getStringExtra(EXTRA_PET_NAME));
            binding.inputSpecies.setText(intent.getStringExtra(EXTRA_PET_SPECIES), false);
            binding.inputBreed.setText(intent.getStringExtra(EXTRA_PET_BREED));
            binding.inputAge.setText(intent.getStringExtra(EXTRA_PET_AGE));
            binding.inputNotes.setText(intent.getStringExtra(EXTRA_PET_NOTES));
            if (binding.inputWeight != null) {
                binding.inputWeight.setText(intent.getStringExtra(EXTRA_PET_WEIGHT));
            }
        }
    }

    private void setUpSpeciesDropdown() {
        String[] species = getResources().getStringArray(R.array.ps_species_options);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, species);
        binding.inputSpecies.setAdapter(adapter);
    }

    private void onSaveClicked() {
        String name = textOf(binding.inputName);
        String age = textOf(binding.inputAge);
        String species = textOf(binding.inputSpecies);
        String breed = textOf(binding.inputBreed);
        String notes = textOf(binding.inputNotes);
        String weight = binding.inputWeight != null ? textOf(binding.inputWeight) : "";

        boolean valid = true;

        if (name.isEmpty()) {
            binding.inputNameLayout.setError(getString(R.string.ps_error_required));
            valid = false;
        } else {
            binding.inputNameLayout.setError(null);
        }

        if (age.isEmpty()) {
            binding.inputAgeLayout.setError(getString(R.string.ps_error_required));
            valid = false;
        } else {
            binding.inputAgeLayout.setError(null);
        }

        if (species.isEmpty()) {
            binding.inputSpeciesLayout.setError(getString(R.string.ps_error_required));
            valid = false;
        } else {
            binding.inputSpeciesLayout.setError(null);
        }

        if (!valid) {
            return;
        }

        Intent result = new Intent();
        result.putExtra(EXTRA_PET_NAME, name);
        result.putExtra(EXTRA_PET_SPECIES, species);
        result.putExtra(EXTRA_PET_BREED, breed.isEmpty() ? null : breed);
        result.putExtra(EXTRA_PET_AGE, age);
        result.putExtra(EXTRA_PET_NOTES, notes.isEmpty() ? null : notes);
        result.putExtra(EXTRA_PET_WEIGHT, weight.isEmpty() ? null : weight);
        // Retain photoUrl if it was passed in
        if (getIntent() != null && getIntent().hasExtra(EXTRA_PET_PHOTO_URL)) {
            result.putExtra(EXTRA_PET_PHOTO_URL, getIntent().getStringExtra(EXTRA_PET_PHOTO_URL));
        }
        setResult(Activity.RESULT_OK, result);
        finish();
    }

    private String textOf(TextView view) {
        return view.getText() == null ? "" : view.getText().toString().trim();
    }
}
