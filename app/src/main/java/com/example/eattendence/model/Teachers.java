package com.example.eattendence.model;

import java.util.ArrayList;
import java.util.HashMap;

public class Teachers {
    public String uid, name,email, contact;
    ArrayList<String> subjects;
    HashMap result;

    public String getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getContact() {
        return contact;
    }

    public HashMap getResult() {
        return result;
    }


    Teachers(){
        result = new HashMap<>();
        result.put("uid", uid);
        result.put("name", name);
        result.put("email", email);
        result.put("cont", contact);
    }

    HashMap getStudent(){
        return result;
    }
}
