package com.example.android.baking;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.example.android.baking.databinding.WidgetConfigureBinding;
import com.example.android.baking.ui.main.MainActivityViewModel;
import com.example.android.baking.ui.main.RecipeFragment;
import com.example.android.baking.utilities.Event;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

public class BakingWidgetConfigureActivity extends AppCompatActivity {

    WidgetConfigureBinding binding;
    private MainActivityViewModel viewModel;

    int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    public BakingWidgetConfigureActivity() {
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

        binding = DataBindingUtil.setContentView(this, R.layout.widget_configure);
        binding.setLifecycleOwner(this);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.recipe_container, RecipeFragment.newInstance(), "recipe").commit();
        }

        viewModel.init(-1);

        addViewModelObservers();
    }

    private void addViewModelObservers() {
        viewModel.getHandleRecipeClickLiveData().observe(this, new Observer<Event<Boolean>>() {
            @Override
            public void onChanged(Event<Boolean> event) {
                if (event != null && Boolean.TRUE.equals(event.getContentIfNotHandled())) {
                    Context context = BakingWidgetConfigureActivity.this;
                    PreferenceManager.getDefaultSharedPreferences(context).edit().putInt("widgetRecipeId" + appWidgetId, viewModel.getRecipeId()).apply();
                    BakingWidget.refresh(context);
                    Intent resultValue = new Intent();
                    resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                    setResult(RESULT_OK, resultValue);
                    finish();
                }
            }
        });
    }
}
