package com.sapienza.cs.sapienzaaddface;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.rest.ClientException;
import com.sapienza.cs.sapienzaaddface.Helpers.ConnectionHelper;

import java.io.IOException;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new CreatePersonGroup().execute("Deneme", "Deneme", "Yanilma");

        //TODO - Login/Register
    }

    private void createPersonGroup(String groupId) throws IOException, ClientException {


    }

    private class CreatePersonGroup extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            /*FaceServiceClient faceServiceClient = ConnectionHelper.getFaceServiceClient();
            try {
                faceServiceClient.createPersonGroup(params[0], params[1], params[2]);
            }catch (Exception e) {
                publishProgress(e.getMessage());
                return null;
            }*/
            return "ok";
        }

        @Override
        protected void onPostExecute(String s) {
            Intent intent = new Intent(MainActivity.this, ViewPersonGroupActivity.class);
            startActivity(intent);
        }
    }
}
