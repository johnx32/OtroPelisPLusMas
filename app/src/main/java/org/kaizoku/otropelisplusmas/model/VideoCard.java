package org.kaizoku.otropelisplusmas.model;

public class VideoCard {
    public static final byte TYPE_PELICULA = 1;
    public static final byte TYPE_SERIE = 2;
    public static final byte TYPE_ANIME = 3;
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
