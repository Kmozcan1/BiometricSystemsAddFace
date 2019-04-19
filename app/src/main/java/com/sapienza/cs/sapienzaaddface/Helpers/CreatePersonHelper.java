package com.sapienza.cs.sapienzaaddface.Helpers;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.contract.CreatePersonResult;
import com.microsoft.projectoxford.face.contract.Face;

public class CreatePersonHelper extends AsyncTask<String, String, String> {

    Context context;
    ProgressDialog dialog;

    public CreatePersonHelper(Context context) {
        this.context = context;
        dialog = new ProgressDialog(context);
    }

    @Override
    protected String doInBackground(String... params) {
        publishProgress("Creating Person...");
        String groupId = params[0];
        String name = params[1];

        FaceServiceClient faceServiceClient = ConnectionHelper.getFaceServiceClient();
        try {
            CreatePersonResult result = faceServiceClient.createPerson(groupId, name, "User Data");
            return result.personId.toString();
        } catch(Exception e) {
            publishProgress(e.getMessage());
            return null;
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
    protected void onPostExecute(String personId) {
        dialog.dismiss();
    }
}
