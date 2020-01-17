package com.example.mesh;

import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import androidx.annotation.RequiresApi;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.mesh.Database.DBManager;
import com.example.mesh.message.MessageActivity;

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

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) { // Reading data from notification
        packageName = sbn.getPackageName();                       // Message source can be obtained from here
        Bundle extras = sbn.getNotification().extras;

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

        currentDate = getCurrentDate();
        if(currentDate == null) return;                       // If android version do not support Calendar

        dbManager.insertMessage(title, text, sourceApp, currentDate);
        dbManager.insertContact(title);
        dbManager.close();

        // Notify HomeFragment and MessageActivity upon receiving new messages
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(com.example.mesh.MainActivity.RECEIVE_JSON));
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

    private Date getCurrentDate() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            return Calendar.getInstance().getTime();
        }
        return null;
    }

    private boolean isContactName(String title) {
        return !title.equals("WhatsApp") &&
                !title.equals("WhatsApp Web") &&
                !title.equals("Line") &&
                !title.equals("Telegram");
    }

}
