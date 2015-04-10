package cgtz.com.cgwallet.model.impl;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import cgtz.com.cgwallet.model.IsNetConnect;

/**
 * 网络是否可用接口的实现
 * Created by Administrator on 2015/3/10.
 */
public class NetConnect implements IsNetConnect{

    @Override
    public boolean isNetConnect(Context context) {
        if(context != null){
            ConnectivityManager mConnectivityManager =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if(mNetworkInfo != null){
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }
}
