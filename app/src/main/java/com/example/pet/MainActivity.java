package com.example.pet;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.pet.data.Pet;
import com.example.pet.databinding.ActivityMainBinding;
import com.example.pet.ui.behavior.BehaviorActivity;
import com.example.pet.ui.dashboard.DashboardViewModel;
import com.example.pet.ui.health.HealthActivity;
import com.example.pet.ui.insights.InsightsActivity;
import com.example.pet.ui.pets.AddPetActivity;
import com.example.pet.ui.pets.PetSelectorBottomSheet;

/**
 * Hosts the PetSense dashboard.
 * Preserves existing navigation and adds multi-pet selector support.
 */
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private DashboardViewModel viewModel;

    private final ActivityResultLauncher<Intent> addPetLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    this::onAddPetResult
            );

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(DashboardViewModel.class);

        setUpBottomNavigation();
        setUpClickListeners();
        observeViewModel();
    }

    private void setUpBottomNavigation() {
        binding.bottomNavigation.setSelectedItemId(R.id.nav_home);

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                showDashboard();
                return true;

            } else if (id == R.id.nav_health) {
                startActivity(new Intent(this, HealthActivity.class));
                return true;

            } else if (id == R.id.nav_behavior) {
                startActivity(new Intent(this, BehaviorActivity.class));
                return true;

            } else if (id == R.id.nav_insights) {
                startActivity(new Intent(this, InsightsActivity.class));
                return true;

            } else if (id == R.id.nav_profile) {
                openPetProfile();
                return true;
            }

            Toast.makeText(this, "Feature coming soon", Toast.LENGTH_SHORT).show();
            return false;
        });
    }

    private void showDashboard() {
        binding.dashboardScroll.setVisibility(View.VISIBLE);
        binding.placeholderContainer.setVisibility(View.GONE);
    }

    private void showPlaceholder(String title) {
        binding.dashboardScroll.setVisibility(View.GONE);
        binding.placeholderContainer.setVisibility(View.VISIBLE);
        binding.tvPlaceholderTitle.setText(title);
    }

    private void setUpClickListeners() {
        // Existing pet-summary card -> Pet Profile screen.
        binding.cardSelectedPet.setOnClickListener(v -> openPetProfile());

        // Multi-pet selector and add-pet button.
        binding.cardPetSelector.setOnClickListener(v -> openPetSelectorSheet());
        binding.btnAddPet.setOnClickListener(v -> openAddPet());

        // Existing quick actions.
        binding.actionHealthCheck.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, HealthActivity.class)));

        binding.actionBehaviorAnalysis.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, BehaviorActivity.class)));

        binding.actionActivity.setOnClickListener(v ->
                Toast.makeText(
                        this,
                        "TODO: open Activity detail screen",
                        Toast.LENGTH_SHORT
                ).show());

        binding.actionCareHistory.setOnClickListener(v ->
                Toast.makeText(
                        this,
                        "TODO: open Care History screen",
                        Toast.LENGTH_SHORT
                ).show());

        binding.actionProfileHelp.setOnClickListener(v ->
                binding.bottomNavigation.setSelectedItemId(R.id.nav_profile));

        binding.btnViewCareSchedule.setOnClickListener(v ->
                Toast.makeText(
                        this,
                        "TODO: open Care Schedule screen",
                        Toast.LENGTH_SHORT
                ).show());

        binding.btnMenu.setOnClickListener(v ->
                Toast.makeText(
                        this,
                        "TODO: open navigation drawer / menu",
                        Toast.LENGTH_SHORT
                ).show());

        // If you already have a notifications screen, wire its click listener here.
    }

    private final ActivityResultLauncher<Intent> petProfileLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                            // Pet was updated! The simplest way is to extract it and update
                            Intent data = result.getData();
                            Pet updated = new Pet(
                                data.getStringExtra(AddPetActivity.EXTRA_PET_NAME),
                                data.getStringExtra(AddPetActivity.EXTRA_PET_SPECIES),
                                data.getStringExtra(AddPetActivity.EXTRA_PET_BREED),
                                data.getStringExtra(AddPetActivity.EXTRA_PET_AGE),
                                data.getStringExtra(AddPetActivity.EXTRA_PET_NOTES),
                                true,
                                data.getStringExtra(AddPetActivity.EXTRA_PET_WEIGHT),
                                data.getStringExtra(AddPetActivity.EXTRA_PET_PHOTO_URL)
                            );
                            updated.setId(data.getStringExtra("id"));
                            viewModel.updatePet(updated, () -> {});
                        }
                    }
            );

    private void openPetProfile() {
        Pet selected = viewModel.getSelectedPet().getValue();
        if (selected == null) return;

        Intent intent = new Intent(this, PetProfileActivity.class);
        intent.putExtra("id", selected.getId());
        intent.putExtra(AddPetActivity.EXTRA_PET_NAME, selected.getName());
        intent.putExtra(AddPetActivity.EXTRA_PET_SPECIES, selected.getSpecies());
        intent.putExtra(AddPetActivity.EXTRA_PET_BREED, selected.getBreed());
        intent.putExtra(AddPetActivity.EXTRA_PET_AGE, selected.getAge());
        intent.putExtra(AddPetActivity.EXTRA_PET_NOTES, selected.getNotes());
        intent.putExtra(AddPetActivity.EXTRA_PET_WEIGHT, selected.getWeight());
        intent.putExtra(AddPetActivity.EXTRA_PET_PHOTO_URL, selected.getPhotoUrl());

        petProfileLauncher.launch(intent);
    }

    private void openPetSelectorSheet() {
        PetSelectorBottomSheet sheet = PetSelectorBottomSheet.newInstance();
        sheet.setOnAddPetRequestedListener(this::openAddPet);
        sheet.show(getSupportFragmentManager(), "pet_selector");
    }

    private void openAddPet() {
        addPetLauncher.launch(new Intent(this, AddPetActivity.class));
    }

    private void onAddPetResult(ActivityResult result) {
        if (result.getResultCode() != Activity.RESULT_OK
                || result.getData() == null) {
            return;
        }

        Intent data = result.getData();

        String name = data.getStringExtra(AddPetActivity.EXTRA_PET_NAME);
        String species = data.getStringExtra(AddPetActivity.EXTRA_PET_SPECIES);
        String breed = data.getStringExtra(AddPetActivity.EXTRA_PET_BREED);
        String age = data.getStringExtra(AddPetActivity.EXTRA_PET_AGE);
        String notes = data.getStringExtra(AddPetActivity.EXTRA_PET_NOTES);

        String weight = data.getStringExtra(AddPetActivity.EXTRA_PET_WEIGHT);
        String photoUrl = data.getStringExtra(AddPetActivity.EXTRA_PET_PHOTO_URL);

        Pet newPet = new Pet(name, species, breed, age, notes, true, weight, photoUrl);

        viewModel.addPet(newPet, () ->
                Toast.makeText(
                        this,
                        name + " added",
                        Toast.LENGTH_SHORT
                ).show()
        );
    }

    private void observeViewModel() {
        viewModel.getUserName().observe(this, name ->
                binding.tvGreeting.setText(
                        getString(R.string.ps_dashboard_greeting, name)
                )
        );

        viewModel.getPetName().observe(this, petName -> {
            binding.tvGreetingSubtitle.setText(
                    getString(R.string.ps_dashboard_subtitle, petName)
            );

            binding.tvPetName.setText(petName);
            binding.tvPetSelectorName.setText(petName);

            binding.ivPetAvatar.setContentDescription(
                    getString(
                            R.string.ps_cd_pet_card_photo_format,
                            petName
                    )
            );
        });

        viewModel.getPetBreedAge().observe(this, breedAge -> {
            binding.tvPetBreedAge.setText(breedAge);
            binding.tvPetSelectorMeta.setText(breedAge);
        });

        viewModel.getPetIsMale().observe(this, isMale ->
                binding.ivPetGenderIcon.setImageResource(
                        isMale
                                ? R.drawable.ic_ps_gender_male
                                : R.drawable.ic_ps_gender_female
                )
        );

        viewModel.getHealthScore().observe(this, score -> {
            String status = viewModel.getHealthStatus().getValue();

            binding.healthScoreView.setScore(score, status);
            binding.tvHealthScoreValue.setText(String.valueOf(score));
        });

        viewModel.getHealthStatus().observe(this, status -> {
            binding.healthScoreView.setStatusText(status);
            binding.tvHealthScoreStatus.setText(status);
        });

        viewModel.getSleepValue().observe(this, binding.tvSleepValue::setText);
        viewModel.getActivityValue().observe(this, binding.tvActivityValue::setText);
        viewModel.getFeedingValue().observe(this, binding.tvFeedingValue::setText);
        viewModel.getWaterValue().observe(this, binding.tvWaterValue::setText);

        viewModel.getCareReminderDetail().observe(
                this,
                binding.tvPreventiveDetail::setText
        );
    }
}