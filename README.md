# Recipe App (Java / Android Views)

A recipe manager built with Java and standard Android Views (XML layouts +
Activities + RecyclerView) — no Jetpack Compose, no Kotlin.

## What's included
- **Recipe list** (`RecipeListActivity`) — search bar, tag filter chips, favorite toggle, FAB to add a recipe
- **Recipe detail** (`RecipeDetailActivity`) — serving scaler that recalculates ingredient quantities live, ingredient list, numbered steps, edit/delete actions
- **Add / Edit recipe** (`AddEditRecipeActivity`) — dynamic ingredient and step rows you can add/remove
- **Cook Mode** (`CookModeActivity`) — fullscreen step-by-step view with a progress bar and a built-in countdown timer per step (uses `Handler`)
- **Shopping list** (`ShoppingListActivity`) — pick recipes, ingredients are combined and quantities summed automatically, checkable items

## What's intentionally left out
Offline persistence / local storage / sync. `RecipeRepository`
(`data/RecipeRepository.java`) holds everything **in memory only** — data
resets whenever the app process is killed.

## Opening the project
1. Open Android Studio → **Open** → select the `RecipeAppJava` folder.
2. Let Gradle sync (it will download the Gradle distribution + dependencies
   the first time — needs an internet connection).
3. Run on an emulator or device (minSdk 24 / Android 7.0+).

## Project structure
```
app/src/main/java/com/example/recipeapp/
  model/        Recipe, Ingredient, RecipeStep, ShoppingListItem, MealType
  data/         RecipeRepository (in-memory singleton, sample data)
  ui/           Activities + RecyclerView adapters
app/src/main/res/
  layout/       XML layouts for every screen and list row
  menu/         Toolbar menus
  values/       colors, strings, theme
```

## Notes
- Sample data (3 recipes) is preloaded in `RecipeRepository.loadSampleData()`
  so the app isn't empty on first launch.
- Icons use built-in Android system drawables (`@android:drawable/...`) so
  the project doesn't need custom image assets to run.
