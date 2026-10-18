package com.example.recipeapp.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Recipe {
    private String id;
    private String title;
    private String imageUrl;
    private int baseServings;
    private int prepTimeMinutes;
    private int cookTimeMinutes;
    private MealType mealType;
    private List<String> tags;
    private List<Ingredient> ingredients;
    private List<RecipeStep> steps;
    private boolean favorite;

    public Recipe(String title) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.baseServings = 4;
        this.prepTimeMinutes = 0;
        this.cookTimeMinutes = 0;
        this.mealType = MealType.DINNER;
        this.tags = new ArrayList<>();
        this.ingredients = new ArrayList<>();
        this.steps = new ArrayList<>();
        this.favorite = false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getBaseServings() {
        return baseServings;
    }

    public void setBaseServings(int baseServings) {
        this.baseServings = baseServings;
    }

    public int getPrepTimeMinutes() {
        return prepTimeMinutes;
    }

    public void setPrepTimeMinutes(int prepTimeMinutes) {
        this.prepTimeMinutes = prepTimeMinutes;
    }

    public int getCookTimeMinutes() {
        return cookTimeMinutes;
    }

    public void setCookTimeMinutes(int cookTimeMinutes) {
        this.cookTimeMinutes = cookTimeMinutes;
    }

    public int getTotalTimeMinutes() {
        return prepTimeMinutes + cookTimeMinutes;
    }

    public MealType getMealType() {
        return mealType;
    }

    public void setMealType(MealType mealType) {
        this.mealType = mealType;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public List<RecipeStep> getSteps() {
        return steps;
    }

    public void setSteps(List<RecipeStep> steps) {
        this.steps = steps;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    /** Returns ingredients scaled to a target serving count. */
    public List<Ingredient> scaledIngredients(int targetServings) {
        List<Ingredient> result = new ArrayList<>();
        if (baseServings <= 0) {
            result.addAll(ingredients);
            return result;
        }
        double factor = (double) targetServings / (double) baseServings;
        for (Ingredient ing : ingredients) {
            result.add(ing.scaledBy(factor));
        }
        return result;
    }
}
