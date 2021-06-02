package org.kaizoku.otropelisplusmas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListenerV1;
import com.thin.downloadmanager.ThinDownloadManager;

import org.kaizoku.otropelisplusmas.databinding.ActivityUpdateBinding;

import java.io.File;

public class UpdateActivity extends AppCompatActivity {
    private ActivityUpdateBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.updateCard.setVisibility(View.VISIBLE);
        binding.downloadBtn.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE, FileProvider.getUriForFile(this, "knf.kuma.fileprovider", getUpdate()))
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                        .putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, false)
                        .putExtra(Intent.EXTRA_INSTALLER_PACKAGE_NAME, getPackageName());
                startActivity(intent);
            } else {
                Intent intent = new Intent(Intent.ACTION_VIEW)
                        .setDataAndType(Uri.fromFile(getUpdate()), "application/vnd.android.package-archive")
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                startActivity(intent);
            }
            finish();
        });
        start();
    }

    private void start() {
        File file = getUpdate();
        if (file.exists())
            file.delete();
        new ThinDownloadManager().add(new DownloadRequest(Uri.parse(getResources().getString(R.string.apk_url)))
                .setDestinationURI(Uri.fromFile(file))
                .setDownloadResumable(false)
                .setStatusListener(new DownloadStatusListenerV1() {
                    @Override
                    public void onDownloadComplete(DownloadRequest downloadRequest) {
                        prepareForIntall();
                    }

                    @Override
                    public void onDownloadFailed(DownloadRequest downloadRequest, int errorCode, String errorMessage) {
                        Log.e("Update Error", "Code: " + errorCode + " Message: " + errorMessage);
                        Toast.makeText(UpdateActivity.this,"Error al actualizar: " + errorMessage,Toast.LENGTH_LONG).show();
                        //Crashlytics.logException(new IllegalStateException("Update failed\nCode: " + errorCode + " Message: " + errorMessage));
                        finish();
                    }

                    @Override
                    public void onProgress(DownloadRequest downloadRequest, long totalBytes, long downloadedBytes, int progress) {
                        setDownloadProgress(progress);
                    }
                }));
    }

    private File getUpdate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            return new File(getFilesDir(), "update.apk");
        else
            return new File(getDownloadsDir(), "update.apk");
    }

    private File getDownloadsDir() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    }

    private void prepareForIntall() {
        setDownloadProgress(100);
        /*final Animation fadein = AnimationUtils.loadAnimation(this, R.anim.fadein);
        fadein.setDuration(1000);
        final Animation fadeout = AnimationUtils.loadAnimation(this, R.anim.fadeout);
        fadeout.setDuration(1000);*/
        binding.progressBarTv.post(new Runnable() {
            @Override
            public void run() {
                binding.progressBarTv.setVisibility(View.INVISIBLE);
                //binding.progressBarTv.startAnimation(fadeout);
            }
        });
        binding.downloadBtn.post(new Runnable() {
            @Override
            public void run() {
                binding.downloadBtn.setVisibility(View.VISIBLE);
                //binding.downloadBtn.startAnimation(fadein);
            }
        });
    }

    private void setDownloadProgress(final int progress) {
        runOnUiThread(new Runnable() {
            //@SuppressLint("SetTextI18n")
            @Override
            public void run() {
                binding.progressBar.setIndeterminate(false);
                binding.progressBar.setProgress(progress);
                binding.progressBarTv.setText(progress + "%");
            }
        });
    }

    /*
    void install(Button button) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE, FileProvider.getUriForFile(this, "knf.kuma.fileprovider", getUpdate()))
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                    .putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, false)
                    .putExtra(Intent.EXTRA_INSTALLER_PACKAGE_NAME, getPackageName());
            startActivity(intent);
        } else {
            Intent intent = new Intent(Intent.ACTION_VIEW)
                    .setDataAndType(Uri.fromFile(getUpdate()), "application/vnd.android.package-archive")
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            startActivity(intent);
        }
        finish();
    }*/

}