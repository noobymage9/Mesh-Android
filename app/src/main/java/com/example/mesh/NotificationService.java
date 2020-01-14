package com.example.mesh;

import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.mesh.message.DBManager;
import com.example.mesh.message.MessageActivity;
import com.example.mesh.message.speechBubbleAdaptor;

import java.text.SimpleDateFormat;
import java.util.Date;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class NotificationService extends NotificationListenerService {

    private final String ANDROID_TITLE_KEY = "android.title";
    private final String ANDROID_TEXT_KEY = "android.text";
    private final String DATE_FORMAT = "MM/dd/yyyy";
    private final String WHATSAPP_PACKAGE = "com.whatsapp";
    private final String TELEGRAM_PACKAGE = "org.telegram.messenger";
    private Context context;
    private String pack, title, text, sourceApp;
    private Date currentDate;
    private DBManager dbManager;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) { // Reading data from notification
        pack = sbn.getPackageName();                              // Message source can be obtained from here

        Bundle extras = sbn.getNotification().extras;
        title = "";
        text = "";
        sourceApp = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        Date currentDate = null;

        dbManager = new DBManager(this);
        dbManager.open();

        if (pack.equals(WHATSAPP_PACKAGE))
            sourceApp = "WhatsApp";
        else if (pack.equals(TELEGRAM_PACKAGE))
            sourceApp = "Telegram";

        if (extras.containsKey(ANDROID_TITLE_KEY)) {
            title = extras.getString(ANDROID_TITLE_KEY);
            // Mostly is contact name. "Whatsapp" and "Telegram" must be thrown
        }

        if (title.equals("WhatApp") || title.equals("Line") || title.equals(("Telegram")))
            return;

        if (extras.containsKey(ANDROID_TEXT_KEY)) {               // Retrieve message content
            if (extras.getCharSequence(ANDROID_TEXT_KEY) != null) {
                text = extras.getCharSequence(ANDROID_TEXT_KEY).toString();
            }
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            currentDate = Calendar.getInstance().getTime();
        }

        dbManager.insertMessage(title, text, sourceApp, currentDate);
        dbManager.close();

        // Sending results to message activity

        Intent i = new Intent(MessageActivity.RECEIVE_JSON);
        LocalBroadcastManager.getInstance(this).sendBroadcast(i);

    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {

    }


}
