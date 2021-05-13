package org.kaizoku.otropelisplusmas.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Chapter implements Parcelable {
    public static final byte TYPE_BANNER_ADAPTATIVE = 1;
    public static final byte TYPE_CHAPTER = 2;
    //private static final byte TYPE_VIDEO = 3;
    public byte type=0;
    public String href;
    public String title;
    public Chapter(String href, String title,byte type) {
        this.href = href;
        this.title = title;
        this.type = type;
    }

    protected Chapter(Parcel in) {
        href = in.readString();
        title = in.readString();
    }

    public static final Creator<Chapter> CREATOR = new Creator<Chapter>() {
        @Override
        public Chapter createFromParcel(Parcel in) {
            return new Chapter(in);
        }

        @Override
        public Chapter[] newArray(int size) {
            return new Chapter[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(href);
        dest.writeString(title);
    }
}
