package org.kaizoku.otropelisplusmas.ui.reproductor;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
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
    private FragmentReproductorBinding binding;
    private HomeViewModel homeViewModel;
    private MediaItem mediaItem;
    private String url_video="";
    private PowerManager.WakeLock wakeLock;

    //private PlayerView exoPlayerView;
    private StyledPlayerView playerView;
    private SimpleExoPlayer player;
    //private SlideshowViewModel slideshowViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //slideshowViewModel = new ViewModelProvider(this).get(SlideshowViewModel.class);
        binding = FragmentReproductorBinding.inflate(inflater,container,false);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

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
        return binding.getRoot();
    }

    public void initVideoPlayer(String url, String type) {
        if (player != null) {
            player.release();
        }

        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();

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

    @Override
    public void onStart() {
        super.onStart();
        PowerManager pm = (PowerManager) getActivity().getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "My Tag:");
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);
        if (wakeLock != null) wakeLock.acquire(10*60*1000L /*10 minutes*/);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
        if (wakeLock != null) wakeLock.release();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public void onVisibilityChange(int visibility) {

    }

}