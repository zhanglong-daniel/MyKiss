/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package cc.milam.mykiss;

import android.os.Bundle;
import android.webkit.WebView;
import android.widget.TextView;

public class H5Activity extends BaseActivity {

    private WebView mWebView;
    private TextView mRefreshTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_h5);
    }
}
