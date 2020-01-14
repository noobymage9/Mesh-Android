package com.example.mesh.message;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    // Table Name
    public static final String messageTableName = "Message";

    // Table columns
    public static final String MSG_USER_ID = "UserID";
    public static final String MSG_CONTENTS = "Message_Contents";
    public static final String MSG_SOURCE_APP = "SourceApp";
    public static final String MSG_TIMESTAMP = "Timestamp";

    // Database info
    static final String databaseName = "Mesh.DB";
    static int databaseVersion = 1;

    //Database creation query
    static final String createTable = "CREATE TABLE "+ messageTableName + "(" +
            MSG_USER_ID + "INTEGER PRIMARY KEY, " +
            MSG_CONTENTS + " TEXT NOT NULL," +
            MSG_SOURCE_APP + " TEXT NOT NULL, " +
            MSG_TIMESTAMP + " DATE NOT NULL);";

    public DatabaseHelper(Context context) {
        super(context, databaseName, null, databaseVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        if (databaseVersion == oldVersion) {
            db.execSQL("drop table if exists " + messageTableName);
            databaseVersion = newVersion;
            onCreate(db);
        }
    }
}