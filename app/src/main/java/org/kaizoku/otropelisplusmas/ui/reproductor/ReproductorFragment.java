package org.kaizoku.otropelisplusmas.ui.reproductor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.MediaMetadata;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.StyledPlayerControlView;
import com.google.android.exoplayer2.ui.StyledPlayerView;

import org.jetbrains.annotations.NotNull;
import org.kaizoku.otropelisplusmas.database.entity.CapituloEnt;
import org.kaizoku.otropelisplusmas.database.entity.SerieEnt;
import org.kaizoku.otropelisplusmas.database.viewmodel.CapituloViewModel;
import org.kaizoku.otropelisplusmas.databinding.FragmentReproductorBinding;
import org.kaizoku.otropelisplusmas.model.Chapter;
import org.kaizoku.otropelisplusmas.model.Season;
import org.kaizoku.otropelisplusmas.model.video_server.FembedServer;
import org.kaizoku.otropelisplusmas.service.PelisplushdService;

import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class ReproductorFragment extends Fragment implements  StyledPlayerControlView.VisibilityListener{
    private static final String TAG = "DL1CS";
    private FragmentReproductorBinding binding;


    //private PowerManager.WakeLock wakeLock;
    private PelisplushdService pelisplushdService;

    private CapituloViewModel capituloViewModel;
    private MediaItem mediaItem;
    private SerieEnt serie;
    private CapituloEnt capitulo;
    //private String url_video="";
    // Controls de season & chapter
    //private List<Season> seasonList=new ArrayList<>();
    //private int seasonPos;
    //private int chapterPos;
    private boolean isSeekReplace=true;

    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            int flags = View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;

            Activity activity = getActivity();
            if (activity != null
                    && activity.getWindow() != null) {
                activity.getWindow().getDecorView().setSystemUiVisibility(flags);
            }
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.hide();
            }

        }
    };
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            //mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };


    //private PlayerView exoPlayerView;
    private StyledPlayerView playerView;
    private SimpleExoPlayer player;



    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView: ");
        Log.i(TAG, "onCreateView: getConfiguration orientation : "+getResources().getConfiguration().orientation);
        binding = FragmentReproductorBinding.inflate(inflater,container,false);

        //if(getActivity().getRequestedOrientation()==ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
        if(getResources().getConfiguration().orientation==ActivityInfo.SCREEN_ORIENTATION_USER){
            hide();

            pelisplushdService = new PelisplushdService();
            capituloViewModel=new ViewModelProvider(this).get(CapituloViewModel.class);

            loadArguments();

            playerView = binding.playerView;
            //playerView.setPlayer(player);
            playerView.setControllerVisibilityListener(this);
            //playerView.setErrorMessageProvider(new PlayerErrorMessageProvider());
            playerView.requestFocus();

            /*ImageButton n = binding.getRoot().findViewById(R.id.exo_next);
            n.setOnClickListener(v -> {
                Log.i(TAG, "onCreateView: otro siguiente");
                Toast.makeText(getContext(), "Siguiente", Toast.LENGTH_SHORT).show();
            });*/
            player = new SimpleExoPlayer
                    .Builder(getContext())
                    .build();
            player.addListener(new Player.EventListener() {
                @Override
                public void onPlayWhenReadyChanged(boolean playWhenReady, int reason) {
                    switch (reason){
                        case Player.PLAY_WHEN_READY_CHANGE_REASON_USER_REQUEST:Log.i(TAG, "onPlayWhenReadyChanged: user request");break;
                        case Player.PLAY_WHEN_READY_CHANGE_REASON_AUDIO_FOCUS_LOSS:Log.i(TAG, "onPlayWhenReadyChanged: audio focus loss");break;
                        case Player.PLAY_WHEN_READY_CHANGE_REASON_AUDIO_BECOMING_NOISY:Log.i(TAG, "onPlayWhenReadyChanged: audio becoming noise");break;
                        case Player.PLAY_WHEN_READY_CHANGE_REASON_REMOTE:Log.i(TAG, "onPlayWhenReadyChanged: remote");break;
                        case Player.PLAY_WHEN_READY_CHANGE_REASON_END_OF_MEDIA_ITEM:Log.i(TAG, "onPlayWhenReadyChanged: end of media item");break;
                    }
                }

                @Override
                public void onIsPlayingChanged(boolean isPlaying) {
                    if(isPlaying)
                        Log.i(TAG, "onIsPlayingChanged: le dio play");
                    else {
                        Log.i(TAG, "onIsPlayingChanged: le dio pause");
                        updateSerieProgress();
                    }
                }

                @Override
                public void onPlaybackStateChanged(int state) {
                    switch (state){
                        case Player.STATE_IDLE:Log.i(TAG, "onPlaybackStateChanged: idle");break;
                        case Player.STATE_BUFFERING:Log.i(TAG, "onPlaybackStateChanged: buffering");break;
                        case Player.STATE_READY:Log.i(TAG, "onPlaybackStateChanged: ready");break;
                        case Player.STATE_ENDED:Log.i(TAG, "onPlaybackStateChanged: ended");break;
                    }
                }

                @Override
                public void onMediaItemTransition(@Nullable @org.jetbrains.annotations.Nullable MediaItem mediaItem, int reason) {
                    switch (reason){
                        case Player.MEDIA_ITEM_TRANSITION_REASON_REPEAT:Log.i(TAG, "onMediaItemTransition: repeat");break;
                        case Player.MEDIA_ITEM_TRANSITION_REASON_AUTO:Log.i(TAG, "onMediaItemTransition: auto");
                            playerLoadNextVideo();
                            break;
                        case Player.MEDIA_ITEM_TRANSITION_REASON_SEEK:Log.i(TAG, "onMediaItemTransition: seek");
                            //printPlaylist();
                                    //playFromPlaylistTest(mediaItem);
                            //Log.i(TAG, "onMediaItemTransition: getPlaybackState"+player.getPlaybackState()+" - getPlayWhenReady"+player.getPlayWhenReady());
                            Log.i(TAG, "onMediaItemTransition: isSeekReplace: "+isSeekReplace);
                            if(isSeekReplace) playerLoadNextVideo();
                            isSeekReplace=true;
                            //printPlaylist();
                            break;
                        case Player.MEDIA_ITEM_TRANSITION_REASON_PLAYLIST_CHANGED:Log.i(TAG, "onMediaItemTransition: playlist changed");break;
                    }
                }

            });
            playerView.setPlayer(player);

            //MediaMetadata md = new MediaMetadata.Builder().setTitle("algo title").build();

            //mediaItem = MediaItem.fromUri("https://ftp.mi.fu-berlin.de/pub/schiller/00_Telematics_Organizational_2015.mp4");
            //mediaItem = MediaItem.fromUri("https://fvs.io/redirector?token=TzVYU0VqMFlZVmNhQVIxMmw1RkJ4RDVFcU5xYjZDbW9ScnZqTEZ1aGlpYnlsYkVRMC8zclJBL1A3Tndya3p6UkIrenJ0Q1ppb1NnSmtlTURIMkh3ekplcjU4SEE4V0R2czJmaHlBZlV2QmU5MXFiK2Q3akg5eW9jSy9NRkE1eUllRkd6bGxxSEtxVndlazVTZzhDaW5LOUt2UEhMZVVrUmJ6a3o6VkFkV2p4K1VLa0tLQUVNem1BSFRJUT09");

            //si es lista de reproduccion
            if(serie!=null && serie.isPlaylist()){
                setPlayerList();
                printPlaylist();
            }
            //si es un solo video
            else{
                // Build the media item.
                //mediaItem = MediaItem.fromUri(url_video);
                Log.i(TAG, "onCreateView: serie: "+serie);
                //mediaItem = MediaItem.fromUri(capitulo.href);
                mediaItem = MediaItem.fromUri(capitulo.file_url);
                //mediaItem = new MediaItem.Builder().setUri(url_video).setMediaMetadata(md).build();

                // Set the media item to be played.
                player.setMediaItem(mediaItem);
                /*player.setMediaItem(MediaItem.fromUri("https://archive.org/download/amv-7/Amv7.mp4"));//movies Amv 7
                player.addMediaItem(MediaItem.fromUri("https://archive.org/download/twitter-1351192681494573058/1351192681494573058.ia.mp4"));//Anime Pics & Gifs ツ - ^-^
                player.addMediaItem(MediaItem.fromUri("https://archive.org/download/capitulo-1-tokyo-revengers-latino-wiloc-anime-app/Capitulo%201%20-%20Tokyo%20Revengers%20Latino%20-%20Wiloc%20Anime%20App.ia.mp4"));//Capitulo 1 Tokyo Revengers Latino Wiloc Anime App
                player.addMediaItem(MediaItem.fromUri("https://archive.org/download/capitulo-22-wiloc-anime-jujutsu-kaisen-latino/Cap%C3%ADtulo%2022%20-%20Wiloc%20Anime%20-%20Jujutsu%20Kaisen%20Latino.ia.mp4"));//Capítulo 22 Wiloc Anime Jujutsu Kaisen Latino
                player.addMediaItem(MediaItem.fromUri("https://archive.org/download/anime-kcd-boku-no-hero-08/%5BAnimeKCD%5D%20Boku%20no%20Hero%2008.mp4"));//[ Anime KCD] Boku No Hero 08
                player.addMediaItem(MediaItem.fromUri("https://archive.org/download/twitter-1351186954864488456/1351186954864488456.mp4"));//Anime SauceBot - @PinkyeBoy @itanimeirl @PinkyeBoy I found this in the Anime database!  𝗧𝗶𝘁𝗹𝗲: Keijo!!!!!!!! 𝗘𝗽𝗶𝘀𝗼𝗱𝗲: 05 ( ⏱️ 00:18:02 / 00:23:42 ) 𝗔𝗰𝗰𝘂𝗿𝗮𝗰𝘆: 73.35% ( 🟡 Medium )
                player.addMediaItem(MediaItem.fromUri("https://archive.org/download/capitulo-2-slime-taoshite-latino-wiloc-anime-app/Capitulo%202%20-%20Tokyo%20Revengers%20Latino%20-%20Wiloc%20Anime%20App.ia.mp4"));//Capitulo 2 Slime Taoshite Latino Wiloc Anime App- revenge
                player.addMediaItem(MediaItem.fromUri("https://archive.org/download/capitulo-24-kimetsu-no-yaiba-latino-wiloc-anime/Capitulo%2023%20-%20Kimetsu%20no%20Yaiba%20Latino%20-%20Wiloc%20Anime.ia.mp4"));//Capitulo 24 Kimetsu No Yaiba Latino Wiloc Anime
                player.addMediaItem(MediaItem.fromUri("https://archive.org/download/Anime_Abandon_Teknoman-x0BpVvXTwlk/Anime_Abandon_Teknoman-x0BpVvXTwlk.mp4"));//Anime Abandon   Teknoman
                //player.addMediaItem(MediaItem.fromUri("https://archive.org/download/parasyte-ep-7-anime-balkan/Parasyte%20EP7%20%28AnimeBalkan%29.mp4"));//Parasyte: The Maxim EP 7 ( Anime Balkan)
                //player.addMediaItem(MediaItem.fromUri(""));
                //player.addMediaItem(new MediaItem.Builder().setUri("url3").setMediaId("mediaId").setMediaMetadata(md).build());
                */
                // Prepare the player.
                player.prepare();
                if(capitulo.progress>0)
                    player.seekTo(capitulo.progress);
                // Start the playback.
                player.play();
            }
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        return binding.getRoot();
    }

    private void updateSerieProgress() {
        Log.i(TAG, "updateSerieProgress: ");
        if(player!=null&&capituloViewModel!=null) {
            SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
                editor.putLong("progress", player.getCurrentPosition());
                editor.putLong("capitulo_id", capitulo.id);
                editor.commit();

            //capitulo.progress = player.getCurrentPosition();
            //capituloViewModel.updateCapitulo(capitulo).subscribe();
        }
    }

    private void loadArguments(){
        Bundle b=getArguments();
        if(b!=null){
            serie=b.getParcelable("serie");
            capitulo=b.getParcelable("capitulo");

            Log.i(TAG, "loadArguments: serie: "+serie==null?"null":serie+" capitulo: "+capitulo==null?"null":capitulo.toString());
            /*
            url_video=b.getString("url","");
            seasonList=b.getParcelableArrayList("season_list");
            seasonPos=b.getInt("season_pos",-1);
            chapterPos=b.getInt("chapter_pos",-1);
            if(seasonList!=null)
                Log.i(TAG, "onCreateView: sp: "+seasonPos+" cp: "+chapterPos+" size: "+seasonList.size());
            */
        }
    }

    private void playerLoadNextVideo() {
        Log.i(TAG, "playerControlFlow: ");
        //Log.i(TAG, "playerControlFlow: pause");
        //player.pause();
        //obteniendo url del nuevo media
        String url = player.getCurrentMediaItem().mediaMetadata.title;
        pelisplushdService.getSingleVideoCartel(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<CapituloEnt, SingleSource<CapituloEnt>>() {
                    @Override
                    public SingleSource<CapituloEnt> apply(@NotNull CapituloEnt capituloEnt) throws Exception {
                        Log.i(TAG, "apply: 1 capituloEnt desde web service");
                        capitulo=capituloEnt;
                        return capituloViewModel.getCapitulo(capituloEnt.href);
                    }
                }).flatMap(new Function<CapituloEnt, SingleSource<Long>>() {
                    @Override
                    public SingleSource<Long> apply(@NotNull CapituloEnt capituloEnt) throws Exception {
                        Log.i(TAG, "apply: 2 capituloEnt desde la db");
                        capitulo.id=capituloEnt.id;
                        return Single.just(capitulo.id);
                    }
                })
                .onErrorResumeNext(new Function<Throwable, SingleSource<Long>>() {
                    @Override
                    public SingleSource<Long> apply(@NotNull Throwable throwable) throws Exception {
                        Log.e(TAG, "apply: 3 error no esta en la db", throwable);
                        return capituloViewModel.insertCapitolo(capitulo);
                    }
                }).flatMap(new Function<Long, SingleSource<?>>() {
                    @Override
                    public SingleSource<?> apply(@NotNull Long id) throws Exception {
                        Log.i(TAG, "apply: 4");
                        capitulo.id=id;
                        capitulo.visto=true;
                        return capituloViewModel.updateCapitulo(capitulo);
                    }
                })
                .subscribe((integer, throwable) -> {
                    if(throwable==null){
                        if(capitulo.videoServerList.get(0) instanceof FembedServer) {
                            FembedServer fserver = (FembedServer) capitulo.videoServerList.get(0);//el cero no es baner, es el 4
                            String url_video = fserver.options.get(0).file;
                            //MediaMetadata mediaMetadata = new MediaMetadata.Builder().setTitle(player.getCurrentMediaItem().mediaMetadata.title).build();
                            MediaItem mediaItem = new MediaItem.Builder()
                                    .setUri(url_video)
                                    .setMediaMetadata(player.getCurrentMediaItem().mediaMetadata)
                                    .build();
                            player.addMediaItem(player.getCurrentWindowIndex()+1,mediaItem);
                            player.removeMediaItem(player.getCurrentWindowIndex());
                            player.prepare();
                            if(capitulo.progress>0)
                                player.seekTo(capitulo.progress);
                            player.play();
                        }
                    }else Log.e(TAG, "playerControlFlow: ", throwable);
                    printPlaylist();
                });
    }

    /**
     * Imprime el playlist player.getMediaItemAt
     */
    private void printPlaylist() {
        String lista="{\n";
        int l = player.getMediaItemCount();
        for (int i = 0; i < l; i++) {
            MediaItem mi = player.getMediaItemAt(i);
            lista+="pos:"+i+",\ntitle: "+mi.mediaMetadata.title+",\nUri: "+mi.playbackProperties.uri+",\n";
        }
        lista+="index: "+player.getCurrentWindowIndex()+" }";
        Log.i(TAG, "printPlaylist: salida: "+lista);
    }

    private void setPlayerList(){
        Log.i(TAG, "setPlayerList: start");
        List<Season> seasonList = serie.seasonList;
        //if(seasonList!=null && seasonPos>=0 && chapterPos>=0){
            int size=seasonList.size();
            for (int i = 0; i < size; i++) {
                List<Chapter> chapters=seasonList.get(i).chapterList;
                int lenght=chapters.size();
                for (int j = 0; j < lenght; j++) {
                    if(chapters.get(j).type==Chapter.TYPE_CHAPTER) {
                        if(serie.seasonPos==i && serie.chapterPos==j) {
                            MediaMetadata mdx = new MediaMetadata.Builder().setTitle(chapters.get(j).href).build();
                            MediaItem mediaix = new MediaItem.Builder()
                                    //.setUri(url_video)
                                    .setUri(capitulo.file_url)
                                    .setMediaMetadata(mdx)
                                    .build();
                            player.addMediaItem(mediaix);

                            player.prepare();
                            isSeekReplace=false;
                            player.seekTo(player.getMediaItemCount()-1, 0);
                            player.play();
                        }else{
                            MediaMetadata md = new MediaMetadata.Builder().setTitle(chapters.get(j).href).build();
                            MediaItem mediai = new MediaItem.Builder()
                                    .setUri("url")
                                    .setMediaMetadata(md)
                                    .build();
                            player.addMediaItem(mediai);
                        }
                    }
                }
            }
        //}
        Log.i(TAG, "setPlayerList: end");
    }

    private void playFromPlaylistTest(MediaItem mediaItem){
        Log.i(TAG, "playFromPlaylist: pause");
        player.pause();
        Log.i(TAG, "playFromPlaylist: reemplazando elemento de la lista");

        MediaMetadata mdx = new MediaMetadata.Builder().setTitle("Parasyte: The Maxim EP 7 ( Anime Balkan)").build();
        //Parasyte: The Maxim EP 7 ( Anime Balkan)
        //MediaItem.fromUri("https://archive.org/download/parasyte-ep-7-anime-balkan/Parasyte%20EP7%20%28AnimeBalkan%29.mp4");
        //MediaItem m = new MediaItem.Builder().setUri("https://archive.org/download/parasyte-ep-7-anime-balkan/Parasyte%20EP7%20%28AnimeBalkan%29.mp4").setMediaId("mediaId").setMediaMetadata(mdx).build();

        MediaItem mediaix = new MediaItem.Builder()
                .setUri("https://archive.org/download/parasyte-ep-7-anime-balkan/Parasyte%20EP7%20%28AnimeBalkan%29.mp4")
                .setMediaMetadata(mdx)
                .build();
        player.addMediaItem(player.getCurrentWindowIndex()+1,mediaix);
        player.removeMediaItem(player.getCurrentWindowIndex());
        //player.prepare();
        //player.seekTo(player.getPreviousWindowIndex(),0);
        //player.play();


        /*
        pelisplushdService.getSingleVideoCartel(mediaItem.mediaMetadata.title)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((videoCartel, throwable) -> {
                    if(throwable==null){
                        MediaMetadata mdx = new MediaMetadata.Builder().setTitle(videoCartel.videoServerLishref).build();
                        MediaItem mediaix = new MediaItem.Builder()
                                .setUri(url_video)
                                .setMediaMetadata(mdx)
                                .build();
                        player.addMediaItem(mediaix);

                        player.addMediaItem();
                    }else{
                        Log.e(TAG, "playFromPlaylist: error al leer url", throwable);
                    }
                });
        */
    }

    public void initVideoPlayer(String url, String type) {
        if (player != null) {
            player.release();
        }

        //BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();

        //player = ExoPlayerFactory.newSimpleInstance((Context) PlayerActivity.this, trackSelector);
        binding.playerView.setPlayer(player);
        // below 2 lines will make screen size to fit
        binding.playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
        player.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);

        player.setPlayWhenReady(true);

        Uri uri = Uri.parse(url);

    }

    private void releasePlayer() {
        if (player != null) {
            player.setPlayWhenReady(false);
            player.stop();
            player.release();
            player = null;
            playerView.setPlayer(null);
        }
    }

    private void pausePlayer() {
        if (player != null) {
            player.setPlayWhenReady(false);
            player.stop();
            //player.release();
            //player = null;
            //playerView.setPlayer(null);
        }
    }



    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "onStart: ");
        //if(getActivity().getRequestedOrientation()==ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
        if(getResources().getConfiguration().orientation==ActivityInfo.SCREEN_ORIENTATION_USER){
            //PowerManager pm = (PowerManager) getActivity().getSystemService(Context.POWER_SERVICE);
            //wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "My Tag:");
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: ");
        //if(getActivity().getRequestedOrientation()==ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
        if(getResources().getConfiguration().orientation==ActivityInfo.SCREEN_ORIENTATION_USER){
            //getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

            getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            //if (wakeLock != null) wakeLock.acquire(10*60*1000L /*10 minutes*/);


            if (getActivity() != null && getActivity().getWindow() != null) {
                getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            }

            // Trigger the initial hide() shortly after the activity has been
            // created, to briefly hint to the user that UI controls
            // are available.
            delayedHide(100);
            //Log.i(TAG, "onStart: onResume1: "+getActivity().getRequestedOrientation());
        }else {
            Log.i(TAG, "onResume: cambiando orientacion");
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            //Log.i(TAG, "onStart: onResume2: "+getActivity().getRequestedOrientation());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "onPause: ");

        //if(getActivity().getRequestedOrientation()==ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            //releasePlayer();
            pausePlayer();
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            if (getActivity() != null && getActivity().getWindow() != null) {
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

                // Clear the systemUiVisibility flag
                getActivity().getWindow().getDecorView().setSystemUiVisibility(0);
            }
            show();
        //}
    }

    @Override
    public void onStop() {
        Log.i(TAG, "onStop: ");
        updateSerieProgress();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: ");
        //if(getActivity().getRequestedOrientation()==ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            //getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

            releasePlayer();
            //pausePlayer();
            //if (wakeLock != null) wakeLock.release();
            //getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

            //exitFullscreen();
            show();
        //}
    }

    private void exitFullscreen(){
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        getActivity().getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    @Override
    public void onVisibilityChange(int visibility) {

    }

    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        //mControlsView.setVisibility(View.GONE);

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        //mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.show();
        }
    }

    void hidex(){
        /*window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        or View.SYSTEM_UI_FLAG_FULLSCREEN)*/
        /*
        WindowManager.LayoutParams attrs = getActivity().getWindow().getAttributes();
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        getActivity().getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        */

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        int flags = View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;

        Activity activity = getActivity();
        if (activity != null && activity.getWindow() != null)
            activity.getWindow().getDecorView().setSystemUiVisibility(flags);

        /*//ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }*/
    }

    private void showx(){


        // Show the system bar
        //mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);


        // Schedule a runnable to display UI elements after a delay

            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            //mControlsView.setVisibility(View.VISIBLE);

        /*ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.show();
        }*/
    }

    @Nullable
    private ActionBar getSupportActionBar() {
        ActionBar actionBar = null;
        if (getActivity() instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            actionBar = activity.getSupportActionBar();
        }
        return actionBar;
    }

    /*private String getNextChapterUrl(){
        if(seasonList!=null) {
            int size = seasonList.size();
            for (int i = 0; i < size; i++) {
                List<Chapter> chapterList = seasonList.get(seasonPos).chapterList;
                int length = chapterList.size();
                for (int j = 0; j > length; j++) {

                }
            }
        }
        return "";
    }*/

}