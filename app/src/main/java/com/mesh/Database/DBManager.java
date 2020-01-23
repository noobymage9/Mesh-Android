package com.mesh.Database;

import android.app.ExpandableListActivity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;
import android.util.Log;

import com.mesh.message.Message;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DBManager {
    private DatabaseHelper dbHelper;
    private Context context;
    private SQLiteDatabase database;
    SimpleDateFormat dateFormat = new SimpleDateFormat
            ("yyyy-MM-dd HH:mm:ss zzz yyy", Locale.getDefault());
    SimpleDateFormat time = new SimpleDateFormat("hh:mm a");

    public DBManager(Context c) {
        context = c;
    }

    public DBManager open() throws SQLException
    {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    /****************************/
    /**Message table functions**/
    /***************************/
    public void insertMessage(String userID, String contents, String sourceApp, Date timeStamp)
    {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.MSG_USER_ID, userID);
        contentValue.put(DatabaseHelper.MSG_CONTENTS, contents);
        contentValue.put(DatabaseHelper.MSG_SOURCE_APP, sourceApp);
        contentValue.put(DatabaseHelper.MSG_TIMESTAMP, dateFormat.format(timeStamp));
        database.insert(DatabaseHelper.messageTableName, null, contentValue);
    }

    //Helper function that gets cursor for any entry in Mesages
    private Cursor getMessageTableEntry(String contactName)
    {
        Cursor c = database.rawQuery("SELECT * FROM "
                + DatabaseHelper.messageTableName + " where " + DatabaseHelper.MSG_USER_ID +
                " = '" + contactName + "'", null);
        c.moveToFirst();

        return c;
    }

    private Cursor getMessageTableEntry(int messageID)
    {
        Cursor c = database.rawQuery("SELECT * FROM "
                + DatabaseHelper.messageTableName + " where " + DatabaseHelper.MSG_ID +
                " = '" + messageID + "'", null);
        c.moveToFirst();

        return c;
    }

    //Get all messages for 1 user
    public ArrayList<Message> getMessages(String contactName)
    {
        ArrayList<Message> messages = new ArrayList<>();
        Message m;
        Cursor c = getMessageTableEntry(contactName);

        if (c.moveToFirst()) //c.getCount doesnt work, movetofirst resets cursor when view is created
        {
            do {
                try {
                    m = new Message(
                            c.getString(c.getColumnIndex(DatabaseHelper.MSG_ID)),
                            c.getString(c.getColumnIndex(DatabaseHelper.MSG_USER_ID)),
                            c.getString(c.getColumnIndex(DatabaseHelper.MSG_CONTENTS)),
                            c.getString(c.getColumnIndex(DatabaseHelper.MSG_SOURCE_APP)),
                            dateFormat.parse(c.getString
                                    (c.getColumnIndex(DatabaseHelper.MSG_TIMESTAMP)))
                    );

                    messages.add(m);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } while(c.moveToNext());
        }

        return messages;
    }

    public Message getLatestMessageTime(String contactName)
    {
        ArrayList<Message> messageList = getMessages(contactName);

        if (messageList.size() > 0)
            return messageList.get(messageList.size() - 1);

        /*
        Cursor c = database.rawQuery("SELECT * FROM " +
                DatabaseHelper.messageTableName + " WHERE " + DatabaseHelper.MSG_USER_ID + " = '" +
                contactName + "' ORDER BY CAST(" + DatabaseHelper.MSG_TIMESTAMP +
                " as DATE) DESC LIMIT 1;", null);

        c.moveToFirst();

        try {
            m = new Message(
                    c.getString(c.getColumnIndex(DatabaseHelper.MSG_ID)),
                    c.getString(c.getColumnIndex(DatabaseHelper.MSG_USER_ID)),
                    c.getString(c.getColumnIndex(DatabaseHelper.MSG_CONTENTS)),
                    c.getString(c.getColumnIndex(DatabaseHelper.MSG_SOURCE_APP)),
                    dateFormat.parse(c.getString
                            (c.getColumnIndex(DatabaseHelper.MSG_TIMESTAMP)))
            );
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
         */

        return new Message("","","","", null);
    }

    public String getSourceApp(int messageID)
    {
        Cursor c = getMessageTableEntry(messageID);

        return c.getString(c.getColumnIndex(DatabaseHelper.MSG_SOURCE_APP));
    }

    /*
    public Date getTimeStamp(int messageID)
    {
        Cursor c = getMessageTableEntry(messageID);
        Date d = null;

        try {
            d = dateFormat.parse(c.getString(c.getColumnIndex(DatabaseHelper.MSG_SOURCE_APP)));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return d;
    }
     */

    //Editing existing message
    public int updateMessageTable(String userID, String contents, String SourceApp, Date timeStamp)
    {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.MSG_CONTENTS, contents);
        contentValue.put(DatabaseHelper.MSG_SOURCE_APP, SourceApp);
        contentValue.put(DatabaseHelper.MSG_TIMESTAMP, dateFormat.format(timeStamp));
        int i = database.update(DatabaseHelper.messageTableName, contentValue,
                DatabaseHelper.MSG_USER_ID + " = " + userID, null);
        return i;
    }

    public void deleteFromMessageTable(String userID)
    {
        database.delete(DatabaseHelper.messageTableName,
                DatabaseHelper.MSG_USER_ID + " = " + userID, null);
    }

    /*******************************/
    /**Message Tag table functions**/
    /*******************************/

    public ArrayList<Integer> getTagIDs(int messageID)
    {
        Cursor c = database.rawQuery("SELECT " + DatabaseHelper.MSG_TAG_ID +
                " FROM " + DatabaseHelper.messageTagsTableName + " WHERE " + DatabaseHelper.MSG_ID
                + " = " + messageID + ";", null);
        c.moveToFirst();

        ArrayList<Integer> tagIDs = new ArrayList<>();
        if (c.moveToFirst())
        {
            do {
                tagIDs.add(Integer.parseInt
                        (c.getString(c.getColumnIndex(DatabaseHelper.MSG_TAG_ID))));
            }while(c.moveToNext());
        }

        return tagIDs;
    }

    public void insertTag(int messageID, int tagID)
    {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.MSG_ID, messageID);
        contentValue.put(DatabaseHelper.MSG_TAG_ID, tagID);
        database.insert(DatabaseHelper.messageTagsTableName, null, contentValue);
    }

    public void deleteTag(int messageID, int tagID)
    {
        database.delete(DatabaseHelper.messageTagsTableName,
                DatabaseHelper.MSG_ID + " = '" + messageID + "' AND " +
                        DatabaseHelper.MSG_TAG_ID + " = " + tagID + ");", null);
    }

    /****************************/
    /**Contacts table functions**/
    /****************************/
    public void insertContact(String name, Date timeStamp)
    {
        //Query database for duplicate name
        Cursor c = database.rawQuery("SELECT * FROM " + DatabaseHelper.contactsTableName
                + " WHERE " + DatabaseHelper.CONTACT_NAME + " = '" + name + "';", null);

        if (!c.moveToFirst())
        {
            ContentValues contentValue = new ContentValues();
            contentValue.put(DatabaseHelper.CONTACT_NAME, name);
            contentValue.put(DatabaseHelper.CONTACT_LATEST_TIMESTAMP, dateFormat.format(timeStamp));
            database.insert(DatabaseHelper.contactsTableName, null, contentValue);
        }
        else
        {
            c.moveToFirst();
            updateContactsTable(c.getInt(c.getColumnIndex(DatabaseHelper.CONTACT_ID)),
                    name, timeStamp);
        }
    }

    //Helper function to get cursor for all contacts
    private Cursor getAllContacts()
    {
        Cursor c = database.rawQuery("SELECT * FROM " + DatabaseHelper.contactsTableName + ";",
                null);
        c.moveToFirst();

        return c;
    }

    public ArrayList<String> getAllContactNames()
    {
        Cursor c = getAllContacts();
        ArrayList<String> contactNames = new ArrayList<>();
        if (c.moveToFirst())
        {
            do {
                contactNames.add(c.getString(c.getColumnIndex(DatabaseHelper.CONTACT_NAME)));
            } while (c.moveToNext());
        }

        return contactNames;
    }

    public ArrayList<Integer> getAllContactIDs()
    {
        Cursor c = getAllContacts();
        ArrayList<Integer> contactIDs = new ArrayList<>();
        if (c.moveToFirst())
        {
            do {
                contactIDs.add(c.getInt(c.getColumnIndex(DatabaseHelper.CONTACT_ID)));
            } while (c.moveToNext());
        }

        return contactIDs;
    }

    public ArrayList<Integer> getAllContactLatestTimestamps()
    {
        Cursor c = getAllContacts();
        ArrayList<Integer> contactTimestamps = new ArrayList<>();
        if (c.moveToFirst())
        {
            do {
                contactTimestamps.add(c.getInt(c.getColumnIndex(DatabaseHelper.CONTACT_LATEST_TIMESTAMP)));
            } while (c.moveToNext());
        }

        return contactTimestamps;
    }

    private Cursor getContactTableEntry(String contactName)
    {
        Cursor c = database.rawQuery("SELECT * FROM " + DatabaseHelper.contactsTableName +
                " WHERE " + DatabaseHelper.CONTACT_NAME + " = " + "'" + contactName + "';",
                null);
        c.moveToFirst();

        return c;
    }

    public String getContactLatestMessageTime(String contactName)
    {
        Cursor c = getContactTableEntry(contactName);

        try {
            return time.format(dateFormat.parse(c.getString
                    (c.getColumnIndex(DatabaseHelper.CONTACT_LATEST_TIMESTAMP))));
        }
        catch (Exception e)
        {

        }

        return "";
    }

    public int updateContactsTable(int contactID, String contactName, Date latestMessageDate)
    {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.CONTACT_ID, contactID);
        contentValue.put(DatabaseHelper.CONTACT_NAME, contactName);
        contentValue.put(DatabaseHelper.CONTACT_LATEST_TIMESTAMP,
                dateFormat.format(latestMessageDate));
        int i = database.update(DatabaseHelper.contactsTableName, contentValue,
                DatabaseHelper.CONTACT_ID + " = '" + contactID + "'", null);
        return i;
    }

    public void deleteFromContactsTable(int contactID)
    {
        database.delete(DatabaseHelper.contactsTableName,
                DatabaseHelper.CONTACT_ID + " = " + contactID, null);
    }

    /****************************/
    /**Settings table functions**/
    /****************************/

    public void insertContactSortSetting(int setting)
    {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.SETTINGS_CONTACT_SORT_ORDER, setting);
        database.insert(DatabaseHelper.settingsTableName, null, cv);
    }

    public void insertDeleteNotificationSetting(boolean setting)
    {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.SETTINGS_DELETE_NOTI_ON_STARTUP, setting);
        database.insert(DatabaseHelper.settingsTableName, null, cv);
    }

    //Only 1 entry in settings table, so ID is always 0
    public void updateContactSortSetting(int setting)
    {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.SETTINGS_CONTACT_SORT_ORDER, setting);
        database.update(DatabaseHelper.settingsTableName, cv,
                DatabaseHelper.SETTINGS_TABLE_ID + " = 0", null);
    }

    public int getContactSortSetting()
    {
        Cursor c =  database.rawQuery("SELECT " + DatabaseHelper.SETTINGS_CONTACT_SORT_ORDER +
                " FROM " + DatabaseHelper.settingsTableName + ";", null);
        c.moveToFirst();

        return c.getInt(0);
    }

    //Only 1 entry in settings table, so ID is always 0
    public void updateDeleteNotficationsSetting(boolean setting)
    {
        ContentValues cv = new ContentValues();
        if(setting)
            cv.put(DatabaseHelper.SETTINGS_DELETE_NOTI_ON_STARTUP, 1);
        else
            cv.put(DatabaseHelper.SETTINGS_DELETE_NOTI_ON_STARTUP, 0);
        database.update(DatabaseHelper.settingsTableName, cv,
                DatabaseHelper.SETTINGS_TABLE_ID + " = 1", null);
    }

    public boolean getDeleteNotificationSetting()
    {
        Cursor c =  database.rawQuery("SELECT " +
                DatabaseHelper.SETTINGS_DELETE_NOTI_ON_STARTUP +
                " FROM " + DatabaseHelper.settingsTableName + ";", null);
        c.moveToFirst();

        return c.getInt(0) > 0;
    }

    //Again assuming only 1 entry in settings table
    public void restoreDefaultSettings()
    {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.SETTINGS_CONTACT_SORT_ORDER,
                DatabaseHelper.defaultSortContactSetting);
        cv.put(DatabaseHelper.SETTINGS_DELETE_NOTI_ON_STARTUP,
                DatabaseHelper.defaultDeleteNotificationSetting);
        database.update(DatabaseHelper.settingsTableName, cv,
                DatabaseHelper.SETTINGS_TABLE_ID + " = 1", null);
    }
}
