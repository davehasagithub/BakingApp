package com.example.android.baking.ui.steps;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;

import com.example.android.baking.R;
import com.example.android.baking.data.struct.MasterItem;
import com.example.android.baking.data.struct.Recipe;
import com.example.android.baking.databinding.MasterDetailFragmentBinding;
import com.example.android.baking.ui.MainActivityViewModel;
import com.example.android.baking.ui.steps.detail.DetailFragment;
import com.example.android.baking.ui.steps.master.MasterFragment;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

public class MasterDetailFragment extends Fragment {

    // this can be used to change the ui behavior. if false:
    // - the detail pane will be initially empty until a master item is selected.
    // - if the same item is clicked again, the detail pane goes away.
    // - when going from 2 pane to 1, the user returns to the master list if no detail was showing.
    public final static boolean AUTO_SELECT_FIRST_ON_TWO_PANE = true;

    private MasterDetailFragmentBinding binding;
    private MasterDetailFragmentViewModel viewModel;
    private MainActivityViewModel activityViewModel;

    public static MasterDetailFragment newInstance() {
        return new MasterDetailFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        activityViewModel = ViewModelProviders.of((AppCompatActivity) context).get(MainActivityViewModel.class);
        viewModel = ViewModelProviders.of(MasterDetailFragment.this).get(MasterDetailFragmentViewModel.class);
    }

