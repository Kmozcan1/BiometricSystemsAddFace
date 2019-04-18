package com.sapienza.cs.sapienzaaddface;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

public class ViewPersonGroupActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewpersongroup);
    }

    public void addFace(View view) {
        Intent intent = new Intent(this, AddFaceActivity.class);
        startActivity(intent);
    }
}
