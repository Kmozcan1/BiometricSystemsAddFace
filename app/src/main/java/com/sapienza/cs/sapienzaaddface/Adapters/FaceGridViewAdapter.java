package com.sapienza.cs.sapienzaaddface.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.microsoft.projectoxford.face.contract.Face;
import com.microsoft.projectoxford.face.contract.FaceRectangle;
import com.sapienza.cs.sapienzaaddface.Helpers.ImageHelper;
import com.sapienza.cs.sapienzaaddface.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class FaceGridViewAdapter extends BaseAdapter {
    List<UUID> faceIdList;
    List<FaceRectangle> faceRectList;
    List<Bitmap> faceThumbnails;
    List<Boolean> faceChecked;

    private final Context context;


    public FaceGridViewAdapter(Context context, Face[] detectionResult, Uri faceUri) throws IOException {

        faceIdList = new ArrayList<>();
        faceRectList = new ArrayList<>();
        faceThumbnails = new ArrayList<>();
        faceChecked = new ArrayList<>();
        this.context = context;
        Bitmap bitmap = ImageHelper.bitmapFromUri(context, faceUri);
        bitmap = fixImageOrientation(faceUri, bitmap);
        //bitmap = ImageHelper.rotateBitmap(bitmap, ImageHelper.getImageRotationAngle(faceUri, context.getContentResolver()));
        if (detectionResult != null) {
            List<Face> faces = Arrays.asList(detectionResult);
            for (Face face : faces) {
                try {
                    // Crop face thumbnail with five main landmarks drawn from original image.
                    faceThumbnails.add(ImageHelper.generateFaceThumbnail(
                            bitmap, face.faceRectangle));

                    faceIdList.add(null);
                    faceRectList.add(face.faceRectangle);

                    faceChecked.add(false);
                } catch (IOException e) {

                }
            }
        }
    }

    @Override
    public int getCount() {
        return faceRectList.size();
    }

    @Override
    public Object getItem(int position) {
        return faceRectList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // set the item view
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView =
                    inflater.inflate(R.layout.item_face_with_checkbox, parent, false);
        }
        convertView.setId(position);

        ((ImageView)convertView.findViewById(R.id.image_face))
                .setImageBitmap(faceThumbnails.get(position));

        // set the checked status of the item
        CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.checkbox_face);
        checkBox.setChecked(faceChecked.get(position));
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                faceChecked.set(position, isChecked);
            }
        });

        return convertView;
    }

    public Bitmap fixImageOrientation(Uri faceUri, Bitmap bitmap) throws IOException {
        InputStream imageInputStream = context.getContentResolver().openInputStream(faceUri);
        ExifInterface ei = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            ei = new ExifInterface(imageInputStream);
        }
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);

        Bitmap rotatedBitmap = null;
        switch(orientation) {

            case ExifInterface.ORIENTATION_ROTATE_90:
                rotatedBitmap = rotateImage(bitmap, 90);
                break;

            case ExifInterface.ORIENTATION_ROTATE_180:
                rotatedBitmap = rotateImage(bitmap, 180);
                break;

            case ExifInterface.ORIENTATION_ROTATE_270:
                rotatedBitmap = rotateImage(bitmap, 270);
                break;

            case ExifInterface.ORIENTATION_NORMAL:
            default:
                rotatedBitmap = bitmap;
        }
        return rotatedBitmap;
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }
}