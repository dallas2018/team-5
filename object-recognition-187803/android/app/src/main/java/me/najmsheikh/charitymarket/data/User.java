package me.najmsheikh.charitymarket.data;

public class User {

    private String name;
    private String email;
    private String photoURL;
    private String defaultNGO;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    public String getDefaultNGO() {
        return defaultNGO;
    }

    public void setDefaultNGO(String defaultNGO) {
        this.defaultNGO = defaultNGO;
    }
}
