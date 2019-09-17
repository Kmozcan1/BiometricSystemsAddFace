package com.sapienza.cs.sapienzaaddface.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.microsoft.projectoxford.face.contract.Person;
import com.sapienza.cs.sapienzaaddface.adapters.FaceGridViewAdapter;
import com.sapienza.cs.sapienzaaddface.helpers.DeletePersonHelper;
import com.sapienza.cs.sapienzaaddface.helpers.FirebaseHelper;
import com.sapienza.cs.sapienzaaddface.helpers.GetPersonFaceHelper;
import com.sapienza.cs.sapienzaaddface.helpers.GetPersonsHelper;
import com.sapienza.cs.sapienzaaddface.listeners.FirebaseListener;
import com.sapienza.cs.sapienzaaddface.objects.ImageObject;
import com.sapienza.cs.sapienzaaddface.R;

import java.util.List;
import java.util.UUID;

public class ViewPersonGroupActivity extends Activity {
    private FaceGridViewAdapter faceGridViewAdapter;
    private GridView faceGridView;
    Button signOut;
    FirebaseAuth fAuth;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewpersongroup);
        fAuth= FirebaseAuth.getInstance();

        if(FirebaseHelper.getGroupId() != null) {
            FirebaseHelper.setGroupId(null);
        }

        getActionBar().show();
        faceGridView = findViewById(R.id.gridview_people);
        //faceGridViewAdapter = new FaceGridViewAdapter(getBaseContext(), null);

        if (FirebaseHelper.getGroupId() == null) {
            FirebaseHelper.getSingleUser(ViewPersonGroupActivity.this,
                    fAuth.getCurrentUser().getUid(), new FirebaseListener() {
                        @Override
                        public void onCallBack(Object value, ValueEventListener listener, DatabaseReference query) {
                            FirebaseHelper.setGroupId((String) value);
                            //new ListPersons(ViewPersonGroupActivity.this).execute(FirebaseHelper.getGroupId());
                            query.removeEventListener(listener);
                            getImagesFromFirebase();
                        }
                    });
        }



        signOut= findViewById(R.id.button_signout);

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fAuth.signOut();
                Intent intent = new Intent(ViewPersonGroupActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        faceGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(ViewPersonGroupActivity.this)
                        .setTitle("Delete Person").setMessage("Are you sure you want to delete this person?")

                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                ImageObject image = (ImageObject)faceGridViewAdapter.getItem(position);
                                new DeletePerson(ViewPersonGroupActivity.this).execute(image.getGroupId(), image.getUid());
                            }
                        }).setNegativeButton(android.R.string.no, null).show();
                return true;
            }
        });

        faceGridView.setOnItemClickListener(new GridView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                ImageObject image = (ImageObject)faceGridViewAdapter.getItem(position);

                Intent intent = new Intent(getBaseContext(), AddFingerprintActivity.class);
                intent.putExtra("PERSON_ID", image.getUid().toString());
                startActivity(intent);
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
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





        }
    }

    private class DeletePerson extends DeletePersonHelper {
        public DeletePerson(Context context) { super (context); }

        @Override
        protected void onPostExecute(UUID personId) {
            super.onPostExecute(personId);
            FirebaseHelper.deleteFace(personId);
            //new ListPersons(ViewPersonGroupActivity.this).execute(FirebaseHelper.getGroupId());
            getImagesFromFirebase();
        }
    }

    @Override
    public void onBackPressed() {
        int a = 1;
    }

    private void getImagesFromFirebase() {
        FirebaseHelper.getImages(ViewPersonGroupActivity.this,
            FirebaseHelper.getGroupId(), new FirebaseListener() {
                @Override
                public void onCallBack(Object value, ValueEventListener listener, DatabaseReference query) {
                    List<ImageObject> faceList;
                    faceList = (List<ImageObject>) value;
                    faceGridViewAdapter = new FaceGridViewAdapter(getBaseContext(), faceList);
                    faceGridView = findViewById(R.id.gridview_people);
                    faceGridView.setAdapter(faceGridViewAdapter);
                    query.removeEventListener(listener);
                }
            });
    }
}
