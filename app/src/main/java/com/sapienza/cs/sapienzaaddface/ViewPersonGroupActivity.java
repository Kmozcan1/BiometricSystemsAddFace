package com.sapienza.cs.sapienzaaddface;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.microsoft.projectoxford.face.contract.Person;
import com.sapienza.cs.sapienzaaddface.Adapters.FaceGridViewAdapter;
import com.sapienza.cs.sapienzaaddface.Helpers.FirebaseHelper;
import com.sapienza.cs.sapienzaaddface.Helpers.GetPersonFaceHelper;
import com.sapienza.cs.sapienzaaddface.Helpers.GetPersonsHelper;
import com.sapienza.cs.sapienzaaddface.Listeners.FirebaseListener;
import com.sapienza.cs.sapienzaaddface.Objects.ImageObject;

import java.util.List;

public class ViewPersonGroupActivity extends Activity {
    private FaceGridViewAdapter faceGridViewAdapter;
    Button signOut;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewpersongroup);
        fAuth= FirebaseAuth.getInstance();

        if (FirebaseHelper.getGroupId() == null) {
            FirebaseHelper.getSingleUser(ViewPersonGroupActivity.this,
                    fAuth.getCurrentUser().getUid(), new FirebaseListener() {
                        @Override
                        public void onCallBack(Object value, ValueEventListener listener, DatabaseReference query) {
                            FirebaseHelper.setGroupId((String) value);
                        }
                    });
        }

        new ListPersons(ViewPersonGroupActivity.this).execute(FirebaseHelper.getGroupId());


        signOut= findViewById(R.id.button_signout);

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fAuth.signOut();
                Intent intent = new Intent(ViewPersonGroupActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    public void addFace(View view) {
        Intent intent = new Intent(this, AddFaceActivity.class);
        startActivity(intent);
    }

    private class ListPersons extends GetPersonsHelper {
        public ListPersons(Context context) {
            super(context);
        }

        @Override
        protected void onPostExecute(Person[] personList) {
            super.onPostExecute(personList);
            new GetFaces(ViewPersonGroupActivity.this).execute(FirebaseHelper.getGroupId(), personList);
        }
    }

    private class GetFaces extends GetPersonFaceHelper {
        public GetFaces(Context context) { super (context); }

        @Override
        protected void onPostExecute(List<ImageObject> faceList) {
            super.onPostExecute(faceList);

            FirebaseHelper.getImages(ViewPersonGroupActivity.this,
                    FirebaseHelper.getGroupId(), new FirebaseListener() {
                @Override
                public void onCallBack(Object value, ValueEventListener listener, DatabaseReference query) {
                    List<ImageObject> faceList;
                    faceList = (List<ImageObject>) value;
                    faceGridViewAdapter = new FaceGridViewAdapter(getBaseContext(), faceList);
                    GridView gridView = findViewById(R.id.gridview_people);
                    gridView.setAdapter(faceGridViewAdapter);
                }
            });

        }
    }
}
