package com.mesh.ui.home;

import android.os.Parcel;
import android.os.Parcelable;

import java.sql.Blob;

public class Contact implements Parcelable {
    protected int id;
    protected byte[] icon;
    protected String name;


    public Contact(int id, byte[] icon, String name) {
        this.id = id;
        this.icon = icon;
        this.name = name;
    }

    protected Contact(Parcel in) {
        id = in.readInt();
        icon = in.createByteArray();
        name = in.readString();
    }

    public static final Creator<Contact> CREATOR = new Creator<Contact>() {
        @Override
        public Contact createFromParcel(Parcel in) {
            return new Contact(in);
        }

        @Override
        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };

    public int getID() {return this.id;}

    public byte[] getProfilePic() {
        return this.icon;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeByteArray(icon);
        dest.writeString(name);
    }

    public String toString(){
        return this.name;
    }
}