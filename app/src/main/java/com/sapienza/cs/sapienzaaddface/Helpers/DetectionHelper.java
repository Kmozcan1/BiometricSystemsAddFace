package com.sapienza.cs.sapienzaaddface.Helpers;

import android.os.AsyncTask;

import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.contract.Face;

import java.io.InputStream;

public class DetectionHelper extends AsyncTask<InputStream, String, Face[]> {

    @Override
    protected Face[] doInBackground(InputStream... params) {
        publishProgress("Detecting...");
        FaceServiceClient faceServiceClient = ConnectionHelper.getFaceServiceClient();
        try {
            return faceServiceClient.detect(params[0], true, false, null);
        } catch(Exception e) {
            return null;
        }
    }
}
