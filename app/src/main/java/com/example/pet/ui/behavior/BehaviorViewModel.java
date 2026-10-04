package com.example.pet.ui.behavior;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BehaviorViewModel extends ViewModel {

    public enum AnalysisState { IDLE, ANALYZING, RESULT }

    private final MutableLiveData<AnalysisState> state = new MutableLiveData<>(AnalysisState.IDLE);
    private final MutableLiveData<List<BehaviorAnalysisItem>> history = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<String> petName = new MutableLiveData<>("Max");
    private final MutableLiveData<String> petBreedAge = new MutableLiveData<>("Golden Retriever • 2 Years");

    // UI Result LiveData fields
    private final MutableLiveData<String> currentBehavior = new MutableLiveData<>();
    private final MutableLiveData<String> currentEmoji = new MutableLiveData<>();
    private final MutableLiveData<String> currentConfidence = new MutableLiveData<>();
    private final MutableLiveData<String> motion = new MutableLiveData<>();
    private final MutableLiveData<String> posture = new MutableLiveData<>();
    private final MutableLiveData<String> energy = new MutableLiveData<>();
    private final MutableLiveData<String> aiInsight = new MutableLiveData<>();
    private final MutableLiveData<String> actionSuggestion = new MutableLiveData<>();

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final OkHttpClient client = new OkHttpClient();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    // IMPORTANT: Use "10.0.2.2" for Emulator, or your PC's IP (e.g. "192.168.1.5") for physical device
    private final String PYTHON_SERVER_URL = "http://10.0.2.2:5000/analyze";

    // --- GETTERS ---
    public LiveData<AnalysisState> getState() { return state; }
    public LiveData<List<BehaviorAnalysisItem>> getHistory() { return history; }
    public LiveData<String> getPetName() { return petName; }
    public LiveData<String> getPetBreedAge() { return petBreedAge; }

    public LiveData<String> getCurrentBehavior() { return currentBehavior; }
    public LiveData<String> getCurrentEmoji() { return currentEmoji; }
    public LiveData<String> getCurrentConfidence() { return currentConfidence; }
    public LiveData<String> getMotion() { return motion; }
    public LiveData<String> getPosture() { return posture; }
    public LiveData<String> getEnergy() { return energy; }
    public LiveData<String> getAiInsight() { return aiInsight; }
    public LiveData<String> getActionSuggestion() { return actionSuggestion; }

    public void startRealAnalysis(Bitmap bitmap) {
        state.setValue(AnalysisState.ANALYZING);

        executor.execute(() -> {
            try {
                // 1. Convert Bitmap to byte array
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
                byte[] byteArray = stream.toByteArray();

                // 2. Build OkHttp request to Python server
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("image", "frame.jpg",
                                RequestBody.create(byteArray, MediaType.parse("image/jpeg")))
                        .build();

                Request request = new Request.Builder()
                        .url(PYTHON_SERVER_URL)
                        .post(requestBody)
                        .build();

                // 3. Execute network call
                Response response = client.newCall(request).execute();
                if (response.isSuccessful() && response.body() != null) {
                    String responseData = response.body().string();
                    JSONObject json = new JSONObject(responseData);
                    String behaviorResult = json.getString("behavior");
                    String confidence = json.optString("confidence", "95%");

                    // 4. Update UI and Save to Firestore on Main Thread
                    mainHandler.post(() -> {
                        populateResultData(behaviorResult, confidence);
                        saveToFirestore(behaviorResult, confidence);
                        state.setValue(AnalysisState.RESULT);
                    });
                } else {
                    mainHandler.post(() -> {
                        populateResultData("Error connecting to AI", "0%");
                        state.setValue(AnalysisState.RESULT);
                    });
                }
            } catch (Exception e) {
                Log.e("BehaviorViewModel", "Network error", e);
                mainHandler.post(() -> {
                    populateResultData("Network Failure", "0%");
                    state.setValue(AnalysisState.RESULT);
                });
            }
        });
    }

    private void populateResultData(String behavior, String confidence) {
        currentBehavior.setValue(behavior);
        currentConfidence.setValue(confidence);
        String name = petName.getValue();

        String lowerBehavior = behavior.toLowerCase();
        if (lowerBehavior.contains("rest") || lowerBehavior.contains("ly")) {
            currentEmoji.setValue("😴");
            motion.setValue("Low");
            posture.setValue("Relaxed");
            energy.setValue("Low");
            aiInsight.setValue(name + " appears deeply relaxed. This low-intensity state is great for recovery.");
            actionSuggestion.setValue("Let them rest. Avoid loud noises.");
        } else if (lowerBehavior.contains("walk") || lowerBehavior.contains("mov")) {
            currentEmoji.setValue("🐾");
            motion.setValue("Moderate");
            posture.setValue("Upright");
            energy.setValue("Medium");
            aiInsight.setValue(name + " is showing moderate activity. Posture indicates a calm but alert state.");
            actionSuggestion.setValue("Perfect time for a short training session.");
        } else if (lowerBehavior.contains("run")) {
            currentEmoji.setValue("⚡");
            motion.setValue("High");
            posture.setValue("Active");
            energy.setValue("High");
            aiInsight.setValue(name + " is highly energetic! Vigorous movement detected.");
            actionSuggestion.setValue("Ensure they have access to plenty of water.");
        } else if (lowerBehavior.contains("eat") || lowerBehavior.contains("drink")) {
            currentEmoji.setValue("🦴");
            motion.setValue("Low");
            posture.setValue("Engaged");
            energy.setValue("Low");
            aiInsight.setValue(name + " is currently focused on eating or drinking.");
            actionSuggestion.setValue("Let them finish their meal in peace.");
        } else {
            currentEmoji.setValue("😌");
            motion.setValue("Moderate");
            posture.setValue("Relaxed");
            energy.setValue("Moderate");
            aiInsight.setValue(name + " is displaying calm behavior.");
            actionSuggestion.setValue("Keep monitoring their daily routine.");
        }
    }

    private void saveToFirestore(String behavior, String confidence) {
        String timestamp = new SimpleDateFormat("h:mm a", Locale.getDefault()).format(new Date());

        Map<String, Object> record = new HashMap<>();
        record.put("petName", petName.getValue());
        record.put("behavior", behavior);
        record.put("confidence", confidence);
        record.put("timestamp", timestamp);

        db.collection("behavior_history").add(record)
                .addOnSuccessListener(documentReference -> addCurrentResultToHistory(behavior, confidence, timestamp))
                .addOnFailureListener(e -> Log.e("Firestore", "Error saving record", e));
    }

    private void addCurrentResultToHistory(String behavior, String confidence, String time) {
        List<BehaviorAnalysisItem> currentList = history.getValue();
        if (currentList == null) currentList = new ArrayList<>();
        List<BehaviorAnalysisItem> newList = new ArrayList<>(currentList);

        String emoji = currentEmoji.getValue() != null ? currentEmoji.getValue() : "🐾";

        newList.add(0, new BehaviorAnalysisItem("Just Now", emoji, behavior, confidence, time));
        history.setValue(newList);
    }

    public void resetAnalysis() {
        state.setValue(AnalysisState.IDLE);
    }

    public void loadInitialData() {
        List<BehaviorAnalysisItem> mockHistory = new ArrayList<>();
        mockHistory.add(new BehaviorAnalysisItem("Yesterday", "🐾", "Active", "91%", "6:15 PM"));
        history.setValue(mockHistory);
    }
}