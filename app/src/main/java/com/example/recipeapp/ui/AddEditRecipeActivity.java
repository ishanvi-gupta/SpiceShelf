package com.example.recipeapp.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.recipeapp.R;
import com.example.recipeapp.data.RecipeRepository;
import com.example.recipeapp.model.Ingredient;
import com.example.recipeapp.model.Recipe;
import com.example.recipeapp.model.RecipeStep;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

public class AddEditRecipeActivity extends AppCompatActivity {

    private final RecipeRepository repository = RecipeRepository.getInstance();

    private Recipe existing; // null when creating a new recipe

    private EditText editTitle;
    private EditText editServings;
    private EditText editPrepTime;
    private EditText editCookTime;
    private EditText editTags;
    private LinearLayout containerIngredients;
    private LinearLayout containerSteps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_recipe);

        String recipeId = getIntent().getStringExtra(RecipeListActivity.EXTRA_RECIPE_ID);
        existing = recipeId != null ? repository.getRecipe(recipeId) : null;
        boolean isEditing = existing != null;

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(isEditing ? "Edit Recipe" : "New Recipe");
        toolbar.setNavigationOnClickListener(v -> finish());

        editTitle = findViewById(R.id.editTitle);
        editServings = findViewById(R.id.editServings);
        editPrepTime = findViewById(R.id.editPrepTime);
        editCookTime = findViewById(R.id.editCookTime);
        editTags = findViewById(R.id.editTags);
        containerIngredients = findViewById(R.id.containerIngredients);
        containerSteps = findViewById(R.id.containerSteps);

        if (isEditing) {
            editTitle.setText(existing.getTitle());
            editServings.setText(String.valueOf(existing.getBaseServings()));
            editPrepTime.setText(String.valueOf(existing.getPrepTimeMinutes()));
            editCookTime.setText(String.valueOf(existing.getCookTimeMinutes()));
            editTags.setText(String.join(", ", existing.getTags()));

            for (Ingredient ing : existing.getIngredients()) {
                addIngredientRow(String.valueOf(ing.getQuantity()), ing.getUnit(), ing.getName());
            }
            for (RecipeStep step : existing.getSteps()) {
                addStepRow(step.getText());
            }
        } else {
            editServings.setText("4");
            editPrepTime.setText("0");
            editCookTime.setText("0");
        }

        if (containerIngredients.getChildCount() == 0) {
            addIngredientRow("", "", "");
        }
        if (containerSteps.getChildCount() == 0) {
            addStepRow("");
        }

        findViewById(R.id.buttonAddIngredient).setOnClickListener(v -> addIngredientRow("", "", ""));
        findViewById(R.id.buttonAddStep).setOnClickListener(v -> addStepRow(""));
        findViewById(R.id.buttonSave).setOnClickListener(v -> saveRecipe());
    }

    private void addIngredientRow(String quantity, String unit, String name) {
        View row = LayoutInflater.from(this).inflate(R.layout.item_ingredient_input, containerIngredients, false);
        ((EditText) row.findViewById(R.id.editQuantity)).setText(quantity);
        ((EditText) row.findViewById(R.id.editUnit)).setText(unit);
        ((EditText) row.findViewById(R.id.editIngredientName)).setText(name);

        ImageButton removeButton = row.findViewById(R.id.buttonRemove);
        removeButton.setOnClickListener(v -> {
            if (containerIngredients.getChildCount() > 1) {
                containerIngredients.removeView(row);
            }
        });

        containerIngredients.addView(row);
    }

    private void addStepRow(String text) {
        View row = LayoutInflater.from(this).inflate(R.layout.item_step_input, containerSteps, false);
        ((EditText) row.findViewById(R.id.editStepText)).setText(text);

        ImageButton removeButton = row.findViewById(R.id.buttonRemove);
        removeButton.setOnClickListener(v -> {
            if (containerSteps.getChildCount() > 1) {
                containerSteps.removeView(row);
            }
        });

        containerSteps.addView(row);
    }

    private void saveRecipe() {
        String title = editTitle.getText().toString().trim();
        if (title.isEmpty()) title = "Untitled Recipe";

        Recipe recipe = existing != null ? existing : new Recipe(title);
        recipe.setTitle(title);
        recipe.setBaseServings(parseIntOrDefault(editServings.getText().toString(), 4));
        recipe.setPrepTimeMinutes(parseIntOrDefault(editPrepTime.getText().toString(), 0));
        recipe.setCookTimeMinutes(parseIntOrDefault(editCookTime.getText().toString(), 0));

        List<String> tags = new ArrayList<>();
        for (String tag : editTags.getText().toString().split(",")) {
            String trimmed = tag.trim();
            if (!trimmed.isEmpty()) tags.add(trimmed);
        }
        recipe.setTags(tags);

        List<Ingredient> ingredients = new ArrayList<>();
        for (int i = 0; i < containerIngredients.getChildCount(); i++) {
            View row = containerIngredients.getChildAt(i);
            String name = ((EditText) row.findViewById(R.id.editIngredientName)).getText().toString().trim();
            if (name.isEmpty()) continue;
            String qtyText = ((EditText) row.findViewById(R.id.editQuantity)).getText().toString().trim();
            String unit = ((EditText) row.findViewById(R.id.editUnit)).getText().toString().trim();
            double qty = 0.0;
            try {
                qty = qtyText.isEmpty() ? 0.0 : Double.parseDouble(qtyText);
            } catch (NumberFormatException ignored) {
            }
            ingredients.add(new Ingredient(name, qty, unit));
        }
        recipe.setIngredients(ingredients);

        List<RecipeStep> steps = new ArrayList<>();
        for (int i = 0; i < containerSteps.getChildCount(); i++) {
            View row = containerSteps.getChildAt(i);
            String text = ((EditText) row.findViewById(R.id.editStepText)).getText().toString().trim();
            if (!text.isEmpty()) {
                steps.add(new RecipeStep(text));
            }
        }
        recipe.setSteps(steps);

        if (existing != null) {
            repository.updateRecipe(recipe);
        } else {
            repository.addRecipe(recipe);
        }

        finish();
    }

    private int parseIntOrDefault(String text, int defaultValue) {
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
