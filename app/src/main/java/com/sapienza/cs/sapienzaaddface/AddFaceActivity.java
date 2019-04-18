package com.sapienza.cs.sapienzaaddface;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import android.widget.GridView;
import android.widget.Toast;

import com.microsoft.projectoxford.face.contract.Face;
import com.sapienza.cs.sapienzaaddface.Adapters.FaceGridViewAdapter;
import com.sapienza.cs.sapienzaaddface.Helpers.DetectionHelper;
import com.sapienza.cs.sapienzaaddface.Helpers.ImageHelper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddFaceActivity extends Activity {

    static final int REQUEST_TAKE_PHOTO = 1;
    private Uri photoURI;
    FaceGridViewAdapter faceGridViewAdapter;
    ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addface);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle(getString(R.string.progress_dialog_title));
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode)
        {
            case REQUEST_TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    Uri imageUri = Uri.parse(String.valueOf(photoURI));
                    /*Bitmap bitmap = ImageHelper.loadSizeLimitedBitmapFromUri(
                            imageUri, getContentResolver());
                    if (bitmap != null) {
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                        InputStream imageInputStream = new ByteArrayInputStream(stream.toByteArray());
                        new DetectFace().execute(imageInputStream);
                    }*/
                    InputStream imageInputStream = null;
                    try {
                        imageInputStream = getContentResolver().openInputStream(photoURI);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    new DetectFace().execute(imageInputStream);
                }
                break;
            default:
                break;
        }
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
        @Override
        protected void onPostExecute(Face[] faces) {
            mProgressDialog.dismiss();
            if (faces == null) {
                Context context = getApplicationContext();
                CharSequence text = "No faces were found in the photo!";
                int duration = Toast.LENGTH_SHORT;
                Toast.makeText(context, text, duration).show();
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

        @Override
        protected void onPreExecute() {
            mProgressDialog.show();
        }

        @Override
        protected void onProgressUpdate(String... progress) {
            mProgressDialog.setMessage(progress[0]);
        }
    }

}
