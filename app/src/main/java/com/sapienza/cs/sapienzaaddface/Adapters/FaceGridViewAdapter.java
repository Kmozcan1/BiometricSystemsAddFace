package com.sapienza.cs.sapienzaaddface.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.microsoft.projectoxford.face.contract.Face;
import com.microsoft.projectoxford.face.contract.FaceRectangle;
import com.sapienza.cs.sapienzaaddface.Enumerations.FaceGridViewMode;
import com.sapienza.cs.sapienzaaddface.Helpers.ImageHelper;
import com.sapienza.cs.sapienzaaddface.Objects.ImageObject;
import com.sapienza.cs.sapienzaaddface.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FaceGridViewAdapter extends BaseAdapter {
    public static List<FaceRectangle> faceRectList;
    public static List<Bitmap> faceThumbnails;
    private static List<ImageObject> faceList;
    private final Context context;
    private static FaceGridViewMode faceGridViewMode;


    public FaceGridViewAdapter(Context context, Face[] detectionResult, Uri faceUri) throws IOException {
        faceRectList = new ArrayList<>();
        faceThumbnails = new ArrayList<>();
        this.context = context;
        faceGridViewMode = FaceGridViewMode.Add;
        Bitmap bitmap = ImageHelper.bitmapFromUri(context, faceUri);
        bitmap = ImageHelper.fixImageOrientation(context, faceUri, bitmap);
        if (detectionResult != null) {
            List<Face> faces = Arrays.asList(detectionResult);
            for (Face face : faces) {
                faceThumbnails.add(ImageHelper.generateFaceThumbnail(
                        bitmap, face.faceRectangle));
                faceRectList.add(face.faceRectangle);

            }
        }
    }

    public FaceGridViewAdapter(Context context, List<ImageObject> faceList) {
        this.context = context;
        faceGridViewMode = FaceGridViewMode.View;
        this.faceList = faceList;
    }

    @Override
    public int getCount() {
        switch (faceGridViewMode) {
            case View:
                return faceList.size();
            case Add:
                return faceRectList.size();
            default:
                return faceRectList.size();
        }
    }

    @Override
    public Object getItem(int position) {
        switch (faceGridViewMode) {
            case View:
                return faceList.get(position);
            case Add:
                return faceRectList.get(position);
            default:
                return faceRectList.get(position);
        }
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

        switch (faceGridViewMode) {
            case View:
                ((ImageView)convertView.findViewById(R.id.image_face)).setImageBitmap(faceList.get(position).getImage());
                ((TextView)convertView.findViewById(R.id.text_person)).setText(faceList.get(position).getPersonName());
                break;
            case Add:
                ((ImageView)convertView.findViewById(R.id.image_face)).setImageBitmap(faceThumbnails.get(position));
                break;
        }

        return convertView;
    }


}