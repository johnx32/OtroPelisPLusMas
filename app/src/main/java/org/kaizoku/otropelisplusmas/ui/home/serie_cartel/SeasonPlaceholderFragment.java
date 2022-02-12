package org.kaizoku.otropelisplusmas.ui.home.serie_cartel;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.kaizoku.otropelisplusmas.R;
import org.kaizoku.otropelisplusmas.adapter.ChapterAdapter;
import org.kaizoku.otropelisplusmas.database.entity.SerieEnt;
import org.kaizoku.otropelisplusmas.databinding.FragmentPlaceholderSeasonBinding;
import org.kaizoku.otropelisplusmas.model.Season;

import java.util.ArrayList;
import java.util.List;

public class SeasonPlaceholderFragment extends Fragment implements ChapterAdapter.OnCardChapterListener{
    private static final String TAG = "sd4fd";
    private FragmentPlaceholderSeasonBinding binding;
    private ChapterAdapter chapterAdapter;
    private SerieEnt serie;

    public static SeasonPlaceholderFragment newInstance(List<Season> seasonList, int seasonPos) {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("season_list", (ArrayList) seasonList);
        bundle.putInt("season_pos",seasonPos);
        SeasonPlaceholderFragment fragment = new SeasonPlaceholderFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public static SeasonPlaceholderFragment newInstance(SerieEnt serie) {
        Bundle b = new Bundle();
        b.putParcelable("serie",serie);
        SeasonPlaceholderFragment fragment = new SeasonPlaceholderFragment();
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPlaceholderSeasonBinding.inflate(inflater,container,false);

        setRecycler();
        loadArgumentos();

        return binding.getRoot();
    }

    private void loadArgumentos(){
        Bundle bundle = getArguments();
        if(bundle!=null){
            serie=bundle.getParcelable("serie");
            chapterAdapter.setChapterList(serie.getCurrentSeason());
        }
    }

    private void setRecycler() {
        binding.fragPlaceholderSeasonRv.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.fragPlaceholderSeasonRv.setLayoutManager(layoutManager);
        chapterAdapter = new ChapterAdapter(this);
        binding.fragPlaceholderSeasonRv.setAdapter(chapterAdapter);
    }

    @Override
    public void onClickCardChapter(String href,int chapterPos) {
        serie.setChapterPos(href,chapterPos);

        Bundle b=new Bundle();
        b.putParcelable("serie",serie);

        NavHostFragment.findNavController(this)
                .navigate(R.id.action_cartelFragment_to_videoCartelFragment,b);
    }
}