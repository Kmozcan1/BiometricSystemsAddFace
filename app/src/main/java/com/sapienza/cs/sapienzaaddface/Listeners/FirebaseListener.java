package com.sapienza.cs.sapienzaaddface.Listeners;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public interface FirebaseListener {
    void onCallBack(Object value, ValueEventListener listener, DatabaseReference query);
}
