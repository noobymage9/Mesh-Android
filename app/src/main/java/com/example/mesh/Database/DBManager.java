package com.example.mesh.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.sql.Blob;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DBManager {
    private DatabaseHelper dbHelper;
    private Context context;
    private SQLiteDatabase database;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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
        Cursor c = database.rawQuery("SELECT " + DatabaseHelper.MSG_CONTENTS + " FROM "
                + DatabaseHelper.messageTableName + " where " + DatabaseHelper.MSG_USER_ID +
                " = '" + contactName + "'", null);
        c.moveToFirst();

        return c;
    }

    private Cursor getMessageTableEntry(int messageID)
    {
        Cursor c = database.rawQuery("SELECT " + DatabaseHelper.MSG_CONTENTS + " FROM "
                + DatabaseHelper.messageTableName + " where " + DatabaseHelper.MSG_ID +
                " = '" + messageID + "'", null);
        c.moveToFirst();

        return c;
    }

    //Get all messages for 1 user
    public ArrayList<String> getMessages(String contactName)
    {
        ArrayList<String> messages = new ArrayList<>();
        Cursor c = getMessageTableEntry(contactName);

        if (c.moveToFirst()) //c.getCount doesnt work, movetofirst resets cursor when view is created
        {
            do
            {
                messages.add(c.getString(c.getColumnIndex(DatabaseHelper.MSG_CONTENTS)));
            } while (c.moveToNext());
        }

        return messages;
    }

    public String getSourceApp(int messageID)
    {
        Cursor c = getMessageTableEntry(messageID);

        return c.getString(c.getColumnIndex(DatabaseHelper.MSG_SOURCE_APP));
    }

    public Date getTimeStamp(int messageID)
    {
        Cursor c = getMessageTableEntry(messageID);

        return new Date(c.getShort(c.getColumnIndex(DatabaseHelper.MSG_TIMESTAMP)));
    }

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

    /****************************/
    /**Contacts table functions**/
    /***************************/
    public void insertContact(String name)
    {
        //Query database for duplicate name
        Cursor c = database.rawQuery("SELECT " + DatabaseHelper.CONTACT_NAME +
                " FROM " + DatabaseHelper.contactsTableName
                + " WHERE " + DatabaseHelper.CONTACT_NAME + " = '" + name + "';", null);

        if (!c.moveToFirst())
        {
            ContentValues contentValue = new ContentValues();
            contentValue.put(DatabaseHelper.CONTACT_NAME, name);
            database.insert(DatabaseHelper.contactsTableName, null, contentValue);
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
        ArrayList<Integer> contactNames = new ArrayList<>();
        if (c.moveToFirst())
        {
            do {
                contactNames.add(c.getInt(c.getColumnIndex(DatabaseHelper.CONTACT_ID)));
            } while (c.moveToNext());
        }

        return contactNames;
    }

    private Cursor getContactTableEntry(int contactID)
    {
        Cursor c = database.rawQuery("SELECT * FROM " + DatabaseHelper.contactsTableName +
                " WHERE " + DatabaseHelper.CONTACT_ID + " = " + "'" + contactID + "');",
                null);
        c.moveToFirst();

        return c;
    }

    public int updateContactsTable(String contactID, String contactName)
    {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.CONTACT_ID, contactID);
        contentValue.put(DatabaseHelper.CONTACT_NAME, contactName);
        int i = database.update(DatabaseHelper.contactsTableName, contentValue,
                DatabaseHelper.CONTACT_ID + " = " + contactID, null);
        return i;
    }

    public void deleteFromContactsTable(String contactID)
    {
        database.delete(DatabaseHelper.contactsTableName,
                DatabaseHelper.CONTACT_ID + " = " + contactID, null);
    }
}
