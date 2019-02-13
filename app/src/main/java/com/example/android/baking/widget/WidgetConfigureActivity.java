package com.example.android.baking.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.example.android.baking.R;
import com.example.android.baking.databinding.WidgetConfigureActivityBinding;
import com.example.android.baking.ui.MainActivityViewModel;
import com.example.android.baking.ui.recipe.RecipeFragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

public class WidgetConfigureActivity extends AppCompatActivity {

    private MainActivityViewModel viewModel;

    private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    public WidgetConfigureActivity() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setResult(RESULT_CANCELED);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

        viewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);

        WidgetConfigureActivityBinding binding = DataBindingUtil.setContentView(this, R.layout.widget_configure_activity);
        binding.setLifecycleOwner(this);

        if (savedInstanceState == null) {
            // reuse recipe fragment here
            getSupportFragmentManager().beginTransaction().add(R.id.recipe_container, RecipeFragment.newInstance(), "recipe").commit();
        }

        viewModel.init(-1);

        addViewModelObservers();
    }

    private void addViewModelObservers() {
        viewModel.getHandleRecipeClickLiveData().observe(this, event -> {
            if (event != null && Boolean.TRUE.equals(event.getContentIfNotHandled())) {
                Context context = WidgetConfigureActivity.this;
                PreferenceManager.getDefaultSharedPreferences(context).edit().putInt("widgetRecipeId" + appWidgetId, viewModel.getRecipeId()).apply();
                BakingWidget.refresh(context);
                Intent resultValue = new Intent();
                resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                setResult(RESULT_OK, resultValue);
                finish();
            }
        });
    }
}
