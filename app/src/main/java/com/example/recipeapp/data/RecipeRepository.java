package com.example.recipeapp.data;

import com.example.recipeapp.model.Ingredient;
import com.example.recipeapp.model.MealType;
import com.example.recipeapp.model.Recipe;
import com.example.recipeapp.model.RecipeStep;
import com.example.recipeapp.model.ShoppingListItem;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;

/**
 * Single in-memory source of truth for the whole app. Nothing here is written
 * to disk or a database — all data lives only as long as the process does.
 * Offline persistence/sync was intentionally left out of this build.
 */
public class RecipeRepository {

    private static RecipeRepository instance;

    private final List<Recipe> recipes = new ArrayList<>();
    private final List<String> shoppingListRecipeIds = new ArrayList<>();
    private final List<ShoppingListItem> shoppingListItems = new ArrayList<>();

    private RecipeRepository() {
        loadSampleData();
    }

    public static synchronized RecipeRepository getInstance() {
        if (instance == null) {
            instance = new RecipeRepository();
        }
        return instance;
    }

    // ---------- CRUD ----------

    public List<Recipe> getAllRecipes() {
        return recipes;
    }

    public Recipe getRecipe(String id) {
        for (Recipe r : recipes) {
            if (r.getId().equals(id)) return r;
        }
        return null;
    }

    public void addRecipe(Recipe recipe) {
        recipes.add(recipe);
    }

    public void updateRecipe(Recipe updated) {
        for (int i = 0; i < recipes.size(); i++) {
            if (recipes.get(i).getId().equals(updated.getId())) {
                recipes.set(i, updated);
                return;
            }
        }
    }

    public void deleteRecipe(String id) {
        Recipe toRemove = getRecipe(id);
        if (toRemove != null) {
            recipes.remove(toRemove);
        }
        shoppingListRecipeIds.remove(id);
        regenerateShoppingList();
    }

    public void toggleFavorite(String id) {
        Recipe r = getRecipe(id);
        if (r != null) {
            r.setFavorite(!r.isFavorite());
        }
    }

    // ---------- Search / filter ----------

    public List<Recipe> filterRecipes(String query, String tagFilter) {
        List<Recipe> result = new ArrayList<>();
        String q = query == null ? "" : query.trim().toLowerCase(Locale.getDefault());
        for (Recipe recipe : recipes) {
            boolean matchesQuery = q.isEmpty()
                    || recipe.getTitle().toLowerCase(Locale.getDefault()).contains(q)
                    || matchesAnyIngredient(recipe, q)
                    || matchesAnyTag(recipe, q);

            boolean matchesTag = tagFilter == null || recipe.getTags().contains(tagFilter);

            if (matchesQuery && matchesTag) {
                result.add(recipe);
            }
        }
        return result;
    }

    private boolean matchesAnyIngredient(Recipe recipe, String q) {
        for (Ingredient ing : recipe.getIngredients()) {
            if (ing.getName().toLowerCase(Locale.getDefault()).contains(q)) return true;
        }
        return false;
    }

    private boolean matchesAnyTag(Recipe recipe, String q) {
        for (String tag : recipe.getTags()) {
            if (tag.toLowerCase(Locale.getDefault()).contains(q)) return true;
        }
        return false;
    }

    public List<String> getAllTags() {
        TreeSet<String> tagSet = new TreeSet<>();
        for (Recipe recipe : recipes) {
            tagSet.addAll(recipe.getTags());
        }
        return new ArrayList<>(tagSet);
    }

    // ---------- Shopping list ----------

    public List<String> getShoppingListRecipeIds() {
        return shoppingListRecipeIds;
    }

    public List<ShoppingListItem> getShoppingListItems() {
        return shoppingListItems;
    }

    public void toggleRecipeInShoppingList(String recipeId) {
        if (shoppingListRecipeIds.contains(recipeId)) {
            shoppingListRecipeIds.remove(recipeId);
        } else {
            shoppingListRecipeIds.add(recipeId);
        }
        regenerateShoppingList();
    }

    public void regenerateShoppingList() {
        LinkedHashMap<String, ShoppingListItem> combined = new LinkedHashMap<>();
        for (String id : shoppingListRecipeIds) {
            Recipe recipe = getRecipe(id);
            if (recipe == null) continue;
            for (Ingredient ing : recipe.getIngredients()) {
                String key = ing.getName().toLowerCase(Locale.getDefault()) + "|"
                        + ing.getUnit().toLowerCase(Locale.getDefault());
                ShoppingListItem existing = combined.get(key);
                if (existing != null) {
                    existing.addQuantity(ing.getQuantity());
                } else {
                    combined.put(key, new ShoppingListItem(ing.getName(), ing.getQuantity(), ing.getUnit()));
                }
            }
        }
        shoppingListItems.clear();
        shoppingListItems.addAll(combined.values());
    }

