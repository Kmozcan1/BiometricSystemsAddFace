package com.sapienza.cs.sapienzaaddface.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.dhruv.timerbutton.TimerButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.sapienza.cs.sapienzaaddface.helpers.FirebaseHelper;
import com.sapienza.cs.sapienzaaddface.listeners.FirebaseListener;
import com.sapienza.cs.sapienzaaddface.R;

public class AddFingerprintActivity extends Activity {
    private static final long MILLIS_IN_FUTURE = 30000L;
    private TimerButton timerButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addfingerprint);
        timerButton = findViewById(R.id.timer_button);
        timerButton.setDuration(MILLIS_IN_FUTURE);



        timerButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                

            }
        });



    }



    public void startTimer(View view) {
        timerButton.startAnimation();
        String personId = getIntent().getStringExtra("PERSON_ID");
        FirebaseHelper.insertFingerprint(personId);



        FirebaseHelper.getSingleFingerprint(AddFingerprintActivity.this,
                personId, new FirebaseListener() {
                    @Override
                    public void onCallBack(Object value, ValueEventListener listener, DatabaseReference query) {
                        if ((Integer)value == 1) {
                            timerButton.reset();
                        }
                    }
                });
    }
}
