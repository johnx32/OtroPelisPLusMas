package org.kaizoku.otropelisplusmas.service;

import android.util.Log;

import org.kaizoku.otropelisplusmas.database.entity.MediaEnt;
import org.kaizoku.otropelisplusmas.model.Cartel;
import org.kaizoku.otropelisplusmas.model.Chapter;
import org.kaizoku.otropelisplusmas.model.FullPage;
import org.kaizoku.otropelisplusmas.model.ItemPage;
import org.kaizoku.otropelisplusmas.model.Season;
import org.kaizoku.otropelisplusmas.model.SerieCartel;
import org.kaizoku.otropelisplusmas.model.VideoCard;
import org.kaizoku.otropelisplusmas.model.CapituloCartel;
import org.kaizoku.otropelisplusmas.model.video_server.FembedServer;
import org.kaizoku.otropelisplusmas.model.video_server.VideoServer;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Single;


public class PelisplushdService {

    public  static final String url_base="https://pelisplushd.net";
    public  static final String base_series="/serie";
    private static final String TAG = "slcsa2";

    public PelisplushdService() {
    }

    /*public PelisplushdService(OnMenuVideoListener onMenuVideoListener) {
        this.onMenuVideoListener = onMenuVideoListener;
    }

    public void loadMenuCards(String url){
        Thread h = new Thread(new Runnable() {
            @Override
            public void run() {
                loadMenuCardsCallback(url);
            }
        });
        h.start();
    }*/

    private FullPage loadMenuCards(String url){
        FullPage fullPage;
        List<MediaEnt> listVideo=new ArrayList<>();
        List<ItemPage> paginacion = new ArrayList<>();

        try{
            Document doc = Jsoup.connect(url)
                    .timeout(12000)
                    .get();
            Elements ele = doc.select("div.Posters>a.Posters-link");
            for(Element e:ele){
                String title="", rating="", urlv="", src="";
                if(e.selectFirst("p")!=null)title=e.selectFirst("p").text();
                if(e.selectFirst("div.stars span")!=null)rating=e.selectFirst("div.stars span").text();
                if(e.attr("href")!=null)urlv=e.attr("href");
                if(e.selectFirst("img")!=null)src=e.selectFirst("img").attr("src");
                //byte type = getType(urlv);
                listVideo.add(new MediaEnt(title,rating,urlv,src));

            }

            Elements pag = doc.select("ul.pagination>li>a");
            for(Element a:pag)
                paginacion.add(new ItemPage(a.text(),a.attr("href")));
            //if(onMenuVideoListener!=null)onMenuVideoListener.onLoadMenuVideos(listVideo,paginacion);
            return new FullPage(url,listVideo,paginacion);
        }catch(Exception e){e.printStackTrace();}
        return  new FullPage(null,null,null);
    }

    public Single<FullPage> loadMenuCardsSingle(String url){
        return Single.create(emitter -> {
            try {
                emitter.onSuccess(loadMenuCards(url));
            }catch (Exception e){emitter.onError(e);e.printStackTrace();}
        });
    }



    private Cartel getCartelFromDoc(Document doc){
        try {
            String isrc,ititulo,isinopsis,irating;

            Element src=doc.selectFirst("div.card-body>div>div>img");isrc=src==null?"":src.attr("src");
            Element titulo=doc.selectFirst("div.card-body>div>div>h1");ititulo=titulo==null?"":titulo.text();
            Element sinopsis=doc.selectFirst("div.card-body>div>div>div.text-large");isinopsis=sinopsis==null?"":sinopsis.text();//son 2 pero elegir el 1ro
            Element rating=doc.selectFirst("div.card-body>div>div>div>div>span");irating=rating==null?"":rating.text();
            return new Cartel(isrc,ititulo,isinopsis,irating);
        }catch(Exception e){e.printStackTrace();}
        return null;
    }

    private SerieCartel getSerieCartel(String url){
        try {
            Document doc = Jsoup.connect(url)
                    .timeout(12000)
                    .get();
            List<Season> seasonList=new ArrayList<>();
            Cartel cartel = getCartelFromDoc(doc);

            Elements tablist = doc.select("div.card>div.VideoPlayer>ul>li>a");
            Elements tabpanel = doc.select("div.card>div.tab-content>div.tab-pane[role]");
            int size=tabpanel.size();
            for (int i=0;i<size;i++) {
                List<Chapter> chapterList=new ArrayList<>();
                Elements capitulos = tabpanel.get(i).select("a");
                int j=0;
                for(Element e:capitulos){
                    if((j%5)==0)
                        chapterList.add(new Chapter("","",Chapter.TYPE_BANNER_ADAPTATIVE));
                    String href=e.attr("href");
                    String titulo=e.text();
                    chapterList.add(new Chapter(href,titulo,Chapter.TYPE_CHAPTER));
                    j++;
                }
                String title="";
                if(i<tablist.size())title=tablist.get(i).text();
                seasonList.add(new Season(title,chapterList));
            }
            SerieCartel serieCartel = new SerieCartel(cartel,seasonList);
            serieCartel.hrefs = url;
            return serieCartel;
        }catch(Exception e){e.printStackTrace();}
        return null;
    }

