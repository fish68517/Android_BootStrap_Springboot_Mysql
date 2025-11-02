package com.archive.app;

import android.content.Context;
import android.util.Log;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.maps.MapsInitializer;
import com.archive.app.model.User;
import com.hjq.permissions.OnPermission;
import com.hjq.permissions.XXPermissions;
import com.iflytek.sparkchain.core.LogLvl;
import com.iflytek.sparkchain.core.SparkChain;
import com.iflytek.sparkchain.core.SparkChainConfig;

import java.util.List;

public class MyApplication extends android.app.Application{


    public static User curUser;

    private static final String TAG = "MyApplication";
    private Context context;



    public static void setUser(User user) {

        // save user to shared preferences or database or any other storage
        curUser = user;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize your application here
        context = getApplicationContext();
        // 初始化高德地图SDK隐私合规：在调用任何SDK功能之前调用
        // 务必确保用户已经同意了隐私政策，并且您已在用户同意后再进行SDK初始化
        // 实际项目中，您可能需要在用户点击“同意”按钮后才调用这些方法
       /* MapsInitializer.updatePrivacyShow(this, true, true);
        MapsInitializer.updatePrivacyAgree(this, true);*/
        AMapLocationClient.updatePrivacyShow(this, true, true);
        AMapLocationClient.updatePrivacyAgree(this, true);

        MapsInitializer.updatePrivacyShow(context,true,true);
        MapsInitializer.updatePrivacyAgree(context,true);


    }



}
