package org.kaizoku.otropelisplusmas.updater;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.kaizoku.otropelisplusmas.R;

public class Checker {
    public static void check(final Context contexto, final CheckerListener listener) {
        if (isConnected(contexto)){
            Thread h = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Document document = Jsoup.connect(contexto.getResources().getString(R.string.version_num)).get();
                        int code_new = Integer.parseInt(document.select("body").first().ownText().trim());
                        int code = contexto.getPackageManager().getPackageInfo(contexto.getPackageName(), 0).versionCode;
                        //listener.onNeedUpdate(String.valueOf(code), String.valueOf(code_new));
                        if (code_new > code) {
                            listener.onNeedUpdate(String.valueOf(code), String.valueOf(code_new));
                        } else {
                            Log.e("Version", "Up to date: " + code);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            h.start();
        }
    }

    public static boolean isConnected(Context c){
        try {
            ConnectivityManager cm = (ConnectivityManager)c.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();
        }catch (Exception e){
            Log.e("TAG", "isConnected: ", e);
            return false;
        }
    }

    public interface CheckerListener {
        void onNeedUpdate(String o_code, String n_code);
    }
}
