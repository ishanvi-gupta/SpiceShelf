package com.example.recipeapp.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipeapp.R;
import com.example.recipeapp.data.RecipeRepository;
import com.google.android.material.appbar.MaterialToolbar;

public class ShoppingListActivity extends AppCompatActivity {

    private final RecipeRepository repository = RecipeRepository.getInstance();

    private RecyclerView recyclerRecipeSelection;
    private RecyclerView recyclerShoppingItems;
    private TextView textEmptyShoppingList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_clear) {
                repository.clearShoppingList();
                refresh();
                return true;
            }
            return false;
        });

        recyclerRecipeSelection = findViewById(R.id.recyclerRecipeSelection);
        recyclerRecipeSelection.setLayoutManager(new LinearLayoutManager(this));

        recyclerShoppingItems = findViewById(R.id.recyclerShoppingItems);
        recyclerShoppingItems.setLayoutManager(new LinearLayoutManager(this));

        textEmptyShoppingList = findViewById(R.id.textEmptyShoppingList);

        refresh();
    }

    private void refresh() {
        RecipeSelectionAdapter selectionAdapter = new RecipeSelectionAdapter(
                repository.getAllRecipes(),
                repository.getShoppingListRecipeIds(),
                recipe -> {
                    repository.toggleRecipeInShoppingList(recipe.getId());
                    refresh();
                }
        );
        recyclerRecipeSelection.setAdapter(selectionAdapter);

        ShoppingItemAdapter itemAdapter = new ShoppingItemAdapter(
                repository.getShoppingListItems(),
                item -> {
                    repository.toggleShoppingItemChecked(item.getName(), item.getUnit());
                    refresh();
                }
        );
        recyclerShoppingItems.setAdapter(itemAdapter);

        boolean isEmpty = repository.getShoppingListItems().isEmpty();
        textEmptyShoppingList.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        recyclerShoppingItems.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }
}
