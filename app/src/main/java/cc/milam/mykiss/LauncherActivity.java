/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package cc.milam.mykiss;

import com.tencent.stat.StatService;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import cc.milam.push.PushProxy;

public class LauncherActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        initMTA();
        initPushSdk();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(LauncherActivity.this, MainActivity.class));
                finish();
            }
        }, 800);
    }

    @Override
    public void onBackPressed() {
    }

    private void initMTA() {
        // 请在初始化时调用，参数为Application或Activity或Service
        StatService.setContext(this.getApplication());
        // 自动监控Activity生命周期，可以代替之前手动调用的onResume和onPause，防止漏打点的情况
        StatService.registerActivityLifecycleCallbacks(getApplication());
    }

    private void initPushSdk() {
        PushProxy.init(this);
    }
}
