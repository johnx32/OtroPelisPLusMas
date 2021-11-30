package org.kaizoku.otropelisplusmas.ui.home.serie_cartel;

import android.content.Context;
import android.content.SharedPreferences;
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
import org.kaizoku.otropelisplusmas.database.entity.CapituloEnt;
import org.kaizoku.otropelisplusmas.database.entity.MediaEnt;
import org.kaizoku.otropelisplusmas.database.entity.SerieEnt;
import org.kaizoku.otropelisplusmas.database.viewmodel.CapituloViewModel;
import org.kaizoku.otropelisplusmas.database.viewmodel.SerieViewModel;
import org.kaizoku.otropelisplusmas.databinding.FragmentSerieCartelBinding;
import org.kaizoku.otropelisplusmas.model.Chapter;
import org.kaizoku.otropelisplusmas.model.Season;
import org.kaizoku.otropelisplusmas.service.PelisplushdService;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

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
    private TabLayoutMediator tabLayoutMediator;

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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        checkPendingCapituloProgress();
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
                            Log.i(TAG, "loadArgumentos: suscribe ");
                            setFloatingactionbuttonCallback(serie);
                            loadCapitulosVistos(serie);
                            //initTabLayout(serie);
                        }else Log.e(TAG, "loadArgumentos: subscribe error", throwable);
                    });
        }
    }

    @Override
    public void onResume() {
        Log.i(TAG, "onResume: serie: "+serie);
        //if(serie!=null)
        //    loadCapitulosVistos(serie);
        super.onResume();
    }

    private void initTabLayout(SerieEnt serieEnt) {
        Log.i(TAG, "initTabLayout: ");
        //Log.i(TAG, "initTabLayout: serie: "+serie);
        //tabSeasonStateAdapter.setTabSeasonList(serie.seasonList);
        tabSeasonStateAdapter = new TabSeasonStateAdapter(getChildFragmentManager(),getLifecycle());
        tabSeasonStateAdapter.setSerie(serieEnt);
        binding.fragCartelViewpagerVp2.setAdapter(tabSeasonStateAdapter);
        if (tabLayoutMediator == null){
            tabLayoutMediator = new TabLayoutMediator(
                    binding.fragCartelViewpagerTabs,
                    binding.fragCartelViewpagerVp2,
                    (tab, position) -> {
                        tab.setText(serieEnt.seasonList.get(position).seasonTitle);
                    }
            );
            tabLayoutMediator.attach();
        }else{
            tabLayoutMediator.detach();
            tabLayoutMediator=null;
            tabLayoutMediator = new TabLayoutMediator(
                    binding.fragCartelViewpagerTabs,
                    binding.fragCartelViewpagerVp2,
                    (tab, position) -> {
                        tab.setText(serieEnt.seasonList.get(position).seasonTitle);
                    }
            );
            tabLayoutMediator.attach();
        }

    }

    private void loadSerieEnt(SerieEnt serie){
        Log.i(TAG, "loadSerieEnt: ");
        //Log.i(TAG, "loadSerieEnt: serie: "+serie);
        ((MainActivity)getActivity()).setDisplayShowTitleEnabled(true);
        //((MainActivity)getActivity()).setTitleToolbar(serieCartel.name);
        getActivity().setTitle(serie.titulo);
        binding.fragCartelSinopsis.setText(serie.sinopsis);
        try {
            ((MainActivity)getActivity()).loadImgToolbar(serie.src_img);
        }catch (Exception e){e.printStackTrace();}
    }

    private void setFloatingactionbuttonCallback(SerieEnt serieEnt) {
        Log.i(TAG, "setFloatingactionbuttonCallback: fablistener inicializadondo");
        if(serieEnt.favorito_id>0)
            ((MainActivity)getActivity()).setFabImage(R.drawable.ic_favorite_white_24dp);
        else
            ((MainActivity)getActivity()).setFabImage(R.drawable.ic_favorite_border_white_24dp);

        ((MainActivity)getActivity()).setOnFabListener( (fab) -> {
            Log.i(TAG, "setFloatingactionbuttonCallback: ");
            long favorito_bk = serieEnt.favorito_id;
            if(serieEnt.favorito_id>0)
                serieEnt.favorito_id = 0;
            else
                serieEnt.favorito_id = 1;
            serieViewModel.updateSerie(serieEnt)
                    .subscribe((integer, throwable) -> {
                        if(throwable==null){
                            if(serieEnt.favorito_id>0){
                                fab.setImageResource(R.drawable.ic_favorite_white_24dp);
                            }else{
                                fab.setImageResource(R.drawable.ic_favorite_border_white_24dp);
                            }
                        }else{
                            serieEnt.favorito_id=favorito_bk;
                            Log.e(TAG, "setFloatingactionbuttonCallback: error: "+throwable.getMessage(), throwable);
                        }
                    });
        });
    }

    private void loadCapitulosVistos(SerieEnt serieEnt){
        Log.i(TAG, "loadCapitulosVistos: ");
        //Log.i(TAG, "loadCapitulosVistos: serieEnt: "+serieEnt);
        List<Season> seasonList=serieEnt.seasonList;
        List<String> capitulosList=new ArrayList<>();
        for (Season s:seasonList)
            for (Chapter c:s.chapterList)
                capitulosList.add(c.href);
        serieViewModel.getCapituloConVisto(capitulosList)
            .subscribe((capituloEnts, throwable) -> {
                if(throwable==null){
                    for (Season s:seasonList)
                        for (Chapter c:s.chapterList)
                            for (CapituloEnt ce:capituloEnts)
                                if(c.href.equals(ce.href)){
                                    c.visto=ce.visto;break;
                                }
                    initTabLayout(serieEnt);
                }else Log.e(TAG, "loadCapitulosVistos: error ",throwable);
            });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((MainActivity)getActivity()).setDisplayShowTitleEnabled(false);
        ((MainActivity)getActivity()).setFabImage(R.drawable.ic_favorite_border_white_24dp);
    }

    private void checkPendingCapituloProgress() {
        Log.i(TAG, "checkPendingCapituloProgress: checando shared");
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        long progress = sharedPref.getLong("progress",0);
        long capitulo_id = sharedPref.getLong("capitulo_id",0);

        if(progress>0){
            CapituloViewModel capituloViewModel;
            capituloViewModel = new ViewModelProvider(this).get(CapituloViewModel.class);

            capituloViewModel.getCapitulo(capitulo_id)
                    .flatMap(capituloEnt -> {
                        capituloEnt.progress = progress;
                        return capituloViewModel.updateCapitulo(capituloEnt);
                    })
                    .subscribe((integer, throwable) -> {
                        if(throwable==null) {
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.remove("progress");
                            editor.remove("capitulo_id");
                            editor.commit();
                            Log.i(TAG, "checkPendingCapituloProgress: capitulo pendiente progreso actualizado con exito");
                        }
                    });
        }
    }
}
