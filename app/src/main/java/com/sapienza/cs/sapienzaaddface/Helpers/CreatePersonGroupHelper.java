package com.sapienza.cs.sapienzaaddface.Helpers;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.contract.CreatePersonResult;

public class CreatePersonGroupHelper extends AsyncTask<String, String, String> {

    Context context;
    ProgressDialog dialog;

    public CreatePersonGroupHelper(Context context) {
        this.context = context;
        dialog = new ProgressDialog(context);
    }
    @Override
    protected String doInBackground(String... params) {
        publishProgress("Creating Person Group...");
        String groupId = params[0];
        String name = params[1];

        FaceServiceClient faceServiceClient = ConnectionHelper.getFaceServiceClient();
        try {
            faceServiceClient.createPersonGroup(groupId, name, "User Data");
            return "OKAY";
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
    protected void onPostExecute(String result) {
        dialog.dismiss();
    }
}
