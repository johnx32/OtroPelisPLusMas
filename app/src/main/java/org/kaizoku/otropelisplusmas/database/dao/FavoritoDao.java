package org.kaizoku.otropelisplusmas.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import org.kaizoku.otropelisplusmas.database.entity.CapituloEnt;
import org.kaizoku.otropelisplusmas.database.entity.FavoritoEnt;
import org.kaizoku.otropelisplusmas.database.entity.SerieEnt;

import java.util.List;

import io.reactivex.Single;

@Dao
public interface FavoritoDao {
    //CREATE
    @Insert
    Single<Long> insertFavorito(FavoritoEnt favorito);
    @Insert
    Single<Long[]> insertFavoritos(List<FavoritoEnt> favoritos);//long que es el nuevo rowId para el elemento insertado. Si el parámetro es un arreglo o una colección, debería mostrar long[] o List<Long>.

    //READ
    @Query("select * from favorito where id=:id")
    Single<FavoritoEnt> getFavorito(long id);
    @Query("select * from serie where serie.favorito_id=1;")
    Single<List<SerieEnt>> getFavoritosSerie();

    //UPDATE
    @Update
    Single<Integer> updateFavorito(FavoritoEnt favorito);//int que indica la cantidad de filas actualizadas en la base de datos.

    //DELETE
    @Delete
    Single<Integer> deleteFavorito(FavoritoEnt favorito);//int que indica la cantidad de filas que se quitaron de la base de datos.
}
