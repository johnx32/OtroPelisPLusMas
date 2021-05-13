package org.kaizoku.otropelisplusmas.adapter;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BaseAdapter extends RecyclerView.ViewHolder {
    public static final byte TYPE_ADVIEW = 1;
    public static final byte TYPE_VIDEO = 2;
    //private static final byte TYPE_CHAPTER = 3;
    public byte type=0;
    public BaseAdapter(@NonNull View itemView) {
        super(itemView);
    }
}
