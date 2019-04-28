package com.sapienza.cs.sapienzaaddface.Helpers;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.contract.CreatePersonResult;

public class TrainPersonGroupHelper extends AsyncTask<String, String, Boolean> {

    Context context;
    ProgressDialog dialog;

    public TrainPersonGroupHelper(Context context) {
        this.context = context;
        dialog = new ProgressDialog(context);
    }
    @Override
    protected Boolean doInBackground(String... params) {
        publishProgress("Training Person Group...");
        String groupId = params[0];

        FaceServiceClient faceServiceClient = ConnectionHelper.getFaceServiceClient();
        try {
            faceServiceClient.trainPersonGroup(groupId);
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
