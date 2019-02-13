package com.example.android.baking.ui;

import android.os.Bundle;

import com.example.android.baking.R;
import com.example.android.baking.databinding.MainActivityBinding;
import com.example.android.baking.ui.recipe.RecipeFragment;
import com.example.android.baking.ui.steps.MasterDetailFragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

public class MainActivity extends AppCompatActivity {

    private MainActivityViewModel viewModel;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("recipeId", viewModel.getRecipeId());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);

        MainActivityBinding binding = DataBindingUtil.setContentView(this, R.layout.main_activity);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        setSupportActionBar(binding.toolbar);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.recipe_container, RecipeFragment.newInstance(), "recipe").commit();
        }

        viewModel.init(savedInstanceState == null ? -1 : savedInstanceState.getInt("recipeId"));

        addViewModelObservers();
    }

    private void addViewModelObservers() {
        viewModel.getHandleRecipeClickLiveData().observe(this, event -> {
                if (event != null && Boolean.TRUE.equals(event.getContentIfNotHandled())) {
                    Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.recipe_container);
                    if (currentFragment instanceof RecipeFragment) {
                        getSupportFragmentManager().beginTransaction()
                                .setReorderingAllowed(true)
                                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                                .detach(currentFragment)
                                .add(R.id.recipe_container, MasterDetailFragment.newInstance())
                                .commit();
                    }
                }
        });
    }

    @Override
    public void onBackPressed() {
        boolean handled = false;

        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.recipe_container);
        if (currentFragment instanceof MasterDetailFragment) {
            handled = ((MasterDetailFragment) currentFragment).onBackPressed();
            if (!handled) {
                Fragment recipeFragment = getSupportFragmentManager().findFragmentByTag("recipe");
                if (recipeFragment != null) {
                    handled = true;
                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_left)
                            .attach(recipeFragment)
                            .remove(currentFragment)
                            .runOnCommit(() -> viewModel.clearRecipe())
                            .commit();
                }
            }
        }

        if (!handled) {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