    @Override
    public void onSaveInstanceState(@NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("wasTwoPane", viewModel.isTwoPane());
        outState.putInt("masterItemId", viewModel.getMasterItemId());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = MasterDetailFragmentBinding.inflate(inflater);
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
                binding.setLifecycleOwner(MasterDetailFragment.this);
                return true;
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewModel.init(savedInstanceState == null ? -1 : savedInstanceState.getInt("masterItemId"));
        viewModel.setTwoPane(binding.singlePaneContainer == null);
        addViewModelObservers();

        FragmentManager fragmentManager = getChildFragmentManager();
        if (savedInstanceState == null) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            MasterFragment masterFragment = MasterFragment.newInstance();
            if (viewModel.isTwoPane()) {
                fragmentTransaction.add(R.id.master_container, masterFragment, "master");
                if (MasterDetailFragment.AUTO_SELECT_FIRST_ON_TWO_PANE) {
                    fragmentTransaction.add(R.id.detail_container, DetailFragment.newInstance(), "detail");
                }
            } else {
                fragmentTransaction.add(R.id.single_pane_container, masterFragment, "master");
            }
            fragmentTransaction.commit();
        } else {
            if (savedInstanceState.getBoolean("wasTwoPane", false) != viewModel.isTwoPane()) {
                handleChangeInPaneCount(fragmentManager);
            }
        }
    }

    // this is where much of the magic happens to let this be a single-activity app while still allowing the pane count to change on rotate.
    // fragments are removed from their old container before adding to new ones. using detach manually rather than fighting with the back stack.
    private void handleChangeInPaneCount(FragmentManager fragmentManager) {
        if (viewModel.isTwoPane() && MasterDetailFragment.AUTO_SELECT_FIRST_ON_TWO_PANE) {
            viewModel.selectFirstMasterItemIfNotSet();
        }

        MasterFragment masterFragment = (MasterFragment) fragmentManager.findFragmentByTag("master");
        if (masterFragment != null) {
            DetailFragment detailFragment = (DetailFragment) fragmentManager.findFragmentByTag("detail");

            if (detailFragment == null && MasterDetailFragment.AUTO_SELECT_FIRST_ON_TWO_PANE) {
                detailFragment = DetailFragment.newInstance();
            }

            boolean detailsActive = detailFragment != null;

            FragmentTransaction fragmentTransaction1 = fragmentManager.beginTransaction();
            fragmentTransaction1.remove(masterFragment);
            if (detailsActive) {
                fragmentTransaction1.remove(detailFragment);
            }
            fragmentTransaction1.commitNow();

            FragmentTransaction fragmentTransaction2 = fragmentManager.beginTransaction();
            fragmentTransaction2.add(viewModel.isTwoPane() ? R.id.master_container : R.id.single_pane_container, masterFragment, "master");
            if (detailsActive) {
                fragmentTransaction2.add(viewModel.isTwoPane() ? R.id.detail_container : R.id.single_pane_container, detailFragment, "detail");
            }
            if (!viewModel.isTwoPane() && detailsActive) {
                fragmentTransaction2.detach(masterFragment);
            }
            fragmentTransaction2.commit();
        }
    }

    private List<MasterItem> createMasterItems(Recipe recipe) {
        List<MasterItem> masterItems = new ArrayList<>();
        masterItems.add(new MasterItem.MasterItemIngredientsButton(recipe.getIngredients()));
        for (int i = 0; i < recipe.getSteps().size(); i++) {
            masterItems.add(new MasterItem.MasterItemStep(recipe.getSteps().get(i)));
        }
        return masterItems;
    }

    private void addViewModelObservers() {
        activityViewModel.getRecipeLiveData().observe(getViewLifecycleOwner(), recipe -> {
            updateTitle();

            if (recipe != null) {
                viewModel.setMasterItems(createMasterItems(recipe));
            }
        });

        viewModel.getMasterItemLiveData().observe(getViewLifecycleOwner(), item -> updateTitle());

        viewModel.getHandleMasterItemClickLiveData().observe(getViewLifecycleOwner(), event -> {
            if (event != null) {
                Boolean load = event.getContentIfNotHandled();
                if (load != null) {
                    if (load) {
                        handleMasterItemClick();
                    } else {
                        handleMasterItemReclick();
                    }
                }
            }
        });
    }

    private void handleMasterItemClick() {
        FragmentManager fragmentManager = getChildFragmentManager();
        if (viewModel.isTwoPane()) {
            Fragment detailFragment = fragmentManager.findFragmentById(R.id.detail_container);
            if (detailFragment == null) {
                detailFragment = DetailFragment.newInstance();
                fragmentManager.beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.detail_container, detailFragment, "detail")
                        .commit();
            }
        } else {
            Fragment currentFragment = fragmentManager.findFragmentById(R.id.single_pane_container);
            if (currentFragment instanceof MasterFragment) {
                DetailFragment detailFragment = DetailFragment.newInstance();
                fragmentManager.beginTransaction()
                        .setReorderingAllowed(true)
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                        .detach(currentFragment)
                        .add(R.id.single_pane_container, detailFragment, "detail")
                        .commit();
            }
        }
    }

    private void handleMasterItemReclick() {
        FragmentManager fragmentManager = getChildFragmentManager();
        if (viewModel.isTwoPane()) {
            Fragment detailFragment = fragmentManager.findFragmentById(R.id.detail_container);
            if (detailFragment instanceof DetailFragment) {
                fragmentManager.beginTransaction()
                        .setReorderingAllowed(true)
                        .remove(detailFragment)
                        .commit();
            }
        }
    }

    public boolean onBackPressed() {
        boolean handled = false;
        if (!viewModel.isTwoPane()) {
            FragmentManager fragmentManager = getChildFragmentManager();
            Fragment currentFragment = fragmentManager.findFragmentById(R.id.single_pane_container);
            if (currentFragment instanceof DetailFragment) {
                MasterFragment masterFragment = (MasterFragment) fragmentManager.findFragmentByTag("master");
                if (masterFragment != null) {
                    handled = true;
                    fragmentManager.beginTransaction()
                            .setReorderingAllowed(true)
                            .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_left)
                            .attach(masterFragment)
                            .remove(currentFragment)
                            .runOnCommit(() -> viewModel.clearMasterItem())
                            .commit();
                }
            }
        }
        return handled;
    }

    private void updateTitle() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            ActionBar actionBar = activity.getSupportActionBar();
            if (actionBar != null) {
                String title = getString(R.string.app_name);
                String subtitle = null;
                Recipe recipe = activityViewModel.getRecipeLiveData().getValue();
                MasterItem masterItem = viewModel.getMasterItemLiveData().getValue();

                if (recipe != null) {
                    title = recipe.getRecipeDb().getName();
                    if (!viewModel.isTwoPane())
                        if (masterItem instanceof MasterItem.MasterItemStep) {
                            subtitle = ((MasterItem.MasterItemStep) masterItem).getStep().getCleanedShortDescription();
                        } else if (masterItem instanceof MasterItem.MasterItemIngredientsButton) {
                            subtitle = "Ingredients";
                        }
                }

                actionBar.setTitle(title);
                actionBar.setSubtitle(subtitle);
                actionBar.setDisplayHomeAsUpEnabled(recipe != null);
            }
        }
    }
}
