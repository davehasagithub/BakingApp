package com.example.android.baking;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.Nullable;

public class BakingIntentService extends IntentService {

    private static final String ACTION_UPDATE_BAKING_WIDGETS = "com.example.android.baking.action.update_baking_widgets";

    public BakingIntentService() {
        super("BakingIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_UPDATE_BAKING_WIDGETS.equals(action)) {
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
                int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, BakingWidget.class));
                appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list_view);
                BakingWidget.updateAppWidgets(this, appWidgetManager, appWidgetIds);
            }
        }
    }

    public static void startActionUpdateBakingWidgets(Context context) {
        Intent intent = new Intent(context, BakingIntentService.class);
        intent.setAction(ACTION_UPDATE_BAKING_WIDGETS);
        context.startService(intent);
    }
}
