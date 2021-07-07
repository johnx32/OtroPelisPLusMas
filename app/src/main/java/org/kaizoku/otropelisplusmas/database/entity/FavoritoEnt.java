package org.kaizoku.otropelisplusmas.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "favorito")
public class FavoritoEnt {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public String name;
    public Date created_at;
    public Date updated_at;

    public FavoritoEnt() {
    }
}
