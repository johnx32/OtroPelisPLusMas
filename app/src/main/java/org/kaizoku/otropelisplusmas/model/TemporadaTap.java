package org.kaizoku.otropelisplusmas.model;

import java.util.List;

public class TemporadaTap {
    public String name;
    public List<CapituloCard> capitulos;

    public TemporadaTap(String name, List<CapituloCard> capitulos) {
        this.name = name;
        this.capitulos = capitulos;
    }
}
