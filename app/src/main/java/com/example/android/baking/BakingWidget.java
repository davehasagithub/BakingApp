package com.example.android.baking;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

import com.example.android.baking.data.struct.Recipe;

import timber.log.Timber;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link BakingWidgetConfigureActivity BakingWidgetConfigureActivity}
 */
public class BakingWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Timber.d("xxx onUpdate");
        // java.lang.IllegalStateException: Not allowed to start service Intent { act=com.example.android.baking.action.update_baking_widgets cmp=com.example.android.baking/.BakingIntentService }: app is in background uid UidRecord{9af51e2 u0a87 RCVR idle change:uncached procs:1 seq(0,0,0)}
        BakingIntentService.startActionUpdateBakingWidgets(context);
    }

    public static void updateAppWidgets(Context context, AppWidgetManager appWidgetManager, Recipe recipe, int[] appWidgetIds) {
        Timber.d("xxx updateAppWidgets");
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
            views.setTextViewText(R.id.appwidget_text, recipe == null ? "Recipe Not Available" : recipe.recipeDb.getName().toUpperCase());
            views.setRemoteAdapter(R.id.widget_list_view, new Intent(context, BakingRemoteViewsService.class).putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId));
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    public static void refresh(Context context) {
        Timber.d("xxx refresh");
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
