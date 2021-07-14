package org.kaizoku.otropelisplusmas.database.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;

import org.jetbrains.annotations.NotNull;
import org.kaizoku.otropelisplusmas.model.Season;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "serie",indices =  {@Index(value = {"href"},unique = true)})
public class SerieEnt extends MediaEnt {
    public int capProgres;
    @Ignore
    public List<Season> seasonList=new ArrayList<>();

    public SerieEnt() {
    }
    public SerieEnt(MediaEnt media) {
        super(media);
    }


    protected SerieEnt(Parcel in){
        super(in);
        capProgres = in.readInt();
        seasonList = in.readArrayList(Season.class.getClassLoader());
    }

    public static final Creator<SerieEnt> CREATOR = new Creator<SerieEnt>() {
        @Override
        public SerieEnt createFromParcel(Parcel in) {
            return new SerieEnt(in);
        }

        @Override
        public SerieEnt[] newArray(int size) {
            return new SerieEnt[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(capProgres);
        dest.writeList(seasonList);
    }

    @NonNull
    @NotNull
    @Override
    public String toString() {
        return super.toString()+"\n capProgres: "+capProgres+"\n seasonList-size: "+seasonList.size();
    }

}
