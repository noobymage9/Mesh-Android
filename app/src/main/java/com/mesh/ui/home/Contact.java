package com.mesh.ui.home;

import java.sql.Blob;

public class Contact {
    protected int id;
    protected byte[] icon;
    protected String name;


    public Contact(int id, byte[] icon, String name) {
        this.id = id;
        this.icon = icon;
        this.name = name;
    }

    public int getID() {return this.id;}

    public byte[] getProfilePic() {
        return this.icon;
    }

    public String getName() {
        return this.name;
    }

}