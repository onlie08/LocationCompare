package com.sfmap.map.demo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Process;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static String[] PERMISSIONS_REQUEST = {
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS
    };

    EditText edit_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermission();
        initView();
        initData();
    }

    private void initData() {
        String user = (String)AppPreferences.instance().get("user","");
        if(!TextUtils.isEmpty(user)){
            Intent intent = new Intent();
            intent.putExtra("userCount",user);
            intent.setClass(MainActivity.this,CompareActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void initView() {
        edit_user = findViewById(R.id.edit_user);
        findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(edit_user.getText().toString())){
                    Toast.makeText(MainActivity.this,"大侠留个号呗",Toast.LENGTH_LONG).show();
                    return;
                }
                String user = edit_user.getText().toString().trim();
                AppPreferences.instance().put("user",user);
                Intent intent = new Intent();
                intent.putExtra("userCount",user);
                intent.setClass(MainActivity.this,CompareActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (this.checkPermission(Manifest.permission.READ_PHONE_STATE, Process.myPid(), Process.myUid())
                    != PackageManager.PERMISSION_GRANTED || this.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Process.myPid(), Process.myUid())
                    != PackageManager.PERMISSION_GRANTED || this.checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION, Process.myPid(), Process.myUid())
                    != PackageManager.PERMISSION_GRANTED) {
                this.requestPermissions(PERMISSIONS_REQUEST, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(this, "软件退出，运行权限被禁止", Toast.LENGTH_SHORT).show();
                    System.exit(0);
                }
            }
        }
    }
}
