package com.mesh.message;

import android.os.Parcel;
import android.os.Parcelable;

public class UserCollection implements Parcelable {
    private int userCollectionID;
    private String userCollectionName;
    private boolean selected = false;

    public UserCollection(int userCollectionID, String userCollectionName)
    {
        this.userCollectionID = userCollectionID;
        this.userCollectionName = userCollectionName;
    }


    protected UserCollection(Parcel in) {
        userCollectionID = in.readInt();
        userCollectionName = in.readString();
        selected = in.readByte() != 0;
    }

    public static final Creator<UserCollection> CREATOR = new Creator<UserCollection>() {
        @Override
        public UserCollection createFromParcel(Parcel in) {
            return new UserCollection(in);
        }

        @Override
        public UserCollection[] newArray(int size) {
            return new UserCollection[size];
        }
    };

    public void setSelected(boolean b){this.selected = b;}
    public int getID() { return userCollectionID;}
    public String getName() {return userCollectionName;}
    public boolean isSelected(){return selected;}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(userCollectionID);
        dest.writeString(userCollectionName);
        dest.writeByte((byte) (selected ? 1 : 0));
    }

    public String toString(){
        return userCollectionName;
    }
}
