package com.example.android.baking.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

import com.example.android.baking.R;
import com.example.android.baking.data.repo.RecipeRepository;
import com.example.android.baking.data.struct.Recipe;
import com.example.android.baking.ui.MainActivity;

import java.util.Arrays;

import timber.log.Timber;

// references:
// https://developer.android.com/guide/topics/appwidgets/
// https://android.googlesource.com/platform/development/+/master/samples/WeatherListWidget
// https://android.googlesource.com/platform/development/+/master/samples/StackWidget/src/com/example/android/stackwidget
public class BakingWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Timber.d("onUpdate %s", Arrays.toString(appWidgetIds));
        refresh(context);
    }

    public static void updateAppWidgets(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Timber.d("updateAppWidgets %s", Arrays.toString(appWidgetIds));
        for (int appWidgetId : appWidgetIds) {
            int widgetRecipeId = PreferenceManager.getDefaultSharedPreferences(context).getInt("widgetRecipeId" + appWidgetId, -1);

            Recipe recipe = RecipeRepository.getInstance().loadRecipe(context, widgetRecipeId);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);

            views.setTextViewText(R.id.appwidget_text, recipe == null ? "Recipe Not Available" : recipe.recipeDb.getName().toUpperCase());

            final Intent intent = new Intent(context, WidgetRemoteViewsService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            views.setRemoteAdapter(R.id.widget_list_view, intent);

            Intent appIntent = new Intent(context, MainActivity.class);
            PendingIntent appPendingIntent = PendingIntent.getActivity(context, 0, appIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.widget_wrapper, appPendingIntent);
            views.setPendingIntentTemplate(R.id.widget_list_view, appPendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    public static void refresh(Context context) {
        Timber.d("refresh");
        WidgetIntentService.startActionUpdateBakingWidgets(context);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        Timber.d("onDeleted %s", Arrays.toString(appWidgetIds));
        for (int appWidgetId : appWidgetIds) {
            PreferenceManager.getDefaultSharedPreferences(context).edit().remove("widgetRecipeId" + appWidgetId).apply();
        }
    }
}
