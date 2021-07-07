package org.kaizoku.otropelisplusmas.ui.home.video_cartel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.kaizoku.otropelisplusmas.model.CapituloCartel;

public class VideoCartelViewModel extends ViewModel {
    private MutableLiveData<CapituloCartel> videoCartel;
    public LiveData<CapituloCartel> getListVideoCartel(){
        if(videoCartel==null)
            videoCartel = new MutableLiveData<CapituloCartel>();
        return videoCartel;
    }
    public void setVideoCartel(CapituloCartel capituloCartel){
        this.videoCartel.setValue(capituloCartel);
    }
}
