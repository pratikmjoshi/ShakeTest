package com.example.pratikjoshi.shaketest;

import android.app.Application;

/**
 * Created by Pratik Joshi on 14/07/2016.
 */
public class ContactDB extends Application {
    EmergContactDBHelper dbHelper;
    @Override
    public void onCreate() {
        super.onCreate();
        dbHelper = new EmergContactDBHelper(this);
    }
}
