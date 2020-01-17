package com.mesh.message;

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

    public Date getTimeStamp()
    {
        return timeStamp;
    }
}
