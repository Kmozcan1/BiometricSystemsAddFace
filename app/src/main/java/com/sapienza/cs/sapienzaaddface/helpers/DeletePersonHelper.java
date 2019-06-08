package com.sapienza.cs.sapienzaaddface.helpers;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.microsoft.projectoxford.face.FaceServiceClient;

import java.util.UUID;

public class DeletePersonHelper extends AsyncTask<Object, String, UUID> {
    Context context;
    ProgressDialog dialog;

    public DeletePersonHelper(Context context) {
        this.context = context;
        dialog = new ProgressDialog(context);
    }

    @Override
    protected UUID doInBackground(Object... params) {
        publishProgress("Deleting Person...");
        String groupId = (String)params[0];
        UUID personId = (UUID)params[1];

        FaceServiceClient faceServiceClient = ConnectionHelper.getFaceServiceClient();
        try {
            faceServiceClient.deletePerson(groupId, personId);
            return personId;
        } catch(Exception e) {
            publishProgress(e.getMessage());
            return personId;
        }
    }

    @Override
    protected void onPreExecute() {
        dialog = new ProgressDialog(context);
        dialog.setTitle("Please Wait");
        dialog.show();
    }

    @Override
    protected void onProgressUpdate(String... progress) {
        dialog.setMessage(progress[0]);
    }

    @Override
    protected void onPostExecute(UUID personId) {
        dialog.dismiss();
    }
}
