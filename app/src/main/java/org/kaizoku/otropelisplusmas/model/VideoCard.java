package org.kaizoku.otropelisplusmas.model;

public class VideoCard {

    public String name;
    public String rating;
    public String url;
    public String src_img;
    public byte type;
    public VideoCard(String name, String rating, String url, String src_img, byte type) {
        this.name = name;
        this.rating = rating;
        this.url = url;
        this.src_img = src_img;
        this.type = type;
    }
}
