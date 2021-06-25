package org.kaizoku.otropelisplusmas.ui.reproductor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
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

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.StyledPlayerControlView;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;

import org.kaizoku.otropelisplusmas.databinding.FragmentReproductorBinding;
import org.kaizoku.otropelisplusmas.ui.home.HomeViewModel;

public class ReproductorFragment extends Fragment implements  StyledPlayerControlView.VisibilityListener{
    private static final String TAG = "DL1CS";
    private FragmentReproductorBinding binding;
    private HomeViewModel homeViewModel;
    private MediaItem mediaItem;
    private String url_video="";
    //private PowerManager.WakeLock wakeLock;


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
    private String TAGs ="asasd";
    //private SlideshowViewModel slideshowViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //slideshowViewModel = new ViewModelProvider(this).get(SlideshowViewModel.class);
        binding = FragmentReproductorBinding.inflate(inflater,container,false);
        //getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);



        hide();




        Bundle b=getArguments();
        if(b!=null){
            url_video=b.getString("url","");
        }


        playerView = binding.playerView;
        //playerView.setPlayer(player);
        playerView.setControllerVisibilityListener(this);
        //playerView.setErrorMessageProvider(new PlayerErrorMessageProvider());
        playerView.requestFocus();

        player = new SimpleExoPlayer.Builder(getContext()).build();
        playerView.setPlayer(player);


        //mediaItem = MediaItem.fromUri("https://ftp.mi.fu-berlin.de/pub/schiller/00_Telematics_Organizational_2015.mp4");
        //mediaItem = MediaItem.fromUri("https://fvs.io/redirector?token=TzVYU0VqMFlZVmNhQVIxMmw1RkJ4RDVFcU5xYjZDbW9ScnZqTEZ1aGlpYnlsYkVRMC8zclJBL1A3Tndya3p6UkIrenJ0Q1ppb1NnSmtlTURIMkh3ekplcjU4SEE4V0R2czJmaHlBZlV2QmU5MXFiK2Q3akg5eW9jSy9NRkE1eUllRkd6bGxxSEtxVndlazVTZzhDaW5LOUt2UEhMZVVrUmJ6a3o6VkFkV2p4K1VLa0tLQUVNem1BSFRJUT09");
        mediaItem = MediaItem.fromUri(url_video);

        // Build the media item.

        // Set the media item to be played.
        player.setMediaItem(mediaItem);
        // Prepare the player.
        player.prepare();
        // Start the playback.
        player.play();

        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        return binding.getRoot();
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
        Log.i(TAGs, "onStart: ");
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        //PowerManager pm = (PowerManager) getActivity().getSystemService(Context.POWER_SERVICE);
        //wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "My Tag:");
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Log.i(TAG, "onStart: orientacion: "+getActivity().getRequestedOrientation());
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAGs, "onResume: ");
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
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAGs, "onPause: ");



        //releasePlayer();
        pausePlayer();
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (getActivity() != null && getActivity().getWindow() != null) {
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

            // Clear the systemUiVisibility flag
            getActivity().getWindow().getDecorView().setSystemUiVisibility(0);
        }
        show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAGs, "onDestroy: ");
        //getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

        releasePlayer();
        //pausePlayer();
        //if (wakeLock != null) wakeLock.release();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

        //exitFullscreen();
        show();
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
    /*@Nullable
    private ActionBar getSupportActionBar() {

        return ((AppCompatActivity)getActivity()).getSupportActionBar();
    }*/

}