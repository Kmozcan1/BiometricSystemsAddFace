package com.sapienza.cs.sapienzaaddface.helpers;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.contract.AddPersistedFaceResult;
import com.microsoft.projectoxford.face.contract.FaceRectangle;
import com.sapienza.cs.sapienzaaddface.objects.ImageObject;

import java.io.InputStream;
import java.util.UUID;

public class AddFaceHelper extends AsyncTask<Object, String, AddPersistedFaceResult> {

    Context context;
    ProgressDialog dialog;
    Bitmap faceMap;
    String groupId;
    String userData;
    UUID personId;

    public AddFaceHelper(Context context) {
        this.context = context;
        dialog = new ProgressDialog(context);
    }

    @Override
    protected AddPersistedFaceResult doInBackground(Object... params) {
        publishProgress("Adding Face...");
        groupId =  (String) params[0];
        personId = UUID.fromString((String)params[1]);
        InputStream imageInputStream = (InputStream) params[2];
        FaceRectangle faceRectangle = (FaceRectangle) params[3];
        faceMap = (Bitmap) params[4];
        userData = (String) params[5];

        FaceServiceClient faceServiceClient = ConnectionHelper.getFaceServiceClient();

        try {
            AddPersistedFaceResult result =  faceServiceClient.addPersonFace(groupId, personId, imageInputStream, userData, faceRectangle);
            return result;
        } catch(Exception e) {
            publishProgress(e.getMessage());
            return null;
        }
    }
    @Override
    protected void onPreExecute() {
        dialog.setTitle("Please Wait");
        dialog.show();
    }

    @Override
    protected void onProgressUpdate(String... progress) {
        dialog.setMessage(progress[0]);
    }

    @Override
    protected void onPostExecute(AddPersistedFaceResult result) {
        String imageString = ImageHelper.bitmapToString(faceMap);
        FirebaseHelper.insertImage(new ImageObject(personId, groupId, imageString, userData));
        dialog.dismiss();
    }
}
