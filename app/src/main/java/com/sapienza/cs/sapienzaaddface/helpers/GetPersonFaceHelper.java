package com.sapienza.cs.sapienzaaddface.helpers;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.contract.Person;
import com.microsoft.projectoxford.face.contract.PersonFace;
import com.sapienza.cs.sapienzaaddface.objects.ImageObject;
import java.util.ArrayList;
import java.util.List;

public class GetPersonFaceHelper extends AsyncTask<Object, String, List<ImageObject>> {

    String groupId;
    Context context;
    ProgressDialog dialog;
    List<ImageObject> imageList;

    public GetPersonFaceHelper(Context context) {
        this.context = context;
        dialog = new ProgressDialog(context);
    }

    @Override
    protected List<ImageObject> doInBackground(Object... params) {
        publishProgress("Fetching Faces...");
        groupId = (String)params[0];
        Person[] personList = (Person[])params[1];
        List<PersonFace> faceList = new ArrayList<>();

        FaceServiceClient faceServiceClient = ConnectionHelper.getFaceServiceClient();
        try {
            /*for (Person person : personList) {
                if (person.persistedFaceIds.length > 0) {
                    PersonFace face = faceServiceClient.getPersonFace(groupId, person.personId, person.persistedFaceIds[0]);
                    faceList.add(face);
                }
            }*/
            return null;
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
    protected void onPostExecute(List<ImageObject> faceList) {
        dialog.dismiss();
    }
}
