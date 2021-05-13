package org.kaizoku.otropelisplusmas.ui.home.serie_cartel;

import android.os.Bundle;
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
import org.kaizoku.otropelisplusmas.model.Season;

public class SeasonPlaceholderFragment extends Fragment implements ChapterAdapter.OnCardChapterListener{
    private static final String TAG = "sd4fd";
    private FragmentPlaceholderSeasonBinding binding;
    private ChapterAdapter chapterAdapter;
    private Season season;

    public static SeasonPlaceholderFragment newInstance(Season season) {
        Log.i(TAG, "newInstance: s: "+season.chapterList.size());
        Bundle bundle = new Bundle();
        bundle.putParcelable("season",season);
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
            season=bundle.getParcelable("season");
            chapterAdapter.setChapterList(season.chapterList);
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
    public void onClickCardChapter(String href) {
        ((MainActivity)getActivity()).showInterstitialAd();
        Bundle b=new Bundle();
        b.putString("url",href);
        NavHostFragment.findNavController(this)
                .navigate(R.id.action_cartelFragment_to_videoCartelFragment,b);
    }
}
