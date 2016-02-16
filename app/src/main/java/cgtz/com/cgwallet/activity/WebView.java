package cgtz.com.cgwallet.activity;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import cgtz.com.cgwallet.MApplication;
import cgtz.com.cgwallet.R;
import cgtz.com.cgwallet.utility.Constants;
import cgtz.com.cgwallet.utils.JavaScriptinterface;
import cgtz.com.cgwallet.utils.LogUtils;
import cgtz.com.cgwallet.utils.ScreenUtils;

/**
 * Created by chen on 2016-1-21.
 */
public class WebView extends BaseActivity {
    private String TAG = "WebViewActivity";
    public android.webkit.WebView webView;
    private TextView pb;
    private int Progress;
    private int width;
    private ViewGroup.LayoutParams lp;
    private String title;
    private String url;
    private int type;
    private long id;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        showBack(true);
        setBackListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backListener();
            }
        });
        MApplication.registActivities(this);//存储该activity
        webView = (android.webkit.WebView) findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);//可用JS
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        //添加手机缩放
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setSupportZoom(true);//出现缩放工具
        webView.getSettings().setUseWideViewPort(true);//扩大比例缩放
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);//优先使用缓存
        //2016年1月26日10:27:48  覆盖的返回键
        Button bt = new Button(this);
        bt.setBackgroundColor(Color.TRANSPARENT);
        webView.addView(bt);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backListener();
            }
        });
        intent = getIntent();

        /** 与js交互，JavaScriptinterface 是个接口，与js交互时用到的，
         *  这个接口实现了从网页跳到app中的activity 的
         *  方法，特别重要
         * **/
        webView.addJavascriptInterface(new JavaScriptinterface(this, webView),
                "android");

        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);//滚动条风格，为0就是不给滚动条留空间，滚动条覆盖在网页上
        CookieManager.getInstance().setAcceptCookie(true);
        pb = (TextView) findViewById(R.id.progress_bar_2);
        lp = pb.getLayoutParams();
        width = ScreenUtils.getScreenWidth(this) / 100;
        webView.setWebChromeClient(new WebChromeClient() {//用于修改进度条
            @Override
            public void onProgressChanged(android.webkit.WebView view, int newProgress) {
                Progress = newProgress;
                if (newProgress < 100) {
                    lp.width = width * Progress;
                    pb.setLayoutParams(lp);
                } else {
                    pb.setVisibility(View.GONE);
                }
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(final android.webkit.WebView view, final String url) {
                try {
                    Uri uri = Uri.parse(url);
                    if ("http".equalsIgnoreCase(uri.getScheme()) || "https".equalsIgnoreCase(uri.getScheme())) {
                        view.loadUrl(url); //载入网页
                        if (getIntent().getBooleanExtra("reCharge", false)) {//
                            if (url.startsWith(Constants.ONLINE_HTTP + "util/paySuccess") || url.startsWith(Constants.ONLINE_HTTP + "util/payFail")) {
                                if (intent.getBooleanExtra("orderFrom", false)) {
                                    Intent orderDetailIntent = new Intent(WebView.this, OrderMobiledetailActivity.class);
                                    orderDetailIntent.putExtra("status", "2");
                                    orderDetailIntent.putExtra("userNo", intent.getStringExtra("userNo"));
                                    startActivity(orderDetailIntent);
                                } else {
                                    Intent mobileDetail = new Intent(WebView.this, MobiledetailActivity.class);
                                    mobileDetail.putExtra("code", intent.getIntExtra("code", 0));
                                    mobileDetail.putExtra("pay", intent.getStringExtra("pay"));
                                    mobileDetail.putExtra("operator", intent.getStringExtra("operator"));
                                    mobileDetail.putExtra("orderNo", intent.getStringExtra("orderNo"));
                                    mobileDetail.putExtra("time", intent.getStringExtra("time"));
                                    mobileDetail.putExtra("parValue", intent.getStringExtra("parValue"));
                                    mobileDetail.putExtra("userNumber", intent.getStringExtra("userNumber"));
                                    mobileDetail.putExtra("payMethod", intent.getIntExtra("payMethod", 0));
                                    startActivity(mobileDetail);
                                }
                            }
                        } else {
                            if (url.startsWith(Constants.ONLINE_HTTP + "util/paySuccess") || url.startsWith(Constants.ONLINE_HTTP + "util/payFail")) {//http://cgupload.applinzi.com/success.php
                                Intent intent = new Intent(getApplicationContext(), WaterdetailActivity.class);//http://172.16.34.188:45680/wallet2/show/payFail
                                intent.putExtra("jdCode", true);
                                intent.putExtra("jdOrderNo", getIntent().getStringExtra("orderNo"));
                                startActivity(intent);
                            }
                        }
                        return true;
                    }
                } catch (Exception e) {
                }
                return true;
            }//重写点击动作,用webview载入

            @Override
            public void onReceivedSslError(final android.webkit.WebView view, SslErrorHandler handler,
                                           SslError error) {
                handler.proceed();
            }
        });
        if (savedInstanceState != null) {
            id = savedInstanceState.getLong("projectID", 0);
            type = savedInstanceState.getInt("type", 0);
            if (type != 0) {
                webView.loadUrl(url);
            } else {
                url = savedInstanceState.getString("url");
                title = savedInstanceState.getString("title");
                setTitle(title);
                webView.loadUrl(url);
            }
        } else {
            Intent intent = getIntent();
            id = intent.getLongExtra("projectID", 0);
            type = intent.getIntExtra("type", 0);
            if (type != 0) {
//                    webView.loadUrl(url);
            } else {
                url = getIntent().getStringExtra("url");
                title = getIntent().getStringExtra("title");
                setTitle(title);
                webView.loadUrl(url);
            }
        }
        LogUtils.i(TAG, "访问路径：" + url);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return true;
    }


    @Override
    public void onBackPressed() {
//  如果是从话费充值的订单列表进入到WebView并且点击返回退出时
        if (intent.getBooleanExtra("orderFrom", false)) {
            if (webView.canGoBack()) {
                webView.goBack(); // goBack()表示返回WebView的上一页面
            } else {
                startActivity(new Intent(getApplicationContext(), OrderformActivity.class));
                finish();
            }
        }
        if (webView.canGoBack()) {
            webView.goBack(); // goBack()表示返回WebView的上一页面
        } else {
            MApplication.destroyActivity(WebView.this);
            finish();
        }
    }

//  2016年1月29日16:19:06  测试
//  对返回进行处理（ActionBar返回键）
    public void backListener() {
//      如果是从话费充值的订单列表进入到WebView并且点击返回退出时
        if (intent.getBooleanExtra("orderFrom", false)) {
            if (webView.canGoBack()) {
                webView.goBack(); // goBack()表示返回WebView的上一页面
            } else {
                startActivity(new Intent(getApplicationContext(), OrderformActivity.class));
                finish();
            }
        }
        if (webView.canGoBack()) {
            webView.goBack(); // goBack()表示返回WebView的上一页面
        } else {
            MApplication.destroyActivity(WebView.this);
            finish();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * Activity被系统杀死时被调用.
     * 例如:屏幕方向改变时,Activity被销毁再重建;当前Activity处于后台,系统资源紧张将其杀死.
     * 另外,当跳转到其他Activity或者按Home键回到主屏时该方法也会被调用,系统是为了保存当前View组件的状态.
     * 在onPause之前被调用.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("url", url);
        outState.putString("title", title);
        outState.putLong("projectID", id);
        outState.putInt("type", type);
    }
}
