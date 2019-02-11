package com.example.android.baking.ui.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.example.android.baking.R;
import com.example.android.baking.data.struct.Recipe;
import com.example.android.baking.databinding.SharedMasterRowBinding;
import com.example.android.baking.ui.main.RecipeAdapter.ViewHolder;
import com.example.android.baking.utilities.EspressoIdlingResource;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver;

public class RecipeAdapter extends ListAdapter<Recipe, ViewHolder> implements DefaultLifecycleObserver {

    public static final String RECIPE_NAME_FOR_TEST = "Brownies";
    public static final int PAYLOAD_UPDATE_BACKGROUND = 0;

    final private RecipeAdapterCallback recipeAdapterCallback;
    private AdapterDataObserver adapterDataObserver;

    RecipeAdapter(RecipeAdapterCallback recipeAdapterCallback, Lifecycle lifecycle) {
        super(RecipeAdapter.DIFF_CALLBACK);
        this.recipeAdapterCallback = recipeAdapterCallback;
        lifecycle.addObserver(this);
    }

    @Override
    public void onCreate(@NonNull LifecycleOwner owner) {
        EspressoIdlingResource.increment();
        adapterDataObserver = new AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);

                for (int i = positionStart; i < positionStart + itemCount; i++) {
                    if (RecipeAdapter.RECIPE_NAME_FOR_TEST.equalsIgnoreCase(getItem(i).getRecipeDb().getName())) {
                        EspressoIdlingResource.decrement();
                        break;
                    }
                }

            }
        };
        registerAdapterDataObserver(adapterDataObserver);
    }

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        if (adapterDataObserver != null) {
            unregisterAdapterDataObserver(adapterDataObserver);
        }
    }

    public interface RecipeAdapterCallback {
        void onClickItem(int position, Recipe recipe);

        boolean isActivePosition(int position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        return new ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()), R.layout.shared_master_row, viewGroup, false));
    }

    class ViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        final SharedMasterRowBinding binding;

        ViewHolder(SharedMasterRowBinding binding) {
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
        Context context = viewHolder.binding.getRoot().getContext();
        int color = ContextCompat.getColor(context, recipe.recipeDb.isPlaceholder() ? R.color.cardBackgroundPlaceholderColor : (selected ? R.color.cardBackgroundSelectedColor : R.color.cardBackgroundColor));
        viewHolder.binding.card.setCardBackgroundColor(color);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Recipe recipe = getItem(position);
        if (recipe != null) {
            Context context = viewHolder.binding.getRoot().getContext();
            viewHolder.binding.setIsPlaceholder(recipe.recipeDb.isPlaceholder());
            viewHolder.binding.setText(recipe.recipeDb.getName());
            viewHolder.binding.setImageUrl(recipe.recipeDb.getImageUrl());
            viewHolder.binding.setImageContentDescription(context.getString(R.string.accessibility_recipe_icon));
            viewHolder.binding.setSecondRow(context.getString(R.string.recipe_serves_how_many) + " " + recipe.recipeDb.getServings());
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
