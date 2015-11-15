package com.smeanox.apps.flatplanmgr;

/**
 * An author
 */
public class Author {
    private String firstName;
    private String lastName;
    private String Role;
    private String MailAddress;

    public Author(String firstName, String lastName, String role, String mailAddress) {
        this.firstName = firstName;
        this.lastName = lastName;
        Role = role;
        MailAddress = mailAddress;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName(){
        return (firstName + " " + lastName).trim();
    }

    public String getRole() {
        return Role;
    }

    public void setRole(String role) {
        Role = role;
    }

    public String getMailAddress() {
        return MailAddress;
    }

    public void setMailAddress(String mailAddress) {
        MailAddress = mailAddress;
    }

    @Override
    public String toString() {
        return getFullName();
    }
}
