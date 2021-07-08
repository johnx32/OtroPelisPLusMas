package org.kaizoku.otropelisplusmas.database.entity;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "capitulo",indices =  {@Index(value = {"href"},unique = true)})
public class CapituloEnt extends MediaEnt{
    public String href_serie;
    public long progres;

    public CapituloEnt() {
    }
}
