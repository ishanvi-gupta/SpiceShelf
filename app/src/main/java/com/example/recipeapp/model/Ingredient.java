package com.example.recipeapp.model;

import java.util.UUID;

public class Ingredient {
    private String id;
    private String name;
    private double quantity;
    private String unit; // e.g. "g", "cup", "tsp", "" for whole items

    public Ingredient(String name, double quantity, String unit) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    /** Returns a copy scaled by the given factor (used for serving scaling). */
    public Ingredient scaledBy(double factor) {
        return new Ingredient(name, quantity * factor, unit);
    }
}
