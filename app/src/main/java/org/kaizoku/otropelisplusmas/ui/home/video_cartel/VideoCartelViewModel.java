package org.kaizoku.otropelisplusmas.ui.home.video_cartel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.kaizoku.otropelisplusmas.model.VideoCartel;
import org.kaizoku.otropelisplusmas.model.video_server.VideoServer;

import java.util.List;

public class VideoCartelViewModel extends ViewModel {
    private MutableLiveData<VideoCartel> videoCartel;
    public LiveData<VideoCartel> getListVideoCartel(){
        if(videoCartel==null)
            videoCartel = new MutableLiveData<VideoCartel>();
        return videoCartel;
    }
    public void setVideoCartel(VideoCartel videoCartel){
        this.videoCartel.setValue(videoCartel);
    }
}
