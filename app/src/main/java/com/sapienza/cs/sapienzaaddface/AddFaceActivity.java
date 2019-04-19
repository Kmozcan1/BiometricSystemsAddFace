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
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.microsoft.projectoxford.face.contract.Face;
import com.microsoft.projectoxford.face.contract.FaceRectangle;
import com.sapienza.cs.sapienzaaddface.Adapters.FaceGridViewAdapter;
import com.sapienza.cs.sapienzaaddface.Helpers.AddFaceHelper;
import com.sapienza.cs.sapienzaaddface.Helpers.CreatePersonHelper;
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
    public static final int REQUEST_PICK_IMAGE = 0;
    private Uri photoURI;
    private FaceGridViewAdapter faceGridViewAdapter;
    private InputStream imageInputStream;
    private int mPosition;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addface);


        GridView gridView = findViewById(R.id.gridview_faces);
        gridView.setOnItemClickListener(new GridView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                mPosition = position;
                new CreatePerson(AddFaceActivity.this).execute("123", "Kadir");

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
            /*ByteArrayOutputStream stream = new ByteArrayOutputStream();
            Bitmap bitmap = ImageHelper.bitmapFromUri(getApplicationContext(), photoURI);
            bitmap = ImageHelper.fixImageOrientation(getApplicationContext(), photoURI, bitmap);
            int quality = 100;
            if (bitmap.getByteCount() > 10000000) {
                quality = 50;
            }
            bitmap.compress(Bitmap.CompressFormat.PNG, quality, stream);
            //imageInputStream = new ByteArrayInputStream(stream.toByteArray());*/
            imageInputStream = getContentResolver().openInputStream(photoURI);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
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
                CharSequence text = "No faces were found in the photo!";
                int duration = Toast.LENGTH_SHORT;
                Toast.makeText(context, text, duration).show();
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
            if (personId != null) {
                try {
                    new AddFace(AddFaceActivity.this).execute("123", personId, getContentResolver().openInputStream(photoURI), faceRect);
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
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result != false) {
                Intent intent = new Intent(AddFaceActivity.this, ViewPersonGroupActivity.class);
                startActivity(intent);
            }
        }
    }



}
