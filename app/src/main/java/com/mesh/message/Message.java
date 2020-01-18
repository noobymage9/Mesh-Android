package com.mesh.message;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Message {
    private String id, contactName, content, sourceApp;
    long timeStamp;

    public Message(String id, String contactName, String content, String sourceApp, long timeStamp) {
        this.id = id;
        this.contactName= contactName;
        this.content = content;
        this.sourceApp = sourceApp;
        this.timeStamp = timeStamp;
    }

    public String getID()
    {
        return id;
    }

    public String getContactName()
    {
        return contactName;
    }

    public String getMessageContent()
    {
        return content;
    }

    public String getSourceApp()
    {
        return sourceApp;
    }

    public long getDate()
    {
        return timeStamp;
    }

    public String getTime() {
        DateFormat time = new SimpleDateFormat("hh:mm a");
        return time.format(timeStamp);
    }


}
