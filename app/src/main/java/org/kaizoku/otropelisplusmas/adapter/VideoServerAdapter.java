package org.kaizoku.otropelisplusmas.adapter;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import org.kaizoku.otropelisplusmas.R;
import org.kaizoku.otropelisplusmas.model.video_server.FembedServer;
import org.kaizoku.otropelisplusmas.model.video_server.VideoServer;
import org.kaizoku.otropelisplusmas.view_group.FembedOptionLayout;

import java.util.ArrayList;
import java.util.List;

public class VideoServerAdapter extends RecyclerView.Adapter<VideoServerAdapter.VideoServerViewHolder> {
    public static final byte OPTION_PLAY=1;
    public static final byte OPTION_EXT=2;
    public static final byte OPTION_CAST=3;
    List<VideoServer> list=new ArrayList<>();
    AdSize adSize;

    public VideoServerAdapter(OnCardListener onCardListener,AdSize adSize) {
        this.onCardListener = onCardListener;
        this.adSize = adSize;
    }

    public void setList(List<VideoServer> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VideoServerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_video_server,parent,false);
        VideoServerViewHolder videoServerViewHolder = new VideoServerViewHolder(v);
        return videoServerViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull VideoServerViewHolder holder, int position) {
        holder.name.setText(list.get(position).nameServer);
        Log.i("TAG", "onBindViewHolder: type: "+getItemViewType(position));
        switch (getItemViewType(position)){
            case VideoServer.SERVER_FEMBED:
                FembedServer fServer=(FembedServer)list.get(position);
                for(int i=0;i<fServer.options.size();i++) {
                    int finalI = i;
                    holder.contenedor.addView(
                        new FembedOptionLayout(
                                holder.contenedor.getContext(),fServer.options.get(i).label,
                                v -> {{onCardListener.onClickCard(((FembedServer) list.get(position)).options.get(finalI).file,OPTION_PLAY);}},
                                v -> {onCardListener.onClickCard(((FembedServer) list.get(position)).options.get(finalI).file,OPTION_EXT);},
                                v -> {onCardListener.onClickCard(((FembedServer) list.get(position)).options.get(finalI).file,OPTION_CAST);}
                            )
                    );
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(list.get(position) instanceof FembedServer)
            return VideoServer.SERVER_FEMBED;
        return 0;
    }

    public class VideoServerViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        LinearLayout contenedor;
        AdView adView;
        LinearLayout linearLayout;
        public VideoServerViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.cv_video_server_ll_name);
            contenedor = itemView.findViewById(R.id.cv_video_server_ll_contenedor);
            linearLayout = itemView.findViewById(R.id.cv_video_server_linearlayout);
            /*itemView.setOnClickListener(v -> {
                switch (getItemViewType()){
                    case VideoServer.SERVER_FEMBED:
                        FembedServer fServer=(FembedServer)list.get(getAdapterPosition());
                        onCardListener.onClickCard(fServer.options.get(0).file);
                        break;
                }
            });*/
            // Step 1 - Create an AdView and set the ad unit ID on it.
            //adView = itemView.findViewById(R.id.cv_video_server_ll_adView);
            adView = new AdView(itemView.getContext());
            adView.setAdUnitId(itemView.getContext().getString(R.string.id_baner1));
            linearLayout.addView(adView);
            loadBanner();
        }

        private void loadBanner() {
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


    }

    private OnCardListener onCardListener;
    public interface OnCardListener{
        void onClickCard(String file_url, byte option);
    }
}
