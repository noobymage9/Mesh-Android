package com.mesh.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.util.Log;

import com.mesh.message.Message;
import com.mesh.message.UserCollection;
import com.mesh.ui.home.Contact;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import androidx.annotation.RequiresApi;

public class DBManager {
    private DatabaseHelper dbHelper;
    private Context context;
    private SQLiteDatabase database;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss zzz yyy", Locale.getDefault());
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

    /***************************/
    /**Message table functions**/
    /***************************/
    public void insertMessage(int userID, String contents, String sourceApp, Date timeStamp) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.MSG_USER_ID, userID);
        contentValue.put(DatabaseHelper.MSG_CONTENTS, contents);
        contentValue.put(DatabaseHelper.MSG_SOURCE_APP, sourceApp);
        contentValue.put(DatabaseHelper.MSG_TIMESTAMP, dateFormat.format(timeStamp));
        database.insert(DatabaseHelper.messageTableName, null, contentValue);
        insertVirtualMessage(userID, contents, sourceApp, timeStamp);
    }/*Message from groups*/

    public void insertMessage(int userID, int groupID, String contents, String sourceApp, Date timeStamp) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.MSG_GROUP_ID, groupID);
        contentValue.put(DatabaseHelper.MSG_USER_ID, userID);
        contentValue.put(DatabaseHelper.MSG_CONTENTS, contents);
        contentValue.put(DatabaseHelper.MSG_SOURCE_APP, sourceApp);
        contentValue.put(DatabaseHelper.MSG_TIMESTAMP, dateFormat.format(timeStamp));
        database.insert(DatabaseHelper.messageTableName, null, contentValue);
        insertVirtualMessage(userID, groupID, contents, sourceApp, timeStamp);
    }/*All messages from individual*/

    private Cursor getAllMessagesFromUserDB(int userID) {
        Cursor c = database.rawQuery("SELECT * FROM " + DatabaseHelper.messageTableName + " where "
                + DatabaseHelper.MSG_USER_ID + " = " + userID, null);
        c.moveToFirst();
        return c;
    }/*All messages from group*/

    private Cursor getAllMessagesFromGroupDB(int groupID) {
        Cursor c = database.rawQuery("SELECT * FROM " + DatabaseHelper.messageTableName + " where "
                + DatabaseHelper.MSG_GROUP_ID + " = " + groupID, null);
        c.moveToFirst();
        return c;
    }

    private Cursor getMessageTableEntry(int messageID) {
        Cursor c = database.rawQuery("SELECT * FROM " + DatabaseHelper.messageTableName + " where "
                + DatabaseHelper.MSG_ID + " = " + messageID, null);
        c.moveToFirst();
        return c;
    }

    private Message constructMessage(Cursor c) {
        try {
            return new Message(c.getInt(c.getColumnIndex(DatabaseHelper.MSG_ID)),
                    getContactName(c.getInt(c.getColumnIndex(DatabaseHelper.MSG_USER_ID))),
                    c.getString(c.getColumnIndex(DatabaseHelper.MSG_GROUP_ID)),
                    c.getString(c.getColumnIndex(DatabaseHelper.MSG_CONTENTS)),
                    c.getString(c.getColumnIndex(DatabaseHelper.MSG_SOURCE_APP)),
                    dateFormat.parse(c.getString(c.getColumnIndex(DatabaseHelper.MSG_TIMESTAMP))));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Message constructMessageWithTag(Cursor c, int tag) {
        try {
            return new Message(c.getInt(c.getColumnIndex(DatabaseHelper.MSG_ID)),
                    getContactName(c.getInt(c.getColumnIndex(DatabaseHelper.MSG_USER_ID))),
                    getGroupName(c.getInt(c.getColumnIndex(DatabaseHelper.MSG_GROUP_ID))),
                    c.getString(c.getColumnIndex(DatabaseHelper.MSG_CONTENTS)),
                    c.getString(c.getColumnIndex(DatabaseHelper.MSG_SOURCE_APP)),
                    dateFormat.parse(c.getString(c.getColumnIndex(DatabaseHelper.MSG_TIMESTAMP))), tag);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }/*Get all messages for 1 user*/

    public static class MyObject implements Comparable<MyObject> {

        private Date dateTime;

        public Date getDateTime() {
            return dateTime;
        }

        public void setDateTime(Date datetime) {
            this.dateTime = datetime;
        }

        @Override
        public int compareTo(MyObject o) {
            return getDateTime().compareTo(o.getDateTime());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public ArrayList<Message> getMessages(int contactID) {
        ArrayList<Message> messages = new ArrayList<>();
        ArrayList<Message> childMessages = new ArrayList<>();
        Message m;
        Cursor c;
        if (isGroup(contactID)) {
            String groupName = getContactName(contactID);

            c = getAllMessagesFromGroupDB(getGroupID(groupName));
        }
        else
            c = getAllMessagesFromUserDB(contactID);

        if (c.moveToFirst()) /*c.getCount doesnt work, movetofirst resets cursor when view is created*/
            do try {
                m = constructMessage(c);
                messages.add(m);
            } catch (Exception e) {
                e.printStackTrace();
            } while (c.moveToNext());

        if (isMergeParent(contactID))
        {
            ArrayList<Integer> childIDs = getAllChildContactIDs(contactID);
            for (int childContactID : childIDs)
            {
                childMessages = getMessages(childContactID);
                messages.addAll(childMessages);
            }
        }

        Comparator<Message> compareMessagesByDate = (Message m1, Message m2) ->
                m1.getRawDate().compareTo( m2.getRawDate() );

        Collections.sort(messages, compareMessagesByDate);

        c.close();
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

    /******************************************/
    /**Virtual Message Search table functions**/
    /******************************************/

    private ArrayList<Integer> searchFirstIndexInstancesOfString(String word, String searchField)
    {
        ArrayList<Integer> indexResults = new ArrayList<>();
        int currentSearchIndex;

        for (int i = 0; i <= word.length() - searchField.length(); i++)
        {
            currentSearchIndex = word.substring(i).indexOf(searchField);
            if (currentSearchIndex > -1)
            {
                indexResults.add(currentSearchIndex);
                i += searchField.length();
            }
        }

        return indexResults;
    }

    private void insertVirtualMessage(int userID, String contents, String sourceApp, Date timeStamp) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.MSG_USER_ID, userID);
        contentValue.put(DatabaseHelper.MSG_CONTENTS, contents);
        contentValue.put(DatabaseHelper.MSG_SOURCE_APP, sourceApp);
        contentValue.put(DatabaseHelper.MSG_TIMESTAMP, dateFormat.format(timeStamp));
        database.insert(DatabaseHelper.messageSearchTableName, null, contentValue);
    }

    private void insertVirtualMessage(int userID, int groupID, String contents, String sourceApp, Date timeStamp) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.MSG_USER_ID, userID);
        contentValue.put(DatabaseHelper.MSG_GROUP_ID, groupID);
        contentValue.put(DatabaseHelper.MSG_CONTENTS, contents);
        contentValue.put(DatabaseHelper.MSG_SOURCE_APP, sourceApp);
        contentValue.put(DatabaseHelper.MSG_TIMESTAMP, dateFormat.format(timeStamp));
        database.insert(DatabaseHelper.messageSearchTableName, null, contentValue);
    }

    public HashMap<ArrayList<Integer>, Message> searchMessages(String searchField) {

        if (searchField == null || searchField.equals(""))
            return null;

        HashMap<ArrayList<Integer>, Message> searchResults = new HashMap<>();
        ArrayList<Integer> searchIndexes = new ArrayList<>();
        ArrayList<Message> messages = new ArrayList<>();
        Message m;

        if (searchField == null || searchField.equals(""))
            return null;

        Cursor c = database.rawQuery("SELECT * FROM " + DatabaseHelper.messageSearchTableName +
                " WHERE " + DatabaseHelper.MSG_CONTENTS + " LIKE \"%" + searchField + "%\"", null);

        if (c.moveToFirst()) {
            do {
                m = constructMessage(c);
                searchIndexes = searchFirstIndexInstancesOfString(m.getMessageContent(), searchField);
                searchResults.put(searchIndexes, m);
            } while (c.moveToNext());
        }

        return searchResults;
    }

    //Tagging messages to user collections
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

        c.close();
        return tagIDs;
    }

    public ArrayList<Message> getMessagesInUserCollection(int collectionID) {
        Cursor messageTagsTableCursor = database.rawQuery("SELECT * FROM "
                + DatabaseHelper.messageTagsTableName + " WHERE " +
                DatabaseHelper.MSGTAG_COLLECTION_ID + " = " + collectionID, null);
        messageTagsTableCursor.moveToFirst();
        ArrayList<Message> messages = new ArrayList<>();
        Message currentMessage;
        Cursor messageTableCursor;

        if (messageTagsTableCursor.moveToFirst()) {
            do {
                messageTableCursor = getMessageTableEntry
                        (messageTagsTableCursor.getInt
                                (messageTagsTableCursor.getColumnIndex(DatabaseHelper.MSGTAG_MSG_ID)));
                currentMessage = constructMessageWithTag(messageTableCursor,
                        messageTagsTableCursor.getInt(messageTagsTableCursor.getColumnIndex(
                                DatabaseHelper.MSGTAG_ID)));
                messages.add(currentMessage);
            } while (messageTagsTableCursor.moveToNext());
            messageTableCursor.close();
        }

        messageTagsTableCursor.close();
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

    public void delete(int tagID, int messageID) {
        database.delete(DatabaseHelper.messageTagsTableName,
                DatabaseHelper.MSGTAG_ID + " = " + tagID + " AND " +
                        DatabaseHelper.MSGTAG_MSG_ID + " = " + messageID, null);
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

    /**************************************/
    /**Message Tag Search table functions**/
    /**************************************/

    private void insertVirtualCollection(String collectionName) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.COLLECTIONS_NAME, collectionName);
        database.insert(DatabaseHelper.userCollectionSearchTableName, null, contentValue);
    }

    //returns all indexOf indexes as well as the collection name that was found from sql
    public HashMap<ArrayList<Integer>, String> searchCollectionNames(String searchField) {

        if (searchField == null || searchField.equals(""))
            return null;

        HashMap<ArrayList<Integer>, String> searchResults = new HashMap<>();
        ArrayList<String> collectionNames = new ArrayList<>();
        ArrayList<Integer> searchIndexes = new ArrayList<>();

        String currentCollectionName;

        if (searchField == null || searchField.equals(""))
            return null;

        Cursor c = database.rawQuery("SELECT * FROM " + DatabaseHelper.userCollectionSearchTableName +
                " WHERE " + DatabaseHelper.COLLECTIONS_NAME + " LIKE \"%" + searchField + "%\"", null);

        if (c.moveToFirst()) {
            do {
                currentCollectionName = c.getString(c.getColumnIndex(DatabaseHelper.COLLECTIONS_NAME));
                collectionNames.add(currentCollectionName);

                //method found in message search section
                searchIndexes = searchFirstIndexInstancesOfString(currentCollectionName, searchField);
                searchResults.put(searchIndexes, currentCollectionName);
            } while (c.moveToNext());
        }

        return searchResults;
    }

    /****************************/
    /**Contacts table functions**/
    /****************************/

    //Theres 2 types of contact with the same name:
    //1.Contact that messages you directly
    //2.Contact that only messages in one or more of your group chats
    //if contact's name exists in table, check for whether its type 1 or type 2.
    //if the inserted type does not exist for that name, insert new contact and return
    //new created id.
    //If contact name of the inserted type both match a record in the table, return
    //the ID of that matching record instead
    public int insertContact(String name, Date timeStamp, int isGroup, int isGroupUser) {
        //Query database for duplicate name and whether that name is a group user or actual contact
        Cursor c = database.rawQuery("SELECT * FROM " + DatabaseHelper.contactsTableName
                + " WHERE " + DatabaseHelper.CONTACT_NAME + " = '" + name + "' AND " +
                DatabaseHelper.CONTACT_IS_GROUP_USER + " = " + isGroupUser, null);

        if (!c.moveToFirst()) {
            ContentValues contentValue = new ContentValues();
            contentValue.put(DatabaseHelper.CONTACT_NAME, name);
            contentValue.put(DatabaseHelper.CONTACT_LATEST_TIMESTAMP, dateFormat.format(timeStamp));
            contentValue.put(DatabaseHelper.CONTACT_IS_GROUP, isGroup);
            contentValue.put(DatabaseHelper.CONTACT_IS_GROUP_USER, isGroupUser);
            database.insert(DatabaseHelper.contactsTableName, null, contentValue);

            c = getLatestContactEntry();
            //Making default order of contact to be = to descending ID
            int latestContactID = c.getInt(c.getColumnIndex(DatabaseHelper.CONTACT_ID));
            contentValue = new ContentValues();
            contentValue.put(DatabaseHelper.CONTACT_CUSTOM_ORDER, latestContactID);
            database.update(DatabaseHelper.contactsTableName, contentValue,
                    DatabaseHelper.CONTACT_ID + " = " + latestContactID, null);
            return latestContactID;
        } else {
            c.moveToFirst();
            updateContactTimestamp(c.getInt(c.getColumnIndex(DatabaseHelper.CONTACT_ID)),
                    name, timeStamp);
            return c.getInt(c.getColumnIndex(DatabaseHelper.CONTACT_ID));
        }
    }

    //only for individual user, no point doing for group
    public ArrayList<String> getContactMostUsedSourceApps(int contactID) {

        //Determine whether to compare to user ID or group ID
        String msgTableColumnToCompare;
        int idToCompare;

        if (isGroup(contactID)) {
            msgTableColumnToCompare = DatabaseHelper.MSG_GROUP_ID;
            idToCompare = getGroupID(getContactName(contactID));
        } else {
            msgTableColumnToCompare = DatabaseHelper.MSG_USER_ID;
            idToCompare = contactID;
        }

        Cursor c = database.rawQuery("SELECT " + DatabaseHelper.MSG_SOURCE_APP + ", " +
                        "COUNT(" + DatabaseHelper.MSG_SOURCE_APP + ") AS total FROM " +
                        DatabaseHelper.messageTableName + " WHERE " + msgTableColumnToCompare + " = " +
                        idToCompare + " GROUP BY " + DatabaseHelper.MSG_SOURCE_APP + " ORDER BY total DESC",
                null);

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

    private Contact constructContact(Cursor c)
    {
        return new Contact(c.getInt(c.getColumnIndex(DatabaseHelper.CONTACT_ID)),
                c.getString(c.getColumnIndex(DatabaseHelper.CONTACT_PROFILE_PIC)),
                c.getString(c.getColumnIndex(DatabaseHelper.CONTACT_NAME)),
                BooleanEnum.getBoolean(c.getInt(c.getColumnIndex(DatabaseHelper.CONTACT_IS_FAVOURITE))));
    }

    private ArrayList<Contact> constructContactListFromCursor(Cursor c)
    {
        ArrayList<Contact> contacts = new ArrayList<>();
        int isGroupUser, isMergeChild;
        Contact currentContact;

        if (c.moveToFirst()) {
            do {
                currentContact = constructContact(c);

                isGroupUser = c.getInt(c.getColumnIndex(DatabaseHelper.CONTACT_IS_GROUP_USER));
                isMergeChild = c.getInt(c.getColumnIndex((DatabaseHelper.CONTACT_IS_MERGE_CHILD)));

                if (!BooleanEnum.getBoolean(isGroupUser) && !BooleanEnum.getBoolean(isMergeChild))
                    contacts.add(currentContact);

            } while (c.moveToNext());
        }

        return contacts;
    }

    private Cursor getAllContactsDB() {
        Cursor c = database.rawQuery("SELECT * FROM " + DatabaseHelper.contactsTableName,
                null);
        c.moveToFirst();

        return c;
    }

    //Helper function to get cursor for all contacts
    private Cursor getAllContactsSortByRecencyDB() {
        Cursor c = database.rawQuery("SELECT * FROM " + DatabaseHelper.contactsTableName
                        + " ORDER BY " + DatabaseHelper.CONTACT_LATEST_TIMESTAMP + " DESC ",
                null);
        c.moveToFirst();

        return c;
    }

    private Cursor getAllContactsSortByFrequencyDB() {
        Cursor c = database.rawQuery("SELECT * FROM " + DatabaseHelper.contactsTableName
                + " ORDER BY (SELECT COUNT(*) FROM " + DatabaseHelper.messageTableName +
                " WHERE " + DatabaseHelper.messageTableName + "." + DatabaseHelper.MSG_USER_ID
                + " = " + DatabaseHelper.contactsTableName + "." + DatabaseHelper.CONTACT_ID
                + ") DESC ", null);
        c.moveToFirst();

        return c;
    }

    private Cursor getAllContactsSortByOrderDB() {
        Cursor c = database.rawQuery("SELECT * FROM " + DatabaseHelper.contactsTableName
                        + " ORDER BY " + DatabaseHelper.CONTACT_CUSTOM_ORDER + " DESC ",
                null);
        c.moveToFirst();

        return c;
    }

    private Cursor getAllContactsSortByNameDB() {
        Cursor c = database.rawQuery("SELECT * FROM " + DatabaseHelper.contactsTableName
                        + " ORDER BY " + DatabaseHelper.CONTACT_NAME + " ASC ",
                null);
        c.moveToFirst();

        return c;
    }

    public String getContactName(int contactID) {
        Cursor c = getContactDB(contactID);

        return c.getString(c.getColumnIndex(DatabaseHelper.CONTACT_NAME));
    }

    public ArrayList<Contact> getAllContactsForHome()
    {
        Cursor c;
        ArrayList<Contact> contacts = new ArrayList<>();

        c = getAllContactsSortByNameDB();
       contacts = constructContactListFromCursor(c);

        c.close();
        return contacts;
    }

    public ArrayList<Contact> getAllContacts() {
        Cursor c;
        ArrayList<Contact> contacts = new ArrayList<>();

        if (!getCustomContactSortSetting()) {
            switch (getContactSortSetting()) {
                case Recency:
                    c = getAllContactsSortByRecencyDB();
                    break;
                case Frequency:
                    c = getAllContactsSortByFrequencyDB();
                    break;
                default:
                    c = getAllContactsDB();
                    break;
            }
            reinitializeContactsOrder(c);;
        } else
            c = getAllContactsSortByOrderDB();

        contacts = constructContactListFromCursor(c);

        c.close();
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
            return time.format(Objects.requireNonNull(dateFormat.parse(c.getString
                    (c.getColumnIndex(DatabaseHelper.CONTACT_LATEST_TIMESTAMP)))));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public void mergeContacts(int childContactID, int parentContactID)
    {
        updateContactMergeStatus(childContactID, DatabaseHelper.CONTACT_IS_MERGE_CHILD, true);
        updateContactMergeStatus(parentContactID, DatabaseHelper.CONTACT_IS_MERGE_PARENT, true);
        insertContactMergeStatus(childContactID, parentContactID);
    }

    public void unMergeContacts(int childContactID, int parentContactID)
    {
        updateContactMergeStatus(childContactID, DatabaseHelper.CONTACT_IS_MERGE_CHILD, false);
        if(!isMergeParent(parentContactID))
            updateContactMergeStatus(parentContactID, DatabaseHelper.CONTACT_IS_MERGE_PARENT, false);

        deleteContactMergeStatus(childContactID, parentContactID);
    }

    private int getContactOrder(int contactID) {
        Cursor c = database.rawQuery("SELECT " + DatabaseHelper.CONTACT_CUSTOM_ORDER + " FROM " +
                DatabaseHelper.contactsTableName + " WHERE " + DatabaseHelper.CONTACT_ID + " = " +
                contactID, null);
        c.moveToFirst();

        return c.getInt(c.getColumnIndex(DatabaseHelper.CONTACT_CUSTOM_ORDER));
    }

    public void swapContactPositions(int contactID1, int contactID2) {
        int tempPosition = getContactOrder(contactID1);
        updateContactOrder(contactID1, getContactOrder(contactID2));
        updateContactOrder(contactID2, tempPosition);
    }

    public boolean isGroup(int contactID) {
        Cursor c = database.rawQuery("SELECT " + DatabaseHelper.CONTACT_IS_GROUP + " FROM " +
                DatabaseHelper.contactsTableName + " WHERE " + DatabaseHelper.CONTACT_ID +
                " = " + contactID, null);
        if (c.moveToFirst())
            return c.getInt(c.getColumnIndex(DatabaseHelper.CONTACT_IS_GROUP)) == 1;

        return false;
    }

    private void reinitializeContactsOrder(Cursor c) {
        int tempID, orderCounter = 1;

        if (c.moveToLast())
        {
            do {
                tempID = c.getInt(c.getColumnIndex(DatabaseHelper.CONTACT_ID));
                updateContactOrder(tempID, orderCounter);
                orderCounter++;
            } while (c.moveToPrevious());
        }
    }

    public ArrayList<Contact> getFavouriteContacts()
    {
        Cursor c = getAllContactsDB();
        ArrayList<Contact> contacts = new ArrayList<>();
        Contact currentContact;

        if (c.moveToFirst())
        {
            do
            {
                if (BooleanEnum.getBoolean
                        (c.getInt(c.getColumnIndex(DatabaseHelper.CONTACT_IS_FAVOURITE)))) {
                    currentContact = constructContact(c);
                    contacts.add(currentContact);
                }
            } while (c.moveToNext());
        }

        c.close();
        return contacts;
    }

    public int setFavouriteContact(int contactID)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.CONTACT_IS_FAVOURITE, BooleanEnum.getIntValueOfBoolean(true));
        int i = database.update(DatabaseHelper.contactsTableName, contentValues,
                DatabaseHelper.CONTACT_ID + " = " + contactID, null);
        return i;
    }

    public int removeFavouriteContact(int contactID)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.CONTACT_IS_FAVOURITE, BooleanEnum.getIntValueOfBoolean(false));
        int i = database.update(DatabaseHelper.contactsTableName, contentValues,
                DatabaseHelper.CONTACT_ID + " = " + contactID, null);
        return i;
    }

    private int updateContactMergeStatus(int contactID, String mergeStatus, boolean isMerged)
    {
        ContentValues cv = new ContentValues();
        cv.put(mergeStatus, isMerged);
        int i = database.update(DatabaseHelper.contactsTableName, cv,
                DatabaseHelper.CONTACT_ID + " = " + contactID, null);
        return i;
    }

    private int updateContactOrder(int contactID, int order) {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.CONTACT_CUSTOM_ORDER, order);
        int i = database.update(DatabaseHelper.contactsTableName, cv,
                DatabaseHelper.CONTACT_ID + " = " + contactID, null);
        return i;
    }

    private int updateContactTimestamp(int contactID, String contactName, Date latestMessageDate) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.CONTACT_ID, contactID);
        contentValue.put(DatabaseHelper.CONTACT_NAME, contactName);
        contentValue.put(DatabaseHelper.CONTACT_LATEST_TIMESTAMP,
                dateFormat.format(latestMessageDate));
        int i = database.update(DatabaseHelper.contactsTableName, contentValue,
                DatabaseHelper.CONTACT_ID + " = " + contactID, null);
        return i;
    }

    public void deleteFromContactsTable(int contactID) {
        database.delete(DatabaseHelper.contactsTableName,
                DatabaseHelper.CONTACT_ID + " = '" + contactID + "'", null);
    }

    /**
     * Terry failure below
     * Same category
     * Contact Table
     */
    public void insertIcon(String icon, String id) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.CONTACT_PROFILE_PIC, icon);
        database.update(DatabaseHelper.contactsTableName, contentValues, DatabaseHelper.CONTACT_ID + "=?", new String[]{id});
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
        Cursor c = database.rawQuery("SELECT * FROM " +
                DatabaseHelper.groupsTableName + " WHERE " + DatabaseHelper.GROUPS_ID + " = " +
                groupID, null);
        c.moveToFirst();

        return c;
    }

    private Cursor getGroupEntry(String groupName) {
        Cursor c = database.rawQuery("SELECT * FROM " +
                DatabaseHelper.groupsTableName + " WHERE " + DatabaseHelper.GROUPS_NAME + " = '" +
                groupName + "'", null);
        c.moveToFirst();

        return c;
    }

    public String getGroupName(int groupID) {

        if (groupID < 1)
            return "";

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

    public void deleteGroup(int groupID)
    {
        database.delete(DatabaseHelper.groupsTableName,
                DatabaseHelper.GROUPS_ID + " = " + groupID, null);
    }

    /*************************/
    /**Merge table functions**/
    /*************************/

    private boolean isMergeParent(int contactID)
    {
        Cursor c = database.rawQuery("SELECT * FROM " +
                DatabaseHelper.contactMergeStatusTableName + " WHERE " + DatabaseHelper.MERGE_PARENT_ID +
                " = " + contactID, null);

        if (c.moveToFirst()) {
            c.close();
            return true;
        }
        else
        {
            c.close();
            return false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public ArrayList<Contact> getAllMergeParentContacts()
    {
        Cursor c = database.rawQuery("SELECT DISTINCT " + DatabaseHelper.MERGE_PARENT_ID + " FROM " +
                DatabaseHelper.contactMergeStatusTableName, null);
        ArrayList<Contact> parentContacts = new ArrayList<>();
        Contact currentContact;

        if (c.moveToFirst())
        {
            do {
                currentContact = constructContact(getContactDB(c.getInt(c.getColumnIndex(DatabaseHelper.MERGE_PARENT_ID))));
                parentContacts.add(currentContact);
            } while (c.moveToNext());

            parentContacts.sort(Comparator.comparing(Contact::getName));
        }

        return parentContacts;
    }

    private ArrayList<Integer> getAllChildContactIDs(int contactID)
    {
        ArrayList<Integer> childContactIDs = new ArrayList<>();

        Cursor c = database.rawQuery("SELECT " + DatabaseHelper.MERGE_CHILD_ID + " FROM " +
                DatabaseHelper.contactMergeStatusTableName + " WHERE " + DatabaseHelper.MERGE_PARENT_ID +
                " = " + contactID, null);

        if (c.moveToFirst())
        {
            do {
                childContactIDs.add(c.getInt(c.getColumnIndex(DatabaseHelper.MERGE_CHILD_ID)));
            } while (c.moveToNext());
        }

        return childContactIDs;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public ArrayList<Contact> getAllMergeChildContacts(int contactID)
    {
        ArrayList<Contact> childContacts = new ArrayList<>();
        ArrayList<Integer> childContactIDs = getAllChildContactIDs(contactID);
        Contact currentContact;

        if (childContactIDs.size() > 0)
        {
            for (int childContactID : childContactIDs)
            {
                currentContact = constructContact(getContactDB(childContactID));
                childContacts.add(currentContact);
            }

            childContacts.sort(Comparator.comparing(Contact::getName));
        }

        return childContacts;
    }

    public void insertContactMergeStatus(int childContactID, int parentContactID)
    {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.MERGE_CHILD_ID, childContactID);
        cv.put(DatabaseHelper.MERGE_PARENT_ID, parentContactID);
        database.insert(DatabaseHelper.contactMergeStatusTableName, null, cv);
    }

    public void deleteContactMergeStatus(int childContactID, int parentContactID)
    {
        database.delete(DatabaseHelper.contactMergeStatusTableName,
                DatabaseHelper.MERGE_CHILD_ID + " = " + childContactID + " AND " +
                DatabaseHelper.MERGE_PARENT_ID + " = " + parentContactID, null);
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

    //Only 1 entry in settings table, so ID is always 1
    public void updateContactSortSetting(SortSetting setting) {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.SETTINGS_CONTACT_SORT_ORDER, SortSetting.getSettingID(setting));
        database.update(DatabaseHelper.settingsTableName, cv,
                DatabaseHelper.SETTINGS_TABLE_ID + " = 1", null);
    }

    //Only 1 entry in settings table, so ID is always 1
    public void updateDeleteNotficationsSetting(boolean setting) {
        ContentValues cv = new ContentValues();
        if (setting)
            cv.put(DatabaseHelper.SETTINGS_DELETE_NOTI_ON_STARTUP, BooleanEnum.getIntValueOfBoolean(true));
        else
            cv.put(DatabaseHelper.SETTINGS_DELETE_NOTI_ON_STARTUP, BooleanEnum.getIntValueOfBoolean(false));
        database.update(DatabaseHelper.settingsTableName, cv,
                DatabaseHelper.SETTINGS_TABLE_ID + " = 1", null);
    }

    public void updateCustomContactOrderSetting(boolean setting) {
        ContentValues cv = new ContentValues();
        if (setting)
            cv.put(DatabaseHelper.SETTINGS_CUSTOM_CONTACT_ORDER, 1);
        else
            cv.put(DatabaseHelper.SETTINGS_CUSTOM_CONTACT_ORDER, 0);
        database.update(DatabaseHelper.settingsTableName, cv,
                DatabaseHelper.SETTINGS_TABLE_ID + " = 1", null);
    }

    public boolean getCustomContactSortSetting() {
        Cursor c = database.rawQuery("SELECT " + DatabaseHelper.SETTINGS_CUSTOM_CONTACT_ORDER +
                " FROM " + DatabaseHelper.settingsTableName + ";", null);
        c.moveToFirst();

        return BooleanEnum.getBoolean(c.getInt(0));
    }

    public SortSetting getContactSortSetting() {
        Cursor c = database.rawQuery("SELECT " + DatabaseHelper.SETTINGS_CONTACT_SORT_ORDER +
                " FROM " + DatabaseHelper.settingsTableName + ";", null);
        c.moveToFirst();

        return SortSetting.getSetting((c.getInt(0)));
    }

    public boolean getDeleteNotificationSetting() {
        Cursor c = database.rawQuery("SELECT " +
                DatabaseHelper.SETTINGS_DELETE_NOTI_ON_STARTUP +
                " FROM " + DatabaseHelper.settingsTableName + ";", null);
        c.moveToFirst();

        return BooleanEnum.getBoolean(c.getInt(0));
    }

    //Again assuming only 1 entry in settings table
    public void restoreDefaultSettings() {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.SETTINGS_CONTACT_SORT_ORDER,
                DatabaseHelper.defaultSortContactSetting);
        cv.put(DatabaseHelper.SETTINGS_DELETE_NOTI_ON_STARTUP,
                DatabaseHelper.defaultDeleteNotificationSetting);
        cv.put(DatabaseHelper.SETTINGS_CUSTOM_CONTACT_ORDER,
                DatabaseHelper.defaultCustomContactOrder);
        database.update(DatabaseHelper.settingsTableName, cv,
                DatabaseHelper.SETTINGS_TABLE_ID + " = 1", null);
    }

    /******************************/
    /**User Login table functions**/
    /******************************/

    public void insertLoginDetails(String userID, String password)
    {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.LOGIN_USER_ID, userID);
        cv.put(DatabaseHelper.LOGIN_PASSWORD, password);
        database.insert(DatabaseHelper.loginDetailsTableName, null, cv);
    }

    public void updateLoginDetails(String userID, String password)
    {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.LOGIN_USER_ID, userID);
        cv.put(DatabaseHelper.LOGIN_PASSWORD, password);
        database.update(DatabaseHelper.loginDetailsTableName, cv,
                DatabaseHelper.LOGIN_ID + " = 1", null);
    }
}