package org.kaizoku.otropelisplusmas.model;

import org.kaizoku.otropelisplusmas.model.video_server.VideoServer;

import java.util.List;

public class CapituloCartel extends Cartel{
    public String hrefs;
    public String hrefc;
    public List<VideoServer> videoServerList;

    public CapituloCartel(Cartel cartel, List<VideoServer> videoServerList) {
        super(cartel.src_img, cartel.name, cartel.sinopsis, cartel.rating,cartel.url_disqus);
        this.videoServerList = videoServerList;
    }
}
