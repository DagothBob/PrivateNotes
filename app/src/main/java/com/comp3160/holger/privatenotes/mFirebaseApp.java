package com.comp3160.holger.privatenotes;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

// This class exists solely for preventing the
// "setPersistenceEnabled must be called before
//  any other uses of FireBaseDatabase" error
// when the app is resumed.
public class mFirebaseApp extends Application {
    
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
