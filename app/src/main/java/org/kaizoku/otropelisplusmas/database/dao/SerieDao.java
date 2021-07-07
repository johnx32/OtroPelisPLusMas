package org.kaizoku.otropelisplusmas.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import org.kaizoku.otropelisplusmas.database.entity.CapituloEnt;
import org.kaizoku.otropelisplusmas.database.entity.SerieEnt;

import java.util.List;

import io.reactivex.Single;

@Dao
public interface SerieDao {
    //CREATE
    @Insert
    Single<Long> insertSerie(SerieEnt serie);
    @Insert
    Single<Long[]> insertSeries(List<SerieEnt> series);//long que es el nuevo rowId para el elemento insertado. Si el parámetro es un arreglo o una colección, debería mostrar long[] o List<Long>.

    //READ
    @Query("select * from serie where id=:id;")
    Single<SerieEnt> getSerie(long id);
    @Query("select * from serie where hrefs=:href;")
    Single<SerieEnt> getSerie(String href);
    @Query("select * from serie;")
    Single<List<SerieEnt>> getSeries();

    //UPDATE
    @Update
    Single<Integer> updateSerie(SerieEnt serie);//int que indica la cantidad de filas actualizadas en la base de datos.

    //DELETE
    @Delete
    Single<Integer> deleteSerie(SerieEnt serie);//int que indica la cantidad de filas que se quitaron de la base de datos.
}
