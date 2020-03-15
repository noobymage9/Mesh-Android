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
    public static final String usersTableName = "User_Table";
    public static final String messageTableName = "Message";
    public static final String messageTagsTableName = "Message_Tags";
    public static final String contactsTableName = "Contacts";
    public static final String groupsTableName = "Groups";
    public static final String userCollectionsTableName = "User_Collections";
    public static final String settingsTableName = "Settings";
    public static final String messageSearchTableName = "Message_Search";
    public static final String contactMergeStatusTable = "Merged_Contacts";

    /***************************/
    /**Database table columns**/
    /**************************/
    public static final String MSG_ID = "MessageID";
    public static final String MSG_USER_ID = "UserID";
    public static final String MSG_GROUP_ID = "Group_ID";
    public static final String MSG_CONTENTS = "Message_Contents";
    public static final String MSG_SOURCE_APP = "SourceApp";
    public static final String MSG_TIMESTAMP = "Timestamp";

    public static final String MSGTAG_ID = "MessageTagID";
    public static final String MSGTAG_MSG_ID = "MessageTag_Message_ID";
    public static final String MSGTAG_COLLECTION_ID = "Group_Tag_ID";

    public static final String CONTACT_ID = "Contact_ID";
    public static final String CONTACT_PROFILE_PIC = "Profile_Picture";
    public static final String CONTACT_NAME = "Contact_Name";
    public static final String CONTACT_LATEST_TIMESTAMP = "Latest_Message_Timestamp";
    public static final String CONTACT_IS_GROUP = "Is_Group";
    public static final String CONTACT_IS_GROUP_USER = "Is_Group_User";
    public static final String CONTACT_CUSTOM_ORDER = "Custom_Contact_Order";

    public static final String GROUPS_ID = "Group_ID";
    public static final String GROUPS_NAME = "Group_Name";

    public static final String COLLECTIONS_ID = "Collection_ID";
    public static final String COLLECTIONS_NAME = "Collection_Name";

    public static final String SETTINGS_TABLE_ID = "Settings_Table_ID";
    public static final String SETTINGS_CONTACT_SORT_ORDER = "Contact_Sort_Order";
    public static final String SETTINGS_DELETE_NOTI_ON_STARTUP = "Clear_Notifications";
    public static final String SETTINGS_CUSTOM_CONTACT_ORDER = "Custom_Contact_Order_Setting";

    public static final String MERGE_ID = "Merge_Status_ID";

    /**************************/
    /**Default Setting Values**/
    /**************************/
    public static final int defaultSortContactSetting = 0;
    public static final boolean defaultDeleteNotificationSetting = false;
    public static final boolean defaultCustomContactOrder = false;

    /*************************/
    /**Database information**/
    /************************/
    static final String databaseName = "Mesh.DB";
    static int databaseVersion = 30;

    /****************************/
    /**Database table creation**/
    /***************************/

    static final String createMessageTable = "CREATE TABLE "+ messageTableName + "(" +
            MSG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            MSG_GROUP_ID + " INTEGER DEFAULT -1, " +
            MSG_USER_ID + " INTEGER, " +
            MSG_CONTENTS + " TEXT NOT NULL, " +
            MSG_SOURCE_APP + " TEXT NOT NULL, " +
            MSG_TIMESTAMP + " DATE NOT NULL, " +
            "FOREIGN KEY (" + MSG_USER_ID + ") REFERENCES " + contactsTableName + "(" +
            CONTACT_ID + ") ON DELETE CASCADE);";

    static final String createMessageTagsTable = "CREATE TABLE " + messageTagsTableName + "(" +
            MSGTAG_ID  + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            MSGTAG_MSG_ID + " INTEGER, " +
            MSGTAG_COLLECTION_ID + " INTEGER, " +
            "FOREIGN KEY (" + MSGTAG_COLLECTION_ID + ") REFERENCES " + userCollectionsTableName + "(" +
            COLLECTIONS_ID + ")" + "ON DELETE CASCADE);";

    static final String createContactsTable = "CREATE TABLE " + contactsTableName + "(" +
            CONTACT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            CONTACT_NAME + " STRING NOT NULL, " +
            CONTACT_PROFILE_PIC + " STRING, " +
            CONTACT_LATEST_TIMESTAMP + " DATE, " +
            CONTACT_IS_GROUP + " INTEGER, " +
            CONTACT_IS_GROUP_USER + " INTEGER DEFAULT 0, " +
            CONTACT_CUSTOM_ORDER + " INTEGER);";

    static final String createGroupsTable = "CREATE TABLE " + groupsTableName + "(" +
            GROUPS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            GROUPS_NAME + " STRING);";

    static final String createUserCollectionsTable = "CREATE TABLE " + userCollectionsTableName + "(" +
            COLLECTIONS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLLECTIONS_NAME + " STRING);";

    static final String createSearchMessageTable = "CREATE VIRTUAL TABLE " + messageSearchTableName +
            " USING fts4 (" +  MSG_ID + ", " +
            MSG_GROUP_ID + ", " +
            MSG_USER_ID + ", " +
            MSG_CONTENTS + ", " +
            MSG_SOURCE_APP + ", " +
            MSG_TIMESTAMP + ")";

    static final String createSettingsTable = "CREATE TABLE " + settingsTableName + "(" +
            SETTINGS_TABLE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            SETTINGS_CONTACT_SORT_ORDER + " INTEGER, " +
            SETTINGS_DELETE_NOTI_ON_STARTUP + " BOOLEAN, " +
            SETTINGS_CUSTOM_CONTACT_ORDER + " BOOLEAN);";

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(createMessageTable);
        db.execSQL(createContactsTable);
        db.execSQL(createMessageTagsTable);
        db.execSQL(createGroupsTable);
        db.execSQL(createUserCollectionsTable);
        db.execSQL(createSettingsTable);
        db.execSQL(createSearchMessageTable);
        //Initializing default settings for app
        ContentValues cv = new ContentValues();
        cv.put(SETTINGS_CONTACT_SORT_ORDER, defaultSortContactSetting);
        cv.put(SETTINGS_DELETE_NOTI_ON_STARTUP, defaultDeleteNotificationSetting);
        cv.put(SETTINGS_CUSTOM_CONTACT_ORDER, defaultCustomContactOrder);
        db.insert(settingsTableName, null, cv);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        if (databaseVersion == oldVersion) {
            db.execSQL("drop table if exists " + messageTableName);
            db.execSQL("drop table if exists " + contactsTableName);
            db.execSQL("drop table if exists " + messageTableName);
            db.execSQL("drop table if exists " + groupsTableName);
            db.execSQL("drop table if exists " + userCollectionsTableName);
            db.execSQL("drop table if exists " + settingsTableName);
            db.execSQL("drop table if exists " + messageSearchTableName);
            databaseVersion = newVersion;
            onCreate(db);
        }
    }
}