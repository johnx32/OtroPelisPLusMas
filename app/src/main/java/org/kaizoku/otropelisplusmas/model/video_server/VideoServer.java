package org.kaizoku.otropelisplusmas.model.video_server;

import android.os.Parcel;
import android.os.Parcelable;

public class VideoServer implements Parcelable {
    public static final byte SERVER_FEMBED=1;
    public String nameServer;

    public VideoServer(String nameServer) {
        this.nameServer = nameServer;
    }

    protected VideoServer(Parcel in) {
        nameServer = in.readString();
    }

    public static final Creator<VideoServer> CREATOR = new Creator<VideoServer>() {
        @Override
        public VideoServer createFromParcel(Parcel in) {
            return new VideoServer(in);
        }

        @Override
        public VideoServer[] newArray(int size) {
            return new VideoServer[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nameServer);
    }
}
