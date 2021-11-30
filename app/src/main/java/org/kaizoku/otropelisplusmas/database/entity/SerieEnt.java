package org.kaizoku.otropelisplusmas.database.entity;

import android.os.Parcel;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;

import org.kaizoku.otropelisplusmas.model.Chapter;
import org.kaizoku.otropelisplusmas.model.Season;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "serie",indices =  {@Index(value = {"href"},unique = true)})
public class SerieEnt extends MediaEnt {
    public int capProgres;
    @Ignore
    public int seasonPos;
    @Ignore
    public int chapterPos;
    @Ignore
    public List<Season> seasonList=new ArrayList<>();

    public SerieEnt() {
    }
    public SerieEnt(MediaEnt media) {
        super(media);
    }
    public List<Chapter> getCurrentSeason(){
        return seasonList.get(seasonPos).chapterList;
    }
    public Chapter getCurrentSeasonChapter(){
        return seasonList.get(seasonPos).chapterList.get(chapterPos);
    }

    protected SerieEnt(Parcel in){
        super(in);
        capProgres = in.readInt();
        seasonPos = in.readInt();
        chapterPos = in.readInt();
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
        dest.writeInt(seasonPos);
        dest.writeInt(chapterPos);
        dest.writeList(seasonList);
    }

    @NonNull
    @Override
    public String toString() {
        return super.toString()+"\n capProgres: "+capProgres+"\n seasonList-size: "+seasonList.size();
    }

    public boolean isPlaylist() {// 0 es el 1er capitulo
        return  seasonList!=null && seasonPos>=0 && chapterPos>=0;
    }

    public void setChapterPos(String href, int chapterPos) {
        List<Chapter> s = getCurrentSeason();
        if(chapterPos<s.size()){
            for(int i=chapterPos;i>=0;i--)
                if (s.get(i).href.equals(href)){
                    this.chapterPos = i;
                    return;
                }
            int size=s.size();
            for (int i=chapterPos;i<size;i++)
                if (s.get(i).href.equals(href)){
                    this.chapterPos = i;
                    return;
                }
        }
    }
}
