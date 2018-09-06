package com.sopan.quotes.utils;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViewsService;

import com.sopan.quotes.provider.QuoteProvider;


public class QuoterWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Log.d("RVF", "OK");
        int appWidgetId = intent.getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);

        return (new QuoteProvider(getApplicationContext()));
    }

}
