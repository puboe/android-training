package com.mercadolibre.puboe.meli.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;

import com.mercadolibre.puboe.meli.SettingsActivity;

/**
 * Created by puboe on 22/07/14.
 */
public class AlarmSetter {

    public static void startAlarm(Context context){
        int time = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString(SettingsActivity.SYNC_FREQ, "0"));
        setAlarm(context, time);
    }

    public static void setAlarm(Context context, double time) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, AlarmBroadcastReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);

        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),(long) (1000 * 60 * time), pi); //Millisec * Second * Minute
    }

    public static void cancelAlarm(Context context) {
        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }
}
