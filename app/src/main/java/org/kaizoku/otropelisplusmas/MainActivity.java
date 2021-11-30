package org.kaizoku.otropelisplusmas;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.widget.TooltipCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

import org.kaizoku.otropelisplusmas.database.viewmodel.CapituloViewModel;
import org.kaizoku.otropelisplusmas.updater.Checker;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "tmain";
    private AppBarConfiguration mAppBarConfiguration;
    //private InterstitialAd mInterstitialAd;
    //admob app-ads.txt
    //google.com, pub-6323075080626234, DIRECT, f08c47fec0942fa0
    /*todo:
        crash analitic,
        cast video, notificaciones,
        imagen toolbar en tablet
     */
    //ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home,
                R.id.nav_peliculas,
                R.id.nav_series,
                R.id.nav_animes,
                R.id.nav_favoritos,
                R.id.nav_about
                //R.id.nav_reproductor
                )
                //.setDrawerLayout(drawer)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        //binding = ActivityMainBinding.inflate(getLayoutInflater());
        ImageView im = findViewById(R.id.icon_changelog);
        TooltipCompat.setTooltipText(navigationView.getHeaderView(0).findViewById(R.id.icon_changelog),"Changelog");
        navigationView.getHeaderView(0).findViewById(R.id.icon_changelog).setOnClickListener(v -> {
            drawer.close();
            navController.navigate(R.id.changelogFragment);
        });


        navigationView.getMenu().findItem(R.id.nav_about).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this,R.style.DialogoTema);
                builder.setTitle("Acerca de")
                        .setMessage("Aplicación de Entretenimiento\n" +
                                    "disfruta de tus series y películas favoritas\n\n" +
                                    "https://kaizokuapps.ga")
                        //.setCancelable(false)
                        .setNegativeButton("Cancelar",null)
                        .setPositiveButton("ir", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String url = "https://kaizokuapps.ga";
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                //i.addCategory(Intent.CATEGORY_APP_BROWSER);
                                //i.addCategory(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(url));
                                startActivity(Intent.createChooser(i,"Abrir Navegador"));
                            }
                        });
                        //.setPositiveButton("si", null);
                AlertDialog dialog = builder.create();
                dialog.show();
                //dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.verde));
                drawer.close();
                return false;
            }
        });
        /*navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_about:
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this,R.style.Theme_AppCompat_Dialog);
                        builder.setTitle("Actualización")
                                .setMessage("Aplicación de Entretenimiento\n" +
                                        "disfruta de tus series y películas favoritas\n" +
                                        "https://kaizokuapps.ga")
                                //.setCancelable(false)
                                .setPositiveButton("si", null);
                        AlertDialog dialog = builder.create();
                        dialog.show();
                        break;
                }
                return true;
            }
        });*/


        //initAds();

        setFloatingactionButtonOnClick();


        /*
        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.Theme_AppCompat_Dialog);
        builder.setTitle("Actualización")
                .setMessage("Parece que la versión " + 10 + " está disponible,\n ¿Quieres actualizar?")
                //.setCancelable(true)
                .setNegativeButton("despues",null)
                .setPositiveButton("si",null);
        AlertDialog dialog = builder.create();
        //dialog.getWindow().setBackgroundDrawableResource(R.color.red);
        dialog.show();*/

        Checker.check(this, new Checker.CheckerListener() {
            @Override
            public void onNeedUpdate(String code, String code_new) {
                Log.i(TAG, "onNeedUpdate: ");
                runOnUiThread(() -> {
                    try {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this,R.style.DialogoTema);
                                builder.setTitle("Actualización")
                                    .setMessage("Hay una versión más reciente disponible,\n versión "+code_new+", ¿desea actualizar ahora?")
                                    //.setCancelable(false)
                                    .setNegativeButton("despues",null)
                                    .setPositiveButton("si", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // Need to ask for write permissions on SDK 23 and up, this is ignored on older versions
                                            if (ContextCompat.checkSelfPermission(MainActivity.this,
                                                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                                            {
                                                ActivityCompat.requestPermissions(MainActivity.this,new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                                            }else{
                                                Intent i = new Intent(MainActivity.this, UpdateActivity.class);
                                                startActivity(i);
                                            }



                                            /*
                                            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                                            } else {
                                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                                            }
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                                                if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                                        == PackageManager.PERMISSION_GRANTED) {

                                                }
                                            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                                                Log.v(TAG,"Permission is granted");
                                                //File write logic here
                                                return true;
                                            }*/
                                            //Intent i = new Intent(MainActivity.this,UpdateActivity.class);
                                            //startActivity(i);
                                        }
                                    });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        });

        mostrarChangelog(navController);

        checkPendingCapituloProgress();
    }

    private void checkPendingCapituloProgress() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
            long progress = sharedPref.getLong("progress",0);
            long capitulo_id = sharedPref.getLong("capitulo_id",0);

            if(progress>0){
                CapituloViewModel capituloViewModel;
                capituloViewModel = new ViewModelProvider(this).get(CapituloViewModel.class);

                capituloViewModel.getCapitulo(capitulo_id)
                        .flatMap(capituloEnt -> {
                            capituloEnt.progress = progress;
                            return capituloViewModel.updateCapitulo(capituloEnt);
                        })
                        .subscribe((integer, throwable) -> {
                            if(throwable==null) {
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.remove("progress");
                                editor.remove("capitulo_id");
                                editor.commit();
                                Log.i(TAG, "checkPendingCapituloProgress: capitulo pendiente progreso actualizado con exito");
                            }
                        });
            }
    }

    /*
    private void initAds() {
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        //List<String> testDeviceIds = Arrays.asList("AAFFE0B4F0C8C25E830B12A27616C1D4");//mi celu mini j1
        List<String> testDeviceIds = Arrays.asList("CF0594B70208DD4C10C0A2CBA8028333");//virtual device android 10
        RequestConfiguration configuration = new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
        MobileAds.setRequestConfiguration(configuration);

        loadInterstitialAd();
    }*/

    private void setFloatingactionButtonOnClick() {
        FloatingActionButton f = findViewById(R.id.fab_main);
        f.setOnClickListener(v -> {
            Log.i(TAG, "setFloatingactionButtonOnClick: ");
            if(onFabListener!=null) {
                Log.i(TAG, "setFloatingactionButtonOnClick: fablistener no es null");
                onFabListener.onClickFab(f);
            }else Log.i(TAG, "setFloatingactionButtonOnClick: fablis es null");
        });
    }
    public void setFabImage(int resId){
        FloatingActionButton f = findViewById(R.id.fab_main);
        f.setImageResource(resId);
    }

    private void mostrarChangelog(NavController navController){
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        //editor.putBoolean("changelog", true);
        //editor.apply();

        try {
            int oldcode = sharedPref.getInt("code",1);
            int newcode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
            Log.i(TAG, "mostrarChangelog: oldcode: "+oldcode+" newcode: "+newcode);
            if(newcode>oldcode) {
                editor.putInt("code", newcode);
                editor.apply();

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this,R.style.Theme_AppCompat_Dialog);
                builder.setTitle("Changelog")
                        .setMessage("¿Desea ver el registro de cambios de esta versión?")
                        .setNegativeButton("NO",null)
                        .setPositiveButton("SI",(dialog, which) -> {
                            navController.navigate(R.id.changelogFragment);
                        });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1) {
            Intent i = new Intent(MainActivity.this, UpdateActivity.class);
            startActivity(i);
        }
    }

    /*
    private void loadInterstitialAd(){
        AdRequest adRequest = new AdRequest.Builder()
                .build();

        InterstitialAd.load(MainActivity.this,getResources().getString(R.string.intersticial01), adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                // The mInterstitialAd reference will be null until
                // an ad is loaded.
                mInterstitialAd = interstitialAd;
                Log.i(TAG, "onAdLoaded");
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Handle the error
                Log.e(TAG, "Error loadInterstitialAd - "+loadAdError.getMessage());
                mInterstitialAd = null;
            }
        });
    }

    public void showInterstitialAd(){
        if (mInterstitialAd != null) {

            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                @Override
                public void onAdDismissedFullScreenContent() {
                    // Called when fullscreen content is dismissed.
                    Log.d("TAG", "The ad was dismissed.");
                    loadInterstitialAd();
                }

                @Override
                public void onAdFailedToShowFullScreenContent(AdError adError) {
                    // Called when fullscreen content failed to show.
                    Log.d("TAG", "The ad failed to show.");
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    // Called when fullscreen content is shown.
                    // Make sure to set your reference to null so you don't
                    // show it a second time.
                    mInterstitialAd = null;
                    Log.d("TAG", "The ad was shown.");
                }
            });

            mInterstitialAd.show(MainActivity.this);
        } else {
            Log.d("TAG", "mInterstitialAd es nulo - The interstitial ad wasn't ready yet.");
        }
    }*/

    @Override
    public void setTitle(CharSequence title) {
        //Toolbar toolbar = findViewById(R.id.toolbar);
        //toolbar.setTitle(title);
        //getSupportActionBar().setTitle(title);
        CollapsingToolbarLayout c = findViewById(R.id.coll_toolbar_layout);
        c.setTitle(title);
    }

    public void setDisplayShowTitleEnabled(boolean enabled){
        CollapsingToolbarLayout c = findViewById(R.id.coll_toolbar_layout);
        c.setTitleEnabled(enabled);
        ImageView im = findViewById(R.id.img_toolbar);
        im.setImageDrawable(null);
    }

    public void loadImgToolbar(String src_img) {
        Picasso.get()
                .load(src_img)
                .into((ImageView) findViewById(R.id.img_toolbar));
    }

    /*public void setTitleToolbar(String title){
        CollapsingToolbarLayout c = findViewById(R.id.coll_toolbar_layout);
        c.setTitle(title);
    }*/


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void setOnFabListener(OnFabListener onFabListener){
        this.onFabListener = onFabListener;
    }
    private OnFabListener onFabListener;
    public interface OnFabListener{
        void onClickFab(FloatingActionButton fab);
    }

}