package com.sopan.quotes.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.sopan.quotes.provider.DBHelper;
import com.sopan.quotes.provider.EAFunctions;


public class ShareActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();

        int id = intent.getIntExtra("quote_id", -1);
        if(id!=-1) {
            String details[] = new DBHelper(getApplicationContext()).getQuote(id);
            String authorName = details[0];
            String quoteText = details[1];

            Uri uri = new EAFunctions().createAndSaveImageFromQuote(quoteText, authorName,
                    getApplicationContext());

            new EAFunctions().shareIt(uri, quoteText + "\n\n\t - " + authorName,
                    getApplicationContext());
        }else{
            String details[] = new DBHelper(getApplicationContext()).getRandomQuote();
            String authorName = details[0];
            String quoteText = details[1];

            Uri uri = new EAFunctions().createAndSaveImageFromQuote(quoteText, authorName,
                    getApplicationContext());

            new EAFunctions().shareIt(uri, quoteText + "\n\n\t - " + authorName,
                    getApplicationContext());
        }

        finish();
    }

    @Override
    protected void onResume() {
        finish();
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}
