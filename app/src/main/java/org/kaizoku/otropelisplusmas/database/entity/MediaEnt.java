package org.kaizoku.otropelisplusmas.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

/**
 * Representa una serie, capitulo, anime, dorama, pelicula, card
 */
public class MediaEnt {
    public static final byte TYPE_PELICULA = 1;
    public static final byte TYPE_SERIE = 2;
    public static final byte TYPE_ANIME = 3;

    @PrimaryKey(autoGenerate = true)
    public long id;
    public String href;
    public String titulo;
    public String src_img;
    public String sinopsis;
    public String url_disqus;
    public String rating;
    public long favorito_id;

    public Date created_at;
    public Date updated_at;

    public MediaEnt() {
    }

    public MediaEnt(String titulo,String rating,String href, String src_img) {
        this.titulo = titulo;
        this.rating=rating;
        this.href=href;
        this.src_img=src_img;
    }
    public byte getType(String url){
        if(url.contains("/pelicula"))return MediaEnt.TYPE_PELICULA;
        if(url.contains("/serie"))return MediaEnt.TYPE_SERIE;
        if(url.contains("/anime"))return MediaEnt.TYPE_ANIME;
        return 0;
    }
    public byte getTypeHref(){
        if(href.contains("/pelicula"))return MediaEnt.TYPE_PELICULA;
        if(href.contains("/serie"))return MediaEnt.TYPE_SERIE;
        if(href.contains("/anime"))return MediaEnt.TYPE_ANIME;
        return 0;
    }
}
