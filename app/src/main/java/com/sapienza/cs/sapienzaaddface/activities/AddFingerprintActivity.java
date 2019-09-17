package com.sapienza.cs.sapienzaaddface.activities;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.dhruv.timerbutton.TimerButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.sapienza.cs.sapienzaaddface.helpers.FirebaseHelper;
import com.sapienza.cs.sapienzaaddface.listeners.FirebaseListener;
import com.sapienza.cs.sapienzaaddface.R;

public class AddFingerprintActivity extends Activity {
    private static final long MILLIS_IN_FUTURE = 30000L;
    private TimerButton timerButton;
    private boolean success = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addfingerprint);
        timerButton = findViewById(R.id.timer_button);
        timerButton.setDuration(MILLIS_IN_FUTURE);



        timerButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (success == false) {

                }
            }
        });



    }

    private void startTimer(long time){
        final Button transparentButton = findViewById(R.id.transparentButton);
        success = false;
        CountDownTimer counter = new CountDownTimer(30000, 1000){
            public void onTick(long millisUntilDone){

            }

            public void onFinish() {
                transparentButton.setVisibility(View.VISIBLE);
                if (success == false) {
                    Toast.makeText(getApplicationContext(), "Fingerprint submission unsuccessful!", Toast.LENGTH_LONG).show();

                }
            }
        }.start();
    }


    public void startTimer(View view) {
        timerButton.startAnimation();
        String personId = getIntent().getStringExtra("PERSON_ID");
        FirebaseHelper.insertFingerprint(personId);


        final Button transparentButton = findViewById(R.id.transparentButton);
        transparentButton.setVisibility(View.GONE);

        FirebaseHelper.getSingleFingerprint(AddFingerprintActivity.this,
            personId, new FirebaseListener() {
                @Override
                public void onCallBack(Object value, ValueEventListener listener, DatabaseReference query) {
                    if ((Integer)value == 1) {
                        timerButton.reset();
                        Toast.makeText(getApplicationContext(), "Fingerprint was successfully submitted!", Toast.LENGTH_LONG).show();
                        transparentButton.setVisibility(View.VISIBLE);
                        success = true;
                    }
                }
            }
        );
    }
}
