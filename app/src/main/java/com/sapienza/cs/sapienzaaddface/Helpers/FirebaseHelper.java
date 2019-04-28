package com.sapienza.cs.sapienzaaddface.Helpers;

import android.app.ProgressDialog;
import android.content.Context;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sapienza.cs.sapienzaaddface.Listeners.FirebaseListener;
import com.sapienza.cs.sapienzaaddface.Objects.ImageObject;

import java.util.ArrayList;
import java.util.List;

public final class FirebaseHelper {
    private static final FirebaseDatabase firebaseDatabase;
    private static Context context;
    private static ProgressDialog dialog;
    static {
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.setPersistenceEnabled(true);
    }

    public static FirebaseDatabase getDatabase() {
        return firebaseDatabase;
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
                if(snapshot.exists()) {
                    imageList.clear();
                    for (DataSnapshot imageObj : snapshot.getChildren()) {
                        String uid = imageObj.getKey();
                        String imageString = imageObj.child("image").getValue(String.class);
                        String personId = imageObj.child("groupId").getValue(String.class);
                        String personName = imageObj.child("personName").getValue(String.class);
                        ImageObject image = new ImageObject(uid, personId, imageString, personName);
                        imageList.add(image);
                    }
                    listener.onCallBack(imageList, this, query.getRef());
                }
                dialog.dismiss();
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

}
