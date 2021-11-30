package org.kaizoku.otropelisplusmas.ui.home.serie_cartel;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import org.kaizoku.otropelisplusmas.database.entity.SerieEnt;
import org.kaizoku.otropelisplusmas.model.Season;

import java.util.ArrayList;
import java.util.List;

import static androidx.viewpager.widget.PagerAdapter.POSITION_NONE;

public class TabSeasonStateAdapter extends FragmentStateAdapter {
    private static final String TAG = "e7g1df1";
    //private List<Season> seasonList=new ArrayList<>();
    private SerieEnt serie;

    public TabSeasonStateAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }
    public void setTabSeasonList(List<Season> seasonList){
        //this.seasonList = seasonList;
        notifyDataSetChanged();
    }
    public void setSerie(SerieEnt serie){
        Log.i(TAG, "setSerie: ");
        this.serie=serie;
        notifyDataSetChanged();
        notifyDataSetChanged();
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Log.i(TAG, "createFragment: position: "+position);
        serie.seasonPos=position;
        //return SeasonPlaceholderFragment.newInstance(seasonList,position);
        return SeasonPlaceholderFragment.newInstance(serie);
    }
    @Override
    public int getItemCount() {
        return serie.seasonList.size();
    }

    /*@Override
    public long getItemId(int position) {
        return POSITION_NONE;
    }*/




}
