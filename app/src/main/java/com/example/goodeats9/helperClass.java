package com.example.goodeats9;

//-----------------------------------------IM/2021/062 - Hasindu ---------------------------------------------------//

public class helperClass {
    String name , email , password ,description;



    public helperClass(String name, String email, String password , String description) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.description = description;
    }

    public helperClass(String name, String description) {
        this.name = name;
        this.description = description;
    }

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

    public String getPassword() {

        return password;
    }

    public void setPassword(String password) {

        this.password = password;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public helperClass(){

    }
}
//-----------------------------------------IM/2021/062 - Hasindu ---------------------------------------------------//
