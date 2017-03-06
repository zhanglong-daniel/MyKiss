/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package cc.milam.mykiss;

import java.util.Map;

import org.json.JSONObject;

import com.damocles.common.log.Log;
import com.damocles.common.network.http.HttpCallback;
import com.damocles.common.network.http.HttpManager;
import com.damocles.common.util.DoubleClickExit;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends BaseActivity implements DoubleClickExit.Callback {

    private static final String URL_DEFAULT = "http://www.milam.cc/mkmz/";

    private static final String URL_CUSTOMER = "http://www.milam.cc/mk";

    private TextView mTitleTextView;
    private WebView mWebView;
    private TextView mRefreshTextView;
    private ImageView mBackImageView;
    private ProgressBar mProgressBar;

    private DoubleClickExit mDoubleClickExit;

    private String mCity = null;

    private boolean mNeedClearHistory = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        load();
    }

    @Override
    public void onBackPressed() {
        if (mWebView != null && mWebView.canGoBack()) {
            mNeedClearHistory = true;
            load();
        } else {
            if (mDoubleClickExit == null) {
                mDoubleClickExit = new DoubleClickExit(this);
                mDoubleClickExit.setClickInterval(2000);
                mDoubleClickExit.setClickTips("再按一次退出" + getString(R.string.app_name));
            }
            mDoubleClickExit.execute(this);
        }
    }

    @Override
    public void onExit() {
        finish();
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    private void initViews() {
        initWebView();
        mTitleTextView = (TextView) findViewById(R.id.main_title);
        mBackImageView = (ImageView) findViewById(R.id.main_back);
        mBackImageView.setVisibility(View.GONE);
        mBackImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mWebView != null && mWebView.canGoBack()) {
                    mWebView.goBack();
                } else {
                    mBackImageView.setVisibility(View.GONE);
                }
            }
        });
        mProgressBar = (ProgressBar) findViewById(R.id.main_progressbar);
        mRefreshTextView = (TextView) findViewById(R.id.main_txt_refresh);
        mRefreshTextView.setVisibility(View.GONE);
        mRefreshTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRefreshTextView.setVisibility(View.GONE);
                mWebView.setVisibility(View.VISIBLE);
                load();
            }
        });
    }

    private void initWebView() {
        mWebView = (WebView) findViewById(R.id.main_webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setSavePassword(true);
        mWebView.getSettings().setSaveFormData(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setSupportMultipleWindows(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView webView, String url, Bitmap favicon) {
                if (webView.canGoBack()) {
                    mBackImageView.setVisibility(View.VISIBLE);
                } else {
                    mBackImageView.setVisibility(View.GONE);
                }
                super.onPageStarted(webView, url, favicon);
            }

            @Override
            public void onPageFinished(WebView webView, String url) {
                if (mWebView.canGoBack()) {
                    mBackImageView.setVisibility(View.VISIBLE);
                } else {
                    mBackImageView.setVisibility(View.GONE);
                }
                super.onPageFinished(webView, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                WebView.HitTestResult hitTestResult = view.getHitTestResult();
                Log.i("MyKiss", "url=" + url);
                if (!TextUtils.isEmpty(url)) {
                    if (url.startsWith("http")) {
                        if (url.startsWith("http://mobile.qq.com/3g")) {
                            return true;
                        }
                        if (hitTestResult == null) {
                            mWebView.loadUrl(url);
                        }
                    } else {
                        try {
                            Uri uri = Uri.parse(url);
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return true;
                    }
                }
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
                super.doUpdateVisitedHistory(view, url, isReload);
                if (mNeedClearHistory) {
                    mNeedClearHistory = false;
                    mWebView.clearHistory();
                }

            }
        });
        mWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onReceivedTitle(WebView view, String title) {
                Log.i("MyKiss", "title=" + title);
                if (mWebView.canGoBack()) {
                    if (!TextUtils.isEmpty(title)) {
                        if (title.length() > 8) {
                            title = title.substring(0, 8);
                        }
                        mTitleTextView.setText(title);
                    }
                } else {
                    mTitleTextView.setText(R.string.app_name);
                }
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress < 100) {
                    mProgressBar.setVisibility(View.VISIBLE);
                    mProgressBar.setProgress(newProgress);
                } else {
                    mProgressBar.setVisibility(View.GONE);
                }
                if (mWebView.canGoBack()) {
                    mBackImageView.setVisibility(View.VISIBLE);
                } else {
                    mBackImageView.setVisibility(View.GONE);
                }
            }
        });
    }

    private void load() {
        // 1 网络不通
        if (!isNetworkConnected()) {
            mWebView.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.GONE);
            mRefreshTextView.setVisibility(View.VISIBLE);
            return;
        }
        mWebView.setVisibility(View.VISIBLE);
        mRefreshTextView.setVisibility(View.GONE);
        // 2 未检测城市
        if (mCity == null) {
            checkCity();
            return;
        }
        // 3 判断城市
        if ("".equals(mCity) || mCity.matches(".*(北京|上海|深圳|成都).*")) {
            loadHtml(URL_DEFAULT);
        } else {
            loadHtml(URL_CUSTOMER);
        }
    }

    /**
     * 检测当前城市
     */
    private void checkCity() {
        String url = "http://int.dpool.sina.com.cn/iplookup/iplookup.php?format=json";
        HttpManager.get(url, new HttpCallback() {
            @Override
            public void onError(String s) {
                Log.e("error", "get city failed！");
                mCity = "";
                load();
            }

            @Override
            public void onSuccess(int i, String s) {
                mCity = "";
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    mCity = jsonObject.getString("city");
                    Log.i("city=" + mCity);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("error", "get city failed！");
                }
                load();
            }

            @Override
            public void onCookies(Map<String, String> map) {

            }
        });
    }

    private void loadHtml(String url) {
        mWebView.loadUrl(url);
    }

    public boolean isNetworkConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
        return activeInfo != null && activeInfo.isConnected();
    }
}
