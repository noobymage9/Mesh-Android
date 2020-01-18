package com.mesh;

import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import androidx.annotation.RequiresApi;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.mesh.Database.DBManager;

import java.util.ArrayList;
import java.util.Date;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class NotificationService extends NotificationListenerService {

    private final String ANDROID_TITLE_KEY = "android.title";
    private final String ANDROID_TEXT_KEY = "android.text";
    private final String DATE_FORMAT = "MM/dd/yyyy";
    private final String WHATSAPP_PACKAGE = "com.whatsapp";
    private final String TELEGRAM_PACKAGE = "org.telegram.messenger";
    private Context context;
    private String packageName, title, text, sourceApp;
    private Date currentDate;
    private DBManager dbManager;
    ArrayList<Long> time = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onNotificationPosted(StatusBarNotification statusBarNotification) { // Reading data from notification
        packageName = statusBarNotification.getPackageName();                       // Message source can be obtained from here
        Bundle extras = statusBarNotification.getNotification().extras;
                                 // Check for duplicate notification
        long notificationTime = statusBarNotification.getNotification().when;
        if (System.currentTimeMillis() - notificationTime > 3000 ||
                isInArray(time, notificationTime)) {
            return;
        } else {
            time.add(notificationTime);
        }

        title = "";
        text = "";
        sourceApp = "";
        currentDate = null;

        dbManager = new DBManager(this);
        dbManager.open();

        sourceApp = getSourceApp(packageName);
        if (sourceApp.length() == 0) return;                   // Source Application is not a Chat Application

        title = getTitle(extras);                             // Retrieve Contact Name
        if(!isContactName(title)) return;                     // Mostly is contact name. "Whatsapp" and "Telegram" must be thrown
        if(title.length() == 0) return;                       // No contact name need to throw

        text = getText(extras);                               // Retrieve Message Content
        if(text.length() == 0) return;                        // No Message Content need to throw

        currentDate = getCurrentDate(statusBarNotification);

        dbManager.insertMessage(title, text, sourceApp, currentDate);
        dbManager.insertContact(title);
        dbManager.close();

        // Notify HomeFragment and MessageActivity upon receiving new messages
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(com.mesh.MainActivity.RECEIVE_JSON));
    }



    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {

    }

    private String getSourceApp(String pack) {
        switch(pack) {
            case WHATSAPP_PACKAGE: return "WhatsApp";
            case TELEGRAM_PACKAGE: return "Telegram";
            default: return "";
        }
    }

    private String getTitle(Bundle extras) {
        if (extras.containsKey(ANDROID_TITLE_KEY)) {
            return extras.getString(ANDROID_TITLE_KEY);
        }
        return "";
    }

    private String getText(Bundle extras) {
        if (extras.containsKey(ANDROID_TEXT_KEY)) {
            CharSequence charSequence = extras.getCharSequence(ANDROID_TEXT_KEY);
            if (charSequence != null) {
                return charSequence.toString();
            }
        }
        return "";
    }

    private Date getCurrentDate(StatusBarNotification statusBarNotification) {
        return new Date(statusBarNotification.getPostTime());
    }

    private boolean isContactName(String title) {
        return !title.equals("WhatsApp") &&
                !title.equals("WhatsApp Web") &&
                !title.equals("Line") &&
                !title.equals("Telegram");
    }

    private boolean isInArray(ArrayList<Long> time, long when) {
        for (int i = 0; i < time.size(); i++) {
            if (time.get(i) == when) {
                return true;
            }
         }
        return false;
    }


}
