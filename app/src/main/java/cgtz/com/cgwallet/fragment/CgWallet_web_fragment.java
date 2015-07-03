package cgtz.com.cgwallet.fragment;

import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import cgtz.com.cgwallet.R;
import cgtz.com.cgwallet.utility.Constants;
import cgtz.com.cgwallet.utils.JavaScriptinterface;
import cgtz.com.cgwallet.utils.LogUtils;
import cgtz.com.cgwallet.utils.ScreenUtils;

/**
 * Created by Administrator on 2015/6/19.
 */
public class CgWallet_web_fragment extends BaseFragment{
    private String TAG = "CgWallet_web_fragment";
    private WebView webView;
    private TextView pb;
    private int Progress;
    private int width;
    private ViewGroup.LayoutParams lp;
    private String url = Constants.WALLET_INTRODUCE;

    /**
     * 刷新页面
     */
    public void reload(){
        webView.reload();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.activity_webview,container,false);
        webView = (WebView)view. findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);//可用JS
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);//优先使用缓存
        //添加手机缩放
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setSupportZoom(true);//出现缩放工具
        webView.getSettings().setUseWideViewPort(true);//扩大比例缩放
//        webView.getSettings().setLoadWithOverviewMode(true);
//        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);//滚动条风格，为0就是不给滚动条留空间，滚动条覆盖在网页上
        CookieManager.getInstance().setAcceptCookie(true);
        pb= (TextView) view.findViewById(R.id.progress_bar_2);
        /** 与js交互，JavaScriptinterface 是个接口，与js交互时用到的，
         *  这个接口实现了从网页跳到app中的activity 的
         *  方法，特别重要
         * **/
            webView.addJavascriptInterface(new JavaScriptinterface(getActivity(), webView),
                    "android");
        setClient();

        LogUtils.i(TAG,"访问路径："+url);
        webView.loadUrl(url);
        return view;
    }

    /**
     * 设置事件
     */
    private void setClient() {
        lp = pb.getLayoutParams();
        width = ScreenUtils.getScreenWidth(getActivity()) / 100;
        webView.setWebChromeClient(new WebChromeClient() {//用于修改进度条
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
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
            public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
                try {
                    Uri uri = Uri.parse(url);
                    if ("http".equalsIgnoreCase(uri.getScheme()) || "https".equalsIgnoreCase(uri.getScheme())) {
                        view.loadUrl(url); //载入网页
                        return true;
                    }
                } catch (Exception e) {
                }
                return true;
            }//重写点击动作,用webview载入
            @Override
            public void onReceivedSslError(final WebView view, SslErrorHandler handler,
                                           SslError error) {
                handler.proceed();
            }
        });
    }
}
