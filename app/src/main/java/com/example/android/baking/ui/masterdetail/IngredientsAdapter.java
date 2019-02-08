package com.example.android.baking.ui.masterdetail;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.android.baking.R;
import com.example.android.baking.data.struct.IngredientDb;
import com.example.android.baking.ui.masterdetail.IngredientsAdapter.ViewHolder;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.databinding.library.baseAdapters.BR;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

public class IngredientsAdapter extends ListAdapter<IngredientDb, ViewHolder> {

    IngredientsAdapter() {
        super(IngredientsAdapter.DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        return new ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()), R.layout.ingredient_row, viewGroup, false));
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final ViewDataBinding binding;

        ViewHolder(ViewDataBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        IngredientDb ingredient = getItem(position);
        if (ingredient != null) {
            viewHolder.binding.setVariable(BR.ingredient, ingredient);
            viewHolder.binding.executePendingBindings();
        }
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    private static final DiffUtil.ItemCallback<IngredientDb> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<IngredientDb>() {
                @Override
                public boolean areItemsTheSame(IngredientDb oldIngredient, IngredientDb newIngredient) {
                    return oldIngredient.getId() == newIngredient.getId();
                }

                @Override
                public boolean areContentsTheSame(IngredientDb oldIngredient, @NonNull IngredientDb newIngredient) {
                    return oldIngredient.equals(newIngredient);
                }
            };
}
