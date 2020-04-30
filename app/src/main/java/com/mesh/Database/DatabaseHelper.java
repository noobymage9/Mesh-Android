package com.mesh.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    DatabaseHelper(Context context) {
        super(context, databaseName, null, databaseVersion);
    }

    /************************/
    /**Database table names**/
    /************************/
    static final String messageTableName = "Message";
    static final String messageTagsTableName = "Message_Tags";
    static final String contactsTableName = "Contacts";
    static final String groupsTableName = "Groups";
    static final String userCollectionsTableName = "User_Collections";
    static final String userCollectionSearchTableName = "Collection_Search";
    static final String settingsTableName = "Settings";
    static final String messageSearchTableName = "Message_Search";
    static final String contactMergeStatusTableName = "Merged_Contact_Status";
    static final String loginDetailsTableName = "Login_Details";

    /***************************/
    /**Database table columns**/
    /**************************/
    static final String MSG_ID = "MessageID";
    static final String MSG_USER_ID = "UserID";
    static final String MSG_GROUP_ID = "Group_ID";
    static final String MSG_CONTENTS = "Message_Contents";
    static final String MSG_SOURCE_APP = "SourceApp";
    static final String MSG_TIMESTAMP = "Timestamp";

    static final String MSGTAG_ID = "MessageTagID";
    static final String MSGTAG_MSG_ID = "MessageTag_Message_ID";
    static final String MSGTAG_COLLECTION_ID = "Group_Tag_ID";

    static final String CONTACT_ID = "Contact_ID";
    static final String CONTACT_PROFILE_PIC = "Profile_Picture";
    static final String CONTACT_NAME = "Contact_Name";
    static final String CONTACT_LATEST_TIMESTAMP = "Latest_Message_Timestamp";
    static final String CONTACT_IS_GROUP = "Is_Group";
    static final String CONTACT_IS_GROUP_USER = "Is_Group_User";
    static final String CONTACT_CUSTOM_ORDER = "Custom_Contact_Order";
    static final String CONTACT_IS_FAVOURITE = "Is_Favourite";
    static final String CONTACT_IS_MERGE_PARENT = "Is_Merge_Parent";
    static final String CONTACT_IS_MERGE_CHILD = "Is_Merge_Child";

    static final String GROUPS_ID = "Group_ID";
    static final String GROUPS_NAME = "Group_Name";

    static final String COLLECTIONS_ID = "Collection_ID";
    static final String COLLECTIONS_NAME = "Collection_Name";

    static final String SETTINGS_TABLE_ID = "Settings_Table_ID";
    static final String SETTINGS_CONTACT_SORT_ORDER = "Contact_Sort_Order";
    static final String SETTINGS_DELETE_NOTI_ON_STARTUP = "Clear_Notifications";
    static final String SETTINGS_CUSTOM_CONTACT_ORDER = "Custom_Contact_Order_Setting";

    static final String MERGE_ID = "Merge_Status_ID";
    static final String MERGE_CHILD_ID = "Merge_Child_ID";
    static final String MERGE_PARENT_ID = "Merge_Parent_ID";

    static final String LOGIN_ID = "Login_Key_ID";
    static final String LOGIN_USER_ID = "Login_User_ID";
    static final String LOGIN_PASSWORD = "Login_Password";

    /**************************/
    /**Default Setting Values**/
    /**************************/
    static final int defaultSortContactSetting = SortSetting.getSettingID(SortSetting.Recency);
    static final boolean defaultDeleteNotificationSetting = false;
    static final boolean defaultCustomContactOrder = false;

    /*************************/
    /**Database information**/
    /************************/
    static final String databaseName = "Mesh.DB";
    static int databaseVersion = 42;

    /****************************/
    /**Database table creation**/
    /***************************/

    private static final String createMessageTable = "CREATE TABLE "+ messageTableName + "(" +
            MSG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            MSG_GROUP_ID + " INTEGER DEFAULT -1, " +
            MSG_USER_ID + " INTEGER, " +
            MSG_CONTENTS + " TEXT NOT NULL, " +
            MSG_SOURCE_APP + " TEXT NOT NULL, " +
            MSG_TIMESTAMP + " DATE NOT NULL, " +
            "FOREIGN KEY (" + MSG_USER_ID + ") REFERENCES " + contactsTableName + "(" +
            CONTACT_ID + ") ON DELETE CASCADE);";

    private static final String createMessageTagsTable = "CREATE TABLE " + messageTagsTableName + "(" +
            MSGTAG_ID  + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            MSGTAG_MSG_ID + " INTEGER, " +
            MSGTAG_COLLECTION_ID + " INTEGER, " +
            "FOREIGN KEY (" + MSGTAG_COLLECTION_ID + ") REFERENCES " + userCollectionsTableName + "(" +
            COLLECTIONS_ID + ")" + "ON DELETE CASCADE);";

    private static final String createContactsTable = "CREATE TABLE " + contactsTableName + "(" +
            CONTACT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            CONTACT_NAME + " STRING NOT NULL, " +
            CONTACT_PROFILE_PIC + " STRING, " +
            CONTACT_LATEST_TIMESTAMP + " DATE, " +
            CONTACT_IS_GROUP + " INTEGER, " +
            CONTACT_IS_GROUP_USER + " INTEGER DEFAULT " + BooleanEnum.getIntValueOfBoolean(false) + ", " +
            CONTACT_CUSTOM_ORDER + " INTEGER, " +
            CONTACT_IS_FAVOURITE + " INTEGER DEFAULT " + BooleanEnum.getIntValueOfBoolean(false) + ", " +
            CONTACT_IS_MERGE_CHILD + " INTEGER DEFAULT " + BooleanEnum.getIntValueOfBoolean(false) + ", " +
            CONTACT_IS_MERGE_PARENT + " INTEGER DEFAULT " + BooleanEnum.getIntValueOfBoolean(false) +");";

    private static final String createGroupsTable = "CREATE TABLE " + groupsTableName + "(" +
            GROUPS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            GROUPS_NAME + " STRING);";

    private static final String createUserCollectionsTable = "CREATE TABLE " + userCollectionsTableName + "(" +
            COLLECTIONS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLLECTIONS_NAME + " STRING);";

    private static final String createSearchMessageTable = "CREATE VIRTUAL TABLE " + messageSearchTableName +
            " USING fts4 (" +  MSG_ID + ", " +
            MSG_GROUP_ID + ", " +
            MSG_USER_ID + ", " +
            MSG_CONTENTS + ", " +
            MSG_SOURCE_APP + ", " +
            MSG_TIMESTAMP + ")";

    private static final String createSearchCollectionsTable = "CREATE VIRTUAL TABLE " + userCollectionsTableName +
            " USING fts4 (" +  COLLECTIONS_ID+ ", " +
            COLLECTIONS_NAME + ")";

    private static final String createContactMergeStatusTable = "CREATE TABLE " + contactMergeStatusTableName + "(" +
            MERGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            MERGE_CHILD_ID + " INTEGER, " +
            MERGE_PARENT_ID + " INTEGER, " +
            "FOREIGN KEY (" + MERGE_CHILD_ID + ") REFERENCES " + contactsTableName + "(" + CONTACT_ID +
            ") ON DELETE CASCADE, " +
            "FOREIGN KEY (" + MERGE_PARENT_ID + ") REFERENCES " + contactsTableName + "(" + CONTACT_ID +
            ") ON DELETE CASCADE);";

    private static final String createSettingsTable = "CREATE TABLE " + settingsTableName + "(" +
            SETTINGS_TABLE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            SETTINGS_CONTACT_SORT_ORDER + " INTEGER, " +
            SETTINGS_DELETE_NOTI_ON_STARTUP + " BOOLEAN, " +
            SETTINGS_CUSTOM_CONTACT_ORDER + " BOOLEAN);";

    private static final String createLoginDetailsTable = "CREATE TABLE " + loginDetailsTableName + "(" +
            LOGIN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            LOGIN_USER_ID + " STRING, " +
            LOGIN_PASSWORD + " STRING);";

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(createMessageTable);
        db.execSQL(createContactsTable);
        db.execSQL(createMessageTagsTable);
        db.execSQL(createGroupsTable);
        db.execSQL(createUserCollectionsTable);
        db.execSQL(createSearchCollectionsTable);
        db.execSQL(createContactMergeStatusTable);
        db.execSQL(createSettingsTable);
        db.execSQL(createSearchMessageTable);
        //Initializing default settings for app
        ContentValues cv = new ContentValues();
        cv.put(SETTINGS_CONTACT_SORT_ORDER, defaultSortContactSetting);
        cv.put(SETTINGS_DELETE_NOTI_ON_STARTUP, defaultDeleteNotificationSetting);
        cv.put(SETTINGS_CUSTOM_CONTACT_ORDER, defaultCustomContactOrder);
        db.insert(settingsTableName, null, cv);
        db.execSQL(createLoginDetailsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        if (databaseVersion != newVersion) {
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