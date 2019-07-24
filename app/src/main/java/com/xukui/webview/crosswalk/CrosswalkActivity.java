package com.xukui.webview.crosswalk;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.xukui.webview.R;

import org.xwalk.core.XWalkView;

public class CrosswalkActivity extends AppCompatActivity {

    private static final String EXTRA_ADDRESS = "EXTRA_ADDRESS";

    private XWalkView webView;

    private String mAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crosswalk);
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
        Intent intent = new Intent(context, CrosswalkActivity.class);
        intent.putExtra(EXTRA_ADDRESS, address);
        return intent;
    }

}