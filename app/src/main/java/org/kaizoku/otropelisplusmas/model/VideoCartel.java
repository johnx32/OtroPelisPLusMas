package org.kaizoku.otropelisplusmas.model;

import org.kaizoku.otropelisplusmas.model.video_server.VideoServer;

import java.util.List;

public class VideoCartel extends Cartel{
    public List<VideoServer> videoServerList;

    public VideoCartel(Cartel cartel, List<VideoServer> videoServerList) {
        super(cartel.src_img, cartel.name, cartel.sinopsis, cartel.rating);
        this.videoServerList = videoServerList;
    }
}
