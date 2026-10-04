package com.example.pet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsetsController;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pet.databinding.ActivityPetProfileBinding;
import com.example.pet.ui.pets.AddPetActivity;

/**
 * PetProfileActivity — Pet Profile Screen
 *
 * Displays a rich pet profile with:
 *  - Pastel header + overlapping circular pet photo
 *  - Identity card (name, breed, gender/age/weight stats)
 *  - About section with "Read more" toggle
 *  - Health Snapshot (4 tiles: Health Score, Activity, Sleep, Last Weight)
 *  - Edit Pet Profile (primary CTA) and Manage Health Records (secondary)
 *  - BottomNavigationView with Profile tab selected
 *
 * NOTE: Firebase, camera, and AI integration are intentionally excluded.
 * Replace the placeholder pet image by loading from Firebase Storage using Glide:
 *   Glide.with(this).load(firebaseImageUrl)
 *        .circleCrop()
 *        .placeholder(R.drawable.ic_pet_profile_placeholder)
 *        .into(binding.imgPet);
 */
public class PetProfileActivity extends AppCompatActivity {

    private ActivityPetProfileBinding binding;

    // Tracks whether the "About" section is expanded
    private boolean isAboutExpanded = false;

    private ActivityResultLauncher<Intent> editPetLauncher = registerForActivityResult(
            new androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    // Update our own intent so when we finish, we send it back to MainActivity
                    getIntent().putExtras(data.getExtras());
                    loadPetData();
                }
            });

    private ActivityResultLauncher<String> imagePickerLauncher = registerForActivityResult(
            new androidx.activity.result.contract.ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    getIntent().putExtra(com.example.pet.ui.pets.AddPetActivity.EXTRA_PET_PHOTO_URL, uri.toString());
                    binding.imgPet.setImageURI(uri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable edge-to-edge so the pastel header extends under the status bar
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        // Inflate layout via View Binding
        binding = ActivityPetProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Light status bar icons (dark icons on pastel background)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            getWindow().getInsetsController()
                    .setSystemBarsAppearance(
                            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS);
        }

        // Apply window insets so the header top accounts for the status bar height
        ViewCompat.setOnApplyWindowInsetsListener(binding.frameHeader, (view, insets) -> {
            int statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
            view.setPadding(0, statusBarHeight, 0, 0);
            return insets;
        });

        loadPetData();
        setupClickListeners();
        setupBottomNavigation();
    }

    private void loadPetData() {
        Intent intent = getIntent();
        if (intent == null) return;
        
        String name = intent.getStringExtra(com.example.pet.ui.pets.AddPetActivity.EXTRA_PET_NAME);
        String breed = intent.getStringExtra(com.example.pet.ui.pets.AddPetActivity.EXTRA_PET_BREED);
        String age = intent.getStringExtra(com.example.pet.ui.pets.AddPetActivity.EXTRA_PET_AGE);
        String weight = intent.getStringExtra(com.example.pet.ui.pets.AddPetActivity.EXTRA_PET_WEIGHT);
        String notes = intent.getStringExtra(com.example.pet.ui.pets.AddPetActivity.EXTRA_PET_NOTES);
        String photoUrl = intent.getStringExtra(com.example.pet.ui.pets.AddPetActivity.EXTRA_PET_PHOTO_URL);
        
        binding.tvPetName.setText(name != null ? name : "Unknown");
        binding.tvBreed.setText(breed != null ? breed : "");
        binding.tvAgeValue.setText(age != null ? age : "--");
        binding.tvWeightValue.setText(weight != null ? weight : "--");
        binding.tvAboutText.setText(notes != null && !notes.isEmpty() ? notes : "No details provided.");
        
        if (photoUrl != null && !photoUrl.isEmpty()) {
            binding.imgPet.setImageURI(android.net.Uri.parse(photoUrl));
        }
    }

    // ─────────────────────────────────────────────────────────────
    //  Click Listeners
    // ─────────────────────────────────────────────────────────────

    private void setupClickListeners() {

        // Back button — return updated data to MainActivity
        binding.btnBack.setOnClickListener(v -> onBackPressed());

        binding.imgPet.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));
        binding.imgEditPhoto.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));

        // More / options — placeholder for future popup menu
        binding.btnMore.setOnClickListener(v -> {
            // TODO: show PopupMenu with options (Share profile, Settings, etc.)
        });

        // "Read more" toggle for the About section
        binding.tvReadMore.setOnClickListener(v -> toggleAboutExpanded());

        // Edit Pet Profile — launch AddPetActivity with current data
        binding.btnEditProfile.setOnClickListener(v -> {
            Intent editIntent = new Intent(this, AddPetActivity.class);
            if (getIntent() != null) {
                editIntent.putExtras(getIntent());
            }
            editPetLauncher.launch(editIntent);
        });

        // Manage Health Records — secondary CTA
        binding.btnManageHealth.setOnClickListener(v -> {
            // TODO: navigate to HealthRecordsActivity
        });
    }

    @Override
    public void onBackPressed() {
        if (getIntent() != null) {
            setResult(RESULT_OK, getIntent());
        }
        super.onBackPressed();
    }

    /**
     * Toggles the "About Buddy" text between collapsed (3 lines) and expanded (all lines).
     */
    private void toggleAboutExpanded() {
        isAboutExpanded = !isAboutExpanded;
        if (isAboutExpanded) {
            binding.tvAboutText.setMaxLines(Integer.MAX_VALUE);
            binding.tvReadMore.setText(getString(R.string.read_more));
            binding.tvReadMore.setVisibility(View.GONE);
        } else {
            binding.tvAboutText.setMaxLines(3);
            binding.tvReadMore.setVisibility(View.VISIBLE);
        }
    }

    // ─────────────────────────────────────────────────────────────
    //  Bottom Navigation
    // ─────────────────────────────────────────────────────────────

    private void setupBottomNavigation() {
        binding.bottomNavigationView.setSelectedItemId(R.id.nav_profile);
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                onBackPressed(); // Go back to Home
                return true;
            } else if (itemId == R.id.nav_health) {
                onBackPressed();
                startActivity(new Intent(this, com.example.pet.ui.health.HealthActivity.class));
                return true;
            } else if (itemId == R.id.nav_behavior) {
                onBackPressed();
                startActivity(new Intent(this, com.example.pet.ui.behavior.BehaviorActivity.class));
                return true;
            } else if (itemId == R.id.nav_insights) {
                onBackPressed();
                startActivity(new Intent(this, com.example.pet.ui.insights.InsightsActivity.class));
                return true;
            } else if (itemId == R.id.nav_profile) {
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Release View Binding reference to avoid memory leaks
        binding = null;
    }
}
