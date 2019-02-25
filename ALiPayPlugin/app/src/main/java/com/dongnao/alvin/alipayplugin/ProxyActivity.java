package com.dongnao.alvin.alipayplugin;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;

import com.dongnao.alvin.pluginstand.PayInterfaceActivity;

import java.lang.reflect.Constructor;

/**
 * Created by Administrator on 2018/3/28.
 */

public class ProxyActivity  extends Activity {
//    需要加载淘票票的  类名
    private String className;
    PayInterfaceActivity payInterfaceActivity;
    // com.dongnao.alvin.taopiaopiao.MainActivity
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState  ) {
        super.onCreate(savedInstanceState );
        className = getIntent().getStringExtra("className");
//        class

        try {
            Class activityClass = getClassLoader().loadClass(className);
            Constructor constructor = activityClass.getConstructor(new Class[]{});
            Object instance= constructor.newInstance(new Object[]{});
//          可以
            payInterfaceActivity = (PayInterfaceActivity) instance;

            payInterfaceActivity.attach(this);
            Bundle bundle = new Bundle();
            payInterfaceActivity.onCreate(bundle);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startActivity(Intent intent) {
        String className1=intent.getStringExtra("className");
        Intent intent1 = new Intent(this, ProxyActivity.class);
        intent1.putExtra("className", className1);
        super.startActivity(intent1);
    }

    @Override
    public ComponentName startService(Intent service) {
        String serviceName = service.getStringExtra("serviceName");
        Intent intent1 = new Intent(this, ProxyService.class);
        intent1.putExtra("serviceName", serviceName);
        return super.startService(intent1);
    }

    @Override
    public ClassLoader getClassLoader() {
        return PluginManager.getInstance().getDexClassLoader();
    }

    @Override
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
//
        IntentFilter newInterFilter = new IntentFilter();
        for (int i=0;i<filter.countActions();i++) {
            newInterFilter.addAction(filter.getAction(i));
        }
        return super.registerReceiver(new ProxyBroadCast(receiver.getClass().getName(),this),newInterFilter);
    }

    @Override
    public Resources getResources() {
        return PluginManager.getInstance().getResources();
    }


    @Override
    protected void onStart() {
        super.onStart();
        payInterfaceActivity.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        payInterfaceActivity.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        payInterfaceActivity.onPause();
    }
}
