package org.kaizoku.otropelisplusmas.database.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import org.jetbrains.annotations.NotNull;
import org.kaizoku.otropelisplusmas.database.OPelisplusRoom;
import org.kaizoku.otropelisplusmas.database.dao.CapituloDao;
import org.kaizoku.otropelisplusmas.database.dao.SerieDao;
import org.kaizoku.otropelisplusmas.database.entity.CapituloEnt;
import org.kaizoku.otropelisplusmas.database.entity.SerieEnt;

import java.util.List;

import io.reactivex.Single;

public class CapituloViewModel extends AndroidViewModel {
    private CapituloDao capituloDao;
    public CapituloViewModel(@NonNull @NotNull Application application) {
        super(application);
        capituloDao = OPelisplusRoom.getInstance(application).capituloDao();
    }

    public Single<Long> insertCapitolo(CapituloEnt capitulo){
        return capituloDao.insertCapitulos(capitulo);
    }
    public Single<CapituloEnt> getCapitulo(long id){
        return capituloDao.getCapitulo(id);
    }
    public Single<CapituloEnt> getCapitulo(String href){
        return capituloDao.getCapitulo(href);
    }
    public Single<List<CapituloEnt>> getCapitulos(){
        return capituloDao.getCapitulos();
    }

    public Single<Integer> updateCapitulo(CapituloEnt capitulo){
        return capituloDao.updateCapitulo(capitulo);
    }
}
