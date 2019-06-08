package com.sapienza.cs.sapienzaaddface.helpers;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.microsoft.projectoxford.face.FaceServiceClient;

public class CreatePersonGroupHelper extends AsyncTask<String, String, Boolean> {

    Context context;
    ProgressDialog dialog;

    public CreatePersonGroupHelper(Context context) {
        this.context = context;
        dialog = new ProgressDialog(context);
    }
    @Override
    protected Boolean doInBackground(String... params) {
        publishProgress("Creating Person Group...");
        String groupId = params[0];
        String name = params[1];

        if (name == null)
            name = "No Name";

        FaceServiceClient faceServiceClient = ConnectionHelper.getFaceServiceClient();
        try {
            faceServiceClient.createPersonGroup(groupId, name, "User Data");
            return true;
        } catch(Exception e) {
            publishProgress(e.getMessage());
            return false;
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
    protected void onPostExecute(Boolean result) {
        dialog.dismiss();
    }
}
