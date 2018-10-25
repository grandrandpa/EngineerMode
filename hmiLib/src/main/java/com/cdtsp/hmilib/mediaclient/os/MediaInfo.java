package com.cdtsp.hmilib.mediaclient.os;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.media.MediaBrowserCompat;

public class MediaInfo implements Parcelable {
    private String mMediaId;
    private String mTitle;

    public MediaInfo(MediaBrowserCompat.MediaItem mediaItem) {
        if (mediaItem != null) {
            this.mMediaId = mediaItem.getMediaId();
        } else {
            throw new IllegalArgumentException("mediaItem is " + mediaItem);
        }
    }

    public MediaInfo(String mediaId, String title) {
        this.mMediaId = mediaId;
        this.mTitle = title;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{mMediaId=" + mMediaId + ", mTitle=" + mTitle + "}";
    }

    protected MediaInfo(Parcel in) {
        mMediaId = in.readStringNoHelper();
        mTitle = in.readStringNoHelper();
    }

    public static final Creator<MediaInfo> CREATOR = new Creator<MediaInfo>() {
        @Override
        public MediaInfo createFromParcel(Parcel in) {
            return new MediaInfo(in);
        }

        @Override
        public MediaInfo[] newArray(int size) {
            return new MediaInfo[size];
        }
    };

    public String getMediaId() {
        return mMediaId;
    }

    public String getTitle() {
        return mTitle;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringNoHelper(mMediaId);
        dest.writeStringNoHelper(mTitle);
    }
}
