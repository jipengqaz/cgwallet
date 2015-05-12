package cgtz.com.cgwallet.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

import java.util.Map;

import cgtz.com.cgwallet.bean.JsonBean;
import cgtz.com.cgwallet.utility.Constants;

/**
 * 自定义Task线程
 * Created by Administrator on 2014/11/25.
 */
public class CustomTask extends AsyncTask<String,Void,String> {
    private Handler handler;//回调Handler
    private String url_;//访问路径
    private boolean flag;//判断get或者post方法  get为false,post为true
    private String content;//post传递的参数
    private String encode = Constants.ENCONDING;//编码
    private int handler_what;//回调handler的 what判断值
    private Context mContext;//所在页面
    private boolean isFinish;//判断是否要关闭所在页面
    /**
     *
     * @param handler
     * @param handler_what  handler 判断值
     * @param url_          接口
     * @param isPost        判断是否为post访问   true为post
     * @param maps          参数集合
     * @param encode        文字编码
     */
    public CustomTask(Handler handler,int handler_what,String url_,
                      boolean isPost,Map<String,String> maps,String encode){
        this.handler = handler;
        this.handler_what = handler_what;
        this.flag = isPost;
        this.url_ = (Constants.IS_TEST?Constants.OFFLINE_HTTP:Constants.ONLINE_HTTP) + url_;
        this.encode = encode;
        if(flag){
            content = HttpUtils.getRequestData(maps,encode).toString();
        }
        LogUtils.e("CustomTask", "url: " + this.url_+" content: "+content);
    }
    /**
     *
     * @param handler
     * @param handler_what  handler 判断值
     * @param url_          接口
     * @param isPost        判断是否为post访问   true为post
     * @param maps          参数集合
     * @param is_ping       判断是否拼接路径  true为拼接
     */
    public CustomTask(Handler handler,int handler_what,String url_,
                      boolean isPost,Map<String,String> maps,boolean is_ping){
        this.handler = handler;
        this.handler_what = handler_what;
        this.flag = isPost;
        if(is_ping) {
            this.url_ = (Constants.IS_TEST?Constants.OFFLINE_HTTP:Constants.ONLINE_HTTP) + url_;
        }else{
            this.url_ = Constants.IS_TEST?Constants.OFFLINE_HTTP:Constants.ONLINE_HTTP;
        }
        if(flag){
            content = HttpUtils.getRequestData(maps,encode).toString();
        }
        LogUtils.e("CustomTask", "url: " + this.url_+" content: "+content);
    }
    /**
     *
     * @param handler
     * @param handler_what  handler 判断值
     * @param url_          接口
     * @param isPost        判断是否为post访问   true为post
     * @param maps          参数集合
     * @param encode        文字编码
     * @param is_ping       判断是否拼接路径  true为拼接
     */
    public CustomTask(Handler handler,int handler_what,String url_,
                      boolean isPost,Map<String,String> maps,String encode,boolean is_ping){
        this.handler = handler;
        this.handler_what = handler_what;
        this.flag = isPost;
        this.flag = true;
        if(is_ping) {
            this.url_ = (Constants.IS_TEST?Constants.OFFLINE_HTTP:Constants.ONLINE_HTTP) + url_;
        }else{
            this.url_ = Constants.IS_TEST?Constants.OFFLINE_HTTP:Constants.ONLINE_HTTP;
        }
        this.encode = encode;
        if(flag){
            content = HttpUtils.getRequestData(maps,encode).toString();
        }
        LogUtils.e("CustomTask", "url: " + this.url_+" content: "+content);
    }

    @Override
    protected String doInBackground(String... params) {
        if(flag){
            if(Constants.IS_TEST){
                return HttpUtils.HttpPost(url_,content,encode);
            }else{
                return HttpUtils.HttpsPost(url_, content, encode);
            }
        }else{
            if(Constants.IS_TEST){
                return HttpUtils.HttpGet(url_, encode);
            }else{
                return HttpUtils.HttpsGet(url_, encode);
            }
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        JsonBean jsonBean = new JsonBean(s);
        handler.sendMessage(handler.obtainMessage(handler_what,jsonBean));
    }
}
