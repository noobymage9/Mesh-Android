package com.mesh.message;

public class UserCollection
{
    private int userCollectionID;
    private String userCollectionName;

    public UserCollection(int userCollectionID, String userCollectionName)
    {
        this.userCollectionID = userCollectionID;
        this.userCollectionName = userCollectionName;
    }

    public int getID() { return userCollectionID;}
    public String getName() {return userCollectionName;}
}
