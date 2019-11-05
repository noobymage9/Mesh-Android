package com.example.mesh;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class NotificationService extends NotificationListenerService {

    private Context context;


    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) { // Reading data from notification
        String pack = sbn.getPackageName();
        Bundle extras = sbn.getNotification().extras;
        String title = "";
        String text = "";
        String info = "";

        if (extras.containsKey("android.title")) {
            title = extras.getString("android.title");
        }

        if (extras.containsKey("android.text")) {
            if (extras.getCharSequence("android.text") != null) {
                text = extras.getCharSequence("android.text").toString();
            }
        }
        if (pack != null) {

            Log.i("Package", pack);
            info += "Package: " + pack + "\n";
        }

        if (title != null) {
            Log.i("Title", title);
            info += "Title: " + title + "\n";
        }

        if (text != null) {
            Log.i("Text", text);
            info += "Text: " + text + "\n";
        }

        // Sending results to message activity
        Intent i = new Intent(MessageActivity.RECEIVE_JSON);
        i.putExtra("json", info);
        LocalBroadcastManager.getInstance(this).sendBroadcast(i);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {

    }


}
