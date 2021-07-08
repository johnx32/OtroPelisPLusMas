package org.kaizoku.otropelisplusmas.model;

import org.kaizoku.otropelisplusmas.database.entity.MediaEnt;

import java.util.List;

public class FullPage {
    public String url;
    public List<MediaEnt> listCard;
    public List<ItemPage> paginacion;

    public FullPage(String url, List<MediaEnt> listCard, List<ItemPage> paginacion) {
        this.url = url;
        this.listCard = listCard;
        this.paginacion = paginacion;
    }
}
