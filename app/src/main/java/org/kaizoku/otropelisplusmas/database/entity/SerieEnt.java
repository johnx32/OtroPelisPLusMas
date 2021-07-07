package org.kaizoku.otropelisplusmas.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "serie",indices =  {@Index(value = {"hrefs"},unique = true)})
public class SerieEnt {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public String titulo;
    public String src_img;
    public String sinopsis;
    public String rating;
    public String hrefs;
    public String url_disqus;
    public int capProgres;
    public long favorito_id;

    public Date created_at;
    public Date updated_at;

    public SerieEnt() {
    }
}
