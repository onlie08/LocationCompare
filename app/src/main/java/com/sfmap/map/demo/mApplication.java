package com.sfmap.map.demo;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import com.sf.appupdater.log.LogInfo;
import com.sf.appupdater.log.LogWriter;


public class mApplication extends Application {
    public static SharedPreferences sharedPreferences;

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences = getSharedPreferences(AppPreferences.PreferenceKey.SP_NAME_NAME,MODE_PRIVATE);
        LogWriter logWriter = new LogWriter() {
            @Override
            public void write(LogInfo logInfo) {
                Log.d("logInfo", "logInfo: " + logInfo);
            }
        };
        SFUpdaterUtils.setAppUpdaterInfo(this,"1455be0b9a30029ed26bd27679d7ce12","837846ef371f49d48f857e8429bf0fad",true, com.sf.appupdater.Environment.PRODUCTION,false,logWriter);

    }


}
