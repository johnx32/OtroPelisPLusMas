package org.kaizoku.otropelisplusmas.database.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import org.jetbrains.annotations.NotNull;
import org.kaizoku.otropelisplusmas.database.OPelisplusRoom;
import org.kaizoku.otropelisplusmas.database.dao.FavoritoDao;
import org.kaizoku.otropelisplusmas.database.entity.SerieEnt;

import java.util.List;

import io.reactivex.Single;

public class FavoritosViewModel extends AndroidViewModel {
    private FavoritoDao favoritoDao;
    public FavoritosViewModel(@NonNull @NotNull Application application) {
        super(application);
        favoritoDao = OPelisplusRoom.getInstance(application).favoritoDao();
    }

    public Single<List<SerieEnt>> getFavoritosSerie(){
        return favoritoDao.getFavoritosSerie();
    }
}
