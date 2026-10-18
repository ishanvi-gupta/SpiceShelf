package com.example.recipeapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipeapp.R;
import com.example.recipeapp.data.RecipeRepository;
import com.example.recipeapp.model.Recipe;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class RecipeListActivity extends AppCompatActivity {

    public static final String EXTRA_RECIPE_ID = "extra_recipe_id";

    private final RecipeRepository repository = RecipeRepository.getInstance();

    private RecipeAdapter adapter;
    private ChipGroup chipGroupFilters;
    private TextView textEmpty;

    private String currentQuery = "";
    private String currentTagFilter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setOnMenuItemClickListener(this::onMenuItemClick);

        EditText editSearch = findViewById(R.id.editSearch);
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentQuery = s.toString();
                refreshList();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        chipGroupFilters = findViewById(R.id.chipGroupFilters);
        textEmpty = findViewById(R.id.textEmpty);

        RecyclerView recyclerView = findViewById(R.id.recyclerRecipes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecipeAdapter(new RecipeAdapter.Listener() {
            @Override
            public void onRecipeClick(Recipe recipe) {
                Intent intent = new Intent(RecipeListActivity.this, RecipeDetailActivity.class);
                intent.putExtra(EXTRA_RECIPE_ID, recipe.getId());
                startActivity(intent);
            }

            @Override
            public void onFavoriteToggle(Recipe recipe) {
                repository.toggleFavorite(recipe.getId());
                refreshList();
            }
        });
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fabAddRecipe);
        fab.setOnClickListener(v ->
                startActivity(new Intent(RecipeListActivity.this, AddEditRecipeActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        buildFilterChips();
        refreshList();
    }

    private boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.action_shopping_list) {
            startActivity(new Intent(this, ShoppingListActivity.class));
            return true;
        }
        return false;
    }

    private void buildFilterChips() {
        chipGroupFilters.removeAllViews();
        List<String> tags = repository.getAllTags();
        for (String tag : tags) {
            Chip chip = new Chip(this);
            chip.setText(tag);
            chip.setCheckable(true);
            chip.setChecked(tag.equals(currentTagFilter));
            chip.setOnClickListener(v -> {
                currentTagFilter = tag.equals(currentTagFilter) ? null : tag;
                buildFilterChips();
                refreshList();
            });
            chipGroupFilters.addView(chip);
        }
    }

    private void refreshList() {
        List<Recipe> filtered = repository.filterRecipes(currentQuery, currentTagFilter);
        adapter.submitList(filtered);
        textEmpty.setVisibility(filtered.isEmpty() ? android.view.View.VISIBLE : android.view.View.GONE);
    }
}
