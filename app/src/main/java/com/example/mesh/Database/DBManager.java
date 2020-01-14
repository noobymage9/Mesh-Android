package com.example.mesh.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.sql.Blob;
import java.text.SimpleDateFormat;
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

    //Messages belonging to 1 user only
    public Cursor fetchMessages(String userID)
    {
        Cursor c = database.rawQuery("SELECT " + DatabaseHelper.MSG_CONTENTS + " FROM "
                + DatabaseHelper.messageTableName + " where " + DatabaseHelper.MSG_USER_ID +
                " = '" + userID + "'", null);
        c.moveToFirst();

        return c;
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
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.CONTACT_NAME, name);
        database.insert(DatabaseHelper.contactsTableName, null, contentValue);
    }

    public Cursor fetchAllContacts()
    {
        Cursor c = database.rawQuery("SELECT * FROM " + DatabaseHelper.contactsTableName + ";",
                null);
        c.moveToFirst();

        return c;
    }

    public Cursor fetchContact(int contactID)
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
