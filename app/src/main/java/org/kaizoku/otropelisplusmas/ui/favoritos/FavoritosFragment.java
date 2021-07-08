package org.kaizoku.otropelisplusmas.ui.favoritos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.jetbrains.annotations.NotNull;
import org.kaizoku.otropelisplusmas.R;
import org.kaizoku.otropelisplusmas.adapter.FavoritosAdapter;
import org.kaizoku.otropelisplusmas.adapter.VideoCardAdapter;
import org.kaizoku.otropelisplusmas.database.entity.SerieEnt;
import org.kaizoku.otropelisplusmas.database.viewmodel.FavoritosViewModel;
import org.kaizoku.otropelisplusmas.databinding.FragmentFavoritosBinding;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class FavoritosFragment extends Fragment implements FavoritosAdapter.OnCardListener {
    private FragmentFavoritosBinding binding;
    private FavoritosAdapter adapter;
    private FavoritosViewModel favoritosViewModel;
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        binding = FragmentFavoritosBinding.inflate(inflater,container,false);
        initFavoritosAdapter();
        favoritosViewModel = new ViewModelProvider(this).get(FavoritosViewModel.class);
        favoritosViewModel.getFavoritosSerie()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((series, throwable) -> {
                    if(throwable==null){
                        adapter.setLista(series);
                    }
                });

        return binding.getRoot();
    }

    private void initFavoritosAdapter() {
        binding.recyclerviewFavoritos.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),3);
        binding.recyclerviewFavoritos.setLayoutManager(gridLayoutManager);
        adapter = new FavoritosAdapter(this);
        binding.recyclerviewFavoritos.setAdapter(adapter);
    }

    @Override
    public void onClickCard(SerieEnt serie) {
        Bundle b=new Bundle();
        b.putString("url",serie.href);
        //if is serie
        NavHostFragment.findNavController(this)
                .navigate(R.id.action_nav_favoritos_to_cartelFragment,b);
        //if is video
    }
}
