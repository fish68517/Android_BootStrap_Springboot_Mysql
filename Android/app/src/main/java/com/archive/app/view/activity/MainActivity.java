package com.archive.app.view.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.archive.app.R;
import com.archive.app.asr.AsrFragment;
import com.archive.app.view.fragment.HelpFragment;
import com.archive.app.view.fragment.ScheduleFragment;
import com.archive.app.view.fragment.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hjq.permissions.OnPermission;
import com.hjq.permissions.XXPermissions;
import com.iflytek.sparkchain.core.LogLvl;
import com.iflytek.sparkchain.core.SparkChain;
import com.iflytek.sparkchain.core.SparkChainConfig;

import java.util.List;

/**
 * 主界面
 */
public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton fab;

    private static final String TAG = "MainActivity";

    private boolean isAuth = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        fab = findViewById(R.id.fab);

        fab.setOnClickListener(view -> {
            startActivity(new android.content.Intent(MainActivity.this, AddEditScheduleActivity.class));
        });

        // 默认加载日程Fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ScheduleFragment()).commit();
        }

        setupBottomNavigation();
        // 讯飞语音相关的
        getPermission();
    }

    private void getPermission(){
        XXPermissions.with(this).permission("android.permission.WRITE_EXTERNAL_STORAGE"
                , "android.permission.READ_EXTERNAL_STORAGE"
                , "android.permission.INTERNET"
                , "android.permission.MANAGE_EXTERNAL_STORAGE").request(new OnPermission() {
            @Override
            public void hasPermission(List<String> granted, boolean all) {
                Log.d(TAG,"SDK获取系统权限成功:"+all);
                for(int i=0;i<granted.size();i++){
                    Log.d(TAG,"获取到的权限有："+granted.get(i));
                }
                if(all){
                    // createWorkDir();
                    // 检查是否为模拟器环境，避免在模拟器上初始化SDK
                    if (!isEmulator()) {
                        SDKInit();
                    } else {
                        Log.d(TAG, "检测到模拟器环境，跳过SDK初始化");
                        Toast.makeText(MainActivity.this, "模拟器环境跳过SDK初始化，部分功能可能无法使用", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void noPermission(List<String> denied, boolean quick) {
                if(quick){
                    Log.e(TAG,"onDenied:被永久拒绝授权，请手动授予权限");
                    XXPermissions.startPermissionActivity(MainActivity.this,denied);
                }else{
                    Log.e(TAG,"onDenied:权限获取失败");
                }
            }
        });
    }

    public void SDKInit(){
        try {
            Log.d(TAG,"initSDK");
            // 初始化SDK，Appid等信息在清单中配置
            SparkChainConfig sparkChainConfig = SparkChainConfig.builder();
            sparkChainConfig.appID("8cfc9739")
                    .apiKey("12f24927eca76a6e1dcd864f71a8aad3")
                    .apiSecret("M2M3YjcwYWY1ZmFhNTJlYzExNmMxZDEw")//应用申请的appid三元组
//                .uid("")
//                .logPath("/sdcard/iflytek/AEELog.txt")
                    .logLevel(LogLvl.VERBOSE.getValue());

            int ret = SparkChain.getInst().init(getApplicationContext(),sparkChainConfig);
            String result;
            if(ret == 0){
                result = "SDK初始化成功,请选择相应的功能点击体验。";
                isAuth = true;
                Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
            }else{
                result = "SDK初始化失败,错误码:" + ret;
                isAuth = false;
            }
            Log.d(TAG,result);
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "SDK初始化异常：" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();
            if (itemId == R.id.nav_schedule) {
                selectedFragment = new ScheduleFragment();
            } else if (itemId == R.id.nav_voice) {
                selectedFragment = new AsrFragment();
            } else if (itemId == R.id.nav_settings) {
                selectedFragment = new SettingsFragment();
            } else {
                selectedFragment = new HelpFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }
            return true;
        });
    }

    // 添加判断是否为模拟器的方法
    // ... existing code ...
    // 添加判断是否为模拟器的方法
    private boolean isEmulator() {
        Log.d(TAG, "设备信息：MODEL=" + android.os.Build.MODEL +
                ", MANUFACTURER=" + android.os.Build.MANUFACTURER +
                ", BRAND=" + android.os.Build.BRAND +
                ", DEVICE=" + android.os.Build.DEVICE +
                ", PRODUCT=" + android.os.Build.PRODUCT);

        boolean isEmulator = android.os.Build.MODEL.contains("Emulator") ||
                android.os.Build.MODEL.contains("OPPO") ||
                android.os.Build.MANUFACTURER.contains("OPPO") ||
                (android.os.Build.BRAND.startsWith("OPPO") && android.os.Build.DEVICE.startsWith("gracelte")) ||
                android.os.Build.PRODUCT.equals("PCRT00");

        Log.d(TAG, "是否为模拟器环境: " + isEmulator);
        return isEmulator;
    }


}