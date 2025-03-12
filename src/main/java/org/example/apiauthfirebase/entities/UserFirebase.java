package org.example.apiauthfirebase.entities;

public class UserFirebase {
    private String uid;
    private String email;

    public UserFirebase(String uid, String email) {
        this.uid = uid;
        this.email = email;
    }

    public UserFirebase() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
