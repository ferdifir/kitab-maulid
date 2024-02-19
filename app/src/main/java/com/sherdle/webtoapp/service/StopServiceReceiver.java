package com.sherdle.webtoapp.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StopServiceReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && intent.getAction() != null && intent.getAction().equals("ACTION_STOP_SERVICE")) {
            context.stopService(new Intent(context, NotificationSoundService.class));
        }
    }
}
