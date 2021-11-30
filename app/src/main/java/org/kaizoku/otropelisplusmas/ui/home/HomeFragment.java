package org.kaizoku.otropelisplusmas.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;

import org.kaizoku.otropelisplusmas.MainActivity;
import org.kaizoku.otropelisplusmas.R;
import org.kaizoku.otropelisplusmas.adapter.ItemPageAdapter;
import org.kaizoku.otropelisplusmas.adapter.VideoCardAdapter;
import org.kaizoku.otropelisplusmas.database.entity.MediaEnt;
import org.kaizoku.otropelisplusmas.databinding.FragmentHomeBinding;
import org.kaizoku.otropelisplusmas.model.FullPage;
import org.kaizoku.otropelisplusmas.service.PelisplushdService;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiConsumer;
import io.reactivex.schedulers.Schedulers;

public class HomeFragment extends Fragment implements
        VideoCardAdapter.OnCardListener,
        ItemPageAdapter.OnCardPaginationListener {
    private static final String TAG = "sfa4e";
    private FragmentHomeBinding binding;
    private VideoCardAdapter videoCardAdapter;
    private ItemPageAdapter itemPageAdapter;
    private PelisplushdService pelisplushdService;
    private HomeViewModel homeViewModel;
    private String url_current;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater,container,false);
        pelisplushdService=new PelisplushdService();
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);


        ((MainActivity)getActivity()).setDisplayShowTitleEnabled(false);
        loadBundle();

        initVideoCardAdapter();
        initItemPaginationAdapter();
        cargarPagina(url_current);
        return binding.getRoot();
    }

    private void loadBundle() {
        Bundle b = getArguments();
        if(b!=null) {
            int index = b.getInt("url",-1);
            if(index>=0)
                url_current = getContext().getResources().getStringArray(R.array.urls)[index];
        }else Log.e(TAG, "onCreateView: bundle Arguments es null");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        homeViewModel.getListaFullpage().observe(getViewLifecycleOwner(),fullPage -> {
            videoCardAdapter.setList(fullPage.listCard);
            if(fullPage.paginacion!=null)
                itemPageAdapter.setList(fullPage.paginacion);
            else
                Log.e(TAG, "onCreateView: fullPage.paginacion es null");
        });

        binding.rvSwipeSeries.setOnRefreshListener(() -> {
            if(url_current!=null)
                cargarPagina(url_current);
        });
    }

    private void initVideoCardAdapter() {
        binding.rvSeries.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),3);
        binding.rvSeries.setLayoutManager(gridLayoutManager);
        videoCardAdapter = new VideoCardAdapter(this);
        binding.rvSeries.setAdapter(videoCardAdapter);
    }

    private void initItemPaginationAdapter() {
        binding.rvPagination.setHasFixedSize(true);
        LinearLayoutManager linearLayout = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        binding.rvPagination.setLayoutManager(linearLayout);
        itemPageAdapter = new ItemPageAdapter(this);
        binding.rvPagination.setAdapter(itemPageAdapter);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_home,menu);
        MenuItem itemSearch = menu.findItem(R.id.menu_search);
        itemSearch.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) { return true; }
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {

                return true;
            }
        });
        SearchView searchView = (SearchView) itemSearch.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                cargarPagina("https://pelisplushd.net/search?s="+query);
                /*pelisplushdService.loadMenuCardsSingle("https://pelisplushd.net/search?s="+query)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(onload);*/
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                //adapterCliente.getFilter().filter(newText);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onClickCard(MediaEnt mediaEnt) {
        //#adsblock
        ((MainActivity)getActivity()).showInterstitialAd();

        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
        int id_nav=navController.getCurrentDestination().getId();

        Bundle b=new Bundle();
        b.putString("url",mediaEnt.href);
        b.putParcelable("media",mediaEnt);

        switch (id_nav){
            case R.id.nav_home:
                if(mediaEnt.getTypeHref()==MediaEnt.TYPE_PELICULA)
                    NavHostFragment.findNavController(this)
                            .navigate(R.id.action_nav_home_to_videoCartelFragment,b);
                else if(mediaEnt.getTypeHref()==MediaEnt.TYPE_SERIE || mediaEnt.getTypeHref()==MediaEnt.TYPE_ANIME)
                    NavHostFragment.findNavController(this)
                            .navigate(R.id.action_nav_home_to_cartelFragment,b);
                break;
            case R.id.nav_peliculas:
                NavHostFragment.findNavController(this)
                        .navigate(R.id.action_nav_peliculas_to_videoCartelFragment,b);
                break;
            case R.id.nav_series:
                NavHostFragment.findNavController(this)
                        .navigate(R.id.action_nav_series_to_cartelFragment,b);
                break;
            case R.id.nav_animes:
                NavHostFragment.findNavController(this)
                        .navigate(R.id.action_nav_animes_to_cartelFragment,b);
                break;
        }

        /*
        switch (mediaEnt.type){
            case MediaEnt.TYPE_PELICULA:
                FragmentNavigator.Extras extras = new FragmentNavigator.Extras.Builder()
                        .addSharedElement( getView().findViewById(R.id.cv_iv_video_src),"chapter_img")
                        .build();
                if(id_nav==R.id.nav_home)
                    NavHostFragment.findNavController(this)
                            .navigate(R.id.action_nav_home_to_videoCartelFragment,b,null,extras);
                else
                    NavHostFragment.findNavController(this)
                            .navigate(R.id.action_nav_peliculas_to_videoCartelFragment,b);
                break;
            case MediaEnt.TYPE_SERIE:
                //((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(true);
                if(id_nav==R.id.nav_home)
                    NavHostFragment.findNavController(this)
                            .navigate(R.id.action_nav_home_to_cartelFragment2,b);
                else
                    NavHostFragment.findNavController(this)
                            .navigate(R.id.action_nav_series_to_cartelFragment,b);
                break;
            case MediaEnt.TYPE_ANIME:
                if(id_nav==R.id.nav_home)
                    NavHostFragment.findNavController(this)
                            .navigate(R.id.action_nav_home_to_cartelFragment2,b);
                else
                    NavHostFragment.findNavController(this)
                            .navigate(R.id.action_nav_animes_to_cartelFragment,b);
                break;
        }*/
    }

    @Override
    public void onClickCardItem(String url) {
        if(!url.contains("pelisplushd.net"))
            url="https://pelisplushd.net/"+url;
        cargarPagina(url);
    }

    private void cargarPagina(String url){
        if(homeViewModel.getListaFullpage().getValue()!=null &&
                homeViewModel.getListaFullpage().getValue().url!=null &&
                homeViewModel.getListaFullpage().getValue().url.equals(url)){
            Log.i(TAG, "lista con elementos: ");
            FullPage fp = homeViewModel.getListaFullpage().getValue();
            videoCardAdapter.setList(fp.listCard);
            if(fp.paginacion!=null)
                itemPageAdapter.setList(fp.paginacion);
            else Log.e(TAG, "cargarPagina: fp.paginacion es nullo");

            binding.pbHomeLoadContent.setVisibility(View.GONE);
            binding.rvSwipeSeries.setRefreshing(false);
        }else {
            Log.i(TAG, "cargarPagina: lista vacia");
            pelisplushdService.loadMenuCardsSingle(url)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe((fullPage, throwable) -> {
                        binding.pbHomeLoadContent.setVisibility(View.GONE);
                        if(throwable==null) {//todo: mover vista de recycler to top
                            homeViewModel.setListaFullpage(fullPage);
                        }else{
                            Log.e(TAG, "onload, error al cargar: ",throwable );
                            //todo:error intentar denuevo
                            Snackbar.make(getView(),"Ocurrio un error al cargar la pagina, intente denuevo",Snackbar.LENGTH_LONG).show();
                        }
                        binding.rvSwipeSeries.setRefreshing(false);
                    });
        }
    }

    /*private BiConsumer<FullPage, Throwable> onload = (fullPage, throwable)->{

    };*/
}