package com.example.android.baking.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.example.android.baking.R;

import java.util.Arrays;

import androidx.annotation.Nullable;
import timber.log.Timber;

public class WidgetIntentService extends IntentService {

    private static final String ACTION_UPDATE_BAKING_WIDGETS = "com.example.android.baking.action.update_baking_widgets";

    public WidgetIntentService() {
        super("WidgetIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_UPDATE_BAKING_WIDGETS.equals(action)) {
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
                int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, BakingWidget.class));
                Timber.d("onHandleIntent ACTION_UPDATE_BAKING_WIDGETS %s", Arrays.toString(appWidgetIds));
                appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list_view);
                BakingWidget.updateAppWidgets(this, appWidgetManager, appWidgetIds);
            }
        }
    }

    public static void startActionUpdateBakingWidgets(Context context) {
        Timber.d("startActionUpdateBakingWidgets");
        try {
            Intent intent = new Intent(context, WidgetIntentService.class);
            intent.setAction(ACTION_UPDATE_BAKING_WIDGETS);
            context.startService(intent);
        } catch (IllegalStateException e) {
            // can happen if espresso test triggers a widget update
            Timber.e("caught illegal state");
        }
    }
}
