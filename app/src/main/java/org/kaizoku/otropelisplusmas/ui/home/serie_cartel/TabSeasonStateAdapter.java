package org.kaizoku.otropelisplusmas.ui.home.serie_cartel;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import org.kaizoku.otropelisplusmas.model.Season;

import java.util.ArrayList;
import java.util.List;

public class TabSeasonStateAdapter extends FragmentStateAdapter {
    private static final String TAG = "e7g1df1";
    private List<Season> seasonList=new ArrayList<>();

    public TabSeasonStateAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }
    public void setTabSeasonList(List<Season> seasonList){
        this.seasonList = seasonList;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return SeasonPlaceholderFragment.newInstance(seasonList,position);
    }
    @Override
    public int getItemCount() {
        return seasonList.size();
    }


}
