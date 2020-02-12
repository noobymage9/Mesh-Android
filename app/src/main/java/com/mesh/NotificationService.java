package com.mesh;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.mesh.Database.DatabaseHelper;

import java.util.ArrayList;
import java.util.Date;
import java.time.LocalDate;
import java.util.regex.Pattern;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class NotificationService extends NotificationListenerService {

    private final String WHATSAPP_PACKAGE = "com.whatsapp";
    private final String TELEGRAM_PACKAGE = "org.telegram.messenger";
    public static final String RECEIVE_JSON = "NotificationService.RECEIVE_JSON";
    private final String ANDROID_TITLE_KEY = "android.title";
    private final String ANDROID_TEXT_KEY = "android.text";
    private Context context;
    private String packageName, title, text, sourceApp;
    private Date currentDate;
    private DBManager dbManager;
    private ArrayList<Long> time = new ArrayList<>();
    private boolean listenerConnected = false;
    private LocalBroadcastManager localBroadcastManager;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onReceive(Context context, Intent intent) {
            if (listenerConnected)
                deleteAllNotification();
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        initialiseLocalBroadcastManager();
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
            if (temp[3].equals("null") && !title.contains(":")) {
                Log.e("TEST", "TEST");
                return;
            }
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

        //getting current date and time
        currentDate = getCurrentDate(statusBarNotification);

        String groupName, contactName;

        if (isGroupName(title)) {
            if (title.contains("("))
                groupName = title.substring(0, title.indexOf("(") - 1);
            else
                groupName = title.substring(0, title.indexOf(":"));

            contactName = title.substring(title.indexOf(":") + 2);

            dbManager.insertGroup(groupName);
            dbManager.insertContact(groupName, currentDate, 1);
            dbManager.insertContact(contactName, currentDate, 0);
            dbManager.insertMessage(dbManager.getLatestContactID(), dbManager.getGroupID(groupName),
                    text, sourceApp, currentDate);
        }
        else {
            contactName = title;
            dbManager.insertContact(contactName, currentDate, 0);
            dbManager.insertMessage(dbManager.getLatestContactID(), text, sourceApp, currentDate);
        }

        dbManager.close();

        // Notify HomeFragment and MessageActivity upon receiving new messages
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(MainActivity.RECEIVE_JSON));
    }



    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
    }

    @Override
    public void onListenerConnected() {
        listenerConnected = true;
    }

    @Override
    public void onListenerDisconnected() {
        super.onListenerDisconnected();
        listenerConnected = false;
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

    private boolean isGroupName(String title) {
        return title.contains(":");
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void deleteAllNotification() {
        StatusBarNotification[] statusBarNotification = this.getActiveNotifications();
        for (StatusBarNotification barNotification : statusBarNotification) {
            String temp = barNotification.getPackageName();
            if (temp.equals(WHATSAPP_PACKAGE) ||
                    temp.equals(TELEGRAM_PACKAGE)) {
                this.cancelNotification(barNotification.getKey());
            }
        }
    }

    private void initialiseLocalBroadcastManager() {
        localBroadcastManager = LocalBroadcastManager.getInstance(this.getApplication());
        localBroadcastManager.registerReceiver(broadcastReceiver, new IntentFilter(RECEIVE_JSON));
    }
}
