package org.kaizoku.otropelisplusmas.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "capitulo")
public class CapituloEnt {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public String titulo;
    public String src_img;
    public String url_disqus;
    //obtener de la serie cartel o no
    public String sinopsis;
    public String rating;
    public String hrefc;
    public String hrefs;
    public long progres;

    public Date created_at;
    public Date updated_at;

    public CapituloEnt() {
    }
}
