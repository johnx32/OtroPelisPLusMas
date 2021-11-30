package org.kaizoku.otropelisplusmas.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import org.kaizoku.otropelisplusmas.database.dao.CapituloDao;
import org.kaizoku.otropelisplusmas.database.dao.FavoritoDao;
import org.kaizoku.otropelisplusmas.database.dao.SerieDao;
import org.kaizoku.otropelisplusmas.database.entity.CapituloEnt;
import org.kaizoku.otropelisplusmas.database.entity.FavoritoEnt;
import org.kaizoku.otropelisplusmas.database.entity.SerieEnt;
import org.kaizoku.otropelisplusmas.database.typeconverters.DateLongConverter;

@Database(entities = {CapituloEnt.class, SerieEnt.class, FavoritoEnt.class},version = 1,exportSchema = false)
@TypeConverters({DateLongConverter.class})
public abstract class OPelisplusRoom extends RoomDatabase {
    public static volatile OPelisplusRoom INSTANCE;
    public static OPelisplusRoom getInstance(Context contexto){
        if(INSTANCE==null){
            synchronized (OPelisplusRoom.class){
                if(INSTANCE==null){
                    INSTANCE = Room.databaseBuilder(contexto.getApplicationContext(),
                            OPelisplusRoom.class,"OPeliplus.db")
                            //.allowMainThreadQueries()
                            .addCallback(new Callback() {
                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    super.onCreate(db);
                                    db.execSQL("insert into favorito(name) values ('favoritos');");
                                }
                            })
                            //.enableMultiInstanceInvalidation()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
    public abstract CapituloDao capituloDao();
    public abstract SerieDao serieDao();
    public abstract FavoritoDao favoritoDao();
}
