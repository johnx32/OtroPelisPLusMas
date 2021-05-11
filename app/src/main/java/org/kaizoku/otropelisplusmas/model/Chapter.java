package org.kaizoku.otropelisplusmas.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Chapter implements Parcelable {
    public String href;
    public String title;
    public Chapter(String href, String title) {
        this.href = href;
        this.title = title;
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
