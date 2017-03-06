/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package cc.milam.push;

import com.damocles.common.log.Log;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushManager;

import android.content.Context;

/**
 * Created by zhanglong02 on 17/3/6.
 */

public final class PushProxy {

    public static void init(Context context) {
        XGPushManager.registerPush(context.getApplicationContext(),
                new XGIOperateCallback() {
                    @Override
                    public void onSuccess(Object data, int flag) {
                        Log.i("push", "注册成功，设备token为：" + data);
                    }

                    @Override
                    public void onFail(Object data, int errCode, String msg) {
                        Log.e("push", "注册失败，错误码：" + errCode + ",错误信息：" + msg);
                    }
                });
    }
}
