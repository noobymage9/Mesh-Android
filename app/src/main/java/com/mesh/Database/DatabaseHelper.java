package com.mesh.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(Context context) {
        super(context, databaseName, null, databaseVersion);
    }

    /************************/
    /**Database table names**/
    /************************/
    public static final String messageTableName = "Message";
    public static final String contactsTableName = "Contacts";

    /***************************/
    /**Database table columns**/
    /**************************/
    public static final String MSG_ID = "MessageID";
    public static final String MSG_USER_ID = "UserID";
    public static final String MSG_CONTENTS = "Message_Contents";
    public static final String MSG_SOURCE_APP = "SourceApp";
    public static final String MSG_TIMESTAMP = "Timestamp";

    public static final String CONTACT_ID = "Contact_ID";
    public static final String CONTACT_PROFILE_PIC = "Profile_Picture";
    public static final String CONTACT_NAME = "Contact_Name";

    /*************************/
    /**Database information**/
    /************************/
    static final String databaseName = "Mesh.DB";
    static int databaseVersion = 3;

    /****************************/
    /**Database table creation**/
    /***************************/
    static final String createMessageTable = "CREATE TABLE "+ messageTableName + "(" +
            MSG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            MSG_USER_ID + " STRING, " +
            MSG_CONTENTS + " TEXT NOT NULL, " +
            MSG_SOURCE_APP + " TEXT NOT NULL, " +
            MSG_TIMESTAMP + " DATE NOT NULL);";

    static final String createContactsTable = "CREATE TABLE " + contactsTableName + "(" +
            CONTACT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            CONTACT_NAME + " STRING NOT NULL, " +
            CONTACT_PROFILE_PIC + " BLOB);";

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(createMessageTable);
        db.execSQL(createContactsTable);
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