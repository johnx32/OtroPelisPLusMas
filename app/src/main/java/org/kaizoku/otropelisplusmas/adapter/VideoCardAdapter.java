package org.kaizoku.otropelisplusmas.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.squareup.picasso.Picasso;

import org.kaizoku.otropelisplusmas.R;
import org.kaizoku.otropelisplusmas.adapter.VideoCardAdapter.VideoViewHolder;
import org.kaizoku.otropelisplusmas.model.VideoCard;

import java.util.ArrayList;
import java.util.List;

public class VideoCardAdapter extends Adapter<VideoViewHolder> {
    private List<VideoCard> list=new ArrayList<>();

    public VideoCardAdapter(OnCardListener onCardListener) {
        this.onCardListener = onCardListener;
    }

    public void setList(List<VideoCard> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_video,parent,false);
        VideoViewHolder videoViewHolder = new VideoViewHolder(v);
        return videoViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        holder.name.setText(list.get(position).name);
        holder.rating.setText(list.get(position).rating);
        Picasso
            .get()
            .load(list.get(position).src_img)
            /*.transform(new Transformation() {
                @Override
                public Bitmap transform(Bitmap source) {
                    x =source.getWidth();
                    int targetWidth = holder.src.getWidth();

                    double aspectRatio = (double) source.getHeight() / (double) source.getWidth();
                    int targetHeight = (int) (targetWidth * aspectRatio);
                    Bitmap result = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false);
                    if (result != source) {
                        // Same bitmap is returned if sizes are the same
                        source.recycle();
                    }
                    return source;
                }

                @Override
                public String key() {
                    return "key";
                }
            })*/
            .fit()
            .into(holder.src);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class VideoViewHolder extends ViewHolder {
        TextView name,rating;
        ImageView src;
        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.cv_tv_name);
            rating = itemView.findViewById(R.id.cv_tv_rating);
            src = itemView.findViewById(R.id.cv_iv_video_src);
            itemView.setOnClickListener(v -> {
                if(onCardListener!=null)onCardListener.onClickCard(list.get(getAdapterPosition()));
            });
        }
    }

    private OnCardListener onCardListener;
    public interface OnCardListener{
        void onClickCard(VideoCard videoCard);
    }

}
