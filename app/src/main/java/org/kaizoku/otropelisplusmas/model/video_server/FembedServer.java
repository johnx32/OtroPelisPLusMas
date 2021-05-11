package org.kaizoku.otropelisplusmas.model.video_server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FembedServer extends VideoServer{
    public List<FembedOptionItem> options=new ArrayList<>();

    public FembedServer(String nameServer, JSONObject jsonObject) {
        super(nameServer);
        try {
            JSONArray lista=jsonObject.getJSONArray("data");
            for (int i=0;i<lista.length();i++){
                options.add(new FembedOptionItem(
                        lista.getJSONObject(i).getString("file"),
                        lista.getJSONObject(i).getString("label"),
                        lista.getJSONObject(i).getString("type")
                ));
            }
        } catch (JSONException e) {e.printStackTrace();}
    }

    public class FembedOptionItem {
        public String file;
        public String label;
        public String type;

        public FembedOptionItem(String file, String label, String type) {
            this.file = file;
            this.label = label;
            this.type = type;
        }
    }
}
