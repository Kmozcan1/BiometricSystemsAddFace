package com.sapienza.cs.sapienzaaddface.Helpers;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.contract.AddPersistedFaceResult;
import com.microsoft.projectoxford.face.contract.FaceRectangle;

import java.io.InputStream;
import java.util.UUID;

public class AddFaceHelper extends AsyncTask<Object, String, Boolean> {

    Context context;
    ProgressDialog dialog;

    public AddFaceHelper(Context context) {
        this.context = context;
        dialog = new ProgressDialog(context);
    }

    @Override
    protected Boolean doInBackground(Object... params) {
        publishProgress("Adding Face...");
        String groupId =  (String) params[0];
        UUID personId = UUID.fromString((String)params[1]);
        InputStream imageInputStream = (InputStream) params[2];
        FaceRectangle faceRectangle = (FaceRectangle) params[3];

        FaceServiceClient faceServiceClient = ConnectionHelper.getFaceServiceClient();
        try {
            AddPersistedFaceResult result =  faceServiceClient.addPersonFace(groupId, personId, imageInputStream, "Kadir", faceRectangle);
            return true;
        } catch(Exception e) {
            publishProgress(e.getMessage());
            return false;
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
    protected void onPostExecute(Boolean result) {
        dialog.dismiss();
    }
}
