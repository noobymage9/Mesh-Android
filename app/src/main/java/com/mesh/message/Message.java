package com.mesh.message;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Message {
    private int id, tagID = -1;
    private String contactName, groupName, content, sourceApp;
    private Date timeStamp;
    private boolean selected = false;
    private boolean isDate;

    public Message(int id, String contactName, String content, String sourceApp, Date timeStamp) {
        this.id = id;
        this.contactName= contactName;
        this.groupName = "";
        this.content = content;
        this.sourceApp = sourceApp;
        this.timeStamp = timeStamp;
        isDate = false;
    }

    public Message(int id, String contactName, String groupName, String
            content, String sourceApp, Date timeStamp) {
        this.id = id;
        this.contactName= contactName;
        this.groupName = groupName;
        this.content = content;
        this.sourceApp = sourceApp;
        this.timeStamp = timeStamp;
        isDate = false;
    }

    public Message(int id, String contactName, String groupName, String
            content, String sourceApp, Date timeStamp, int tagID) {
        this.id = id;
        this.contactName= contactName;
        this.groupName = groupName;
        this.content = content;
        this.sourceApp = sourceApp;
        this.timeStamp = timeStamp;
        this.tagID = tagID;
        isDate = false;
    }

    public Message(Date timeStamp) {
        isDate = true;
        this.timeStamp = timeStamp;
    }

    public int getID()
    {
        return id;
    }

    public String getContactName()
    {
        return contactName;
    }

    public boolean isFromGroup() { return groupName != ""; }

    public String getGroupName() { return groupName; }

    public String getMessageContent()
    {
        return content;
    }

    public String getSourceApp()
    {
        return sourceApp;
    }

    public Date getRawDate()
    {
        return timeStamp;
    }

    public String getDate() {
        DateFormat date = DateFormat.getDateInstance();
        return date.format(timeStamp);
    }

    public String getTime() {
        DateFormat time = DateFormat.getTimeInstance(DateFormat.SHORT);
        return time.format(timeStamp);
    }

    public int getTagID() { return tagID; }

    public void setSelected(boolean b) {
        this.selected = b;

    }

    public boolean isSelected() {
        return selected;
    }

    public boolean isDate() {
        return isDate;
    }

    public boolean isSameDateAs(Message message) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(timeStamp);
        cal2.setTime(message.getRawDate());
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }
}

