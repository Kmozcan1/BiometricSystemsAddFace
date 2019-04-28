package com.sapienza.cs.sapienzaaddface;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.microsoft.projectoxford.face.contract.AddPersistedFaceResult;
import com.microsoft.projectoxford.face.contract.Face;
import com.microsoft.projectoxford.face.contract.FaceRectangle;
import com.sapienza.cs.sapienzaaddface.Adapters.FaceGridViewAdapter;
import com.sapienza.cs.sapienzaaddface.Helpers.AddFaceHelper;
import com.sapienza.cs.sapienzaaddface.Helpers.CreatePersonHelper;
import com.sapienza.cs.sapienzaaddface.Helpers.DetectionHelper;
import com.sapienza.cs.sapienzaaddface.Helpers.FirebaseHelper;
import com.sapienza.cs.sapienzaaddface.Helpers.TrainPersonGroupHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddFaceActivity extends Activity {

    private static final CharSequence NO_FACES_IN_PHOTO = "No faces were found in the photo!";
    private static final CharSequence NAME_EMPTY = "Please submit a name for this photo";
    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final int REQUEST_PICK_IMAGE = 0;
    private Uri photoURI;
    private FaceGridViewAdapter faceGridViewAdapter;
    private InputStream imageInputStream;
    private int mPosition;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addface);
        fAuth= FirebaseAuth.getInstance();

        GridView gridView = findViewById(R.id.gridview_faces);
        gridView.setOnItemClickListener(new GridView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                EditText editTextName = findViewById(R.id.edittext_name);
                mPosition = position;
                if (SubmissionValid()) {
                    new CreatePerson(AddFaceActivity.this).execute(FirebaseHelper.getGroupId(),
                            editTextName.getText().toString());
                } else {
                    Context context = getApplicationContext();
                    int duration = Toast.LENGTH_SHORT;
                    Toast.makeText(context, NAME_EMPTY, duration).show();
                }


            }
        });

    }

    public void fromCamera(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(this,
                        "com.sapienza.cs.sapienzaaddface.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }

    }

    public void fromGallery(View view) {
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

        startActivityForResult(chooserIntent, REQUEST_PICK_IMAGE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode)
        {
            case REQUEST_TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    initializeDetection();
                }
                break;
            case REQUEST_PICK_IMAGE:
                if (resultCode == RESULT_OK) {
                    photoURI = data.getData();
                    initializeDetection();
                }
            default:
                break;
        }
    }

    public void initializeDetection() {
        try {
            imageInputStream = getContentResolver().openInputStream(photoURI);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        new DetectFace(AddFaceActivity.this).execute(imageInputStream);
    }

    String currentPhotoPath;
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private class DetectFace extends DetectionHelper {
        public DetectFace(Context context) {
            super(context);
        }

        @Override
        protected void onPostExecute(Face[] faces) {
            super.onPostExecute(faces);
            if (faces == null || faces.length == 0) {
                Context context = getApplicationContext();
                int duration = Toast.LENGTH_SHORT;
                Toast.makeText(context, NO_FACES_IN_PHOTO, duration).show();
                GridView gridView = findViewById(R.id.gridview_faces);
                gridView.setAdapter(null);
            }
            else {
                try {
                    faceGridViewAdapter = new FaceGridViewAdapter(getBaseContext(), faces, photoURI);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                GridView gridView = findViewById(R.id.gridview_faces);
                gridView.setAdapter(faceGridViewAdapter);
            }
        }
    }

    private class CreatePerson extends CreatePersonHelper {
        public CreatePerson(Context context) {
            super(context);
        }

        @Override
        protected void onPostExecute(String personId) {
            super.onPostExecute(personId);
            FaceRectangle faceRect = faceGridViewAdapter.faceRectList.get(mPosition);
            Bitmap faceMap = faceGridViewAdapter.faceThumbnails.get(mPosition);

            if (personId != null) {
                EditText editTextName = findViewById(R.id.edittext_name);
                String name = editTextName.getText().toString();
                try {
                    new AddFace(AddFaceActivity.this).execute(FirebaseHelper.getGroupId(), personId, getContentResolver().openInputStream(photoURI), faceRect, faceMap, name);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class AddFace extends AddFaceHelper {
        public AddFace(Context context) {
            super(context);
        }

        @Override
        protected void onPostExecute(AddPersistedFaceResult result) {
            super.onPostExecute(result);
            if (result != null) {
                new TrainPersonGroup(AddFaceActivity.this).execute(FirebaseHelper.getGroupId());
            }
        }
    }

    private class TrainPersonGroup extends TrainPersonGroupHelper {
        public TrainPersonGroup(Context context) {
            super(context);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result == true) {
                Intent intent = new Intent(AddFaceActivity.this, ViewPersonGroupActivity.class);
                startActivity(intent);
            }
        }
    }

    private boolean SubmissionValid() {
        EditText editTextName = findViewById(R.id.edittext_name);
        if (editTextName.getText().toString().equals("")) {
            return false;
        }
        return true;
    }



}
