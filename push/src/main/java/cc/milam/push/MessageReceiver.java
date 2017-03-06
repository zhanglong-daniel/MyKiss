/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package cc.milam.push;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import cc.milam.common.*;

import com.damocles.common.log.Log;
import com.tencent.android.tpush.XGPushBaseReceiver;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushRegisterResult;
import com.tencent.android.tpush.XGPushShowedResult;
import com.tencent.android.tpush.XGPushTextMessage;

public class MessageReceiver extends XGPushBaseReceiver {

    @Override
    public void onNotifactionShowedResult(Context context, XGPushShowedResult xgPushShowedResult) {

    }

    @Override
    public void onNotifactionClickedResult(Context context, XGPushClickedResult xgPushClickedResult) {

    }

    @Override
    public void onUnregisterResult(Context context, int i) {

    }

    @Override
    public void onRegisterResult(Context context, int i, XGPushRegisterResult xgPushRegisterResult) {

    }

    @Override
    public void onSetTagResult(Context context, int i, String s) {

    }

    @Override
    public void onDeleteTagResult(Context context, int i, String s) {

    }

    // 消息透传
    @Override
    public void onTextMessage(Context context, XGPushTextMessage message) {
        // 获取自定义key-value
        String content = message.getContent();
        Log.i("push", content);
        Log.i("push", "version_code=" + cc.milam.common.BuildConfig.VERSION_CODE);
        if (!TextUtils.isEmpty(content)) {
            try {
                JSONObject jsonObject = new JSONObject(content);
                String title = jsonObject.getString("title");
                String desc = jsonObject.getString("desc");
                String url = jsonObject.getString("url");
                int versionCode = jsonObject.getInt("versioncode");
                if (versionCode > cc.milam.common.BuildConfig.VERSION_CODE) {
                    showNotify(context, title, desc, url);
                } else {
                    Log.e("push", "目标版本<=当前版本");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.printStackTrace(e);
            }
        }
    }

    private void showNotify(Context context, String title, String desc, String url) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        // title
        builder.setContentTitle(title);
        // desc
        builder.setContentText(desc);
        // url
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        Notification notification = builder.build();
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(999, notification);
    }

}