    public void toggleShoppingItemChecked(String name, String unit) {
        for (ShoppingListItem item : shoppingListItems) {
            if (item.getName().equals(name) && item.getUnit().equals(unit)) {
                item.setChecked(!item.isChecked());
                return;
            }
        }
    }

    public void clearShoppingList() {
        shoppingListRecipeIds.clear();
        shoppingListItems.clear();
    }

    // ---------- Sample data ----------

    private void loadSampleData() {
        Recipe pancakes = new Recipe("Classic Pancakes");
        pancakes.setBaseServings(4);
        pancakes.setPrepTimeMinutes(10);
        pancakes.setCookTimeMinutes(15);
        pancakes.setMealType(MealType.BREAKFAST);
        pancakes.setTags(listOf("breakfast", "vegetarian", "quick"));
        pancakes.setIngredients(listOf(
                new Ingredient("flour", 2.0, "cup"),
                new Ingredient("milk", 1.5, "cup"),
                new Ingredient("eggs", 2.0, ""),
                new Ingredient("sugar", 2.0, "tbsp"),
                new Ingredient("baking powder", 2.0, "tsp"),
                new Ingredient("salt", 0.5, "tsp"),
                new Ingredient("butter", 2.0, "tbsp")
        ));
        pancakes.setSteps(listOf(
                new RecipeStep("Whisk flour, sugar, baking powder, and salt together."),
                new RecipeStep("Whisk in milk and eggs until just combined."),
                new RecipeStep("Melt butter in a pan over medium heat.", 60),
                new RecipeStep("Pour 1/4 cup batter per pancake, cook until bubbles form.", 120),
                new RecipeStep("Flip and cook the other side.", 90)
        ));

        Recipe pasta = new Recipe("Weeknight Garlic Pasta");
        pasta.setBaseServings(2);
        pasta.setPrepTimeMinutes(5);
        pasta.setCookTimeMinutes(20);
        pasta.setMealType(MealType.DINNER);
        pasta.setTags(listOf("dinner", "quick", "vegetarian"));
        pasta.setIngredients(listOf(
                new Ingredient("spaghetti", 200.0, "g"),
                new Ingredient("garlic cloves", 4.0, ""),
                new Ingredient("olive oil", 4.0, "tbsp"),
                new Ingredient("red pepper flakes", 0.5, "tsp"),
                new Ingredient("parmesan", 0.5, "cup"),
                new Ingredient("parsley", 2.0, "tbsp")
        ));
        pasta.setSteps(listOf(
                new RecipeStep("Boil salted water and cook spaghetti until al dente.", 600),
                new RecipeStep("Thinly slice garlic and gently fry in olive oil until golden.", 180),
                new RecipeStep("Add red pepper flakes and a splash of pasta water."),
                new RecipeStep("Toss drained pasta in the garlic oil, top with parmesan and parsley.")
        ));

        Recipe salad = new Recipe("Grilled Chicken Salad");
        salad.setBaseServings(2);
        salad.setPrepTimeMinutes(15);
        salad.setCookTimeMinutes(12);
        salad.setMealType(MealType.LUNCH);
        salad.setTags(listOf("lunch", "high-protein", "gluten-free"));
        salad.setIngredients(listOf(
                new Ingredient("chicken breast", 2.0, ""),
                new Ingredient("mixed greens", 4.0, "cup"),
                new Ingredient("cherry tomatoes", 1.0, "cup"),
                new Ingredient("cucumber", 1.0, ""),
                new Ingredient("olive oil", 3.0, "tbsp"),
                new Ingredient("lemon", 1.0, "")
        ));
        salad.setSteps(listOf(
                new RecipeStep("Season chicken breasts with salt and pepper."),
                new RecipeStep("Grill chicken until cooked through, about 6 min per side.", 360),
                new RecipeStep("Slice cucumber and halve tomatoes."),
                new RecipeStep("Toss greens, tomatoes, and cucumber with olive oil and lemon juice."),
                new RecipeStep("Slice chicken and place on top of the salad.")
        ));

        recipes.add(pancakes);
        recipes.add(pasta);
        recipes.add(salad);
    }

    @SafeVarargs
    private final <T> List<T> listOf(T... items) {
        List<T> list = new ArrayList<>();
        for (T item : items) list.add(item);
        return list;
    }
}
