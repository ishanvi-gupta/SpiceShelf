package com.example.recipeapp.ui;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipeapp.R;
import com.example.recipeapp.model.Recipe;

import java.util.List;

public class RecipeSelectionAdapter extends RecyclerView.Adapter<RecipeSelectionAdapter.ViewHolder> {

    public interface Listener {
        void onToggle(Recipe recipe);
    }

    private final List<Recipe> recipes;
    private final List<String> selectedIds;
    private final Listener listener;

    public RecipeSelectionAdapter(List<Recipe> recipes, List<String> selectedIds, Listener listener) {
        this.recipes = recipes;
        this.selectedIds = selectedIds;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CheckBox checkBox = (CheckBox) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_shopping_recipe_checkbox, parent, false);
        return new ViewHolder(checkBox);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setText(recipe.getTitle());
        holder.checkBox.setChecked(selectedIds.contains(recipe.getId()));
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> listener.onToggle(recipe));
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;

        ViewHolder(CheckBox itemView) {
            super(itemView);
            checkBox = itemView;
        }
    }
}
