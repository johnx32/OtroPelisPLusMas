package org.kaizoku.otropelisplusmas.ui.home.serie_cartel;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.tabs.TabLayoutMediator;

import org.jetbrains.annotations.NotNull;
import org.kaizoku.otropelisplusmas.MainActivity;
import org.kaizoku.otropelisplusmas.R;
import org.kaizoku.otropelisplusmas.database.OPelisplusRoom;
import org.kaizoku.otropelisplusmas.database.entity.MediaEnt;
import org.kaizoku.otropelisplusmas.database.entity.SerieEnt;
import org.kaizoku.otropelisplusmas.database.viewmodel.SerieViewModel;
import org.kaizoku.otropelisplusmas.databinding.FragmentSerieCartelBinding;
import org.kaizoku.otropelisplusmas.service.PelisplushdService;

import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class SerieCartelFragment extends Fragment {
    private static final String TAG = "bf5t1";
    private FragmentSerieCartelBinding binding;
    private PelisplushdService pelisplushdService;
    private SerieViewModel serieViewModel;
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
        serieViewModel = new ViewModelProvider(this).get(SerieViewModel.class);
        tabSeasonStateAdapter = new TabSeasonStateAdapter(getChildFragmentManager(),getLifecycle());
        //binding.fragCartelViewpagerVp2.setAdapter(tabSeasonStateAdapter);

        //setSharedElementEnterTransition(new ChangeBounds());
        loadArgumentos();

        return binding.getRoot();
    }

    private void loadArgumentos() {
        Bundle b = getArguments();
        if(b!=null) {
            MediaEnt media = b.getParcelable("media");
            //String url = b.getString("url", "");
            serieViewModel.getSerie(media.href)
                    .flatMap(new Function<SerieEnt, SingleSource<SerieEnt>>() {
                        @Override
                        public SingleSource<SerieEnt> apply(@NotNull SerieEnt serieEnt) throws Exception {
                            Log.i(TAG, "apply: 1 obtenido de la db");
                            serie=serieEnt;
                            loadSerieEnt(serie);
                            return pelisplushdService.getSingleSerieCartel(media.href)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread());
                        }
                    }).onErrorResumeNext(new Function<Throwable, SingleSource<SerieEnt>>() {
                        @Override
                        public SingleSource<SerieEnt> apply(@NotNull Throwable throwable) throws Exception {
                            Log.e(TAG, "apply: 2 onErrorResumeNext ", throwable);
                            return pelisplushdService.getSingleSerieCartel(media.href)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread());
                        }
                    }).flatMap(new Function<SerieEnt, SingleSource<Long>>() {
                        @Override
                        public SingleSource<Long> apply(@NotNull SerieEnt serieEnt) throws Exception {
                            Log.i(TAG, "apply: 3 serieEnt from web service");
                            loadSerieEnt(serieEnt);
                            if (serie == null){//from error - todo:if serie null insertar
                                serie = serieEnt;
                                return serieViewModel.insertSerie(serie);
                            }else {//from db -
                                serie.seasonList=serieEnt.seasonList;
                                return Single.just(-1l);
                            }
                        }
                    }).subscribe((id, throwable) -> {
                        Log.i(TAG, "loadArgumentos: 4 subscribe");
                        if(throwable==null){
                            if(id>0)
                                serie.id=id;
                            //tabSeasonStateAdapter.setTabSeasonList(serie.seasonList);
                            tabSeasonStateAdapter.setSerie(serie);
                            binding.fragCartelViewpagerVp2.setAdapter(tabSeasonStateAdapter);
                            new TabLayoutMediator(
                                    binding.fragCartelViewpagerTabs,
                                    binding.fragCartelViewpagerVp2,
                                (tab, position) -> {
                                    tab.setText(serie.seasonList.get(position).seasonTitle);
                                }
                            ).attach();
                        }else Log.e(TAG, "loadArgumentos: subscribe error", throwable);
                    });

            /*
            OPelisplusRoom.getInstance(getContext()).serieDao().getSerie(media.href)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .flatMap(new Function<SerieEnt, SingleSource<Long>>() {
                        @Override
                        public SingleSource<Long> apply(SerieEnt serieEnt)  {
                            Log.i(TAG, "loadArgumentos: 1 insertSerie hilo: "+Thread.currentThread().getName());
                            serie=serieEnt;
                                loadSerieEnt(serie);
                            return Single.just(serie.id);
                        }
                    }).onErrorResumeNext(new Function<Throwable, SingleSource<? extends Long>>() {
                        @Override
                        public SingleSource<? extends Long> apply(@NotNull Throwable throwable) throws Exception {
                            Log.e(TAG, "apply: 2 onErrorResumeNext", throwable);
                            Log.i(TAG, "apply: 2 insertSerie");
                            return OPelisplusRoom.getInstance(getContext()).serieDao().insertSerie(new SerieEnt(media))
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread());
                        }
                    }).flatMap(aLong -> {
                        Log.i(TAG, "loadArgumentos: getSerie hilo: "+Thread.currentThread().getName());
                        Log.i(TAG, "loadArgumentos: getSerie- obteniendo la serie long: "+aLong);
                        if(serie!=null && serie.id==aLong) return Single.just(serie);
                        return OPelisplusRoom.getInstance(getContext()).serieDao().getSerie(aLong)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread());
                        //return null;
                    }).subscribe((serieEnt, throwable) -> {
                        Log.i(TAG, "loadArgumentos: subscribe hilo: " + Thread.currentThread().getName());
                        if (throwable == null){
                            if( serieEnt!=null){
                                Log.i(TAG, "loadArgumentos: serieEnt ok");
                                if(serie==null){
                                    serie = serieEnt;
                                    Log.i(TAG, "loadArgumentos: serieEnt -> serie :" + serie);
                                }else if(serie.id == serieEnt.id) Log.i(TAG, "loadArgumentos: serieEnt = serie");
                                else Log.i(TAG, "loadArgumentos: serie y serieEnt distintos!");
                            }else Log.e(TAG, "loadArgumentos: serieEnt es null");
                        }else Log.e(TAG, "loadArgumentos: subscribe - hubo un error: "+throwable,throwable);


                        loadSerieEnt(serie);
                        getSerie(serie);
                        tabSeasonStateAdapter.setTabSeasonList(serie.seasonList);
                        new TabLayoutMediator(binding.fragCartelViewpagerTabs, binding.fragCartelViewpagerVp2,
                                (tab, position) -> {
                                    tab.setText(serie.seasonList.get(position).seasonTitle);
                                }
                        ).attach();
                    });
            */


            /*pelisplushdService.getSingleSerieCartel(media.href).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
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
                });*/
        }
    }

    private void loadSerieEnt(SerieEnt serie){
        Log.i(TAG, "loadSerieEnt: serie: "+serie);
        ((MainActivity)getActivity()).setDisplayShowTitleEnabled(true);
        //((MainActivity)getActivity()).setTitleToolbar(serieCartel.name);
        getActivity().setTitle(serie.titulo);
        binding.fragCartelSinopsis.setText(serie.sinopsis);
        try {
            ((MainActivity)getActivity()).loadImgToolbar(serie.src_img);
        }catch (Exception e){e.printStackTrace();}
    }

    /*
    private void getSerie(SerieEnt serie) {
        Log.i(TAG, "getSerie: ");
        OPelisplusRoom.getInstance(getContext()).serieDao().getSerie(serie.href)
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
//                        SerieEnt serieNew = new SerieEnt();
//                        serieNew.href=serie.href;
//                        serieNew.rating=serie.rating;
//                        serieNew.sinopsis=serie.sinopsis;
//                        serieNew.titulo=serieCartel.name;
//                        serieNew.src_img=serieCartel.src_img;
//                        serieNew.url_disqus=serieCartel.url_disqus;
                          OPelisplusRoom.getInstance(getContext()).serieDao().insertSerie(serie)
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
    */

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

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((MainActivity)getActivity()).setDisplayShowTitleEnabled(false);
        ((MainActivity)getActivity()).setFabImage(R.drawable.ic_favorite_border_white_24dp);
    }
}
