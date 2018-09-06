package com.sopan.quotes.utils;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;

import com.sopan.quotes.R;
import com.sopan.quotes.activity.DailyQuoteActivity;
import com.sopan.quotes.activity.SettingsActivity;
import com.sopan.quotes.activity.ShareActivity;
import com.sopan.quotes.provider.DBHelper;
import com.sopan.quotes.provider.EAFunctions;
import com.sopan.quotes.provider.EAReceiver;

import java.util.Calendar;

public class EANotificationManager {

    public final int NOTIFICATION_ID = 0x142275;

    public void showNotification(Context context) {

        clearNotification(context);
        PowerManager pm = (PowerManager) context
                .getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK, "");
        wl.acquire();
        try{
            DBHelper dbh = new DBHelper(context);
            String values[] = dbh.getRandomQuote();
            String quote = values[1];
            String author = values[0];

            String iconPath = author.replace(" ", "_");
            iconPath = iconPath.replace(".", "_");
            iconPath = iconPath.toLowerCase();

            Resources res = context.getResources();
            int r = res.getIdentifier(iconPath, "mipmap",
                    context.getPackageName());

            Bitmap avatar = new EAFunctions().getRoundBitmap(res, r);

            int qid = Integer.parseInt(values[3]);
            dbh.close();


            Intent shareIntent = new Intent(context, ShareActivity.class);
            shareIntent.putExtra("quote_id", qid);
            shareIntent.putExtra("from_notification", true);
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);



            PendingIntent piShare = PendingIntent.getActivity(context, 0, shareIntent,
                    0);


            Intent dismissIntent = new Intent(context,  EAReceiver.class);
            dismissIntent.setAction("clear");
            dismissIntent.putExtra("nid", qid);
            PendingIntent piDismiss = PendingIntent.getBroadcast(context, 0, dismissIntent, PendingIntent.FLAG_CANCEL_CURRENT);




            Intent viewIntent = new Intent(context, DailyQuoteActivity.class);
            viewIntent.putExtra("quote_id", qid);
            viewIntent.putExtra("dismiss_notification", true);
            PendingIntent pIntent = PendingIntent.getActivity(context, qid,
                    viewIntent, 0);

            NotificationManager notificationManager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);

            Notification noti = new NotificationCompat.Builder(context)
                    .setContentTitle(
                            context.getResources().getString(R.string.app_name))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(avatar)
                    .setLights(Color.BLUE, 300, 300)
                    .setVibrate(new long[]{300, 0, 300, 300})
                    .setContentIntent(pIntent)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(quote + "\n\n - " + author))
                    .addAction(R.drawable.ic_menu_share,
                            "Share"/*getString(R.string.dismiss)*/, piShare)
                    .addAction(android.R.drawable.ic_notification_clear_all,
                            "Dismiss"/*getString(R.string.dismiss)*/, piDismiss)
                    .build();

            notificationManager.notify(NOTIFICATION_ID, noti);
        }catch(Exception e){
            e.printStackTrace();
        }
        setAlarm(context);
        wl.release();
    }



    public void clearNotification(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        try {
            notificationManager.cancel(NOTIFICATION_ID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Calendar getAlarmCalendar(Context context){

        final SharedPreferences pref = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);

        Calendar calendar = Calendar.getInstance();



        int hour = pref.getInt("hour", 7);
        int min = pref.getInt("minute", 0);


        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        if(Calendar.getInstance().after(calendar)){
            calendar.add(Calendar.DATE, 1);
        }

        return calendar;
    }



    public void setAlarm(Context context) {
        Calendar calendar = getAlarmCalendar(context);
        AlarmManager am = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, EAReceiver.class);
        i.setAction("com.easwareapps.quoter.DAILYNOTIFICATION");
        PendingIntent pi = PendingIntent.getBroadcast(context, NOTIFICATION_ID,
                i, PendingIntent.FLAG_UPDATE_CURRENT);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            am.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);
        } else {
            am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);
        }



    }

    public void setAlarmIfNeeded(Context context){


        if(isAlarmEnabled(context)){
            if(!isAlarmUp(context)){
                setAlarm(context);
            }
        }

    }

    private boolean isAlarmEnabled(Context context){
        final SharedPreferences pref = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        return pref.getBoolean(SettingsActivity.ENABLE_DAILY_NOTIFICATION, true);
    }

    private boolean isAlarmUp(Context context){

        /*
            EA TODO
            This alwarys returns true Need to work on this
        */

        Intent intent = new Intent(context, EAReceiver.class)
                .setAction("com.easwareapps.quoter.DAILYNOTIFICATION");
        boolean alarmUp = (PendingIntent.getBroadcast(context, NOTIFICATION_ID,
                intent, PendingIntent.FLAG_NO_CREATE) != null);

        return alarmUp;
    }

    public void cancelAlarm(Context context) {
        Intent intent = new Intent(context, EAReceiver.class)
                .setAction("com.easwareapps.quoter.DAILYNOTIFICATION");
        PendingIntent sender = PendingIntent.getBroadcast(context,
                NOTIFICATION_ID, intent, PendingIntent.FLAG_NO_CREATE);

        AlarmManager alarmManager = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        try {
            alarmManager.cancel(sender);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
