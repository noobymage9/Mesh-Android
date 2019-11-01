package com.example.mesh.ui.home;

public class ContactInfo {
    protected String name;
    protected String icon;
    protected String email;
    protected static final String NAME_PREFIX = "Name_";
    protected static final String SURNAME_PREFIX = "Surname_";
    protected static final String EMAIL_PREFIX = "email_";

    public ContactInfo(String name, String icon, String email) {
        this.name = name;
        this.icon = icon;
        this.email = email;
    }
}