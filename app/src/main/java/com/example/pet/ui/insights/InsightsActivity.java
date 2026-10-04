package com.example.pet.ui.insights;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.pet.MainActivity;
import com.example.pet.R;
import com.example.pet.databinding.ActivityInsightsBinding;
import com.example.pet.ui.behavior.BehaviorActivity;
import com.example.pet.ui.health.HealthActivity;

public class InsightsActivity extends AppCompatActivity {

    private ActivityInsightsBinding binding;
    private InsightsViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityInsightsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this)
                .get(InsightsViewModel.class);

        setupClickListeners();
        setupBottomNavigation();
        observeViewModel();
    }

    private void setupClickListeners() {
        binding.btnBack.setOnClickListener(v -> finish());
    }

    private void setupBottomNavigation() {
        binding.bottomNavigation.setSelectedItemId(R.id.nav_insights);
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
        viewModel.getPetName().observe(this, name -> {
            if (name != null) {
                binding.tvHeroTitle.setText("Personalized\ninsights for " + name);
            }
        });

        viewModel.getPetDetails().observe(this, details -> {
            if (details != null) {
                binding.tvHeroSubtitle.setText(details);
            }
        });

        viewModel.getAiInsight().observe(this, text -> {
            if (text != null) {
                binding.tvAiInsightText.setText(text);
            }
        });

        viewModel.getAiInsightContext().observe(this, context -> {
            if (context != null) {
                binding.tvAiInsightContext.setText(context);
            }
        });

        viewModel.getActivityMetric().observe(this, value -> {
            if (value != null) {
                binding.tvThisWeekActivityValue.setText(value);
            }
        });

        viewModel.getActivityDesc().observe(this, desc -> {
            if (desc != null) {
                binding.tvThisWeekActivityDesc.setText(desc);
            }
        });

        viewModel.getSleepMetric().observe(this, value -> {
            if (value != null) {
                binding.tvThisWeekSleepValue.setText(value);
            }
        });

        viewModel.getSleepDesc().observe(this, desc -> {
            if (desc != null) {
                binding.tvThisWeekSleepDesc.setText(desc);
            }
        });

        viewModel.getBehaviorMetric().observe(this, value -> {
            if (value != null) {
                binding.tvThisWeekBehaviorValue.setText(value);
            }
        });

        viewModel.getBehaviorDesc().observe(this, desc -> {
            if (desc != null) {
                binding.tvThisWeekBehaviorDesc.setText(desc);
            }
        });

        viewModel.getPatternTitle().observe(this, title -> {
            if (title != null) {
                binding.tvPatternTitle.setText(title);
            }
        });

        viewModel.getPatternDesc().observe(this, desc -> {
            if (desc != null) {
                binding.tvPatternDesc.setText(desc);
            }
        });

        viewModel.getRecommendation().observe(this, recommendation -> {
            if (recommendation != null) {
                binding.tvRecommendation.setText(recommendation);
            }
        });
    }
}