package com.xukui.webview.webkit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

import com.xukui.webview.R;

public class WebkitActivity extends AppCompatActivity {

    private static final String EXTRA_ADDRESS = "EXTRA_ADDRESS";

    private WebView webView;

    private String mAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webkit);
        initData();
        initView();
        setView();
    }

    private void initData() {
        mAddress = getIntent().getStringExtra(EXTRA_ADDRESS);
    }

    private void initView() {
        webView = findViewById(R.id.webView);
    }

    private void setView() {
        webView.loadUrl(mAddress);
    }

    public static Intent buildIntent(Context context, String address) {
        Intent intent = new Intent(context, WebkitActivity.class);
        intent.putExtra(EXTRA_ADDRESS, address);
        return intent;
    }

}