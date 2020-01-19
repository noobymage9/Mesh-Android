package com.mesh.message;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Message {
    private String id, contactName, content, sourceApp;
    Date timeStamp;

    public Message(String id, String contactName, String content, String sourceApp, Date timeStamp) {
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

    public String getDate()
    {
        DateFormat date = new SimpleDateFormat("dd/MM/yyyy");
        return date.format(timeStamp);
    }

    public String getTime() {
        DateFormat time = new SimpleDateFormat("hh:mm a");
        return time.format(timeStamp);
    }


}
