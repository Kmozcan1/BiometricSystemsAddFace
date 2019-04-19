package com.sapienza.cs.sapienzaaddface.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.microsoft.projectoxford.face.contract.Face;
import com.microsoft.projectoxford.face.contract.FaceRectangle;
import com.sapienza.cs.sapienzaaddface.Helpers.ImageHelper;
import com.sapienza.cs.sapienzaaddface.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class FaceGridViewAdapter extends BaseAdapter {
    List<UUID> faceIdList;
    public static List<FaceRectangle> faceRectList;
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
        bitmap = ImageHelper.fixImageOrientation(context, faceUri, bitmap);
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
                    inflater.inflate(R.layout.item_face, parent, false);
        }
        convertView.setId(position);

        ((ImageView)convertView.findViewById(R.id.image_face))
                .setImageBitmap(faceThumbnails.get(position));

        return convertView;
    }


}