package org.kaizoku.otropelisplusmas.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.kaizoku.otropelisplusmas.R;
import org.kaizoku.otropelisplusmas.model.Chapter;

import java.util.ArrayList;
import java.util.List;

public class ChapterAdapter extends RecyclerView.Adapter<ChapterAdapter.ChapterViewHolder> {
    List<Chapter> list=new ArrayList<>();

    public ChapterAdapter(OnCardChapterListener onCardListener) {
        this.onCardListener = onCardListener;
    }

    public void setChapterList(List<Chapter> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_chapter,parent,false);
        ChapterViewHolder chapterViewHolder = new ChapterViewHolder(v);
        return chapterViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ChapterViewHolder holder, int position) {
        holder.title.setText(list.get(position).title);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ChapterViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        public ChapterViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.cv_chapter_title);
            itemView.setOnClickListener(v -> {
                    onCardListener.onClickCardChapter(list.get(getAdapterPosition()).href);
            });
        }
    }

    private OnCardChapterListener onCardListener;
    public interface OnCardChapterListener{
        void onClickCardChapter(String href);
    }
}
