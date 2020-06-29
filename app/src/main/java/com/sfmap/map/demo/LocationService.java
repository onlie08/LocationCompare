package com.sfmap.map.demo;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.google.gson.Gson;
import com.sfmap.api.location.SfMapLocation;
import com.sfmap.api.location.SfMapLocationClient;
import com.sfmap.api.location.SfMapLocationClientOption;
import com.sfmap.api.location.SfMapLocationListener;
//import com.sfmap.api.location.client.util.CoordinateTransformUtil;
import com.sfmap.api.maps.MapUtils;
import com.sfmap.api.maps.model.LatLng;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LocationService extends Service implements SfMapLocationListener, AMapLocationListener {
    private LocalBinder mBinder = new LocalBinder();
    private SfMapLocationClient mSfMapLocationClient;
    public AMapLocationClient mAmapLocationClient = null;
    public LocationClient mLocationClient = null;
    private LatLng sfLatlng;
    private LatLng gdLatlng;
    private LatLng bdLatlng;
    private LatLng gpsLatlng;
    private MyLocationListener myListener = new MyLocationListener();
    private OnDataCallback onDataCallback;

    public void setOnDataCallback(OnDataCallback onDataCallback) {
        this.onDataCallback = onDataCallback;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public final class LocalBinder extends Binder {
        public LocationService getService(){
            return LocationService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initSfLocation();
        initGdLocation();
        initBdLocation();
        initSysLocation();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    //-----回调接口--------
    public interface OnDataCallback{

        /*
         * 返回顺丰网络定位结果
         */
        public void sfLocationChange(LatLng latLng);

        /**
         * 返回高德网络定位结果
         */
        public void gdLocationChange(LatLng latLng);

        /**
         * 返回百度网络定位结果
         */
        public void bdLocationChange(LatLng latLng);

        /**
         * 返回gps定位结果
         */
        public void gpsLocationChange(LatLng latLng);

        /**
         * 返回距离
         */
        public void distanceToGps(String sf,String gd,String bd);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSfMapLocationClient.destroy();
        mAmapLocationClient.onDestroy();
        mLocationClient.stop();
    }

    private void initSfLocation() {
        if (mSfMapLocationClient == null) {
            mSfMapLocationClient = new SfMapLocationClient(this.getApplicationContext());
            SfMapLocationClientOption locationOption = new SfMapLocationClientOption();

            locationOption.setInterval(1000);
            locationOption.setOnceLocation(false);
            locationOption.setTraceEnable(true);

            locationOption.setLocationMode(SfMapLocationClientOption.SfMapLocationMode.Battery_Saving);
            locationOption.setUseGjc02(true);
            locationOption.setNeedAddress(false);
            locationOption.setTraceEnable(false);

            mSfMapLocationClient.setLocationOption(locationOption);
            mSfMapLocationClient.setLocationListener(this);
            mSfMapLocationClient.startLocation();
        }
    }

    private void initGdLocation(){
        if(mAmapLocationClient == null){
            mAmapLocationClient = new AMapLocationClient(getApplicationContext());

            AMapLocationClientOption locationClientOption = new AMapLocationClientOption();
            locationClientOption.setInterval(1000);
            locationClientOption.setOnceLocation(false);
            locationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);

            mAmapLocationClient.setLocationOption(locationClientOption);
            mAmapLocationClient.setLocationListener(this);
            mAmapLocationClient.startLocation();
        }
    }

    private void initBdLocation(){
        if(mLocationClient == null){
            mLocationClient = new LocationClient(getApplicationContext());
            mLocationClient.registerLocationListener(myListener);
            LocationClientOption option = new LocationClientOption();
            option.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);
            option.setScanSpan(1000);
            mLocationClient.setLocOption(option);
            mLocationClient.start();
        }
    }

    @SuppressLint("MissingPermission")
    private void initSysLocation(){
        LocationManager locationManager = (LocationManager) this.getSystemService(this.LOCATION_SERVICE);

        String locateType = locationManager.GPS_PROVIDER;
        // 设置监听*器，自动更新的最小时间为间隔N秒(1秒为1*1000，这样写主要为了方便)或最小位移变化超过N米
        locationManager.requestLocationUpdates(locateType, 1000,0,
                locationListener);
    }

    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation bdLocation){
            if(null != bdLocation){
                Log.i("caohai","BDLocation");
                LatLng latLng = new LatLng(bdLocation.getLatitude(),bdLocation.getLongitude());
                bdLatlng = latLng;
                onDataCallback.bdLocationChange(bdLatlng);

            }
        }
    }

    @Override
    public void onLocationChanged(SfMapLocation sfLocation) {
        if(null != sfLocation){
            Log.i("caohai","SfMapLocation");
            String lats = new java.text.DecimalFormat("#.000000").format(sfLocation.getLatitude());
            String lons = new java.text.DecimalFormat("#.000000").format(sfLocation.getLongitude());
            LatLng latLng = new LatLng(Double.parseDouble(lats),Double.parseDouble(lons));
            sfLatlng = latLng;
            onDataCallback.sfLocationChange(sfLatlng);

        }
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if(null != aMapLocation){
            Log.i("caohai","AMapLocation");
            LatLng latLng = new LatLng(aMapLocation.getLatitude(),aMapLocation.getLongitude());
            gdLatlng = latLng;
            onDataCallback.gdLocationChange(gdLatlng);

        }
    }

    private LocationListener locationListener = new LocationListener() {
        /**
         * 位置信息变化时触发:当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
         * @param location
         */
        @Override
        public void onLocationChanged(Location location) {
            if(null != location){
                Log.i("caohai","Location");
                wgs84ToGcj02(location);
                String lats = new java.text.DecimalFormat("#.000000").format(location.getLatitude());
                String lons = new java.text.DecimalFormat("#.000000").format(location.getLongitude());
                LatLng latLng = new LatLng(Double.parseDouble(lats),Double.parseDouble(lons));
                gpsLatlng = latLng;
                onDataCallback.gpsLocationChange(gpsLatlng);

                saveResult();
            }
        }

        /**
         * GPS状态变化时触发:Provider被disable时触发此函数，比如GPS被关闭
         * @param provider
         * @param status
         * @param extras
         */
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        /**
         * 方法描述：GPS开启时触发
         * @param provider
         */
        @Override
        public void onProviderEnabled(String provider) {
        }

        /**
         * 方法描述： GPS禁用时触发
         * @param provider
         */
        @Override
        public void onProviderDisabled(String provider) {
        }
    };


    private void wgs84ToGcj02(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
//        double[] transformed = CoordinateTransformUtil.wgs84togcj02(longitude, latitude);
//        location.setLatitude(transformed[1]);
//        location.setLongitude(transformed[0]);
    }

    private void saveResult() {
        if(null == gpsLatlng || null == sfLatlng || null == gdLatlng || null == bdLatlng){
            return;
        }
        ResultBean resultBean = new ResultBean();
        resultBean.setGpsLoc(gpsLatlng.latitude + "," + gdLatlng.longitude);
        resultBean.setSfLoc(sfLatlng.latitude + "," + sfLatlng.longitude);
        resultBean.setGdLoc(gdLatlng.latitude + "," + gdLatlng.longitude);
        resultBean.setBdLoc(bdLatlng.latitude + "," + bdLatlng.longitude);
        float sfDistance = MapUtils.calculateLineDistance(gpsLatlng,sfLatlng);
        float gdDistance = MapUtils.calculateLineDistance(gpsLatlng,gdLatlng);
        float bdDistance = MapUtils.calculateLineDistance(gpsLatlng,bdLatlng);
        resultBean.setSfDistance(sfDistance);
        resultBean.setGdDistance(gdDistance);
        resultBean.setBdDistance(bdDistance);
        resultBean.setTime(getTimeStamp());
        String result = new Gson().toJson(resultBean);
        saveLogInfo(CompareActivity.logPath,result);
        String sfDis = new java.text.DecimalFormat("#.0").format(sfDistance);
        String gdDis = new java.text.DecimalFormat("#.0").format(gdDistance);
        String bdDis = new java.text.DecimalFormat("#.0").format(bdDistance);
        onDataCallback.distanceToGps("("+sfDis+"米)","("+gdDis+"米)","("+bdDis+"米)");
    }

    public String getTimeStamp() {
        Date dt = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(dt);
    }

    private void saveLogInfo(String path,String infos){
        infos += "\n";
        File file = new File(path);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(file, "rw");
            raf.seek(file.length());
            raf.write(infos.getBytes());
            raf.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
