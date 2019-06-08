package com.sapienza.cs.sapienzaaddface.helpers;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.contract.Face;

import java.io.InputStream;

public class DetectionHelper extends AsyncTask<InputStream, String, Face[]> {

    Context context;
    ProgressDialog dialog;

    public DetectionHelper(Context context) {
        this.context = context;
    }

    @Override
    protected Face[] doInBackground(InputStream... params) {
        publishProgress("Detecting...");
        FaceServiceClient faceServiceClient = ConnectionHelper.getFaceServiceClient();
        try {
            return faceServiceClient.detect(params[0], true, false, null);
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
    protected void onPostExecute(Face[] faces) {
        dialog.dismiss();
    }
}
