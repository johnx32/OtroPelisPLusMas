package org.kaizoku.otropelisplusmas;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

public class ReceiverActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        Uri uri = i.getData();
        Log.i("TAG", "onCreate: getScheme: "+uri.getScheme());
        Log.i("TAG", "onCreate: getEncodedSchemeSpecificPart: "+uri.getEncodedSchemeSpecificPart());
        Log.i("TAG", "onCreate: getPath: "+uri.getPath());//direccion
        Log.i("TAG", "onCreate: getHost: "+uri.getHost());
        Log.i("TAG", "onCreate: toString: "+uri.toString());
        Log.i("TAG", "onCreate: getLastPathSegment: "+uri.getLastPathSegment());
        Log.i("TAG", "onCreate: getQuery: "+uri.getQuery());
        Log.i("TAG", "onCreate: getSchemeSpecificPart: "+uri.getSchemeSpecificPart());

    }
}
