package com.example.mesh.message;

public class Message {
    private String messageID, userID, messageContent, sourceApp, timeStamp;

    public Message(String messageID, String userID, String messageContent, String sourceApp, String timeStamp) {
        this.messageID = messageID;
        this.userID = userID;
        this.messageContent = messageContent;
        this.sourceApp = sourceApp;
        this.timeStamp = timeStamp;
    }
}
