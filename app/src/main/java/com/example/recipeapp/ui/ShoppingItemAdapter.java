package com.example.recipeapp.ui;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipeapp.R;
import com.example.recipeapp.model.ShoppingListItem;

import java.util.List;
import java.util.Locale;

public class ShoppingItemAdapter extends RecyclerView.Adapter<ShoppingItemAdapter.ViewHolder> {

    public interface Listener {
        void onToggle(ShoppingListItem item);
    }

    private final List<ShoppingListItem> items;
    private final Listener listener;

    public ShoppingItemAdapter(List<ShoppingListItem> items, Listener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CheckBox checkBox = (CheckBox) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_shopping_ingredient, parent, false);
        return new ViewHolder(checkBox);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ShoppingListItem item = items.get(position);
        holder.checkBox.setOnCheckedChangeListener(null);

        String qty = item.getQuantity() == Math.round(item.getQuantity())
                ? String.valueOf(Math.round(item.getQuantity()))
                : String.format(Locale.getDefault(), "%.2f", item.getQuantity());
        String text = (qty + " " + item.getUnit() + " " + item.getName()).replace("  ", " ").trim();

        holder.checkBox.setText(text);
        holder.checkBox.setChecked(item.isChecked());
        holder.checkBox.setPaintFlags(
                item.isChecked()
                        ? holder.checkBox.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
                        : holder.checkBox.getPaintFlags() & ~android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
        );

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> listener.onToggle(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;

        ViewHolder(CheckBox itemView) {
            super(itemView);
            checkBox = itemView;
        }
    }
}
