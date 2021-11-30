package org.kaizoku.otropelisplusmas.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.kaizoku.otropelisplusmas.model.FullPage;

public class HomeViewModel extends ViewModel {
    private MutableLiveData<FullPage> listaFullpage;
    public LiveData<FullPage> getListaFullpage(){
        if(listaFullpage==null){
            listaFullpage = new MutableLiveData<>();
        }
        return listaFullpage;
    }
    public void setListaFullpage(FullPage fullpage){
        listaFullpage.setValue(fullpage);
    }
}