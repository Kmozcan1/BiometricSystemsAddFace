package com.sapienza.cs.sapienzaaddface.helpers;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.contract.Person;

public class GetPersonsHelper extends AsyncTask<String, String, Person[]> {

    Context context;
    ProgressDialog dialog;

    public GetPersonsHelper(Context context) {
        this.context = context;
        dialog = new ProgressDialog(context);
    }

    @Override
    protected Person[] doInBackground(String... params) {
        publishProgress("Fetching Person List...");
        String groupId = params[0];

        FaceServiceClient faceServiceClient = ConnectionHelper.getFaceServiceClient();
        try {
            Person[] personList = faceServiceClient.listPersons(groupId);
            return personList;
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
    protected void onPostExecute(Person[] personList) {
        dialog.dismiss();
    }
}
