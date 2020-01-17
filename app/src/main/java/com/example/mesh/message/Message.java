package com.example.mesh.message;

public class Message {
    private String id, contactID, content, sourceApp, timeStamp;

    public Message(String id, String contactID, String content, String sourceApp, String timeStamp) {
        this.id = id;
        this.contactID = contactID;
        this.content = content;
        this.sourceApp = sourceApp;
        this.timeStamp = timeStamp;
    }
}
