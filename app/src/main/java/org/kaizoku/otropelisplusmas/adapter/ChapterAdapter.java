package org.kaizoku.otropelisplusmas.adapter;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import org.kaizoku.otropelisplusmas.R;
import org.kaizoku.otropelisplusmas.model.Chapter;

import java.util.ArrayList;
import java.util.List;

public class ChapterAdapter extends RecyclerView.Adapter<ChapterAdapter.ChapterViewHolder> {
    private List<Chapter> list=new ArrayList<>();
    private AdSize adSize;

    public ChapterAdapter(OnCardChapterListener onCardListener, AdSize adSize) {
        this.onCardListener = onCardListener;
        this.adSize = adSize;
    }

    public void setChapterList(List<Chapter> list) {
        this.list.clear();
        int j=0;
        for (Chapter c:list) {
            if((j%5)==0)
                this.list.add(new Chapter("","",Chapter.TYPE_BANNER_ADAPTATIVE));
            this.list.add(c);
            j++;
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        /*switch (viewType){
            case Chapter.TYPE_BANNER_ADAPTATIVE:
                break;
            case Chapter.TYPE_CHAPTER:
                break;

        }*/
        Log.i("TAG", "onCreateViewHolder: viewtype: "+viewType);
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_chapter,parent,false);
        ChapterViewHolder chapterViewHolder = new ChapterViewHolder(v);
        return chapterViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ChapterViewHolder holder, int position) {
        Log.i("TAG", "onBindViewHolder: pos: "+position+" type"+getItemViewType(position));
        if(getItemViewType(position)==Chapter.TYPE_CHAPTER){
            holder.title.setText(list.get(position).title);
            if(list.get(position).visto)
                holder.color.setBackgroundColor(Color.RED);
            else holder.color.setBackgroundColor(holder.itemView.getResources().getColor(R.color.button));
            holder.itemView.setOnClickListener(v -> {
                onCardListener.onClickCardChapter(list.get(position).href,position);
            });
        }else{
            AdView adView = new AdView(holder.itemView.getContext());
            adView.setAdUnitId(holder.itemView.getContext().getString(R.string.banner_adaptative02));
            holder.cardView.setVisibility(View.GONE);
            holder.ll.addView(adView);
            loadBanner(adView);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position).type;
    }

    public class ChapterViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        //AdView adView;
        CardView cardView;
        LinearLayout ll;
        LinearLayout color;
        public ChapterViewHolder(@NonNull View itemView) {
            super(itemView);
            //Log.i("TAG", "ChapterViewHolder: type"+getItemViewType() list.get(getAdapterPosition()).type);
            title = itemView.findViewById(R.id.cv_chapter_title);
            cardView = itemView.findViewById(R.id.cv_chapter_title_cardview);
            ll = itemView.findViewById(R.id.cv_chapter_linearlayout);
            color = itemView.findViewById(R.id.cv_chapter_color);
        }
    }

    private void loadBanner(AdView adView) {
        // Create an ad request. Check your logcat output for the hashed device ID
        // to get test ads on a physical device, e.g.,
        // "Use AdRequest.Builder.addTestDevice("ABCDE0123") to get test ads on this
        // device."
        AdRequest adRequest = new AdRequest.Builder()
                .build();

        //AdSize adSize = getAdSize();
        // Step 4 - Set the adaptive ad size on the ad view.
        adView.setAdSize(adSize);


        // Step 5 - Start loading the ad in the background.
        adView.loadAd(adRequest);
    }


    /*
    public class BannerViewHolder extends BaseViewHolder{
        TextView title;
        public BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.cv_chapter_title);
            itemView.setOnClickListener(v -> {
                onCardListener.onClickCardChapter(list.get(getAdapterPosition()).href,getAdapterPosition());
            });
        }
    }*/

    private OnCardChapterListener onCardListener;
    public interface OnCardChapterListener{
        void onClickCardChapter(String href,int chapterPos);
    }
}
