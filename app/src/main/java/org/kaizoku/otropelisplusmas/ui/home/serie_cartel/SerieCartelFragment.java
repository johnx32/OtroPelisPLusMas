package org.kaizoku.otropelisplusmas.ui.home.serie_cartel;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayoutMediator;
import com.squareup.picasso.Picasso;

import org.kaizoku.otropelisplusmas.databinding.FragmentSerieCartelBinding;
import org.kaizoku.otropelisplusmas.model.SerieCartel;
import org.kaizoku.otropelisplusmas.service.PelisplushdService;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class SerieCartelFragment extends Fragment {
    private static final String TAG = "bf5t1";
    private FragmentSerieCartelBinding binding;
    private PelisplushdService pelisplushdService;
    private TabSeasonStateAdapter tabSeasonStateAdapter;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pelisplushdService = new PelisplushdService(null);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSerieCartelBinding.inflate(inflater,container,false);

        tabSeasonStateAdapter = new TabSeasonStateAdapter(getChildFragmentManager(),getLifecycle());
        binding.fragCartelViewpagerVp2.setAdapter(tabSeasonStateAdapter);

        Bundle b = getArguments();
        if(b!=null) {
            String url = b.getString("url", "");
            Log.i(TAG, "onCreate: url: " + url);

            pelisplushdService.getSingleSerieCartel(url)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe((serieCartel, throwable) -> {
                        if (throwable == null && serieCartel != null) {
                            setSerieCartel(serieCartel);
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

    private void setSerieCartel(SerieCartel serieCartel){
        //binding.fragCartelTitle.setText(serieCartel.name);
        getActivity().setTitle(serieCartel.name);
        binding.fragCartelSinopsis.setText(serieCartel.sinopsis);
        Picasso.get()
                .load(serieCartel.src_img)
                .into(binding.fragCartelSrc);
    }

}
