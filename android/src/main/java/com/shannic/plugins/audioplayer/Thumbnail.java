package com.shannic.plugins.audioplayer;

import android.os.Parcel;
import android.os.Parcelable;

public class Thumbnail implements Parcelable {
    private String url;
    private int height;
    private int width;

    public Thumbnail() {}

    public Thumbnail(String url, int height, int width) {
        this.url = url;
        this.height = height;
        this.width = width;
    }

    protected Thumbnail(Parcel in) {
        url = in.readString();
        height = in.readInt();
        width = in.readInt();
    }

    public static final Creator<Thumbnail> CREATOR = new Creator<Thumbnail>() {
        @Override
        public Thumbnail createFromParcel(Parcel in) {
            return new Thumbnail(in);
        }

        @Override
        public Thumbnail[] newArray(int size) {
            return new Thumbnail[size];
        }
    };

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
        dest.writeInt(height);
        dest.writeInt(width);
    }
}
