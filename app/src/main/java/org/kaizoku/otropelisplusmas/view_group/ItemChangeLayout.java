package org.kaizoku.otropelisplusmas.view_group;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.chip.Chip;

import org.kaizoku.otropelisplusmas.R;

public class ItemChangeLayout extends LinearLayout {
    public ItemChangeLayout(Context context, String type, String text) {
        super(context);
        View v = LayoutInflater.from(context).inflate(R.layout.view_item_change,this);
        Chip chip = v.findViewById(R.id.change_chip);
            chip.setText(type);
        TextView textview = v.findViewById(R.id.change_text);
            textview.setText(text);
    }
}
