package com.sapienza.cs.sapienzaaddface.helpers;

import android.app.Application;

import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.FaceServiceRestClient;
import com.sapienza.cs.sapienzaaddface.R;

public class ConnectionHelper extends Application {
    private static FaceServiceClient faceServiceClient;
    @Override
    public void onCreate() {
        super.onCreate();
        faceServiceClient = new FaceServiceRestClient(getString(R.string.service_host), getString(R.string.subscription_key));
    }

    public static FaceServiceClient getFaceServiceClient() {
        return faceServiceClient;
    }
}
