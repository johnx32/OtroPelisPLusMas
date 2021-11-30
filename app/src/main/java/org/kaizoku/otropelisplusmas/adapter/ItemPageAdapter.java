package org.kaizoku.otropelisplusmas.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.kaizoku.otropelisplusmas.R;
import org.kaizoku.otropelisplusmas.model.ItemPage;

import java.util.ArrayList;
import java.util.List;

public class ItemPageAdapter extends RecyclerView.Adapter<ItemPageAdapter.ItemPaginationViewHolder> {
    private static final String TAG = "eljs";
    private List<ItemPage> list = new ArrayList<>();

    public ItemPageAdapter(OnCardPaginationListener onCardPaginationListener) {
        this.onCardPaginationListener = onCardPaginationListener;
    }

    public void setList(List<ItemPage> list) {
        if(list!=null)
            this.list = list;
        else Log.i(TAG, "setList: list recivido es null");
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ItemPaginationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_item_pagination,parent,false);
        ItemPaginationViewHolder itemPaginationViewHolder = new ItemPaginationViewHolder(v);
        return itemPaginationViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemPaginationViewHolder holder, int position) {
        holder.b.setText(list.get(position).text);
    }

    @Override
    public int getItemCount() {
        /*if(list==null){
            Log.e(TAG, "getItemCount: Error, list es null");
            return 0;
        }*/
        return list.size();
    }

    public class ItemPaginationViewHolder extends RecyclerView.ViewHolder{
        Button b;
        public ItemPaginationViewHolder(@NonNull View itemView) {
            super(itemView);
            b = itemView.findViewById(R.id.bt_item_pagination);
            b.setOnClickListener(v -> {
                onCardPaginationListener.onClickCardItem(list.get(getAdapterPosition()).href);
            });
        }
    }

    private OnCardPaginationListener onCardPaginationListener;
    public interface OnCardPaginationListener{
        void onClickCardItem(String url);
    }
}
