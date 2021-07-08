package org.kaizoku.otropelisplusmas.model;

import org.kaizoku.otropelisplusmas.database.entity.MediaEnt;

import java.util.List;

public class FullPage {
    public List<MediaEnt> listCard;
    public List<ItemPage> paginacion;

    public FullPage(List<MediaEnt> listCard, List<ItemPage> paginacion) {
        this.listCard = listCard;
        this.paginacion = paginacion;
    }
}
