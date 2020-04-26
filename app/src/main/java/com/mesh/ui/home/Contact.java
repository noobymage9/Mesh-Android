package com.mesh.ui.home;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Contact implements Parcelable {
    protected int id;
    protected String icon;
    protected String name;
    protected boolean isFavourite;

    public Contact(int id, String icon, String name, boolean isFavourite) {
        this.id = id;
        this.icon = icon;
        this.name = name;
        this.isFavourite = isFavourite;
    }

    public Contact(int id, String name) {
        this.id = id;
        this.name = name;
    }

    protected Contact(Parcel in) {
        id = in.readInt();
        icon = in.readString();
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

    public String getProfilePic() {
        return this.icon;
    }

    public String getName() {
        return this.name;
    }

    public boolean getIsFavourite() { return this.isFavourite; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(icon);
        dest.writeString(name);
    }

    @NonNull
    public String toString(){
        return this.name;
    }

    public void setProfilePic(String realPath) {
        this.icon = realPath;
    }
}