package com.example.weather.utils;

import android.app.Application;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class App extends Application {


    public static final String GOOGLE = "https://logo-logos.com/wp-content/uploads/2016/11/Google_icon_logo.png";
    public static final String PROFILE = "https://cultivatedculture.com/wp-content/uploads/2019/12/LinkedIn-Profile-Picture-Example-Madeline-Mann-414x414.jpeg";
    public static final String IMDB = "https://cdn.icon-icons.com/icons2/2389/PNG/512/imdb_logo_icon_145171.png";
    public static final String BASE_URL_CLOUD = "https://s3.wasabisys.com/degoo-production-large-file-us-east1.degoo.info/ADk6L8/srmnDg/json/ChTmE730G_umgqwlPTiOu44Z0y8c3hAA.json?AWSAccessKeyId=QCIW8NA9JUUC4PKQYZTJ&Expires=1631361008&Signature=SbLKMVCO%2F0jqu2gsEVK04HcHfQg%3D&ngsw-bypass=1";

    private static RequestQueue requestQueue;
    private static App instance;
    @Override
    public void onCreate() {

        super.onCreate();
        instance = this;
        requestQueue = Volley.newRequestQueue(this);
    }
    public static synchronized App getInstance(){
        return instance;
    }
    public static synchronized RequestQueue getRequestQueue(){
        if(requestQueue == null){
            requestQueue = Volley.newRequestQueue(instance.getApplicationContext());
        }
        return requestQueue;
    }


}
