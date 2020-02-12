package com.mesh.message;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Message {
    private int id;
    private String contactName, groupName, content, sourceApp;
    private Date timeStamp;
    private boolean selected = false;

    public Message(int id, String contactName, String content, String sourceApp, Date timeStamp) {
        this.id = id;
        this.contactName= contactName;
        this.groupName = "";
        this.content = content;
        this.sourceApp = sourceApp;
        this.timeStamp = timeStamp;
    }

    public Message(int id, String contactName, String groupName, String
            content, String sourceApp, Date timeStamp) {
        this.id = id;
        this.contactName= contactName;
        this.groupName = groupName;
        this.content = content;
        this.sourceApp = sourceApp;
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

    public boolean isFromGroup() { return !groupName.equals(""); }

    public String getGroupName() { return groupName; }

    public String getMessageContent()
    {
        return content;
    }

    public String getSourceApp()
    {
        return sourceApp;
    }

    public String getDate()
    {
        DateFormat date = new SimpleDateFormat("dd/MM/yyyy");
        return date.format(timeStamp);
    }

    public String getTime() {
        DateFormat time = new SimpleDateFormat("hh:mm a");
        return time.format(timeStamp);
    }


    public void setSelected(boolean b) {
        this.selected = b;

    }

    public boolean isSelected() {
        return selected;
    }
}

