package com.example.android.baking;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

import com.example.android.baking.data.repo.RecipeRepository;
import com.example.android.baking.data.struct.Recipe;
import com.example.android.baking.ui.main.MainActivity;
import com.example.android.baking.utilities.EspressoIdlingResource;

import timber.log.Timber;

// adb pull sdcard/Download/Baking_Time__ori_portrait.png .
public class BakingWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        BakingIntentService.startActionUpdateBakingWidgets(context);
    }

    public static void updateAppWidgets(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            int widgetRecipeId = PreferenceManager.getDefaultSharedPreferences(context).getInt("widgetRecipeId" + appWidgetId, -1);

            Recipe recipe = RecipeRepository.getInstance().loadRecipe(context, widgetRecipeId);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);

            views.setTextViewText(R.id.appwidget_text, recipe == null ? "Recipe Not Available" : recipe.recipeDb.getName().toUpperCase());
            views.setRemoteAdapter(R.id.widget_list_view, new Intent(context, BakingRemoteViewsService.class).putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId));

            Intent appIntent = new Intent(context, MainActivity.class);
            PendingIntent appPendingIntent = PendingIntent.getActivity(context, 0, appIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.widget_wrapper, appPendingIntent);
            views.setPendingIntentTemplate(R.id.widget_list_view, appPendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    public static void refresh(Context context) {
        if (EspressoIdlingResource.isInTest()) {
            return;
        }

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, BakingWidget.class));

        Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.setComponent(new ComponentName(context, BakingWidget.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
        context.sendBroadcast(intent);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        Timber.d("xxx onDeleted");
        super.onDeleted(context, appWidgetIds);
        for (int appWidgetId : appWidgetIds) {
            PreferenceManager.getDefaultSharedPreferences(context).edit().remove("widgetRecipeId" + appWidgetId).apply();
        }
    }
}
