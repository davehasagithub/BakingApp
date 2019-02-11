package com.example.android.baking.ui.main;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;

import com.example.android.baking.R;
import com.example.android.baking.data.struct.Recipe;
import com.example.android.baking.databinding.RecipeFragmentBinding;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;

public class RecipeFragment extends Fragment implements RecipeAdapter.RecipeAdapterCallback {

    private RecipeFragmentBinding binding;
    private MainActivityViewModel activityViewModel;
    private RecipeAdapter adapter;

    private int lastPosition = -1;
//    private CountingIdlingResource idlingResource;

    public static RecipeFragment newInstance() {
        return new RecipeFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        activityViewModel = ViewModelProviders.of((AppCompatActivity) context).get(MainActivityViewModel.class);

//        if (context instanceof MainActivity) {
//            idlingResource = ((MainActivity) context).getIdlingResource();
//        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        adapter = new RecipeAdapter(this, getLifecycle());//, idlingResource);
        adapter.setHasStableIds(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = RecipeFragmentBinding.inflate(inflater);
        binding.setMainActivityViewModel(activityViewModel);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        postponeEnterTransition();
        final ViewTreeObserver vto = view.getViewTreeObserver();
        vto.addOnPreDrawListener(new OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (vto.isAlive()) {
                    vto.removeOnPreDrawListener(this);
                } else {
                    view.getViewTreeObserver().removeOnPreDrawListener(this);
                }
                startPostponedEnterTransition();
                binding.setLifecycleOwner(RecipeFragment.this);
                return true;
            }
        });

        binding.recyclerviewRecipes.setHasFixedSize(true);
        binding.recyclerviewRecipes.setAdapter(adapter);
        binding.recyclerviewRecipes.addItemDecoration(new RecipeDecoration());
        binding.recyclerviewRecipes.setLayoutManager(new GridLayoutManager(getContext(), getResources().getInteger(R.integer.recipe_grid_columns)));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        addViewModelObservers();

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            ActionBar actionBar = activity.getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(getString(R.string.app_name));
                actionBar.setSubtitle(null);
                actionBar.setDisplayHomeAsUpEnabled(false);
            }
        }
    }

    private void addViewModelObservers() {
        activityViewModel.getRecipesLiveData().observe(getViewLifecycleOwner(), recipes -> {
            if (adapter != null) {
                adapter.submitList(recipes);
            }
        });

        activityViewModel.getRecipeIndexLiveData().observe(getViewLifecycleOwner(), this::updateSelectedPosition);
    }

    private void updateSelectedPosition(int position) {
        int oldPosition = lastPosition;
        lastPosition = position;
        if (lastPosition != -1) {
            adapter.notifyItemChanged(lastPosition, RecipeAdapter.PAYLOAD_UPDATE_BACKGROUND);
        }
        if (oldPosition != -1) {
            adapter.notifyItemChanged(oldPosition, RecipeAdapter.PAYLOAD_UPDATE_BACKGROUND);
        }
    }

    @Override
    public void onClickItem(int position, Recipe recipe) {
        boolean isReclick = isActivePosition(position);
        updateSelectedPosition(isReclick || recipe == null ? -1 : position);
        activityViewModel.triggerRecipeLoad(isReclick ? null : recipe);
    }

    @Override
    public boolean isActivePosition(int position) {
        return position == lastPosition;
    }
}
