package com.example.recipeapp.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipeapp.R;
import com.example.recipeapp.model.Recipe;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    public interface Listener {
        void onRecipeClick(Recipe recipe);
        void onFavoriteToggle(Recipe recipe);
    }

    private final List<Recipe> recipes = new ArrayList<>();
    private final Listener listener;

    public RecipeAdapter(Listener listener) {
        this.listener = listener;
    }

    public void submitList(List<Recipe> newRecipes) {
        recipes.clear();
        recipes.addAll(newRecipes);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        holder.title.setText(recipe.getTitle());
        holder.meta.setText(recipe.getTotalTimeMinutes() + " min · serves " + recipe.getBaseServings());

        holder.favoriteButton.setImageResource(
                recipe.isFavorite()
                        ? android.R.drawable.btn_star_big_on
                        : android.R.drawable.btn_star_big_off
        );

        holder.chipGroup.removeAllViews();
        int shown = 0;
        for (String tag : recipe.getTags()) {
            if (shown >= 3) break;
            Chip chip = new Chip(holder.chipGroup.getContext());
            chip.setText(tag);
            chip.setClickable(false);
            chip.setCheckable(false);
            chip.setTextSize(12f);
            holder.chipGroup.addView(chip);
            shown++;
        }

        holder.itemView.setOnClickListener(v -> listener.onRecipeClick(recipe));
        holder.favoriteButton.setOnClickListener(v -> listener.onFavoriteToggle(recipe));
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    static class RecipeViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView meta;
        ImageButton favoriteButton;
        ChipGroup chipGroup;

        RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textTitle);
            meta = itemView.findViewById(R.id.textMeta);
            favoriteButton = itemView.findViewById(R.id.buttonFavorite);
            chipGroup = itemView.findViewById(R.id.chipGroupTags);
        }
    }
}
