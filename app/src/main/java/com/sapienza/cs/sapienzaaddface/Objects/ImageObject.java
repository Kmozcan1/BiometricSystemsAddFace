package com.sapienza.cs.sapienzaaddface.Objects;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.sapienza.cs.sapienzaaddface.Helpers.ImageHelper;

import java.util.UUID;

public class ImageObject {
    private UUID imageId;
    private String groupId;
    private Bitmap image;
    private String personName;

    public ImageObject(UUID imageId, String groupId, String imageString, String personName) {
        this.imageId = imageId;
        this.groupId = groupId;
        byte[] decodedString = Base64.decode(imageString, Base64.DEFAULT);
        this.image = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        this.personName = personName;
    }


    public ImageObject(String imageId, String groupId, String imageString, String personName) {
        this.imageId = UUID.fromString(imageId);
        this.groupId = groupId;
        byte[] decodedString = Base64.decode(imageString, Base64.DEFAULT);
        this.image = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        this.personName = personName;
    }

    public UUID getUid() {
        return imageId;
    }

    public String getImageString() {
        return ImageHelper.bitmapToString(image);
    }

    public Bitmap getImage() {
        return image;
    }

    public String getImageIdAsString() {
        return imageId.toString();
    }

    public String getGroupId() {
        return groupId;
    }

    public String getPersonName() {
        return personName;
    }

}
