package com.example.fanutsystem;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class Child implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String dob;
    private String gender;
    private String muac;
    private String weight;
    private String height;

    public Child(String name, String dob, String gender, String muac, String weight, String height) {
        this(UUID.randomUUID().toString(), name, dob, gender, muac, weight, height);
    }

    public Child(String id, String name, String dob, String gender, String muac, String weight, String height) {
        this.id = (id != null && !id.isEmpty()) ? id : UUID.randomUUID().toString();
        this.name = name;
        this.dob = dob;
        this.gender = gender;
        this.muac = muac;
        this.weight = weight;
        this.height = height;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDob() {
        return dob;
    }

    public String getGender() {
        return gender;
    }

    public String getMuac() {
        return muac;
    }

    public String getWeight() {
        return weight;
    }

    public String getHeight() {
        return height;
    }

    public int getAgeInMonths() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        try {
            Date birthDate = sdf.parse(dob);
            Calendar birth = Calendar.getInstance();
            birth.setTime(birthDate);
            Calendar now = Calendar.getInstance();

            int years = now.get(Calendar.YEAR) - birth.get(Calendar.YEAR);
            int months = now.get(Calendar.MONTH) - birth.get(Calendar.MONTH);

            return (years * 12) + months;
        } catch (ParseException e) {
            return 0;
        }
    }
}
