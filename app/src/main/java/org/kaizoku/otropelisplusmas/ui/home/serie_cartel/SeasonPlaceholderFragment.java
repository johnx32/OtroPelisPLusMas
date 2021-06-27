package org.kaizoku.otropelisplusmas.ui.home.serie_cartel;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.ads.AdSize;

import org.kaizoku.otropelisplusmas.MainActivity;
import org.kaizoku.otropelisplusmas.R;
import org.kaizoku.otropelisplusmas.adapter.ChapterAdapter;
import org.kaizoku.otropelisplusmas.databinding.FragmentPlaceholderSeasonBinding;
import org.kaizoku.otropelisplusmas.model.Chapter;
import org.kaizoku.otropelisplusmas.model.Season;

import java.util.ArrayList;
import java.util.List;

public class SeasonPlaceholderFragment extends Fragment implements ChapterAdapter.OnCardChapterListener{
    private static final String TAG = "sd4fd";
    private FragmentPlaceholderSeasonBinding binding;
    private ChapterAdapter chapterAdapter;
    // Controls de season & chapter
    private List<Season> seasonList=new ArrayList<>();
    private int seasonPos;

    public static SeasonPlaceholderFragment newInstance(List<Season> seasonList, int seasonPos) {
        //Log.i(TAG, "newInstance: s: "+season.chapterList.size());
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("season_list", (ArrayList) seasonList);
        bundle.putInt("season_pos",seasonPos);
        SeasonPlaceholderFragment fragment = new SeasonPlaceholderFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPlaceholderSeasonBinding.inflate(inflater,container,false);

        setRecycler();

        Bundle bundle = getArguments();
        if(bundle!=null){
            seasonList=bundle.getParcelableArrayList("season_list");
            seasonPos=bundle.getInt("season_pos");
            chapterAdapter.setChapterList(seasonList.get(seasonPos).chapterList);
        }else Log.i(TAG, "onCreateView: bundle es null");

        return binding.getRoot();
    }

    private void setRecycler() {
        binding.fragPlaceholderSeasonRv.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.fragPlaceholderSeasonRv.setLayoutManager(layoutManager);

        chapterAdapter = new ChapterAdapter(this,getAdSize());

        binding.fragPlaceholderSeasonRv.setAdapter(chapterAdapter);
    }

    private AdSize getAdSize() {
        // Step 2 - Determine the screen width (less decorations) to use for the ad width.
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;

        int adWidth = (int) (widthPixels / density);

        // Step 3 - Get adaptive ad size and return for setting on the ad view.
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(getContext(), adWidth);
    }

    @Override
    public void onClickCardChapter(String href,int chapterPos) {
        //#adsblock
        ((MainActivity)getActivity()).showInterstitialAd();
        Bundle b=new Bundle();
        b.putString("url",href);
        b.putParcelableArrayList("season_list", (ArrayList) seasonList);
        b.putInt("season_pos",seasonPos);
        b.putInt("chapter_pos",chapterPos);
        NavHostFragment.findNavController(this)
                .navigate(R.id.action_cartelFragment_to_videoCartelFragment,b);
    }
}
