package org.kaizoku.otropelisplusmas.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.kaizoku.otropelisplusmas.R;
import org.kaizoku.otropelisplusmas.model.ItemPagination;

import java.util.ArrayList;
import java.util.List;

public class ItemPaginationAdapter extends RecyclerView.Adapter<ItemPaginationAdapter.ItemPaginationViewHolder> {
    List<ItemPagination> list = new ArrayList<>();

    public ItemPaginationAdapter(OnCardPaginationListener onCardPaginationListener) {
        this.onCardPaginationListener = onCardPaginationListener;
    }

    public void setList(List<ItemPagination> list) {
        this.list = list;
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
