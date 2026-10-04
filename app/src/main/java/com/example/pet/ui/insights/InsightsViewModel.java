package com.example.pet.ui.insights;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class InsightsViewModel extends ViewModel {
    private final MutableLiveData<String> petName = new MutableLiveData<>("Max");
    private final MutableLiveData<String> petDetails = new MutableLiveData<>("Golden Retriever • 2 Years");
    
    private final MutableLiveData<String> aiInsight = new MutableLiveData<>(
            "Max has been more active this week, with activity increasing during the evening."
    );
    private final MutableLiveData<String> aiInsightContext = new MutableLiveData<>(
            "Based on recent activity, sleep and behavior data."
    );
    
    private final MutableLiveData<String> activityMetric = new MutableLiveData<>("+12%");
    private final MutableLiveData<String> activityDesc = new MutableLiveData<>("More active");
    
    private final MutableLiveData<String> sleepMetric = new MutableLiveData<>("Stable");
    private final MutableLiveData<String> sleepDesc = new MutableLiveData<>("Healthy pattern");
    
    private final MutableLiveData<String> behaviorMetric = new MutableLiveData<>("Mostly Calm");
    private final MutableLiveData<String> behaviorDesc = new MutableLiveData<>("4 of 7 days");
    
    private final MutableLiveData<String> patternTitle = new MutableLiveData<>("🌙 Evening Activity");
    private final MutableLiveData<String> patternDesc = new MutableLiveData<>("Max is usually most active between 6 PM and 8 PM.");
    
    private final MutableLiveData<String> recommendation = new MutableLiveData<>("Try a short play session around his usual evening activity period.");

    public LiveData<String> getPetName() { return petName; }
    public LiveData<String> getPetDetails() { return petDetails; }
    public LiveData<String> getAiInsight() { return aiInsight; }
    public LiveData<String> getAiInsightContext() { return aiInsightContext; }
    public LiveData<String> getActivityMetric() { return activityMetric; }
    public LiveData<String> getActivityDesc() { return activityDesc; }
    public LiveData<String> getSleepMetric() { return sleepMetric; }
    public LiveData<String> getSleepDesc() { return sleepDesc; }
    public LiveData<String> getBehaviorMetric() { return behaviorMetric; }
    public LiveData<String> getBehaviorDesc() { return behaviorDesc; }
    public LiveData<String> getPatternTitle() { return patternTitle; }
    public LiveData<String> getPatternDesc() { return patternDesc; }
    public LiveData<String> getRecommendation() { return recommendation; }
}
