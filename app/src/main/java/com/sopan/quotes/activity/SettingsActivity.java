package com.sopan.quotes.activity;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TimePicker;

import com.sopan.quotes.utils.EANotificationManager;
import com.sopan.quotes.R;

public class SettingsActivity extends AppCompatActivity {

    Switch switchNotification;
    TimePicker timePicker;

    public static String ENABLE_DAILY_NOTIFICATION = "daily_notification";

    @TargetApi(23)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);

        switchNotification = (Switch) findViewById(R.id.switch_daily_quote);
        timePicker = (TimePicker) findViewById(R.id.time_picker);
        try {
            timePicker.setIs24HourView(true);
        } catch (Exception e) {

        }
        final SharedPreferences pref = getSharedPreferences(getPackageName(), MODE_PRIVATE);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            timePicker.setCurrentHour(pref.getInt("hour", 9));
            timePicker.setCurrentMinute(pref.getInt("minute", 0));
        } else {
            timePicker.setHour(pref.getInt("hour", 8));
            timePicker.setMinute(pref.getInt("minute", 0));
        }

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {

                SharedPreferences.Editor editor = pref.edit();
                editor.putInt("hour", hourOfDay);
                editor.putInt("minute", minute);
                editor.apply();
                setAlarm();
            }
        });

        switchNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = pref.edit();
                editor.putBoolean("daily_notification", isChecked);
                editor.apply();
                changeTimePickerState(isChecked);
            }
        });

        changeTimePickerState(pref.getBoolean(ENABLE_DAILY_NOTIFICATION, true));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationIcon(R.drawable.ic_back_icon);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                finish();
            }
        });

        //new EAReceiver().showNotification(getApplicationContext());
    }

    private void changeTimePickerState(boolean state) {
        switchNotification.setChecked(state);
        if (!state) {
            timePicker.setVisibility(View.GONE);
            new EANotificationManager().cancelAlarm(getApplicationContext());
        } else {
            timePicker.setVisibility(View.VISIBLE);
            setAlarm();
        }
    }

    private void setAlarm() {
        EANotificationManager eanm = new EANotificationManager();
        eanm.cancelAlarm(getApplicationContext());
        eanm.setAlarm(getApplicationContext());
    }

}
