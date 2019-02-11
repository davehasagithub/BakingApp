package com.example.android.baking.ui.main;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.view.WindowManager;

import com.example.android.baking.R;
import com.example.android.baking.databinding.MainActivityBinding;
import com.example.android.baking.ui.masterdetail.MasterDetailFragment;
import com.example.android.baking.utilities.Event;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    MainActivityBinding binding;
    private MainActivityViewModel viewModel;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("recipeId", viewModel.getRecipeId());
    }

//    public IdlingResource init() {
//        return EspressoIdlingResource.init();
//    }

//    private CountingIdlingResource mCountingIdlingResource;
//    @VisibleForTesting
//    public CountingIdlingResource getIdlingResource() {
//        if (mCountingIdlingResource == null) {
//            mCountingIdlingResource = new CountingIdlingResource("blah");
//        }
//        return mCountingIdlingResource;
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        logScreenSpecs();

        // https://github.com/square/okhttp/issues/2372
        // MainActivity.updateAndroidSecurityProvider(this);

        viewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);

        binding = DataBindingUtil.setContentView(this, R.layout.main_activity);
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
        viewModel.getHandleRecipeClickLiveData().observe(this, new Observer<Event<Boolean>>() {
            @Override
            public void onChanged(Event<Boolean> event) {
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
                            .runOnCommit(new Runnable() {
                                public void run() {
                                    viewModel.clearRecipe();
                                }
                            })
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

    public static float getDpFromPixels(Context c, int px) {
        return (int) (px / c.getResources().getDisplayMetrics().density + 0.5f);
    }

    public void logScreenSpecs() {
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        if (wm != null) {
            Point p = new Point();
            wm.getDefaultDisplay().getSize(p);
            Point pdp = new Point(Math.round(getDpFromPixels(this, p.x)), Math.round(getDpFromPixels(this, p.y)));
            Timber.v("screen specs " + p.x + "," + p.y + "px (" + pdp.x + "," + pdp.y + "dp) density:" + getResources().getDisplayMetrics().density + "x");
        }
    }

//    public static void updateAndroidSecurityProvider(Context context) {
//        try {
//            ProviderInstaller.installIfNeeded(context);
//        } catch (GooglePlayServicesRepairableException e) {
//            GoogleApiAvailability.getInstance().showErrorNotification(context, e.getConnectionStatusCode());
//        } catch (GooglePlayServicesNotAvailableException ignore) {
//        }
//    }
}
