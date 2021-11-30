package org.kaizoku.otropelisplusmas.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.kaizoku.otropelisplusmas.R;
import org.kaizoku.otropelisplusmas.database.entity.SerieEnt;

import java.util.ArrayList;
import java.util.List;

public class FavoritosAdapter extends RecyclerView.Adapter<FavoritosAdapter.FavoritoViewHolder> {
    private List<SerieEnt> lista = new ArrayList<>();

    public FavoritosAdapter(OnCardListener onCardListener) {
        this.onCardListener = onCardListener;
    }

    public void setLista(List<SerieEnt> lista) {
        this.lista = lista;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FavoritoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_video,parent,false);
        FavoritoViewHolder favoritoViewHolder = new FavoritoViewHolder(v);
        return favoritoViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FavoritoViewHolder holder, int position) {
        holder.name.setText(lista.get(position).titulo);
        holder.rating.setText(lista.get(position).rating);
        Picasso
            .get()
            .load(lista.get(position).src_img)
            .fit()
            .into(holder.src);
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public class FavoritoViewHolder extends RecyclerView.ViewHolder{
        TextView name,rating;
        ImageView src;
        public FavoritoViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.cv_tv_name);
            rating = itemView.findViewById(R.id.cv_tv_rating);
            src = itemView.findViewById(R.id.cv_iv_video_src);
            itemView.setOnClickListener(v -> {
                if(onCardListener!=null)onCardListener.onClickCard(lista.get(getAdapterPosition()));
            });
        }
    }

    private OnCardListener onCardListener;
    public interface OnCardListener{
        void onClickCard(SerieEnt serie);
    }
}
