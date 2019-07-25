package com.xukui.webview.webkit;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xukui.webview.R;

public class WebkitActivity extends AppCompatActivity {

    private static final String TAG = "WebkitActivity";

    private static final String EXTRA_ADDRESS = "EXTRA_ADDRESS";

    private Toolbar toolbar;
    private TextView tv_title;
    private WebView webView;
    private ProgressBar bar_loading;
    private FrameLayout frame_full;

    private String mAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webkit);
        initData();
        initView();
        setActionBar();
        setView();

        webView.loadUrl(mAddress);
    }

    private void initData() {
        mAddress = getIntent().getStringExtra(EXTRA_ADDRESS);
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        tv_title = findViewById(R.id.tv_title);
        webView = findViewById(R.id.webView);
        bar_loading = findViewById(R.id.bar_loading);
        frame_full = findViewById(R.id.frame_full);
    }

    private void setActionBar() {
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);//不显示默认标题
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);//显示返回键
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onSupportBackPressed();
            }

        });
    }

    private void setView() {
        //支持chrome调试
        WebView.setWebContentsDebuggingEnabled(true);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);//设置js可以直接打开窗口，如window.open()，默认为false
        webSettings.setJavaScriptEnabled(true);//是否允许JavaScript脚本运行，默认为false。设置true时，会提醒可能造成XSS漏洞
        webSettings.setSupportZoom(true);//是否可以缩放，默认true
        webSettings.setBuiltInZoomControls(true);//是否显示缩放按钮，默认false
        webSettings.setUseWideViewPort(true);//设置此属性，可任意比例缩放。大视图模式
        webSettings.setLoadWithOverviewMode(true);//和setUseWideViewPort(true)一起解决网页自适应问题
        webSettings.setAppCacheEnabled(true);//是否使用缓存
        webSettings.setDomStorageEnabled(true);//开启本地DOM存储
        webSettings.setLoadsImagesAutomatically(true);//加载图片
        webSettings.setMediaPlaybackRequiresUserGesture(false);//播放音频，多媒体需要用户手动？设置为false为可自动播放
        //允许使用File协议
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        //支持http和https混合加载
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        webView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (tv_title != null) {
                    tv_title.setText(title);
                }
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (bar_loading != null) {
                    if (newProgress >= 100) {
                        bar_loading.setVisibility(View.GONE);

                    } else {
                        bar_loading.setVisibility(View.VISIBLE);
                        bar_loading.setProgress(newProgress);
                    }
                }
            }

            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                super.onShowCustomView(view, callback);
                if (frame_full == null || frame_full.getChildCount() > 0) {
                    return;
                }

                showFullView(view);
            }

            @Override
            public void onHideCustomView() {
                super.onHideCustomView();
                if (frame_full == null || frame_full.getChildCount() == 0) {
                    return;
                }

                hideFullView();
            }

        });
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Log.d(TAG, "开始加载网页: " + url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d(TAG, "加载网页已结束: " + url);
            }

        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (frame_full != null && frame_full.getChildCount() > 0) {//正处于全屏状态
            hideFullView();
            return true;
        }

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onSupportBackPressed();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private void onSupportBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
            return;
        }

        onBackPressed();
    }

    /**
     * 显示全屏控件
     */
    private void showFullView(View view) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (webView != null) {
            webView.setVisibility(View.GONE);
        }

        if (frame_full != null) {
            frame_full.setVisibility(View.VISIBLE);
            frame_full.addView(view);
            frame_full.bringToFront();
        }
    }

    /**
     * 隐藏全屏控件
     */
    private void hideFullView() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (webView != null) {
            webView.setVisibility(View.VISIBLE);
            webView.reload();
        }

        if (frame_full != null) {
            frame_full.setVisibility(View.GONE);
            frame_full.removeAllViews();
        }
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            webView.clearHistory();
            ViewParent viewParent = webView.getParent();
            if (viewParent != null && viewParent instanceof ViewGroup) {
                ((ViewGroup) viewParent).removeView(webView);
            }
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }

    public static Intent buildIntent(Context context, String address) {
        Intent intent = new Intent(context, WebkitActivity.class);
        intent.putExtra(EXTRA_ADDRESS, address);
        return intent;
    }

}