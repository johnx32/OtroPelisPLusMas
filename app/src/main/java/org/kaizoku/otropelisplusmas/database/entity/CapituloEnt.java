package org.kaizoku.otropelisplusmas.database.entity;

import android.os.Parcel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;

import org.kaizoku.otropelisplusmas.model.video_server.VideoServer;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "capitulo",indices =  {@Index(value = {"href"},unique = true)})
public class CapituloEnt extends MediaEnt {
    public String href_serie;
    public boolean visto=false;
    public long progress;
    public long id_serie;
    @Ignore
    public String file_url;
    @Ignore
    public List<VideoServer> videoServerList = new ArrayList<>();

    public CapituloEnt() {
    }

    @Override
    public boolean equals(@Nullable Object capitulo) {
        if(capitulo instanceof CapituloEnt){
            CapituloEnt c = (CapituloEnt) capitulo;
            if(c.href==href && c.titulo==titulo && c.href_serie==href_serie && c.src_img==src_img && c.sinopsis==sinopsis &&
                c.rating==rating && c.url_disqus==url_disqus)
                return true;
            else return false;
        }else return false;
    }

    public void updateFrom(CapituloEnt capitulo) {
        this.href = capitulo.href;
        this.titulo = capitulo.titulo;
        this.href_serie = capitulo.href_serie;
        this.src_img = capitulo.src_img;
        this.sinopsis = capitulo.sinopsis;
        this.rating = capitulo.rating;
        this.url_disqus = capitulo.url_disqus;
    }



    protected CapituloEnt(Parcel in) {
        super(in);
        href_serie = in.readString();
            byte b = in.readByte();
        visto=b==0?false:true;
        progress = in.readLong();
        id_serie = in.readLong();
        videoServerList = in.readArrayList(VideoServer.class.getClassLoader());
    }

    public static final Creator<CapituloEnt> CREATOR = new Creator<CapituloEnt>() {
        @Override
        public CapituloEnt createFromParcel(Parcel in) {
            return new CapituloEnt(in);
        }

        @Override
        public CapituloEnt[] newArray(int size) {
            return new CapituloEnt[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(href_serie);
        byte b = (byte) (visto?1:0);
        dest.writeByte(b);
        dest.writeLong(progress);
        dest.writeLong(id_serie);
        dest.writeList(videoServerList);
    }

    @NonNull
    @Override
    public String toString() {
        return "\nhref_serie: "+href_serie+"\n visto: "+visto+"\nprogress: "+progress+"\n"+visto+"\n server-list size: "+
                videoServerList.size()+"\n file_url: "+file_url+"\n"+super.toString();
    }
}
