package com.nicholasdoherty.socialcore.genders;

public class Gender {
    private final String genderName;

    public Gender(String genderName){
        this.genderName = genderName;
    }

    public String getName(){ return genderName; }
}
