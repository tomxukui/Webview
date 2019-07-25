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
    private FrameLayout frame_webview;
    private XWebView webView;
    private ProgressBar bar_loading;
    private FrameLayout frame_video;

    private XWebChromeClient mWebChromeClient;

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
        frame_webview = findViewById(R.id.frame_webview);
        webView = findViewById(R.id.webView);
        bar_loading = findViewById(R.id.bar_loading);
        frame_video = findViewById(R.id.frame_video);
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

        View loadingView = getLayoutInflater().inflate(R.layout.view_loading_video, null);

        mWebChromeClient = new XWebChromeClient(frame_webview, frame_video, loadingView, webView) {

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

        };
        mWebChromeClient.setOnToggledFullscreen(new XWebChromeClient.ToggledFullscreenCallback() {

            @Override
            public void toggledFullscreen(boolean fullscreen) {
                if (fullscreen) {
                    WindowManager.LayoutParams attrs = getWindow().getAttributes();
                    attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
                    attrs.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                    getWindow().setAttributes(attrs);

                    if (android.os.Build.VERSION.SDK_INT >= 14) {
                        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
                    }

                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

                } else {
                    WindowManager.LayoutParams attrs = getWindow().getAttributes();
                    attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
                    attrs.flags &= ~WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                    getWindow().setAttributes(attrs);

                    if (android.os.Build.VERSION.SDK_INT >= 14) {
                        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                    }

                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
            }

        });

        webView.setWebChromeClient(mWebChromeClient);
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
        if (mWebChromeClient != null && mWebChromeClient.onBackPressed()) {
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

    @Override
    protected void onResume() {
        super.onResume();
        if (webView != null) {
            webView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (webView != null) {
            webView.onPause();
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