    private CapituloCartel getVideoCartel(String url){
        List<String> listUrlServer=new ArrayList<>();
        List<VideoServer> listServers=new ArrayList<>();
        try {
            Document doc = Jsoup.connect(url)
                    .timeout(12000)
                    .get();
            Cartel cartel = getCartelFromDoc(doc);

            cartel.url_disqus = getDisqusComments(doc);
            Log.i(TAG, "getVideoCartel: url disqus: "+cartel.url_disqus);

            Elements listNameServers = doc.select("div.player>div>ul>li>a");
            Elements scrypts=doc.getElementsByTag("script");
            for(Element script:scrypts)
                if(script.toString().contains("'slug'")){
                    listUrlServer= getVideoServers(script.toString());
                    break;
                }
            int size=listUrlServer.size(); String nameServer="";
            for (int i=0;i<size;i++){
                if(i<listNameServers.size())nameServer=listNameServers.get(i).text();

                if(listUrlServer.get(i).contains("fembed")) {
                    JSONObject json=new JSONObject(getJsonVideoFembed(listUrlServer.get(i)));
                    listServers.add(new FembedServer(nameServer,json));
                }
            }

            return new CapituloCartel(cartel, listServers);
        }catch(Exception e){e.printStackTrace();}
        return null;
    }

    private String getDisqusComments(Document doc){
        String url="";
        try{
            //      Buscar el script
            String title = doc.getElementsByTag("title").first().text();
            String disqus_config = "";
            Elements scrypts = doc.getElementsByTag("script");
            for(Element script:scrypts)
                if(script.toString().contains("disqus_config")){
                    disqus_config = script.toString();
                    break;
                }

            String pageUrl="";
            String match_page_url="this.page.url = '(.*)'";
            Pattern pattern = Pattern.compile(match_page_url);
            Matcher matcher = pattern.matcher(disqus_config);
            while(matcher.find())
                if(matcher.groupCount()>0)
                    pageUrl = matcher.group(1);

            String pageIdentifier="";
            String match_page_identifier="this.page.identifier = '(.*)'";
                pattern = Pattern.compile(match_page_identifier);
                matcher = pattern.matcher(disqus_config);
            while(matcher.find())
                if(matcher.groupCount()>0)
                    pageIdentifier = matcher.group(1);

            url = "https://disqus.com/embed/comments/?base=default&amp;"+
                    "f=pelisplus-hd&amp;"+
                    "t_i="+pageIdentifier+"&amp;"+
                    "t_u="+URLEncoder.encode(pageUrl, "UTF-8")+"&amp;"+
                    "t_d="+URLEncoder.encode(title, "UTF-8")+"&amp;"+
                    "t_t="+URLEncoder.encode(title, "UTF-8")+"&amp;"+
                    "s_o=default#version=a5921af07b365f6dfd62075d2dee3735";
            return url;
        }catch(Exception e){e.printStackTrace();}
        return url;
    }

    /*private List<VideoCard> getVideoItemsFromMenu(String url) {
        List<VideoCard> listVideo=new ArrayList<>();
        try{
            Document doc = Jsoup.connect(url)
                    .timeout(12000)
                    .get();
            Elements ele = doc.select("div.Posters>a.Posters-link");

            for(Element e:ele){
                String name="", rating="", urlv="", src="";
                if(e.selectFirst("p")!=null)name=e.selectFirst("p").text();
                if(e.selectFirst("div.stars span")!=null)rating=e.selectFirst("div.stars span").text();
                if(e.attr("href")!=null)urlv=e.attr("href");
                if(e.selectFirst("img")!=null)src=e.selectFirst("img").attr("src");
                listVideo.add(new VideoCard(name,rating,urlv,src));
            }
        }catch(Exception e){e.printStackTrace();}
        return listVideo;
    }*/

    /*private List<String> getSeridores(String url_video){
        //String url_cap="https://pelisplushd.net/pelicula/kimetsu-no-yaiba-la-pelicula-tren-infinito";
        List<String> listServers=new ArrayList<>();
        try{
            Document doc = Jsoup.connect(url_video)
                    .timeout(12000)
                    .get();
            Elements scrypts=doc.getElementsByTag("script");
            for(Element script:scrypts){
                if(script.toString().contains("'slug'")){
                    listServers= getVideoServers(script.toString());
                    break;
                }
            }
        }catch(Exception e){e.printStackTrace();}
        return listServers;
    }*/

