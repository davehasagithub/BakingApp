package com.example.android.baking;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.android.baking.data.repo.RecipeRepository;
import com.example.android.baking.data.struct.Recipe;

import timber.log.Timber;

public class BakingRemoteViewsService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {

        int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
        Bundle extras = intent.getExtras();
        if (extras != null) {
            appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            //appWidgetId = Integer.valueOf(intent.getData().getSchemeSpecificPart());
        }

        return new BakingRemoteViewsFactory(getApplicationContext(), appWidgetId);
    }
}

class BakingRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private Context context;
    private Recipe recipe;
    private int appWidgetId;

    public BakingRemoteViewsFactory(Context context, int appWidgetId) {
        this.context = context;
        this.appWidgetId = appWidgetId;
    }

    @Override
    public void onDataSetChanged() {
        Timber.d("xxx onDataSetChanged");
        int widgetRecipeId = PreferenceManager.getDefaultSharedPreferences(context).getInt("widgetRecipeId" + appWidgetId, 0);
        recipe = RecipeRepository.getInstance().loadRecipe(context, widgetRecipeId);
    }

    @Override
    public int getCount() {
        return recipe == null ? 0 : recipe.getIngredients().size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_row);
        views.setTextViewText(R.id.appwidget_text, recipe.getIngredients().get(position).getCombinedDescription(context));
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