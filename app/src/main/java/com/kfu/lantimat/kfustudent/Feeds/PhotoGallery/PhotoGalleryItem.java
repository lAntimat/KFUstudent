package com.kfu.lantimat.kfustudent.Feeds.PhotoGallery;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Ильназ on 06.11.2015.
 */
public class PhotoGalleryItem implements Parcelable {

        public String photoUrl;
        public String photoBigSizeUrl;

        public PhotoGalleryItem(String _photoUrl, String _photoBigSizeUrl) {
            photoUrl = _photoUrl;
            photoBigSizeUrl = _photoBigSizeUrl;
        }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getPhotoBigSizeUrl() {
        return photoBigSizeUrl;
    }

    public void setPhotoBigSizeUrl(String photoBigSizeUrl) {
        this.photoBigSizeUrl = photoBigSizeUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.photoUrl);
        dest.writeString(this.photoBigSizeUrl);
    }

    protected PhotoGalleryItem(Parcel in) {
        this.photoUrl = in.readString();
        this.photoBigSizeUrl = in.readString();
    }

    public static final Creator<PhotoGalleryItem> CREATOR = new Creator<PhotoGalleryItem>() {
        @Override
        public PhotoGalleryItem createFromParcel(Parcel source) {
            return new PhotoGalleryItem(source);
        }

        @Override
        public PhotoGalleryItem[] newArray(int size) {
            return new PhotoGalleryItem[size];
        }
    };
}
