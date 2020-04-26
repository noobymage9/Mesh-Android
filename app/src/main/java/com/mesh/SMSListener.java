package com.mesh;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.mesh.Database.DBManager;
import com.mesh.message.MessageActivity;

import java.util.Date;

public class SMSListener extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) {
            int contactID;
            String contactName = "";
            String text;
            Date currentDate;
            String sourceApp = "SMS";
            DBManager dbManager = new DBManager(context);
            dbManager.open();
            for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                contactName = smsMessage.getOriginatingAddress();
                text = smsMessage.getMessageBody();
                currentDate = new Date(smsMessage.getTimestampMillis());

                contactID = dbManager.insertContact(contactName, currentDate, 0, 0);
                dbManager.insertMessage(contactID, text, sourceApp, currentDate);

                Intent myIntent = new Intent(MainActivity.RECEIVE_JSON);
                myIntent.putExtra(MessageActivity.CONTACT_NAME, contactName);
                LocalBroadcastManager.getInstance(context).sendBroadcast(myIntent);

            }
            dbManager.close();

        }
    }
}