package com.example.dell.microtechlab;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by DELL on 05/02/2018.
 */

public class USB_BroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        if (action.equalsIgnoreCase(MainActivity.ACTION_USB_ATTACHED)) {

        }
    }
}
