package org.kaizoku.otropelisplusmas.view_group;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.kaizoku.otropelisplusmas.R;

public class FembedOptionLayout extends LinearLayout {
    TextView name,play,external;
    public FembedOptionLayout(Context context, String name, OnClickListener onClickListenerPlay, OnClickListener onClickListenerExt) {
        super(context);
        View v = LayoutInflater.from(context).inflate(R.layout.view_fembed_option_layout,this);
        this.name = v.findViewById(R.id.fembed_option_tv_name);
        this.name.setText(name);
        this.play = v.findViewById(R.id.fembed_option_tv_play);
        this.play.setOnClickListener(onClickListenerPlay);
        this.external = v.findViewById(R.id.fembed_option_tv_external);
        this.external.setOnClickListener(onClickListenerExt);
    }
}
