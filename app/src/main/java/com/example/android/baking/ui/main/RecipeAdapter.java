package com.example.android.baking.ui.main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.example.android.baking.R;
import com.example.android.baking.data.struct.Recipe;
import com.example.android.baking.ui.main.RecipeAdapter.ViewHolder;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.databinding.library.baseAdapters.BR;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

public class RecipeAdapter extends ListAdapter<Recipe, ViewHolder> {

    public static final int PAYLOAD_UPDATE_BACKGROUND = 0;

    private RecipeAdapterCallback recipeAdapterCallback;

    RecipeAdapter(RecipeAdapterCallback recipeAdapterCallback) {
        super(RecipeAdapter.DIFF_CALLBACK);
        this.recipeAdapterCallback = recipeAdapterCallback;
    }

    public interface RecipeAdapterCallback {
        void onClickItem(int position, Recipe recipe);
        boolean isActivePosition(int position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        return new ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()), R.layout.recipe_item, viewGroup, false));
    }

    class ViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        final ViewDataBinding binding;

        ViewHolder(ViewDataBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (recipeAdapterCallback != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Recipe recipe = getItem(position);
                    if (!recipe.recipeDb.isPlaceholder()) {
                        recipeAdapterCallback.onClickItem(position, recipe);
                    }
                }
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (!payloads.isEmpty() && payloads.get(0) instanceof Integer) {
            Integer payload = (Integer) payloads.get(0);
            if (payload == RecipeAdapter.PAYLOAD_UPDATE_BACKGROUND) {
                updateBackgroundColor(holder, position);
            }
        } else {
            super.onBindViewHolder(holder, position, payloads);
        }
    }

    private void updateBackgroundColor(ViewHolder viewHolder, int position) {
        Recipe recipe = getItem(position);
        boolean selected = (recipeAdapterCallback != null && recipeAdapterCallback.isActivePosition(position));
        viewHolder.binding.getRoot().setBackgroundColor(recipe.recipeDb.isPlaceholder() ? 0xffeeeeee : selected ? 0xff009900 : 0xff999999);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Recipe recipe = getItem(position);
        if (recipe != null) {
            viewHolder.binding.setVariable(BR.recipe, recipe.recipeDb);
            viewHolder.binding.executePendingBindings();
            updateBackgroundColor(viewHolder, position);
        }
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getRecipeDb().getId();
    }

    private static final DiffUtil.ItemCallback<Recipe> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Recipe>() {
                @Override
                public boolean areItemsTheSame(Recipe oldRecipe, Recipe newRecipe) {
                    return oldRecipe.getRecipeDb().getId() == newRecipe.getRecipeDb().getId();
                }

                @Override
                public boolean areContentsTheSame(Recipe oldRecipe, @NonNull Recipe newRecipe) {
                    return oldRecipe.equals(newRecipe);
                }
            };
}
