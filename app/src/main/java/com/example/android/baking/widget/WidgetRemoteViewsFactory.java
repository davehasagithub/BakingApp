package com.example.android.baking.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.android.baking.R;
import com.example.android.baking.data.repo.RecipeRepository;
import com.example.android.baking.data.struct.Recipe;

import timber.log.Timber;

class WidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    final private Context context;
    private Recipe recipe;
    final private int appWidgetId;

    WidgetRemoteViewsFactory(Context context, Intent intent) {
        this.context = context;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onDataSetChanged() {
        Timber.d("onDataSetChanged %d", appWidgetId);
        int widgetRecipeId = PreferenceManager.getDefaultSharedPreferences(context).getInt("widgetRecipeId" + appWidgetId, 0);
        recipe = RecipeRepository.getInstance().loadRecipe(context, widgetRecipeId);
    }

    @Override
    public int getCount() {
        return recipe == null ? 0 : recipe.getIngredients().size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        Timber.d("getViewAt %d %d", appWidgetId, position);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_ingredient_row);
        views.setTextViewText(R.id.appwidget_text1, recipe.getIngredients().get(position).getCombinedAndCleanedIngredientDescription(context));
        // clicks do nothing item-specific for now, just send to the app
        views.setOnClickFillInIntent(R.id.appwidget_text1, new Intent());
        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return (recipe == null ? 0 : recipe.getIngredients().get(position).getId());
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDestroy() {
    }
}
