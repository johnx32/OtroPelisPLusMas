package org.kaizoku.otropelisplusmas.model;

public class Cartel {
    public String src_img;
    public String name;
    public String sinopsis;
    public String rating;
    public String url_disqus;

    public Cartel(String src_img, String name, String sinopsis, String rating) {
        this.src_img = src_img;
        this.name = name;
        this.sinopsis = sinopsis;
        this.rating = rating;
    }
    public Cartel(String src_img, String name, String sinopsis, String rating, String url_disqus) {
        this.src_img = src_img;
        this.name = name;
        this.sinopsis = sinopsis;
        this.rating = rating;
        this.url_disqus = url_disqus;
    }
}
