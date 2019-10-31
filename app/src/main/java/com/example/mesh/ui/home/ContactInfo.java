package com.example.mesh.ui.home;

public class ContactInfo {
    protected String name;
    protected String surname;
    protected String email;
    protected static final String NAME_PREFIX = "Name_";
    protected static final String SURNAME_PREFIX = "Surname_";
    protected static final String EMAIL_PREFIX = "email_";

    public ContactInfo(String name, String surname, String email) {
        this.name = name;
        this.surname = surname;
        this.email = email;
    }
}