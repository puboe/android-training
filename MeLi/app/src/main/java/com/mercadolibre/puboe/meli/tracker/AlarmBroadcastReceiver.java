package com.mercadolibre.puboe.meli.tracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by puboe on 22/07/14.
 */
public class AlarmBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent newIntent = new Intent(context, ItemTrackerService.class);
        context.startService(newIntent);
    }
}
