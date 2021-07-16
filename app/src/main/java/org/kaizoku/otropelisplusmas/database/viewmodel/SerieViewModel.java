package org.kaizoku.otropelisplusmas.database.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import org.jetbrains.annotations.NotNull;
import org.kaizoku.otropelisplusmas.database.OPelisplusRoom;
import org.kaizoku.otropelisplusmas.database.dao.SerieDao;
import org.kaizoku.otropelisplusmas.database.entity.SerieEnt;

import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class SerieViewModel extends AndroidViewModel {
    private SerieDao serieDao;
    public SerieViewModel(@NonNull @NotNull Application application) {
        super(application);
        serieDao = OPelisplusRoom.getInstance(application).serieDao();
    }

    public Single<Long> insertSerie(SerieEnt serie){
        return serieDao.insertSerie(serie)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<SerieEnt>> getSeries(){
        return serieDao.getSeries();
    }

    public Single<SerieEnt> getSerie(String href) {
        return serieDao.getSerie(href)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Integer> updateSerie(SerieEnt serieEnt) {
        return serieDao.updateSerie(serieEnt)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