    private List<String> getVideoServers(String script){
        List<String> listaVideos=new ArrayList<>();
        String match_videoi="video\\[\\d*\\].*=.*'(.*)'";
        Pattern pattern = Pattern.compile(match_videoi);
        Matcher matcher = pattern.matcher(script);

        while(matcher.find())
            if(matcher.groupCount()>0)
                listaVideos.add(matcher.group(1));

        return listaVideos;
    }

    // scraping server fembed
    /*private String getVideoUrl(String url){
        //String s = getJsonVideoFembed(url);
        //String stream = getVideoFileJson(s);
        return stream;
    }*/

    public String getJsonVideoFembed(String url){
        String link="https://pelisplushd.net/fembed.php?url=60epmb04gny622p";
        String json="";
        try{
            Document doc = Jsoup.connect(url)
                    .timeout(12000)
                    .get();
            String src=doc.selectFirst("iframe").attr("src");

            json = getVideoFileJson(src);

        }catch(Exception e){e.printStackTrace();}
        return json;
    }

    private String getVideoFileJson(String url){
        //String  link="https://pelispop.net/v/60epmb04gny622p#caption=&poster=#";
        String url_file="", json="";
        String match_url="(https://.*..*)/v/(.*)#.+";
        Pattern pattern = Pattern.compile(match_url);
        Matcher matcher = pattern.matcher(url);

        while(matcher.find())
            if(matcher.groupCount()==2)
                url_file=matcher.group(1)+"/api/source/"+matcher.group(2);
        ////String link2="https://pelispop.net/api/source/60epmb04gny622p";//resultado
        if(!url.equals(""))
            try{
                Connection.Response  respuesta = Jsoup.connect(url_file)
                        .timeout(12000)
                        .header("Connection", "keep-alive")
                        .header("Content-Length", "49")
                        .header("Accept", "*/*")
                        .header("X-Requested-With", "XMLHttpRequest")
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.72 Safari/537.36")
                        .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                        .header("Sec-GPC", "1")
                        .header("Origin", "https://pelispop.net")
                        .header("Sec-Fetch-Site", "same-origin")
                        .header("Sec-Fetch-Mode", "cors")
                        .header("Sec-Fetch-Dest", "empty")
                        .header("Referer", "https://pelispop.net/v/60epmb04gny622p")
                        .header("Accept-Language", "es-419,es;q=0.9")
                        .header("Host", "pelispop.net")
                        .ignoreContentType(true)
                        .requestBody("r=https%3A%2F%2Fpelisplushd.net%2F&d=pelispop.net")
                        .method(Connection.Method.POST)
                        .execute();
                json = respuesta.body();
            }catch(Exception e){e.printStackTrace();}
        return json;
    }

    public List<ItemPage> getMenuPaginacion(String html) {
        //String url="https://pelisplushd.net/series";
        List<ItemPage> paginacion = new ArrayList<>();
        try{
            Document doc = Jsoup.connect(html)
                    .timeout(12000)
                    .get();
            Elements ele = doc.select("ul.pagination>li>a");
            for(Element a:ele)
                paginacion.add(new ItemPage(a.text(),a.attr("href")));
        }catch(Exception e){e.printStackTrace();}
        return paginacion;
    }

    //---  Singles   -------------------------------------------------------

    /*public Single<List<VideoCard>> getSingleVideoItemsFromMenu(String url){
        return Single.create(new SingleOnSubscribe<List<VideoCard>>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<List<VideoCard>> emitter){
                try {
                    emitter.onSuccess(getVideoItemsFromMenu(url));
                }catch (Exception e){ emitter.onError(e); }
            }
        });
    }*/

    /*public Single<List<String>> getSingleSeridores(String url_cap){
        return Single.create(emitter -> {
            try {
                emitter.onSuccess(getSeridores(url_cap));
            }catch (Exception e){ emitter.onError(e); }
        });
    }*/

    /*public Single<String> getSingleVideoUrl(String url){
        return Single.create(emitter -> {
            try {
                emitter.onSuccess(getVideoUrl(url));
            }catch (Exception e){ emitter.onError(e); }
        });
    }*/

    public Single<SerieCartel> getSingleSerieCartel(String url){
        return Single.create(emitter -> {
            try {
                emitter.onSuccess(getSerieCartel(url));
            }catch (Exception e){emitter.onError(e);e.printStackTrace();}
        });
    }

    public Single<CapituloCartel> getSingleVideoCartel(String url){
        return Single.create(emitter -> {
            try {
                emitter.onSuccess(getVideoCartel(url));
            }catch (Exception e){emitter.onError(e);e.printStackTrace();}
        });
    }

    /*
    private OnMenuVideoListener onMenuVideoListener;
    public interface OnMenuVideoListener{
        void onLoadMenuVideos(List<MediaEnt> listCards, List<ItemPage> paginationList);
    }
    */
}
