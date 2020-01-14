package com.example.mesh.message;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

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

    public void insertMessage(String userID, String contents, String sourceApp, Date timeStamp)
    {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.MSG_USER_ID, userID);
        contentValue.put(DatabaseHelper.MSG_CONTENTS, contents);
        contentValue.put(DatabaseHelper.MSG_SOURCE_APP, sourceApp);
        contentValue.put(DatabaseHelper.MSG_TIMESTAMP, dateFormat.format(timeStamp));
        database.insert(DatabaseHelper.messageTableName, null, contentValue);
    }

    public Cursor fetchMessages(String userID)
    {
        String[] columns = new String[] {DatabaseHelper.MSG_CONTENTS, DatabaseHelper.MSG_TIMESTAMP};
        String[] selectionArgs = new String[] {userID};
        Cursor cursor = database.query(DatabaseHelper.messageTableName, columns,
                "DatabaseHelper.MSG_USER_ID =?", selectionArgs, null,
                null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

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

    public void deleteFromMessageTable(String userID) {
        database.delete(DatabaseHelper.messageTableName,
                DatabaseHelper.MSG_USER_ID + " = " + userID, null);
    }
}
