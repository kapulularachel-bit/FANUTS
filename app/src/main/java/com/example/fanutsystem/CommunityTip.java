package com.example.fanutsystem;

public class CommunityTip {
    private String englishText;
    private String chichewaText;
    private String category;

    public CommunityTip(String englishText, String chichewaText, String category) {
        this.englishText = englishText;
        this.chichewaText = chichewaText;
        this.category = category;
    }

    public String getEnglishText() { return englishText; }
    public String getChichewaText() { return chichewaText; }
    public String getCategory() { return category; }
}
