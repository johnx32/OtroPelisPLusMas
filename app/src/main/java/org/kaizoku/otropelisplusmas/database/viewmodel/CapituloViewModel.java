package org.kaizoku.otropelisplusmas.database.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import org.kaizoku.otropelisplusmas.database.OPelisplusRoom;
import org.kaizoku.otropelisplusmas.database.dao.CapituloDao;
import org.kaizoku.otropelisplusmas.database.entity.CapituloEnt;

import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class CapituloViewModel extends AndroidViewModel {
    private CapituloDao capituloDao;
    public CapituloViewModel(@NonNull Application application) {
        super(application);
        capituloDao = OPelisplusRoom.getInstance(application).capituloDao();
    }

    public Single<Long> insertCapitolo(CapituloEnt capitulo){
        return capituloDao.insertCapitulos(capitulo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    public Single<CapituloEnt> getCapitulo(long id){
        return capituloDao.getCapitulo(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    public Single<CapituloEnt> getCapitulo(String href){
        return capituloDao.getCapitulo(href)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    public Single<List<CapituloEnt>> getCapitulos(){
        return capituloDao.getCapitulos();
    }

    public Single<Integer> updateCapitulo(CapituloEnt capitulo){
        return capituloDao.updateCapitulo(capitulo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
