package org.kaizoku.otropelisplusmas.model.video_server;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FembedServer extends VideoServer implements Parcelable {
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

    public class FembedOptionItem implements Parcelable{
        public String file;
        public String label;
        public String type;

        public FembedOptionItem(String file, String label, String type) {
            this.file = file;
            this.label = label;
            this.type = type;
        }

        protected FembedOptionItem(Parcel in) {
            file = in.readString();
            label = in.readString();
            type = in.readString();
        }

        public final Creator<FembedOptionItem> CREATOR = new Creator<FembedOptionItem>() {
            @Override
            public FembedOptionItem createFromParcel(Parcel in) {
                return new FembedOptionItem(in);
            }

            @Override
            public FembedOptionItem[] newArray(int size) {
                return new FembedOptionItem[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(file);
            dest.writeString(label);
            dest.writeString(type);
        }
    }

    protected FembedServer(Parcel in) {
        super(in);
        options = in.readArrayList(FembedOptionItem.class.getClassLoader());
    }

    public static final Creator<FembedServer> CREATOR = new Creator<FembedServer>() {
        @Override
        public FembedServer createFromParcel(Parcel in) {
            return new FembedServer(in);
        }

        @Override
        public FembedServer[] newArray(int size) {
            return new FembedServer[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeList(options);
    }

}
