package org.kaizoku.otropelisplusmas.ui.home.video_cartel;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.squareup.picasso.Picasso;

import org.kaizoku.otropelisplusmas.R;
import org.kaizoku.otropelisplusmas.adapter.VideoServerAdapter;
import org.kaizoku.otropelisplusmas.databinding.FragmentVideoCartelBinding;
import org.kaizoku.otropelisplusmas.model.VideoCartel;
import org.kaizoku.otropelisplusmas.service.PelisplushdService;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class VideoCartelFragment extends Fragment implements VideoServerAdapter.OnCardListener{
    private FragmentVideoCartelBinding binding;
    private VideoServerAdapter videoServerAdapter;
    private PelisplushdService pelisplushdService;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentVideoCartelBinding.inflate(inflater,container,false);

        pelisplushdService = new PelisplushdService(null);
        intiAdapterServer();

        Bundle b = getArguments();
        if(b!=null) {
            String url = b.getString("url","");
            pelisplushdService.getSingleVideoCartel(url)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe((videoCartel, throwable) -> {
                        if(throwable==null&&videoCartel!=null){
                            setVideoCartel(videoCartel);
                            videoServerAdapter.setList(videoCartel.videoServerList);
                        }
                    });
        }

        return binding.getRoot();
    }

    private void intiAdapterServer() {
        binding.fragSerieRvServers.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        binding.fragSerieRvServers.setLayoutManager(linearLayoutManager);
        videoServerAdapter = new VideoServerAdapter(this);//ultimo error aqui cheaquearlo
        binding.fragSerieRvServers.setAdapter(videoServerAdapter);
    }

    private void setVideoCartel(VideoCartel videoCartel){
        //binding.fragSerieTitle.setText(videoCartel.name);
        getActivity().setTitle(videoCartel.name);
        binding.fragSerieSinopsis.setText(videoCartel.sinopsis);
        Picasso.get()
                .load(videoCartel.src_img)
                .into(binding.fragSerieSrc);
    }

    @Override
    public void onClickCard(String file_url,byte option) {
        switch (option) {
            case VideoServerAdapter.OPTION_PLAY:
                Bundle b=new Bundle();
                b.putString("url",file_url);
                NavHostFragment.findNavController(this)
                        .navigate(R.id.action_videoCartelFragment_to_nav_reproductor,b);
                break;
            case VideoServerAdapter.OPTION_EXT:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(file_url), "video/*");
                startActivity(Intent.createChooser(intent, "PLay video..."));
                break;
        }
    }
}
