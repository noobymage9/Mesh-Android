package com.mesh.ui.home;

import java.sql.Blob;

public class Contact {
    protected int id;
    protected Blob icon;
    protected String name;


    public Contact(int id, Blob icon, String name) {
        this.id = id;
        this.icon = icon;
        this.name = name;
    }

    public int getID() {return this.id;}

    public Blob getBlob() {
        return this.icon;
    }

    public String getName() {
        return this.name;
    }

}