package org.kaizoku.otropelisplusmas.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "serie",indices =  {@Index(value = {"href"},unique = true)})
public class SerieEnt extends MediaEnt{
    public int capProgres;

    public SerieEnt() {
    }
}
