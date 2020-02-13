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
import com.mesh.message.MessageActivity;
import com.mesh.message.UserCollection;
import com.mesh.ui.home.Contact;

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

    public DBManager open() throws SQLException {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    //This table is only for contacts that exist only in groups and are not actual contacts on the
    //user's phone. ALSO CURRENTLY NOT IN USE
    /*************************/
    /**User table functions**/
    /************************/
    /*
    public void insertUser(String userName)
    {
        //Query database for duplicate name
        Cursor c = database.rawQuery("SELECT * FROM " + DatabaseHelper.usersTableName
                + " WHERE " + DatabaseHelper.USER_NAME + " = '" + userName + "';", null);

        if (!c.moveToFirst()) {
            ContentValues contentValue = new ContentValues();
            contentValue.put(DatabaseHelper.USER_NAME, userName);
            database.insert(DatabaseHelper.usersTableName, null, contentValue);
        }
    }

    private Cursor getLatestUserEntry()
    {
        Cursor c = database.rawQuery("SELECT * FROM " + DatabaseHelper.usersTableName +
                " ORDER BY " + DatabaseHelper.USER_ID + " DESC LIMIT 1", null);
        c.moveToFirst();

        return c;
    }

    public int getLatestUserID()
    {
        Cursor c = getLatestUserEntry();

        return c.getInt(c.getColumnIndex(DatabaseHelper.USER_ID));
    }

    private Cursor getUserEntry(int userID)
    {
        Cursor c = database.rawQuery("SELECT * FROM " +
                DatabaseHelper.usersTableName + " WHERE " + DatabaseHelper.USER_ID + " = " +
                userID, null);
        c.moveToFirst();

        return c;
    }

    private Cursor getUserEntry(String userName)
    {
        Cursor c = database.rawQuery("SELECT * FROM " +
                DatabaseHelper.usersTableName + " WHERE " + DatabaseHelper.USER_NAME + " = '" +
                userName + "'", null);
        c.moveToFirst();

        return c;
    }

    public String getUserName(int userID)
    {
        Cursor c = getUserEntry(userID);

        return c.getString(c.getColumnIndex(DatabaseHelper.USER_NAME));
    }

    public int getUserID(String userName)
    {
        Cursor c = getUserEntry(userName);

        return c.getInt(c.getColumnIndex(DatabaseHelper.USER_ID));
    }
     */

    /****************************/
    /**Message table functions**/
    /***************************/

    //Message from individuals
    public void insertMessage(int userID, String contents, String sourceApp, Date timeStamp) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.MSG_USER_ID, userID);
        contentValue.put(DatabaseHelper.MSG_CONTENTS, contents);
        contentValue.put(DatabaseHelper.MSG_SOURCE_APP, sourceApp);
        contentValue.put(DatabaseHelper.MSG_TIMESTAMP, dateFormat.format(timeStamp));
        database.insert(DatabaseHelper.messageTableName, null, contentValue);
    }

    //Message from groups
    public void insertMessage(int userID, int groupID,
                              String contents, String sourceApp, Date timeStamp) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.MSG_GROUP_ID, groupID);
        contentValue.put(DatabaseHelper.MSG_USER_ID, userID);
        contentValue.put(DatabaseHelper.MSG_CONTENTS, contents);
        contentValue.put(DatabaseHelper.MSG_SOURCE_APP, sourceApp);
        contentValue.put(DatabaseHelper.MSG_TIMESTAMP, dateFormat.format(timeStamp));
        database.insert(DatabaseHelper.messageTableName, null, contentValue);
    }

    //All messages from individual
    private Cursor getAllMessagesFromUserDB(int userID) {
        Cursor c = database.rawQuery("SELECT * FROM "
                + DatabaseHelper.messageTableName + " where " + DatabaseHelper.MSG_USER_ID +
                " = " + userID, null);
        c.moveToFirst();

        return c;
    }

    //All messages from group
    private Cursor getAllMessagesFromGroupDB(int groupID) {
        Cursor c = database.rawQuery("SELECT * FROM "
                + DatabaseHelper.messageTableName + " where " + DatabaseHelper.MSG_GROUP_ID +
                " = " + groupID, null);
        c.moveToFirst();

        return c;
    }

    private Cursor getMessageTableEntry(int messageID) {
        Cursor c = database.rawQuery("SELECT * FROM "
                + DatabaseHelper.messageTableName + " where " + DatabaseHelper.MSG_ID +
                " = " + messageID, null);
        c.moveToFirst();

        return c;
    }

    private boolean isGroupMessage(int messageID) {
        Cursor c = getMessageTableEntry(messageID);

        if (c.getInt(c.getColumnIndex(DatabaseHelper.MSG_GROUP_ID)) > 0)
            return true;

        return false;
    }


    private Message constructMessage(Cursor c) {
        try {
            return new Message(
                    c.getInt(c.getColumnIndex(DatabaseHelper.MSG_ID)),
                    getContactName(c.getInt(c.getColumnIndex(DatabaseHelper.MSG_USER_ID))),
                    c.getString(c.getColumnIndex(DatabaseHelper.MSG_GROUP_ID)),
                    c.getString(c.getColumnIndex(DatabaseHelper.MSG_CONTENTS)),
                    c.getString(c.getColumnIndex(DatabaseHelper.MSG_SOURCE_APP)),
                    dateFormat.parse(c.getString
                            (c.getColumnIndex(DatabaseHelper.MSG_TIMESTAMP))));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    //Get all messages for 1 user
    public ArrayList<Message> getMessages(int contactID) {
        ArrayList<Message> messages = new ArrayList<>();
        Message m;
        Cursor c;
        if (isGroup(contactID))
            c = getAllMessagesFromGroupDB(contactID);
        else
            c = getAllMessagesFromUserDB(contactID);

        if (c.moveToFirst()) //c.getCount doesnt work, movetofirst resets cursor when view is created
        {
            do {
                try {
                    m = constructMessage(c);
                    messages.add(m);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } while (c.moveToNext());
        }

        return messages;
    }

    public String getSourceApp(int messageID) {
        Cursor c = getMessageTableEntry(messageID);

        return c.getString(c.getColumnIndex(DatabaseHelper.MSG_SOURCE_APP));
    }

    //Editing existing content
    public int updateMessageTable(String userID, String contents, String SourceApp, Date timeStamp) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.MSG_CONTENTS, contents);
        contentValue.put(DatabaseHelper.MSG_SOURCE_APP, SourceApp);
        contentValue.put(DatabaseHelper.MSG_TIMESTAMP, dateFormat.format(timeStamp));
        int i = database.update(DatabaseHelper.messageTableName, contentValue,
                DatabaseHelper.MSG_USER_ID + " = '" + userID + "'", null);
        return i;
    }

    public void deleteFromMessageTable(int messageID) {
        database.delete(DatabaseHelper.messageTableName,
                DatabaseHelper.MSG_ID + " = " + messageID, null);
    }

    /*******************************/
    /**Message Tag table functions**/
    /*******************************/

    public ArrayList<Integer> getTagIDs(int messageID) {
        Cursor c = database.rawQuery("SELECT " + DatabaseHelper.MSGTAG_ID +
                " FROM " + DatabaseHelper.messageTagsTableName + " WHERE " + DatabaseHelper.MSG_ID
                + " = " + messageID + ";", null);
        c.moveToFirst();

        ArrayList<Integer> tagIDs = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                tagIDs.add(c.getInt(c.getColumnIndex(DatabaseHelper.MSGTAG_ID)));
            } while (c.moveToNext());
        }

        return tagIDs;
    }

    public ArrayList<Message> getMessagesInUserCollection(int collectionID) {
        Cursor collectionsTableCursor = database.rawQuery("SELECT " +
                DatabaseHelper.MSGTAG_MSG_ID + " FROM " + DatabaseHelper.messageTagsTableName +
                " WHERE " + DatabaseHelper.MSGTAG_COLLECTION_ID +
                " = " + collectionID, null);
        collectionsTableCursor.moveToFirst();
        ArrayList<Message> messages = new ArrayList<>();
        Message currentMessage;
        Cursor messageTableCursor;

        if (collectionsTableCursor.moveToFirst()) {
            do {
                messageTableCursor = getMessageTableEntry
                        (collectionsTableCursor.getInt
                                (collectionsTableCursor.getColumnIndex(DatabaseHelper.MSGTAG_MSG_ID)));
                currentMessage = constructMessage(messageTableCursor);
                messages.add(currentMessage);
            } while (collectionsTableCursor.moveToNext());
        }

        return messages;
    }

    public void insertTag(int messageID, int collectionID) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.MSGTAG_MSG_ID, messageID);
        contentValue.put(DatabaseHelper.MSGTAG_COLLECTION_ID, collectionID);
        database.insert(DatabaseHelper.messageTagsTableName, null, contentValue);
    }

    public void deleteTag(int tagID) {
        database.delete(DatabaseHelper.messageTagsTableName,
                DatabaseHelper.MSGTAG_ID + " = " + tagID, null);
    }

    /************************************/
    /**User Collections table functions**/
    /************************************/

    public void insertUserCollection(String collectionName) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.COLLECTIONS_NAME, collectionName);
        database.insert(DatabaseHelper.userCollectionsTableName, null, contentValue);
    }

    public ArrayList<UserCollection> getAllUserCollections() {
        Cursor c = database.rawQuery("SELECT * FROM " +
                DatabaseHelper.userCollectionsTableName, null);
        c.moveToFirst();
        ArrayList<UserCollection> userCollections = new ArrayList<>();
        UserCollection currentCollection;

        if (c.moveToFirst()) {
            do {
                currentCollection = new UserCollection
                        (c.getInt(c.getColumnIndex(DatabaseHelper.COLLECTIONS_ID)),
                                c.getString(c.getColumnIndex(DatabaseHelper.COLLECTIONS_NAME)));
                userCollections.add(currentCollection);
            } while (c.moveToNext());
        }

        return userCollections;
    }

    public void deleteUserCollection(int userCollectionID) {
        database.delete(DatabaseHelper.userCollectionsTableName,
                DatabaseHelper.COLLECTIONS_ID + " = '" + userCollectionID, null);
    }

    /****************************/
    /**Contacts table functions**/
    /****************************/
    public void insertContact(String name, Date timeStamp, int isGroup, int isGroupUser) {
        //Query database for duplicate name
        Cursor c = database.rawQuery("SELECT * FROM " + DatabaseHelper.contactsTableName
                + " WHERE " + DatabaseHelper.CONTACT_NAME + " = '" + name + "';", null);

        if (!c.moveToFirst()) {
            ContentValues contentValue = new ContentValues();
            contentValue.put(DatabaseHelper.CONTACT_NAME, name);
            contentValue.put(DatabaseHelper.CONTACT_LATEST_TIMESTAMP, dateFormat.format(timeStamp));
            contentValue.put(DatabaseHelper.CONTACT_IS_GROUP, isGroup);
            contentValue.put(DatabaseHelper.CONTACT_IS_GROUP_USER, isGroupUser);
            database.insert(DatabaseHelper.contactsTableName, null, contentValue);
        } else {
            c.moveToFirst();
            updateContactsTable(c.getInt(c.getColumnIndex(DatabaseHelper.CONTACT_ID)),
                    name, timeStamp);
        }
    }

    //only for individual user, no point doing for group
    public ArrayList<String> getContactMostUsedSourceApps(int userID) {
        Cursor c = database.rawQuery("SELECT * FROM " +
                DatabaseHelper.messageTableName + " ORDER BY (SELECT COUNT(" +
                DatabaseHelper.MSG_SOURCE_APP + ") FROM " + DatabaseHelper.messageTableName +
                " WHERE " + DatabaseHelper.MSG_USER_ID + " = " + userID + ") DESC GROUP BY " +
                DatabaseHelper.MSG_SOURCE_APP, null);
        c.moveToFirst();

        ArrayList<String> userSourceApps = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                userSourceApps.add(c.getString(c.getColumnIndex(DatabaseHelper.MSG_SOURCE_APP)));
            } while (c.moveToNext());
        }

        return userSourceApps;
    }

    private Cursor getContactDB(int contactID) {
        Cursor c = database.rawQuery("SELECT * FROM " + DatabaseHelper.contactsTableName +
                " WHERE " + DatabaseHelper.CONTACT_ID + " = " + contactID, null);
        c.moveToFirst();

        return c;
    }

    private Cursor getContactDB(String contactName) {
        Cursor c = database.rawQuery("SELECT * FROM " + DatabaseHelper.contactsTableName +
                " WHERE " + DatabaseHelper.CONTACT_NAME + " = '" + contactName + "'", null);
        c.moveToFirst();

        return c;
    }

    public int getContactID(String contactName) {
        Cursor c = getContactDB(contactName);

        return c.getInt(c.getColumnIndex(DatabaseHelper.CONTACT_ID));
    }

    private Cursor getLatestContactEntry() {
        Cursor c = database.rawQuery("SELECT * FROM " + DatabaseHelper.contactsTableName +
                " ORDER BY " + DatabaseHelper.CONTACT_ID + " DESC LIMIT 1", null);
        c.moveToFirst();

        return c;
    }

    private Cursor getAllContactsDB() {
        Cursor c = database.rawQuery("SELECT * FROM " + DatabaseHelper.contactsTableName,
                null);
        c.moveToFirst();

        return c;
    }

    //Helper function to get cursor for all contacts
    private Cursor getAllContactsSortByRecency() {
        Cursor c = database.rawQuery("SELECT * FROM " + DatabaseHelper.contactsTableName
                        + " ORDER BY " + DatabaseHelper.CONTACT_LATEST_TIMESTAMP + " DESC ",
                null);
        c.moveToFirst();

        return c;
    }

    private Cursor getAllContactsSortByFrequency() {
        Cursor c = database.rawQuery("SELECT * FROM " + DatabaseHelper.contactsTableName
                + " ORDER BY (SELECT COUNT(*) FROM " + DatabaseHelper.messageTableName +
                " WHERE " + DatabaseHelper.messageTableName + "." + DatabaseHelper.MSG_USER_ID
                + " = " + DatabaseHelper.contactsTableName + "." + DatabaseHelper.CONTACT_NAME
                + ") DESC ", null);
        c.moveToFirst();

        return c;
    }

    public String getContactName(int contactID) {
        Cursor c = getContactDB(contactID);

        return c.getString(c.getColumnIndex(DatabaseHelper.CONTACT_NAME));
    }

    public ArrayList<Contact> getAllContacts(SortSetting setting) {
        Cursor c;
        switch (setting) {
            case Recency:
                c = getAllContactsSortByRecency();
                break;
            case Frequency:
                c = getAllContactsSortByFrequency();
                break;
            default:
                c = getAllContactsDB();
                break;
        }

        ArrayList<Contact> contacts = new ArrayList<>();
        int isGroupUser;
        Contact currentContact;
        if (c.moveToFirst()) {
            do {
                currentContact = new Contact(c.getInt(c.getColumnIndex(DatabaseHelper.CONTACT_ID)),
                        c.getBlob(c.getColumnIndex(DatabaseHelper.CONTACT_PROFILE_PIC)),
                        c.getString(c.getColumnIndex(DatabaseHelper.CONTACT_NAME)));
                isGroupUser = c.getInt(c.getColumnIndex(DatabaseHelper.CONTACT_IS_GROUP_USER));
                if (isGroupUser != 0)
                    contacts.add(currentContact);
            } while (c.moveToNext());
        }

        return contacts;
    }

    public int getLatestContactID() {
        Cursor c = getLatestContactEntry();

        return c.getInt(c.getColumnIndex(DatabaseHelper.CONTACT_ID));
    }

    public ArrayList<Integer> getAllContactIDs() {
        Cursor c = getAllContactsDB();
        ArrayList<Integer> contactIDs = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                contactIDs.add(c.getInt(c.getColumnIndex(DatabaseHelper.CONTACT_ID)));
            } while (c.moveToNext());
        }

        return contactIDs;
    }

    public ArrayList<Integer> getAllContactLatestTimestamps() {
        Cursor c = getAllContactsDB();
        ArrayList<Integer> contactTimestamps = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                contactTimestamps.add(c.getInt(c.getColumnIndex(DatabaseHelper.CONTACT_LATEST_TIMESTAMP)));
            } while (c.moveToNext());
        }

        return contactTimestamps;
    }

    private Cursor getContactTableEntry(int contactID) {
        Cursor c = database.rawQuery("SELECT * FROM " + DatabaseHelper.contactsTableName +
                        " WHERE " + DatabaseHelper.CONTACT_ID + " = " + "'" + contactID + "';",
                null);
        c.moveToFirst();

        return c;
    }

    public String getContactLatestMessageTime(int contactID) {
        Cursor c = getContactTableEntry(contactID);

        try {
            return time.format(dateFormat.parse(c.getString
                    (c.getColumnIndex(DatabaseHelper.CONTACT_LATEST_TIMESTAMP))));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public boolean isGroup(int contactID) {
        Cursor c = database.rawQuery("SELECT " + DatabaseHelper.CONTACT_IS_GROUP + " FROM " +
                DatabaseHelper.contactsTableName + " WHERE " + DatabaseHelper.CONTACT_ID +
                " = " + contactID, null);
        if (c.moveToFirst())
            return c.getInt(c.getColumnIndex(DatabaseHelper.CONTACT_IS_GROUP)) == 1;

        return false;
    }

    public int updateContactsTable(int contactID, String contactName, Date latestMessageDate) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.CONTACT_ID, contactID);
        contentValue.put(DatabaseHelper.CONTACT_NAME, contactName);
        contentValue.put(DatabaseHelper.CONTACT_LATEST_TIMESTAMP,
                dateFormat.format(latestMessageDate));
        int i = database.update(DatabaseHelper.contactsTableName, contentValue,
                DatabaseHelper.CONTACT_ID + " = '" + contactID + "'", null);
        return i;
    }

    public void deleteFromContactsTable(int contactID) {
        database.delete(DatabaseHelper.contactsTableName,
                DatabaseHelper.CONTACT_ID + " = '" + contactID + "'", null);
    }

    /*************************/
    /**Group table functions**/
    /*************************/

    public void insertGroup(String groupName) {
        //Query database for duplicate name
        Cursor c = database.rawQuery("SELECT * FROM " + DatabaseHelper.groupsTableName
                + " WHERE " + DatabaseHelper.GROUPS_NAME + " = '" + groupName + "';", null);

        if (!c.moveToFirst()) {
            ContentValues cv = new ContentValues();
            cv.put(DatabaseHelper.GROUPS_NAME, groupName);
            database.insert(DatabaseHelper.groupsTableName, null, cv);
        }
    }

    private Cursor getGroupEntry(int groupID) {
        Cursor c = database.rawQuery("SELECT " + DatabaseHelper.GROUPS_NAME + " FROM " +
                DatabaseHelper.groupsTableName + " WHERE " + DatabaseHelper.GROUPS_ID + " = " +
                groupID, null);
        c.moveToFirst();

        return c;
    }

    private Cursor getGroupEntry(String groupName) {
        Cursor c = database.rawQuery("SELECT " + DatabaseHelper.GROUPS_NAME + " FROM " +
                DatabaseHelper.groupsTableName + " WHERE " + DatabaseHelper.GROUPS_NAME + " = '" +
                groupName + "'", null);
        c.moveToFirst();

        return c;
    }

    public String getGroupName(int groupID) {
        Cursor c = getGroupEntry(groupID);

        return c.getString(c.getColumnIndex(DatabaseHelper.GROUPS_NAME));
    }

    public int getGroupID(String groupName) {
        Cursor c = getGroupEntry(groupName);

        return c.getInt(c.getColumnIndex(DatabaseHelper.GROUPS_ID));
    }

    private Cursor getLatestGroupEntry() {
        Cursor c = database.rawQuery("SELECT * FROM " + DatabaseHelper.groupsTableName +
                " ORDER BY " + DatabaseHelper.GROUPS_ID + " DESC LIMIT 1", null);
        c.moveToFirst();

        return c;
    }

    public int getLatestGroupID() {
        Cursor c = getLatestGroupEntry();

        return c.getInt(c.getColumnIndex(DatabaseHelper.GROUPS_ID));
    }

    /****************************/
    /**Settings table functions**/
    /****************************/

    public void insertContactSortSetting(int setting) {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.SETTINGS_CONTACT_SORT_ORDER, setting);
        database.insert(DatabaseHelper.settingsTableName, null, cv);
    }

    public void insertDeleteNotificationSetting(boolean setting) {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.SETTINGS_DELETE_NOTI_ON_STARTUP, setting);
        database.insert(DatabaseHelper.settingsTableName, null, cv);
    }

    //Only 1 entry in settings table, so ID is always 0
    public void updateContactSortSetting(SortSetting setting) {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.SETTINGS_CONTACT_SORT_ORDER, SortSetting.getSettingID(setting));
        database.update(DatabaseHelper.settingsTableName, cv,
                DatabaseHelper.SETTINGS_TABLE_ID + " = 1", null);
    }

    public SortSetting getContactSortSetting() {
        Cursor c = database.rawQuery("SELECT " + DatabaseHelper.SETTINGS_CONTACT_SORT_ORDER +
                " FROM " + DatabaseHelper.settingsTableName + ";", null);
        c.moveToFirst();
        return SortSetting.getSetting((c.getInt(0)));
    }

    //Only 1 entry in settings table, so ID is always 0
    public void updateDeleteNotficationsSetting(boolean setting) {
        ContentValues cv = new ContentValues();
        if (setting)
            cv.put(DatabaseHelper.SETTINGS_DELETE_NOTI_ON_STARTUP, 1);
        else
            cv.put(DatabaseHelper.SETTINGS_DELETE_NOTI_ON_STARTUP, 0);
        database.update(DatabaseHelper.settingsTableName, cv,
                DatabaseHelper.SETTINGS_TABLE_ID + " = 1", null);
    }

    public boolean getDeleteNotificationSetting() {
        Cursor c = database.rawQuery("SELECT " +
                DatabaseHelper.SETTINGS_DELETE_NOTI_ON_STARTUP +
                " FROM " + DatabaseHelper.settingsTableName + ";", null);
        c.moveToFirst();

        return c.getInt(0) > 0;
    }

    //Again assuming only 1 entry in settings table
    public void restoreDefaultSettings() {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.SETTINGS_CONTACT_SORT_ORDER,
                DatabaseHelper.defaultSortContactSetting);
        cv.put(DatabaseHelper.SETTINGS_DELETE_NOTI_ON_STARTUP,
                DatabaseHelper.defaultDeleteNotificationSetting);
        database.update(DatabaseHelper.settingsTableName, cv,
                DatabaseHelper.SETTINGS_TABLE_ID + " = 1", null);
    }
}
