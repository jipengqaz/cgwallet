package cgtz.com.cgwallet.presenter;

import android.content.Context;

import cgtz.com.cgwallet.model.IsNetConnect;
import cgtz.com.cgwallet.model.impl.NetConnect;
import cgtz.com.cgwallet.view.ISplashView;

/**
 * 网络状态执行方法的制定者
 * Created by Administrator on 2015/3/10.
 */
public class SplashPresenter {
    private IsNetConnect connect;
    private ISplashView iView;
    public SplashPresenter(ISplashView iView){
        this.iView = iView;
        connect = new NetConnect();
    }

    /**
     * 根据网络情况，调用不同方法
     * @param context
     */
    public void didFinishLoading(Context context){
        iView.startProcessBar();//调用进度弹窗
        if(connect.isNetConnect(context)){
            iView.startNextActivity();//跳转页面或者网络交互等
        }else{
            iView.showNetError();//显示错误信息
            iView.hideProcessBar();//隐藏进度弹窗
        }
    }
}
