package org.kaizoku.otropelisplusmas.database.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.PrimaryKey;

import java.util.Date;

/**
 * Representa una serie, capitulo, anime, dorama, pelicula, card
 */
public class MediaEnt implements Parcelable {
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
        this.created_at=new Date();
        this.updated_at=new Date();
    }

    public MediaEnt(MediaEnt media){
        this.id=media.id;
            this.href=media.href;
            this.titulo=media.href;
            this.src_img=media.src_img;
        this.sinopsis=media.sinopsis;
        this.url_disqus=media.url_disqus;
        this.favorito_id=media.favorito_id;
        this.created_at=media.created_at;
        this.updated_at=media.updated_at;
            this.rating=media.rating;
    }
    public MediaEnt(String titulo,String rating,String href, String src_img) {
        this.titulo = titulo;
        this.rating=rating;
        this.href=href;
        this.src_img=src_img;
        this.created_at=new Date();
        this.updated_at=new Date();
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


    protected MediaEnt(Parcel in) {
        id = in.readLong();
        href = in.readString();
        titulo = in.readString();
        src_img = in.readString();
        sinopsis = in.readString();
        url_disqus = in.readString();
        rating = in.readString();
        favorito_id = in.readLong();
        created_at.setTime( in.readLong() );
        updated_at.setTime( in.readLong() );
    }

    public static final Creator<MediaEnt> CREATOR = new Creator<MediaEnt>() {
        @Override
        public MediaEnt createFromParcel(Parcel in) {
            return new MediaEnt(in);
        }

        @Override
        public MediaEnt[] newArray(int size) {
            return new MediaEnt[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(href);
        dest.writeString(titulo);
        dest.writeString(src_img);
        dest.writeString(sinopsis);
        dest.writeString(url_disqus);
        dest.writeString(rating);
        dest.writeLong(favorito_id);
        dest.writeLong(created_at.getTime());
        dest.writeLong(updated_at.getTime());
    }

    @NonNull
    @Override
    public String toString() {
        return "id: "+id+"\n href: "+href+"\n titulo: "+titulo+"\n src_img: "+src_img+"\n favorito_id: "+favorito_id;
    }
}
