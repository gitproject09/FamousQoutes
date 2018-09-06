package com.sopan.quotes.utils;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.sopan.quotes.provider.QuoteProvider;

public class QuoterWidget extends AppWidgetProvider {

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        //called when widgets are deleted
        //see that you get an array of widgetIds which are deleted
        //so handle the delete of multiple widgets in an iteration
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        //runs when all of the instances of the widget are deleted from
        //the home screen
        //here you can do some setup
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        //runs when all of the first instance of the widget are placed
        //on the home screen
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {


        for(int currentWidgetId: appWidgetIds){
            ComponentName widget = new ComponentName(context, QuoterWidget.class);
            appWidgetManager.updateAppWidget(widget, new QuoteProvider(context).getViewAt(currentWidgetId));


        }


        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }






}
