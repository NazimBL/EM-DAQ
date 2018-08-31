package com.example.dell.microtechlab;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by DELL on 20/01/2018.
 */

public class MyService extends Service{
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        startActivity(new Intent(this,MainActivity.class));
        return null;
    }
}
