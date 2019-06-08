package com.sapienza.cs.sapienzaaddface.helpers;

import android.app.ProgressDialog;
import android.content.Context;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sapienza.cs.sapienzaaddface.listeners.FirebaseListener;
import com.sapienza.cs.sapienzaaddface.objects.ImageObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class FirebaseHelper {
    private static final FirebaseDatabase firebaseDatabase;
    private static Context context;
    private static ProgressDialog dialog;
    private static String groupId;
    static {
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.setPersistenceEnabled(true);
    }

    public static String getGroupId() {
        return groupId;
    }

    public static void setGroupId(String id) {
        groupId = id;
    }

    private static FirebaseDatabase getDatabase() {
        return firebaseDatabase;
    }

    private static final int ACTIVE_TIME = 30;


    public final static void getSingleFingerprint(Context c, String personId, final FirebaseListener listener) {
        context = c;
        dialog = new ProgressDialog(context);
        dialog.setTitle("Please Wait");
        dialog.show();
        dialog.setMessage("Getting Faces...");
        final DatabaseReference reference = firebaseDatabase.getReference();
        final Query query = reference.child("deneme").child("activePersonID");


        query.addValueEventListener((new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    int enrollFinished = snapshot.child("enrollFinished").getValue(Integer.class);
                    listener.onCallBack(enrollFinished, this, query.getRef());
                } else {
                    listener.onCallBack(0, this, query.getRef());
                }
                dialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        }));
    }


    public final static void getSingleImage(Context c, String personId, final FirebaseListener listener) {
        context = c;
        dialog = new ProgressDialog(context);
        dialog.setTitle("Please Wait");
        dialog.show();
        dialog.setMessage("Getting Faces...");
        final DatabaseReference reference = firebaseDatabase.getReference();
        final Query query = reference.child("images").child(personId);

        final List<ImageObject> imageList = new ArrayList<>();

        query.addValueEventListener((new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                imageList.clear();
                if(snapshot.exists()) {
                    String uid = snapshot.getKey();
                    String imageString = snapshot.child("image").getValue(String.class);
                    String personId = snapshot.child("groupId").getValue(String.class);
                    String personName = snapshot.child("personName").getValue(String.class);
                    ImageObject image = new ImageObject(uid, personId, imageString, personName);
                    imageList.add(image);
                    listener.onCallBack(imageList, this, query.getRef());
                } else {
                    listener.onCallBack(imageList, this, query.getRef());

                }
                dialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        }));
    }

    public final static void getImages(Context c, String groupId, final FirebaseListener listener) {
        context = c;
        dialog = new ProgressDialog(context);
        dialog.setTitle("Please Wait");
        dialog.show();
        dialog.setMessage("Getting Faces...");
        final DatabaseReference reference = firebaseDatabase.getReference();
        final Query query = reference.child("images").orderByChild("groupId").equalTo(groupId);
        final List<ImageObject> imageList = new ArrayList<>();

        query.addValueEventListener((new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                imageList.clear();
                if(snapshot.exists()) {
                    for (DataSnapshot imageObj : snapshot.getChildren()) {
                        String uid = imageObj.getKey();
                        String imageString = imageObj.child("image").getValue(String.class);
                        String groupId = imageObj.child("groupId").getValue(String.class);
                        String personName = imageObj.child("personName").getValue(String.class);
                        ImageObject image = new ImageObject(uid, groupId, imageString, personName);
                        imageList.add(image);
                    }
                    listener.onCallBack(imageList, this, query.getRef());
                } else {
                    listener.onCallBack(imageList, this, query.getRef());

                }
                dialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        }));
    }

    public static final void getSingleUser(Context c, String uid, final FirebaseListener listener) {
        context = c;
        dialog = new ProgressDialog(context);
        dialog.setTitle("Please Wait");
        dialog.setMessage("Getting User...");
        final DatabaseReference reference = firebaseDatabase.getReference();
        final Query query = reference.child("users").child(uid);
        query.addValueEventListener((new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    String groupId = snapshot.child("groupId").getValue(String.class);
                    listener.onCallBack(groupId, this, query.getRef());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        }));
    }

    public static final void insertImage(ImageObject image) {
        final DatabaseReference reference = getDatabase().getReference();
        reference.child("images").child(image.getImageIdAsString()).child("image").setValue(image.getImageString());
        reference.child("images").child(image.getImageIdAsString()).child("groupId").setValue(image.getGroupId());
        reference.child("images").child(image.getImageIdAsString()).child("personName").setValue(image.getPersonName());
    }

    public static final void createUser(String uid, String groupId) {
        final DatabaseReference reference = getDatabase().getReference();
        reference.child("users").child(uid).setValue(uid);
        reference.child("users").child(uid).child("groupId").setValue(groupId);
    }

    public static final void deleteFace(UUID personId) {
        final DatabaseReference reference = getDatabase().getReference();
        DatabaseReference query = reference.child("images").child(personId.toString());
        query.removeValue();
    }

    public final static void insertFingerprint(String personId) {
        final DatabaseReference reference = getDatabase().getReference();
        reference.child("deneme").child("activeTime").setValue(ACTIVE_TIME);
        reference.child("deneme").child("activeUserID").setValue(personId);
        reference.child("deneme").child("enrollFinished").setValue(0);
        reference.child("deneme").child("needsEnroll").setValue(0);
    }
}
