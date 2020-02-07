package com.mesh.Database;

import android.content.ContentValues;
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
    public static final String messageTagsTableName = "Message_Tags";
    public static final String contactsTableName = "Contacts";
    public static final String groupsTableName = "Groups";
    public static final String settingsTableName = "Settings";

    /***************************/
    /**Database table columns**/
    /**************************/
    public static final String MSG_ID = "MessageID";
    public static final String MSG_USER_ID = "UserID";
    public static final String MSG_GROUP_ID = "Group_ID";
    public static final String MSG_CONTENTS = "Message_Contents";
    public static final String MSG_SOURCE_APP = "SourceApp";
    public static final String MSG_TIMESTAMP = "Timestamp";

    public static final String MSG_TAG_ID = "MessageTagID";

    public static final String CONTACT_ID = "Contact_ID";
    public static final String CONTACT_PROFILE_PIC = "Profile_Picture";
    public static final String CONTACT_NAME = "Contact_Name";
    public static final String CONTACT_LATEST_TIMESTAMP = "Latest_Message_Timestamp";
    public static final String CONTACT_IS_GROUP = "Is_Group";

    public static final String GROUPS_ID = "Group_ID";
    public static final String GROUPS_NAME = "Group_Name";

    public static final String SETTINGS_TABLE_ID = "Settings_Table_ID";
    public static final String SETTINGS_CONTACT_SORT_ORDER = "Contact_Sort_Order";
    public static final String SETTINGS_DELETE_NOTI_ON_STARTUP = "Clear_Notifications";

    /**************************/
    /**Default Setting Values**/
    /**************************/
    public static final int defaultSortContactSetting = 0;
    public static final boolean defaultDeleteNotificationSetting = false;

    /*************************/
    /**Database information**/
    /************************/
    static final String databaseName = "Mesh.DB";
    static int databaseVersion = 13;

    /****************************/
    /**Database table creation**/
    /***************************/
    static final String createMessageTable = "CREATE TABLE "+ messageTableName + "(" +
            MSG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            MSG_GROUP_ID + " INTEGER DEFAULT -1, " +
            MSG_USER_ID + " STRING, " +
            MSG_CONTENTS + " TEXT NOT NULL, " +
            MSG_SOURCE_APP + " TEXT NOT NULL, " +
            MSG_TIMESTAMP + " DATE NOT NULL, " +
            "FOREIGN KEY (" + MSG_USER_ID + ") REFERENCES " + contactsTableName + "(" +
            CONTACT_NAME + ") ON DELETE CASCADE);";

    static final String createMessageTagsTable = "CREATE TABLE " + messageTagsTableName + "(" +
            MSG_ID + " INTEGER, " +
            MSG_TAG_ID  + " INTEGER, " +
            "PRIMARY KEY (" + MSG_ID + ", " + MSG_TAG_ID + "), " +
            "FOREIGN KEY (" + MSG_ID + ") REFERENCES " + messageTableName + "(" + MSG_ID + ")" +
            "ON DELETE CASCADE);";

    static final String createContactsTable = "CREATE TABLE " + contactsTableName + "(" +
            CONTACT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            CONTACT_NAME + " STRING NOT NULL, " +
            CONTACT_PROFILE_PIC + " BLOB, " +
            CONTACT_LATEST_TIMESTAMP + " DATE, " +
            CONTACT_IS_GROUP + " INTEGER);";

    static final String createGroupsTable = "CREATE TABLE " + groupsTableName + "(" +
            GROUPS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            GROUPS_NAME + " STRING);";

    static final String createSettingsTable = "CREATE TABLE " + settingsTableName + "(" +
            SETTINGS_TABLE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            SETTINGS_CONTACT_SORT_ORDER + " INTEGER, " +
            SETTINGS_DELETE_NOTI_ON_STARTUP + " BOOLEAN);";

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(createMessageTable);
        db.execSQL(createContactsTable);
        db.execSQL(createMessageTagsTable);
        db.execSQL(createGroupsTable);

        db.execSQL(createSettingsTable);
        //Initializing default settings for app
        ContentValues cv = new ContentValues();
        cv.put(SETTINGS_CONTACT_SORT_ORDER, defaultSortContactSetting);
        db.insert(settingsTableName, null, cv);
        cv = new ContentValues();
        cv.put(SETTINGS_DELETE_NOTI_ON_STARTUP, defaultDeleteNotificationSetting);
        db.insert(settingsTableName, null, cv);
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