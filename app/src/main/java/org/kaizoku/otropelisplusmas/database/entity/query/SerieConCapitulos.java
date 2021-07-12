package org.kaizoku.otropelisplusmas.database.entity.query;

import androidx.room.Embedded;
import androidx.room.Relation;

import org.kaizoku.otropelisplusmas.database.entity.CapituloEnt;
import org.kaizoku.otropelisplusmas.database.entity.SerieEnt;

import java.util.ArrayList;
import java.util.List;

public class SerieConCapitulos {
    @Embedded
    public SerieEnt serie;
    @Relation(
            parentColumn = "id",
            entityColumn = "id_serie"
    )
    public List<CapituloEnt> capitulos = new ArrayList<>();
}
