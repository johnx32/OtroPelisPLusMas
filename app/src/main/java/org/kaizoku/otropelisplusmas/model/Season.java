package org.kaizoku.otropelisplusmas.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Season implements Parcelable {
    public String seasonTitle;
    public List<Chapter> chapterList;
    public Season(String seasonTitle, List<Chapter> chapterList) {
        this.seasonTitle = seasonTitle;
        this.chapterList = chapterList;
    }

    protected Season(Parcel in) {
        seasonTitle = in.readString();
        chapterList = in.createTypedArrayList(Chapter.CREATOR);
    }

    public static final Creator<Season> CREATOR = new Creator<Season>() {
        @Override
        public Season createFromParcel(Parcel in) {
            return new Season(in);
        }

        @Override
        public Season[] newArray(int size) {
            return new Season[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(seasonTitle);
        dest.writeTypedList(chapterList);
    }
}
