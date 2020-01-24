package com.mesh;

import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.text.format.DateFormat;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.mesh.Database.DBManager;

import java.util.ArrayList;
import java.util.Date;
import java.time.LocalDate;
import java.util.regex.Pattern;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class NotificationService extends NotificationListenerService {

    private final String ANDROID_TITLE_KEY = "android.title";
    private final String ANDROID_TEXT_KEY = "android.text";
    public static final String WHATSAPP_PACKAGE = "com.whatsapp";
    public static final String TELEGRAM_PACKAGE = "org.telegram.messenger";
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
        // Message source can be obtained from here
        packageName = statusBarNotification.getPackageName();
        Bundle extras = statusBarNotification.getNotification().extras;

        // Important code. Don't Delete. Ensures that we don't reread the same notification that is already active
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
        // Source Application is not a Chat Application
        if (sourceApp.length() == 0) return;

        // Remove initial duplicate and "New Messages" duplicate
        if (sourceApp.equals("WhatsApp") && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            String[] temp = statusBarNotification.getKey().split(Pattern.quote("|"));
            if (temp[3].equals("null")) return;
        }

        // Retrieve Contact Name
        title = getTitle(extras);
        // Mostly is contact name. "Whatsapp" and "Telegram" must be thrown
        if(!isContactName(title)) return;
        // No contact name need to throw
        if(title.length() == 0) return;

        // Retrieve Message Content
        text = getText(extras);
        // No Message Content need to throw
        if(text.length() == 0) return;

        currentDate = getCurrentDate(statusBarNotification);
        Log.e("TEST", "TEST");
        dbManager.insertMessage(title, text, sourceApp, currentDate);
        dbManager.insertContact(title, currentDate);
        dbManager.close();

        // Notify HomeFragment and MessageActivity upon receiving new messages
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(MainActivity.RECEIVE_JSON));
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

    public void notificationDetails(StatusBarNotification statusBarNotification, Bundle extras){
        String TAG = "Test";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            Log.e(TAG, "Notification Key : " + statusBarNotification.getKey());
        }
        Log.e(TAG, "Notification Id : " + statusBarNotification.getId());
        Log.e(TAG, "Notification postTime : " + statusBarNotification.getPostTime());
        Log.e(TAG, "Notification When : " + statusBarNotification.getNotification().when);
        Log.e(TAG, "Notification From : " + statusBarNotification.getPackageName());
        Log.e(TAG, "Notification Title : " + getTitle(extras));
        Log.e(TAG, "Notification Text : " + getText(extras));
    }
}
