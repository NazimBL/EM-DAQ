package com.example.dell.microtechlab;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by DELL on 08/02/2018.
 */

public class HomeBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        String action=intent.getAction();
        if(action.equals(Intent.ACTION_MAIN)){
            //MainActivity.homeTag=!MainActivity.homeTag;
            Toast.makeText(context,"Yoo",Toast.LENGTH_SHORT).show();
        }
    }
}
