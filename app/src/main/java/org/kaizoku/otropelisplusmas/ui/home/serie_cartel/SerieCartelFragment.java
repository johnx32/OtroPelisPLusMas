package org.kaizoku.otropelisplusmas.ui.home.serie_cartel;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.transition.ChangeBounds;

import com.google.android.material.tabs.TabLayoutMediator;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.kaizoku.otropelisplusmas.MainActivity;
import org.kaizoku.otropelisplusmas.R;
import org.kaizoku.otropelisplusmas.database.OPelisplusRoom;
import org.kaizoku.otropelisplusmas.database.entity.FavoritoEnt;
import org.kaizoku.otropelisplusmas.database.entity.SerieEnt;
import org.kaizoku.otropelisplusmas.databinding.AppBarMainBinding;
import org.kaizoku.otropelisplusmas.databinding.FragmentSerieCartelBinding;
import org.kaizoku.otropelisplusmas.model.SerieCartel;
import org.kaizoku.otropelisplusmas.service.PelisplushdService;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import io.reactivex.functions.Function;
import io.reactivex.internal.subscribers.BlockingBaseSubscriber;
import io.reactivex.schedulers.Schedulers;

public class SerieCartelFragment extends Fragment {
    private static final String TAG = "bf5t1";
    private FragmentSerieCartelBinding binding;
    private PelisplushdService pelisplushdService;
    private TabSeasonStateAdapter tabSeasonStateAdapter;
    private SerieEnt serie;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pelisplushdService = new PelisplushdService();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSerieCartelBinding.inflate(inflater,container,false);

        tabSeasonStateAdapter = new TabSeasonStateAdapter(getChildFragmentManager(),getLifecycle());
        binding.fragCartelViewpagerVp2.setAdapter(tabSeasonStateAdapter);

        //setSharedElementEnterTransition(new ChangeBounds());

        Bundle b = getArguments();
        if(b!=null) {
            String url = b.getString("url", "");

            pelisplushdService.getSingleSerieCartel(url)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe((serieCartel, throwable) -> {
                        if (throwable == null && serieCartel != null) {
                            setSerieCartel(serieCartel);
                            getSerie(serieCartel);
                            tabSeasonStateAdapter.setTabSeasonList(serieCartel.seasonList);
                            new TabLayoutMediator(binding.fragCartelViewpagerTabs, binding.fragCartelViewpagerVp2,
                                    (tab, position) -> {
                                        tab.setText(serieCartel.seasonList.get(position).seasonTitle);
                                    }
                            ).attach();
                        }
                    });
        }

        return binding.getRoot();
    }

    private void getSerie(SerieCartel serieCartel) {
        Log.i(TAG, "getSerie: ");
        OPelisplusRoom.getInstance(getContext()).serieDao().getSerie(serieCartel.hrefs)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((serieEnt, throwable) -> {
                    Log.i(TAG, "getSerie: serieDao().getSerie");
                    if(throwable==null){
                        Log.i(TAG, "getSerie: thr nulo");
                        this.serie = serieEnt;
                        setFloatingactionbuttonCallback();
                    }else{
                        Log.i(TAG, "getSerie: thr error");
                        SerieEnt serieNew = new SerieEnt();
                        serieNew.hrefs=serieCartel.hrefs;
                        serieNew.rating=serieCartel.rating;
                        serieNew.sinopsis=serieCartel.sinopsis;
                        serieNew.titulo=serieCartel.name;
                        serieNew.src_img=serieCartel.src_img;
                        serieNew.url_disqus=serieCartel.url_disqus;
                        OPelisplusRoom.getInstance(getContext()).serieDao().insertSerie(serieNew)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe((aLong, throwable1) -> {
                                    if (throwable1==null){
                                        Log.i(TAG, "getSerie: thr1 null");
                                        OPelisplusRoom.getInstance(getContext()).serieDao().getSerie(aLong)
                                                .subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe((serieEnt2, throwable2) -> {
                                                    if(throwable2==null){
                                                        Log.i(TAG, "getSerie: thr2 nulo");
                                                        this.serie = serieEnt2;
                                                        setFloatingactionbuttonCallback();
                                                    }else Log.e(TAG, "getSerie: msg thr2: "+throwable2.getMessage(), throwable2);
                                                });
                                    }else Log.e(TAG, "getSerie: msg thr1: "+throwable1.getMessage(), throwable1);
                                });
                    }
                });
    }

    private void setFloatingactionbuttonCallback() {
        Log.i(TAG, "setFloatingactionbuttonCallback: fablistener inicializadondo");
        if(serie.favorito_id>0)
            ((MainActivity)getActivity()).setFabImage(R.drawable.ic_favorite_white_24dp);
        else
            ((MainActivity)getActivity()).setFabImage(R.drawable.ic_favorite_border_white_24dp);
        ((MainActivity)getActivity()).setOnFabListener( (fab) -> {
            Log.i(TAG, "setFloatingactionbuttonCallback: ");
            long favorito_bk = serie.favorito_id;
            if(serie.favorito_id>0)
                serie.favorito_id = 0;
            else
                serie.favorito_id = 1;
            OPelisplusRoom.getInstance(getContext()).serieDao().updateSerie(serie)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe((integer, throwable) -> {
                        if(throwable==null){
                            if(serie.favorito_id>0){
                                fab.setImageResource(R.drawable.ic_favorite_white_24dp);
                            }else{
                                fab.setImageResource(R.drawable.ic_favorite_border_white_24dp);
                            }
                        }else{
                            serie.favorito_id=favorito_bk;
                            Log.e(TAG, "setFloatingactionbuttonCallback: error: "+throwable.getMessage(), throwable);
                        }
                    });
        });
    }

    private void setSerieCartel(SerieCartel serieCartel){
        ((MainActivity)getActivity()).setDisplayShowTitleEnabled(true);
        //((MainActivity)getActivity()).setTitleToolbar(serieCartel.name);
        getActivity().setTitle(serieCartel.name);
        binding.fragCartelSinopsis.setText(serieCartel.sinopsis);
        try {
            ((MainActivity)getActivity()).loadImgToolbar(serieCartel.src_img);
        }catch (Exception e){e.printStackTrace();}
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((MainActivity)getActivity()).setDisplayShowTitleEnabled(false);
        ((MainActivity)getActivity()).setFabImage(R.drawable.ic_favorite_border_white_24dp);
    }
}
