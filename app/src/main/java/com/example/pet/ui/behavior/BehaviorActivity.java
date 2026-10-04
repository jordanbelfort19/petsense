package com.example.pet.ui.behavior;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.pet.MainActivity;
import com.example.pet.R;
import com.example.pet.databinding.BehaviorBinding;
import com.example.pet.ui.health.HealthActivity;
import com.example.pet.ui.insights.InsightsActivity;
import com.google.common.util.concurrent.ListenableFuture;

public class BehaviorActivity extends AppCompatActivity {

    private BehaviorBinding binding;
    private BehaviorViewModel viewModel;
    private BehaviorHistoryAdapter adapter;

    private static final int CAMERA_PERMISSION_CODE = 100;
    private boolean isCameraActive = false;

    // Launcher for selecting a video from the gallery
    private ActivityResultLauncher<Intent> videoPickerLauncher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = BehaviorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(BehaviorViewModel.class);

        setupVideoPicker();
        setupViews();
        setupBottomNavigation();
        observeViewModel();

        // Load mock history for demonstration
        viewModel.loadInitialData();
    }

    private void setupVideoPicker() {
        videoPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri videoUri = result.getData().getData();
                        extractFrameAndAnalyze(videoUri);
                    }
                }
        );
    }

    private void setupViews() {
        // Toolbar
        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnMore.setOnClickListener(v ->
                Toast.makeText(this, "More options", Toast.LENGTH_SHORT).show());

        // CAMERA BUTTON LOGIC
        binding.btnStartCamera.setOnClickListener(v -> {
            if (!isCameraActive) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    startCamera();
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
                }
            } else {
                Bitmap frame = binding.viewFinder.getBitmap();
                if (frame != null) {
                    viewModel.startRealAnalysis(frame);
                } else {
                    Toast.makeText(this, "Failed to capture image", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // VIDEO UPLOAD BUTTON LOGIC
        binding.btnUploadVideo.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setDataAndType(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, "video/*");
            videoPickerLauncher.launch(intent);
        });

        // Reset Logic
        binding.btnResetAnalysis.setOnClickListener(v -> {
            viewModel.resetAnalysis();
            resetCameraUI();
        });

        // RecyclerView
        adapter = new BehaviorHistoryAdapter();
        binding.rvRecentAnalyses.setLayoutManager(new LinearLayoutManager(this));
        binding.rvRecentAnalyses.setAdapter(adapter);
    }

    // Extracts a single frame from the uploaded video to send to the AI
    private void extractFrameAndAnalyze(Uri videoUri) {
        try {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(this, videoUri);

            // Extract frame at 1 second mark (1,000,000 microseconds) to avoid blank starting frames
            Bitmap frame = retriever.getFrameAtTime(1000000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
            retriever.release();

            if (frame != null) {
                if (isCameraActive) {
                    resetCameraUI();
                }
                viewModel.startRealAnalysis(frame);
            } else {
                Toast.makeText(this, "Could not extract frame from video", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("VideoAnalysis", "Error reading video", e);
            Toast.makeText(this, "Error reading video file", Toast.LENGTH_SHORT).show();
        }
    }

    // --- CAMERAX INTEGRATION ---

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                Toast.makeText(this, "Camera permission is required.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(binding.viewFinder.getSurfaceProvider());

                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(this, cameraSelector, preview);

                binding.cameraPlaceholder.setVisibility(View.GONE);
                binding.viewFinder.setVisibility(View.VISIBLE);
                binding.btnStartCamera.setText("Capture & Analyze");
                isCameraActive = true;

            } catch (Exception e) {
                Log.e("CameraX", "Binding failed", e);
                Toast.makeText(this, "Failed to start camera.", Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void resetCameraUI() {
        isCameraActive = false;
        binding.viewFinder.setVisibility(View.GONE);
        binding.cameraPlaceholder.setVisibility(View.VISIBLE);
        binding.btnStartCamera.setText("Start Camera Analysis");
    }

    // ---------------------------

    private void setupBottomNavigation() {
        binding.bottomNavigation.setSelectedItemId(R.id.nav_behavior);
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            AppCompatActivity currentActivity = this;

            if (id == R.id.nav_home) {
                if (currentActivity instanceof MainActivity) return true;
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                return true;

            } else if (id == R.id.nav_health) {
                if (currentActivity instanceof HealthActivity) return true;
                startActivity(new Intent(this, HealthActivity.class));
                finish();
                return true;

            } else if (id == R.id.nav_behavior) {
                if (currentActivity instanceof BehaviorActivity) return true;
                startActivity(new Intent(this, BehaviorActivity.class));
                finish();
                return true;

            } else if (id == R.id.nav_insights) {
                if (currentActivity instanceof InsightsActivity) return true;
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
            binding.tvPetContextName.setText(name);
            binding.tvBehaviorMainTitle.setText("Understand " + name + " better");
            binding.tvProgressTitle.setText("Analyzing " + name + "...");
            binding.tvActionSuggestionTitle.setText("Suggested for " + name);
        });

        viewModel.getPetBreedAge().observe(this, binding.tvPetContextDetails::setText);

        // Bind the new Result Data to the UI
        viewModel.getCurrentBehavior().observe(this, binding.tvResultBehavior::setText);
        viewModel.getCurrentEmoji().observe(this, binding.tvResultEmoji::setText);
        viewModel.getCurrentConfidence().observe(this, binding.tvResultConfidence::setText);
        viewModel.getMotion().observe(this, binding.tvIndicatorMotion::setText);
        viewModel.getPosture().observe(this, binding.tvIndicatorPosture::setText);
        viewModel.getEnergy().observe(this, binding.tvIndicatorEnergy::setText);
        viewModel.getAiInsight().observe(this, text -> {
            binding.tvAiInsight.setText(text);
            binding.tvResultExplanation.setText(text); // Also put the insight in the green card
        });
        viewModel.getActionSuggestion().observe(this, binding.tvActionSuggestionDesc::setText);

        viewModel.getState().observe(this, state -> {
            binding.layoutInputState.setVisibility(state == BehaviorViewModel.AnalysisState.IDLE ? View.VISIBLE : View.GONE);
            binding.layoutProgressState.setVisibility(state == BehaviorViewModel.AnalysisState.ANALYZING ? View.VISIBLE : View.GONE);
            binding.layoutResultState.setVisibility(state == BehaviorViewModel.AnalysisState.RESULT ? View.VISIBLE : View.GONE);

            binding.behaviorScroll.smoothScrollTo(0, 0);
        });

        viewModel.getHistory().observe(this, history -> {
            if (history == null || history.isEmpty()) {
                binding.layoutHistoryEmpty.setVisibility(View.VISIBLE);
                binding.rvRecentAnalyses.setVisibility(View.GONE);
            } else {
                binding.layoutHistoryEmpty.setVisibility(View.GONE);
                binding.rvRecentAnalyses.setVisibility(View.VISIBLE);
                adapter.setItems(history);
            }
        });
    }
}