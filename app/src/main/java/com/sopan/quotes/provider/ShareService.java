package com.sopan.quotes.provider;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class ShareService extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        int id = intent.getIntExtra("quote_id", -1);
        if (id != -1) {
            String details[] = new DBHelper(context).getQuote(id);
            String authorName = details[0];
            String quoteText = details[1];

            Uri uri = new EAFunctions().createAndSaveImageFromQuote(quoteText, authorName, context);

            new EAFunctions().shareIt(uri, quoteText + "\n\n\t - " + authorName, context);
        }

        if (intent.getBooleanExtra("from_notification", false)) {
            Intent closeDrawer = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            context.sendBroadcast(closeDrawer);
        }
    }

}
