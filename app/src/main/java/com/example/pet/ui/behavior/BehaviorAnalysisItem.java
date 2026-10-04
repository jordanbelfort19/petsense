package com.example.pet.ui.behavior;

public class BehaviorAnalysisItem {
    private String date;
    private String emoji;
    private String behavior;
    private String confidence;
    private String time;

    public BehaviorAnalysisItem(String date, String emoji, String behavior, String confidence, String time) {
        this.date = date;
        this.emoji = emoji;
        this.behavior = behavior;
        this.confidence = confidence;
        this.time = time;
    }

    public String getDate() { return date; }
    public String getEmoji() { return emoji; }
    public String getBehavior() { return behavior; }
    public String getConfidence() { return confidence; }
    public String getTime() { return time; }
}
