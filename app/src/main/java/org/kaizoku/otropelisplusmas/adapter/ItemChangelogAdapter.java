package org.kaizoku.otropelisplusmas.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.kaizoku.otropelisplusmas.R;
import org.kaizoku.otropelisplusmas.adapter.ItemChangelogAdapter.ItemChangelogViewHolder;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import org.jetbrains.annotations.NotNull;
import org.kaizoku.otropelisplusmas.model.ItemChangelog;
import org.kaizoku.otropelisplusmas.view_group.ItemChangeLayout;

import java.util.ArrayList;
import java.util.List;

public class ItemChangelogAdapter extends Adapter<ItemChangelogViewHolder> {
    private List<ItemChangelog> list = new ArrayList<>();

    public void setList(List<ItemChangelog> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @NotNull
    @Override
    public ItemChangelogViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_item_changelog,parent,false);
        ItemChangelogViewHolder itemChangelogViewHolder = new ItemChangelogViewHolder(v);
        return itemChangelogViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ItemChangelogViewHolder holder, int position) {
        holder.code.setText(String.valueOf(list.get(position).code));
        holder.name.setText(list.get(position).name);
        int size = list.get(position).changes.size();
        for (int i=0;i<size;i++) {
            ItemChangeLayout item = new ItemChangeLayout(holder.itemView.getContext(), list.get(position).changes.get(i).type, list.get(position).changes.get(i).text);
            holder.ll.addView(item);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ItemChangelogViewHolder extends ViewHolder{
        TextView name,code;
        LinearLayout ll;
        public ItemChangelogViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            code = itemView.findViewById(R.id.code);
            ll = itemView.findViewById(R.id.changes_list);
        }
    }

}
