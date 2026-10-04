package com.example.pet.ui.health;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.pet.MainActivity;
import com.example.pet.R;
import com.example.pet.databinding.ActivityHealthBinding;
import com.example.pet.ui.behavior.BehaviorActivity;
import com.example.pet.ui.dashboard.DashboardViewModel;
import com.example.pet.ui.insights.InsightsActivity;

public class HealthActivity extends AppCompatActivity {

    private ActivityHealthBinding binding;
    private DashboardViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHealthBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Reusing the existing DashboardViewModel as it perfectly models the health state requested
        viewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        
        setupViews();
        setupBottomNavigation();
        observeViewModel();
    }

    private void setupViews() {
        binding.btnBack.setOnClickListener(v -> finish());
        
        binding.btnRefresh.setOnClickListener(v -> 
            Toast.makeText(this, "Refreshing health data...", Toast.LENGTH_SHORT).show());
            
        binding.btnInfo.setOnClickListener(v -> 
            Toast.makeText(this, "About Health Center", Toast.LENGTH_SHORT).show());
            
        binding.btnDateSelector.setOnClickListener(v -> 
            Toast.makeText(this, "Select Date", Toast.LENGTH_SHORT).show());
            
        binding.btnViewDetails.setOnClickListener(v -> 
            Toast.makeText(this, "Detailed breakdown coming soon", Toast.LENGTH_SHORT).show());
    }

    private void setupBottomNavigation() {
        binding.bottomNavigation.setSelectedItemId(R.id.nav_health);

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            AppCompatActivity currentActivity = this;

            if (id == R.id.nav_home) {
                if (currentActivity instanceof MainActivity) {
                    return true;
                }
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                return true;

            } else if (id == R.id.nav_health) {
                if (currentActivity instanceof HealthActivity) {
                    return true;
                }
                startActivity(new Intent(this, HealthActivity.class));
                finish();
                return true;

            } else if (id == R.id.nav_behavior) {
                if (currentActivity instanceof BehaviorActivity) {
                    return true;
                }
                startActivity(new Intent(this, BehaviorActivity.class));
                finish();
                return true;

            } else if (id == R.id.nav_insights) {
                if (currentActivity instanceof InsightsActivity) {
                    return true;
                }
                startActivity(new Intent(this, InsightsActivity.class));
                finish();
                return true;

            } else {
                Toast.makeText(this, "Feature coming soon", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }
    private void observeViewModel() {
        viewModel.getPetName().observe(this, name -> binding.tvHeaderPetName.setText(name));
        viewModel.getPetBreedAge().observe(this, details -> binding.tvHeaderPetDetails.setText(details));

        viewModel.getHealthScore().observe(this, score -> {
            String status = viewModel.getHealthStatus().getValue();
            binding.healthScoreView.setScore(score, status);
        });
        
        viewModel.getHealthStatus().observe(this, status -> binding.healthScoreView.setStatusText(status));

        viewModel.getActivityValue().observe(this, value -> binding.tvActivityValue.setText(value));
        viewModel.getSleepValue().observe(this, value -> binding.tvSleepValue.setText(value));
        viewModel.getFeedingValue().observe(this, value -> binding.tvFeedingValue.setText(value));
        
        viewModel.getHealthInsight().observe(this, insight -> binding.tvHealthInsight.setText(insight));
    }
}
