package org.kaizoku.otropelisplusmas.view_group;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.chip.Chip;

import org.kaizoku.otropelisplusmas.R;
import org.kaizoku.otropelisplusmas.model.ItemChangelog;

public class ItemChangeLayout extends LinearLayout {
    public ItemChangeLayout(Context context, byte type, String text) {
        super(context);
        View v = LayoutInflater.from(context).inflate(R.layout.view_item_change,this);
        Chip chip = v.findViewById(R.id.change_chip);
            String tipo = ItemChangelog.getType(type);
            chip.setText(tipo);
            Log.i("TAG", "ItemChangeLayout: type: "+type);
            switch (type){
                case 3: chip.setChipBackgroundColor(ColorStateList.valueOf(getResources().getColor(R.color.verde))); break;
                case 2: chip.setChipBackgroundColor(ColorStateList.valueOf(getResources().getColor(R.color.amarillo))); break;
                case 1: chip.setChipBackgroundColor(ColorStateList.valueOf(getResources().getColor(R.color.red))); break;
            }
        TextView textview = v.findViewById(R.id.change_text);
            textview.setText(text);
    }
}
