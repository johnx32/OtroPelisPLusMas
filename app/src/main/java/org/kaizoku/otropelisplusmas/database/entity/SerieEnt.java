package org.kaizoku.otropelisplusmas.database.entity;

import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;

import org.jetbrains.annotations.NotNull;
import org.kaizoku.otropelisplusmas.model.Season;

import java.util.List;

@Entity(tableName = "serie",indices =  {@Index(value = {"href"},unique = true)})
public class SerieEnt extends MediaEnt {
    public int capProgres;
    @Ignore
    public List<Season> seasonList;
    public SerieEnt() {
    }
    public SerieEnt(MediaEnt media) {
        super(media);
    }

    @NonNull
    @NotNull
    @Override
    public String toString() {
        return super.toString()+"\n capProgres: "+capProgres;
    }

}
