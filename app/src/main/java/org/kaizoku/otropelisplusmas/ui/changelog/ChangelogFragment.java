package org.kaizoku.otropelisplusmas.ui.changelog;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.kaizoku.otropelisplusmas.R;
import org.kaizoku.otropelisplusmas.adapter.ItemChangelogAdapter;
import org.kaizoku.otropelisplusmas.databinding.FragmentChangelogBinding;
import org.kaizoku.otropelisplusmas.model.Change;
import org.kaizoku.otropelisplusmas.model.ItemChangelog;

import java.util.ArrayList;
import java.util.List;

public class ChangelogFragment extends Fragment {
    private static final String TAG = "TAG";
    FragmentChangelogBinding binding;
    ItemChangelogAdapter adapter;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentChangelogBinding.inflate(inflater,container,false);
        initAdapter();
        Thread h = new Thread(() -> {
                Log.i(TAG, "onCreateView: log: "+getChangelog());

                try {
                    List<ItemChangelog> list = new ArrayList<>();
                    JSONObject jo = new JSONObject(getChangelog());
                    JSONArray releases = jo.getJSONArray("releases");
                    //Log.i("TAG", "onCreateView: releases: "+releases);
                    int size = releases.length();
                    for(int i=0;i<size;i++){
                        ItemChangelog item = new ItemChangelog();
                        JSONObject jobject = releases.getJSONObject(i);
                        //Log.i("TAG", "onCreateView: jo: "+jobject);
                        item.code = jobject.getInt("code");
                        item.name = jobject.getString("version");
                        JSONArray joChanges = jobject.getJSONArray("changes");
                        int sizeChanges = joChanges.length();
                        for (int j=0;j<sizeChanges;j++){
                            Change cambio = new Change();
                            cambio.type = (byte)joChanges.getJSONObject(j).getInt("type");
                            cambio.text = joChanges.getJSONObject(j).getString("text");
                            item.changes.add(cambio);
                        }
                        list.add(item);
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.setList(list);
                        }
                    });
                }catch (Exception e){e.printStackTrace();}
            }
        );
        h.start();
        return binding.getRoot();
    }

    private void initAdapter() {
        binding.recyclerview.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        binding.recyclerview.setLayoutManager(linearLayoutManager);
        adapter = new ItemChangelogAdapter();
        binding.recyclerview.setAdapter(adapter);
    }

    private String getChangelog(){
        String url = getContext().getResources().getString(R.string.url_changelog);
        String urlJson = url;
        String json="";
        try {
            Document doc = Jsoup.connect(urlJson)
                    .timeout(12000)
                    .get();
            json = doc.body().text();
        }catch (Exception e){e.printStackTrace();}
        return json;
    }
}
