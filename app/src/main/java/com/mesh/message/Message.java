package com.mesh.message;

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

    public Date getDate()
    {
        return timeStamp;
    }

    public String getTime() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(timeStamp);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(cal.get(Calendar.HOUR_OF_DAY));
        stringBuilder.append(":");
        stringBuilder.append(cal.get(Calendar.MINUTE));
        return stringBuilder.toString();
    }


}
