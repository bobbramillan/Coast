package com.coast;

public class User {

    private final String userId;
    private final String name;
    private final String email;
    private final String password;
    private final String birthDate;
    private final String about;
    private final String createdAt;
    private final String lastSignIn;
    private final String lastSignOut;

    public User(String userId, String name, String email, String password,
                String birthDate, String about, String createdAt,
                String lastSignIn, String lastSignOut) {
        this.userId      = userId;
        this.name        = name;
        this.email       = email;
        this.password    = password;
        this.birthDate   = birthDate;
        this.about       = about;
        this.createdAt   = createdAt;
        this.lastSignIn  = lastSignIn;
        this.lastSignOut = lastSignOut;
    }

    public String getUserId()    { return userId; }
    public String getName()      { return name; }
    public String getEmail()     { return email; }
    public String getPassword()  { return password; }
    public String getBirthDate() { return birthDate; }
    public String getAbout()     { return about; }
    public String getCreatedAt() { return createdAt; }
    public String getLastSignIn()  { return lastSignIn; }
    public String getLastSignOut() { return lastSignOut; }
}