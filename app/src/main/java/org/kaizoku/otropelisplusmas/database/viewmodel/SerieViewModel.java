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

public class SerieViewModel extends AndroidViewModel {
    private SerieDao serieDao;
    public SerieViewModel(@NonNull @NotNull Application application) {
        super(application);
        serieDao = OPelisplusRoom.getInstance(application).serieDao();
    }

    public Single<Long> insertSerie(SerieEnt serie){
        return serieDao.insertSerie(serie);
    }

    public Single<List<SerieEnt>> getSeries(){
        return serieDao.getSeries();
    }
}
