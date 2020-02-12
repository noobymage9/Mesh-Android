package com.mesh.message;

public class UserCollection
{
    private int userCollectionID;
    private String userCollectionName;
    private boolean selected = false;

    public UserCollection(int userCollectionID, String userCollectionName)
    {
        this.userCollectionID = userCollectionID;
        this.userCollectionName = userCollectionName;
    }


    public void setSelected(boolean b){this.selected = b;}
    public int getID() { return userCollectionID;}
    public String getName() {return userCollectionName;}
    public boolean isSelected(){return selected;}
}
