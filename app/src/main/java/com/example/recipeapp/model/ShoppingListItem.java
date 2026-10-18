package com.example.recipeapp.model;

public class ShoppingListItem {
    private String name;
    private double quantity;
    private String unit;
    private boolean checked;

    public ShoppingListItem(String name, double quantity, String unit) {
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.checked = false;
    }

    public String getName() {
        return name;
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

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public void addQuantity(double amount) {
        this.quantity += amount;
    }
}
