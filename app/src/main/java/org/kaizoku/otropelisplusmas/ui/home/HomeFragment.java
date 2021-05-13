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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.formats.UnifiedNativeAd;

import org.kaizoku.otropelisplusmas.MainActivity;
import org.kaizoku.otropelisplusmas.R;
import org.kaizoku.otropelisplusmas.adapter.ItemPaginationAdapter;
import org.kaizoku.otropelisplusmas.adapter.VideoCardAdapter;
import org.kaizoku.otropelisplusmas.databinding.FragmentHomeBinding;
import org.kaizoku.otropelisplusmas.model.ItemPagination;
import org.kaizoku.otropelisplusmas.model.VideoCard;
import org.kaizoku.otropelisplusmas.service.PelisplushdService;

import java.util.List;

public class HomeFragment extends Fragment implements
        VideoCardAdapter.OnCardListener,
        ItemPaginationAdapter.OnCardPaginationListener,
        PelisplushdService.OnMenuVideoListener {
    private static final String TAG = "sfa4e";
    private FragmentHomeBinding binding;
    private VideoCardAdapter videoCardAdapter;
    private ItemPaginationAdapter itemPaginationAdapter;
    private PelisplushdService pelisplushdService;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater,container,false);

        String url=getUrlFromBundle();

        pelisplushdService=new PelisplushdService(this);
        /*String finalUrl = url;
        Thread h = new Thread(new Runnable() {
            @Override
            public void run() {
                pelisplushdService.getMenuPaginacion(finalUrl);
            }
        });
        h.start();*/

        //(new Thread(() -> pelisplushdService.getInfoCartel("https://pelisplushd.net/serie/bones"))).start();

        //setTitle();

        initVideoCardAdapter();
        initItemPaginationAdapter();
        loadAdapter(url);

        /*AdView mAdView = binding.getRoot().findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);*/

        /*AdLoader adLoader = new AdLoader.Builder(getContext(), "ca-app-pub-3940256099942544/2247696110")
                .forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                    @Override
                    public void onUnifiedNativeAdLoaded(@NonNull UnifiedNativeAd unifiedNativeAd) {
                        NativeTemplateStyle styles = new
                                NativeTemplateStyle.Builder().build();
                        //.withMainBackgroundColor(background).build();

                        TemplateView template = binding.myTemplate;
                        template.setStyles(styles);
                        template.setNativeAd(unifiedNativeAd);
                    }
                }).build();
                .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                    @Override
                    public void onNativeAdLoaded(NativeAd nativeAd) {
                        NativeTemplateStyle styles = new NativeTemplateStyle.Builder().build();
                                //.withMainBackgroundColor(background).build();

                        //TemplateView template = binding.getRoot().findViewById(R.id.my_template);
                        //template.setStyles(styles);
                        binding.myTemplate.setStyles(styles);
                        //template.setNativeAd(NativeAd);
                        binding.myTemplate.setNativeAd(nativeAd);
                    }
                })
                .build();

        adLoader.loadAd(new AdRequest.Builder().build());*/

        return binding.getRoot();
    }

    private void setTitle() {
        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
        int id_nav=navController.getCurrentDestination().getId();
        switch (id_nav){
            case R.id.nav_home:
                //getActivity().setTitle("");
                break;
            case R.id.nav_peliculas:
                getActivity().setTitle("Peliculas");
                break;
            case R.id.nav_series:
                getActivity().setTitle("Series");
                break;
            case R.id.nav_animes:
                getActivity().setTitle("Animes");
                break;
        }

    }

    private String getUrlFromBundle(){
        String url="";
        int index = -1;
        Bundle b = getArguments();
        if(b!=null) {
            index = b.getInt("url",-1);
            if(index>=0)
                url = getContext().getResources().getStringArray(R.array.urls)[index];
        }else Log.i(TAG, "onCreateView: b es null");
        return url;
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
        itemPaginationAdapter = new ItemPaginationAdapter(this);
        binding.rvPagination.setAdapter(itemPaginationAdapter);
    }

    private void loadAdapter(String url) {
        Log.i(TAG, "loadAdapter: ");
        pelisplushdService.loadMenuCards(url);
        /*pelisplushdService.getSingleVideoItemsFromMenu(url)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe((videoCards, throwable) -> {
                if(throwable==null){
                    Log.i(TAG, "loadAdapter: single  size: "+videoCards.size());
                    videoCardAdapter.setList(videoCards);
                }else Log.e(TAG, "loadAdapter: ", throwable);
            });*/
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
                pelisplushdService.loadMenuCards("https://pelisplushd.net/search?s="+query);
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
    public void onLoadMenuVideos(List<VideoCard> listCards, List<ItemPagination> paginationList) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                videoCardAdapter.setList(listCards);
                itemPaginationAdapter.setList(paginationList);
                /*int size = paginationList.size();
                if(size>4) {
                    Button b1 = new Button(getContext());
                    b1.setText(paginationList.get(0).text);
                    b1.setBackgroundResource(R.drawable.item_pagination);
                    binding.llPagination.addView(b1);
                    Button b2 = new Button(getContext());
                    b2.setText(paginationList.get(1).text);
                    b2.setBackgroundResource(R.drawable.item_pagination);
                    binding.llPagination.addView(b2);

                    Button b3 = new Button(getContext());
                    b3.setText(paginationList.get(size-1).text);
                    binding.llPagination.addView(b3);
                    Button b4 = new Button(getContext());
                    b4.setText(paginationList.get(size-2).text);
                    binding.llPagination.addView(b4);
                }*/
            }
        });
    }

    @Override
    public void onClickCard(VideoCard videoCard) {
        ((MainActivity)getActivity()).showInterstitialAd();

        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
        int id_nav=navController.getCurrentDestination().getId();

        Bundle b=new Bundle();
        b.putString("url",videoCard.url);
        switch (videoCard.type){
            case VideoCard.TYPE_PELICULA:
                if(id_nav==R.id.nav_home)
                    NavHostFragment.findNavController(this)
                            .navigate(R.id.action_nav_home_to_videoCartelFragment,b);
                else
                    NavHostFragment.findNavController(this)
                            .navigate(R.id.action_nav_peliculas_to_videoCartelFragment,b);
                break;
            case VideoCard.TYPE_SERIE:
                if(id_nav==R.id.nav_home)
                    NavHostFragment.findNavController(this)
                            .navigate(R.id.action_nav_home_to_cartelFragment2,b);
                else
                    NavHostFragment.findNavController(this)
                            .navigate(R.id.action_nav_series_to_cartelFragment,b);
                break;
            case VideoCard.TYPE_ANIME:
                if(id_nav==R.id.nav_home)
                    NavHostFragment.findNavController(this)
                            .navigate(R.id.action_nav_home_to_cartelFragment2,b);
                else
                    NavHostFragment.findNavController(this)
                            .navigate(R.id.action_nav_animes_to_cartelFragment,b);
                break;
        }

        /*
        pelisplushdService.getSingleSeridores(videoCard.url)
                .subscribeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe((servidores, throwable) -> {
                    if(throwable==null) {


                        for(String s:servidores) {
                            Log.i(TAG, "onClickCard: s: "+s);
                            if (s.contains("fembed"))
                                pelisplushdService.getSingleVideoUrl(s)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe((stream, throwable1) -> {
                                            Log.i(TAG, "onClickCard: stream: "+stream);
                                            if (throwable1==null){
                                                JSONObject jsonResponse = new JSONObject(stream);
                                                String video_file_url = jsonResponse.getJSONArray("data").getJSONObject(0).getString("file");
                                                Bundle b=new Bundle();
                                                b.putString("url",video_file_url);
                                                NavHostFragment.findNavController(this)
                                                        .navigate(R.id.action_nav_home_to_nav_reproductor,b);
                                            }else Log.e(TAG, "onClickCard: ", throwable1);
                                        });
                        }
                    }else Log.e(TAG, "onClickCard: ", throwable);
                });

         */
    }

    @Override
    public void onClickCardItem(String url) {
        ((MainActivity)getActivity()).showInterstitialAd();
        if(!url.contains("pelisplushd.net"))
            url="https://pelisplushd.net/"+url;
        pelisplushdService.loadMenuCards(url);
    }
}