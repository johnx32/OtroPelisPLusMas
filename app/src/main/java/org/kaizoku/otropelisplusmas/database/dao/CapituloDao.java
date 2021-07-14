package org.kaizoku.otropelisplusmas.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import org.kaizoku.otropelisplusmas.database.entity.CapituloEnt;

import java.util.List;

import io.reactivex.Single;
@Dao
public interface CapituloDao {
    //CREATE
    @Insert
    Single<Long> insertCapitulos(CapituloEnt capitulo);
    @Insert
    Single<Long[]> insertCapitulos(List<CapituloEnt> capitulos);//long que es el nuevo rowId para el elemento insertado. Si el parámetro es un arreglo o una colección, debería mostrar long[] o List<Long>.

    //READ
    @Query("select * from capitulo where id=:id")
    Single<CapituloEnt> getCapitulo(long id);
    @Query("select * from capitulo where href=:href")
    Single<CapituloEnt> getCapitulo(String href);
    @Query("select * from capitulo;")
    Single<List<CapituloEnt>> getCapitulos();

    //UPDATE
    @Update
    Single<Integer> updateCapitulo(CapituloEnt capitulo);//int que indica la cantidad de filas actualizadas en la base de datos.

    //DELETE
    @Delete
    Single<Integer> deleteCapitulo(CapituloEnt capitulo);//int que indica la cantidad de filas que se quitaron de la base de datos.
}
