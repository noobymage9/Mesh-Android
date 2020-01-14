package com.example.mesh.ui.home;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class ContactInfo implements Parcelable { // Parcelable is an efficient version of serializable for android. This is required to pass the object from recycler view to the message activity
    protected String name;
    protected Bitmap icon;
    protected String content;
    protected int id;
    protected static final String NAME_PREFIX = "Name_";
    protected static final String SURNAME_PREFIX = "Surname_";
    protected static final String EMAIL_PREFIX = "email_";

    private static int IDCount = 1;

    public ContactInfo(String name, Bitmap icon, String content) {
        this.name = name;
        this.icon = icon;
        this.content = content;
        this.id = IDCount;
        IDCount++;
    }

    public ContactInfo(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public ContactInfo(Parcel in) {
        name = in.readString();
        //icon = in.readParcelable(Bitmap.class.getClassLoader());
        //content = in.readString();
        id = in.readInt();
    }

    public static final Creator<ContactInfo> CREATOR = new Creator<ContactInfo>() {
        @Override
        public ContactInfo createFromParcel(Parcel in) {
            return new ContactInfo(in);
        }

        @Override
        public ContactInfo[] newArray(int size) {
            return new ContactInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        //dest.writeParcelable(icon, flags);
        //dest.writeString(content);
        dest.writeInt(id);
    }

    public String getName() {
        return this.name;
    }

    public Bitmap getBitmap() {
        return this.icon;
    }

    public int getID() {return this.id;}
}