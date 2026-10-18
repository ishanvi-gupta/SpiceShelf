package com.example.recipeapp.model;

import java.util.UUID;

public class RecipeStep {
    private String id;
    private String text;
    private Integer timerSeconds; // null means no timer for this step

    public RecipeStep(String text) {
        this(text, null);
    }

    public RecipeStep(String text, Integer timerSeconds) {
        this.id = UUID.randomUUID().toString();
        this.text = text;
        this.timerSeconds = timerSeconds;
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getTimerSeconds() {
        return timerSeconds;
    }

    public void setTimerSeconds(Integer timerSeconds) {
        this.timerSeconds = timerSeconds;
    }

    public boolean hasTimer() {
        return timerSeconds != null && timerSeconds > 0;
    }
}
