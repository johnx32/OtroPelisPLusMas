package org.kaizoku.otropelisplusmas.model;

import java.util.List;

public class SerieCartel extends Cartel{
    public String hrefs;
    public List<Season> seasonList;
    public SerieCartel(Cartel cartel, List<Season> seasonList) {
        super(cartel.src_img, cartel.name, cartel.sinopsis, cartel.rating);
        this.seasonList = seasonList;
    }
}
