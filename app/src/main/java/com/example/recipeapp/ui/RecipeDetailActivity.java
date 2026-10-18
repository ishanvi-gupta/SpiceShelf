package com.example.recipeapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.recipeapp.R;
import com.example.recipeapp.data.RecipeRepository;
import com.example.recipeapp.model.Ingredient;
import com.example.recipeapp.model.Recipe;
import com.example.recipeapp.model.RecipeStep;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;
import java.util.Locale;

public class RecipeDetailActivity extends AppCompatActivity {

    private final RecipeRepository repository = RecipeRepository.getInstance();

    private Recipe recipe;
    private int servings;

    private TextView textServings;
    private LinearLayout containerIngredients;
    private LinearLayout containerSteps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        String recipeId = getIntent().getStringExtra(RecipeListActivity.EXTRA_RECIPE_ID);
        recipe = repository.getRecipe(recipeId);

        if (recipe == null) {
            finish();
            return;
        }

        servings = recipe.getBaseServings();

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(recipe.getTitle());
        toolbar.setNavigationOnClickListener(v -> finish());
        toolbar.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.action_edit) {
                Intent intent = new Intent(this, AddEditRecipeActivity.class);
                intent.putExtra(RecipeListActivity.EXTRA_RECIPE_ID, recipe.getId());
                startActivity(intent);
                return true;
            } else if (id == R.id.action_delete) {
                confirmDelete();
                return true;
            }
            return false;
        });

        TextView textTimeSummary = findViewById(R.id.textTimeSummary);
        textTimeSummary.setText(recipe.getPrepTimeMinutes() + " min prep · "
                + recipe.getCookTimeMinutes() + " min cook");

        findViewById(R.id.buttonStartCooking).setOnClickListener(v -> {
            Intent intent = new Intent(this, CookModeActivity.class);
            intent.putExtra(RecipeListActivity.EXTRA_RECIPE_ID, recipe.getId());
            startActivity(intent);
        });

        textServings = findViewById(R.id.textServings);
        findViewById(R.id.buttonServingsMinus).setOnClickListener(v -> {
            if (servings > 1) {
                servings--;
                renderIngredients();
            }
        });
        findViewById(R.id.buttonServingsPlus).setOnClickListener(v -> {
            servings++;
            renderIngredients();
        });

        containerIngredients = findViewById(R.id.containerIngredients);
        containerSteps = findViewById(R.id.containerSteps);

        renderIngredients();
        renderSteps();
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Delete recipe?")
                .setMessage("\"" + recipe.getTitle() + "\" will be permanently deleted. This can't be undone.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    repository.deleteRecipe(recipe.getId());
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void renderIngredients() {
        textServings.setText(String.valueOf(servings));
        containerIngredients.removeAllViews();

        List<Ingredient> scaled = recipe.scaledIngredients(servings);
        for (Ingredient ing : scaled) {
            TextView line = new TextView(this);
            String qty = formatQuantity(ing.getQuantity());
            String text = ("• " + qty + " " + ing.getUnit() + " " + ing.getName())
                    .replace("  ", " ").trim();
            line.setText(text);
            line.setTextSize(16f);
            line.setPadding(0, 8, 0, 8);
            containerIngredients.addView(line);
        }
    }

    private void renderSteps() {
        containerSteps.removeAllViews();
        List<RecipeStep> steps = recipe.getSteps();
        for (int i = 0; i < steps.size(); i++) {
            TextView line = new TextView(this);
            line.setText((i + 1) + ". " + steps.get(i).getText());
            line.setTextSize(16f);
            line.setPadding(0, 10, 0, 10);
            containerSteps.addView(line);
        }
    }

    private String formatQuantity(double quantity) {
        if (quantity == Math.round(quantity)) {
            return String.valueOf(Math.round(quantity));
        }
        return String.format(Locale.getDefault(), "%.2f", quantity);
    }
}
