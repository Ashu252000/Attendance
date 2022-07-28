package com.example.eattendence.model;

import java.util.HashMap;

public class Students {

    public String uid,prn,branch, name,email, semester, contact;
    HashMap result;



    public String getUid() {
        return uid;
    }

    public String getPrn() {
        return prn;
    }

    public String getBranch() {
        return branch;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getSemester() {
        return semester;
    }

    public String getContact() {
        return contact;
    }

    public HashMap getResult() {
        return result;
    }

    Students(){
        result = new HashMap<>();
        result.put("uid", uid);
        result.put("prn", prn);
        result.put("branch", branch);
        result.put("name", name);
        result.put("email", email);
        result.put("sem", semester);
        result.put("cont", contact);
    }

    HashMap getStudent(){
        return result;
    }
}
