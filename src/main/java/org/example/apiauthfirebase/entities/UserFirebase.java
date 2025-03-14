package org.example.apiauthfirebase.entities;

public class UserFirebase {
    private String email;
    private boolean authorized;

    public UserFirebase(String email, boolean authorized) {
        this.email = email;
        this.authorized = authorized;
    }

    public UserFirebase() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isAuthorized() {
        return authorized;
    }

    public void setAuthorized(boolean authorized) {
        this.authorized = authorized;
    }
}

