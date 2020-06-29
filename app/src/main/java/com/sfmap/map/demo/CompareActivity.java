package com.sfmap.map.demo;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.sfmap.api.maps.CameraUpdateFactory;
import com.sfmap.api.maps.MapController;
import com.sfmap.api.maps.MapView;
import com.sfmap.api.maps.model.BitmapDescriptorFactory;
import com.sfmap.api.maps.model.LatLng;
import com.sfmap.api.maps.model.Marker;
import com.sfmap.api.maps.model.MarkerOptions;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class CompareActivity extends AppCompatActivity {
    private MapView mMapView;
    private MapController mMap;
    private Marker sfMarker;
    private Marker gdMarker;
    private Marker bdMarker;
    private Marker gpsMarker;
    public static String logPath = "";
    private String userCount;
    private List<File> fileList;
    private TextView tv_gps;
    private TextView tv_net_sf;
    private TextView tv_net_gd;
    private TextView tv_net_bd;
    private TextView tv_sf_distance;
    private TextView tv_gd_distance;
    private TextView tv_bd_distance;
    private LocationService mLocationService;//xxg

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare);
        mMapView = findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        File sfFile = new File(Environment
                .getExternalStorageDirectory().getAbsolutePath() + "/sflocation");
        if(sfFile.exists()){
            fileList = getLogFiles(sfFile);
            if(!fileList.isEmpty()){
                pushFile(fileList.get(0));
            }
        }
        userCount = getIntent().getStringExtra("userCount");
        initView();
        initMapSetting();
        logPath = Environment
                .getExternalStorageDirectory().getAbsolutePath() + "/sflocation/" + getDayStamp() + "_"+ userCount+"_CompareLog.txt";
        SFUpdaterUtils.checkVersion(this);
        Intent intent = new Intent(this, LocationService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
        Log.i("caohai","bindService");
    }

    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            Log.i("caohai","onServiceConnected");
            mLocationService = ((LocationService.LocalBinder) iBinder).getService();
            mLocationService.setOnDataCallback(new LocationService.OnDataCallback() {
                @Override
                public void sfLocationChange(LatLng latLng) {
                    if(sfMarker == null){
                        sfMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("顺丰位置").icon(BitmapDescriptorFactory.fromResource(R.mipmap.sf_poi)));
                    }else {
                        sfMarker.setPosition(latLng);
                    }
                    mMap.setMapCenter(latLng);
                    tv_net_sf.setText(latLng.latitude + "," + latLng.longitude);
                }

                @Override
                public void gdLocationChange(LatLng latLng) {
                    if(gdMarker == null){
                        gdMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("高德位置").icon(BitmapDescriptorFactory.fromResource(R.mipmap.gd_poi)));
                    }else {
                        gdMarker.setPosition(latLng);
                    }
                    tv_net_gd.setText(latLng.latitude + "," + latLng.longitude);
                }

                @Override
                public void bdLocationChange(LatLng latLng) {
                    if(bdMarker == null){
                        bdMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("百度位置").icon(BitmapDescriptorFactory.fromResource(R.mipmap.bd_poi)));
                    }else {
                        bdMarker.setPosition(latLng);
                    }
                    tv_net_bd.setText(latLng.latitude + "," + latLng.longitude);
                }

                @Override
                public void gpsLocationChange(LatLng latLng) {
                    if(gpsMarker == null){
                        gpsMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("GPS位置").icon(BitmapDescriptorFactory.fromResource(R.mipmap.gps_poi)));
                    }else {
                        gpsMarker.setPosition(latLng);
                    }
                    tv_gps.setText(latLng.latitude + "," + latLng.longitude);
                }

                @Override
                public void distanceToGps(String sf, String gd, String bd) {
                    tv_sf_distance.setText(sf);
                    tv_gd_distance.setText(gd);
                    tv_bd_distance.setText(bd);
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i("caohai","onServiceDisconnected");
        }
    };

    private void initView() {
        tv_gps = findViewById(R.id.tv_gps);
        tv_net_sf = findViewById(R.id.tv_net_sf);
        tv_net_gd = findViewById(R.id.tv_net_gd);
        tv_net_bd = findViewById(R.id.tv_net_bd);
        tv_sf_distance = findViewById(R.id.tv_sf_distance);
        tv_gd_distance = findViewById(R.id.tv_gd_distance);
        tv_bd_distance = findViewById(R.id.tv_bd_distance);
    }

    private void initMapSetting() {
        mMap = mMapView.getMap();
        this.mMap.getUiSettings().setZoomControlsEnabled(false);
        this.mMap.getUiSettings().setCompassEnabled(false);
        this.mMap.setTrafficEnabled(false);
        this.mMap.setMyLocationEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.zoomTo(18));//114.425367,30.453917
        mMap.setMapCenter(new LatLng(30.453917, 114.425367));

        ActivityCompat.requestPermissions(this,
                new String[] {Manifest.permission.ACCESS_COARSE_LOCATION},
                200);

    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    private void pushFile(File file){
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("path","file")
                .addFormDataPart("fileName",file.getName());
                //在这里添加服务器除了文件之外的其他参数
        RequestBody fileBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        //添加文件(uploadfile就是你服务器中需要的文件参数)
        builder.addFormDataPart("file", file.getName(), fileBody);
        List<MultipartBody.Part> parts = builder.build().parts();

        RetrofitUtil.getInstance().getApiService()
                .pushFile(parts)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ReturnBean>() {
                    private Disposable mDisposable;

                    @Override
                    public void onSubscribe(Disposable d) {
                        mDisposable = d;
                    }

                    @Override
                    public void onNext(ReturnBean value) {
                        if (value.getCode() != 1) {
                            Toast.makeText(CompareActivity.this, value.getMsg(), Toast.LENGTH_SHORT).show();
                        } else {

                        }
                        mDisposable.dispose();//注销
                        fileList.get(0).delete();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(CompareActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                        mDisposable.dispose();//注销
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    private List<File> getLogFiles(File path){
        List<File> fileList = new ArrayList<>();
        File[] files = path.listFiles();
        for(File file : files){
            if(file.getName().endsWith("CompareLog.txt")){
                fileList.add(file);
            }
        }
        return fileList;
    }

    public String getDayStamp() {
        Date dt = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        return sdf.format(dt);
    }

}
