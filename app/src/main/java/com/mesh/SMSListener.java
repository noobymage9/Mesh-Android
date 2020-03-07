package com.mesh;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsMessage;

import com.mesh.Database.DBManager;

import java.util.Date;

public class SMSListener extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) {
            int contactID;
            String contactName, text;
            Date currentDate;
            String sourceApp = "SMS";
            DBManager dbManager = new DBManager(context);
            for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                contactName = smsMessage.getOriginatingAddress();
                text = smsMessage.getMessageBody();
                currentDate = new Date(System.nanoTime());
                contactID = dbManager.insertContact(contactName, currentDate, 0, 0);
                dbManager.insertMessage(contactID, text, sourceApp, currentDate);
            }

        }
    }
}