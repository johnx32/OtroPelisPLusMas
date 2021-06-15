package org.kaizoku.otropelisplusmas.ui.home.video_cartel;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.ads.AdSize;
import com.google.android.material.snackbar.Snackbar;
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
                            //agregar webview aqui

                            Log.i("TAG", "onCreateView: url disqus: "+videoCartel.url_disqus);

                            if(videoCartel.url_disqus!=null) {
                                // use cookies to remember a logged in status
                                //CookieSyncManager.createInstance(this);
                                //CookieSyncManager.getInstance().startSync();

                                binding.webview.setVerticalScrollBarEnabled(true);
                                binding.webview.requestFocus();
                                binding.webview.getSettings().setJavaScriptEnabled(true);
                                binding.webview.setWebViewClient(new WebViewClient());
                                binding.webview.loadUrl(videoCartel.url_disqus);

                                    //If you are using Android Lollipop i.e. SDK 21, then:
                                //CookieManager.getInstance().setAcceptCookie(true);
                                    //won't work. You need to use:
                                CookieManager.getInstance().setAcceptThirdPartyCookies(binding.webview, true);

                                binding.refresh.setOnClickListener(v -> {
                                    binding.webview.loadUrl(videoCartel.url_disqus);
                                });

                            }else{
                                //binding.webview.setVisibility(View.GONE);
                                Log.i("TAG", "onCreateView: info");
                            }
                            //binding.webview.loadUrl("https://disqus.com/embed/comments/?base=default&amp;f=https-animeflv-net&amp;t_i=episode_58508&amp;t_u=https%3A%2F%2Fwww3.animeflv.net%2Fver%2Fseijo-no-maryoku-wa-bannou-desu-10&amp;t_d=Seijo%20no%20Maryoku%20wa%20Bannou%20Desu%20Episodio%2010&amp;t_t=Seijo%20no%20Maryoku%20wa%20Bannou%20Desu%20Episodio%2010&amp;s_o=default#version=a5921af07b365f6dfd62075d2dee3735");
                        }
                    });
        }

        return binding.getRoot();
    }

    private void intiAdapterServer() {
        binding.fragSerieRvServers.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        binding.fragSerieRvServers.setLayoutManager(linearLayoutManager);
        videoServerAdapter = new VideoServerAdapter(this,getAdSize());//ultimo error aqui cheaquearlo
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

    private AdSize getAdSize() {
        // Step 2 - Determine the screen width (less decorations) to use for the ad width.
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;

        int adWidth = (int) (widthPixels / density);

        // Step 3 - Get adaptive ad size and return for setting on the ad view.
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(getContext(), adWidth);
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
                startActivity(Intent.createChooser(intent, "Play Video"));
                break;
            case VideoServerAdapter.OPTION_CAST:
                Snackbar.make(getView(),"Proximamente",Snackbar.LENGTH_LONG).show();
                break;
        }
    }
}
