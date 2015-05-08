package cgtz.com.cgwallet.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import cgtz.com.cgwallet.Service.Image_download_Service;
import cgtz.com.cgwallet.Service.Provinces_download_Service;
import cgtz.com.cgwallet.utils.LogUtils;
import cgtz.com.cgwallet.utils.Start_update_value;


/**
 * 用于关闭下载省市和启动图的服务的
 * Created by Administrator on 2014/11/8.
 */
public class Provinces_Observer extends BroadcastReceiver {
    private String TAG = "Provinces_Observer";
    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtils.e("123456",intent.getBooleanExtra("judge_Service",false)+"");
        if(intent.getBooleanExtra("judge_Service",false)){
            context.stopService(new Intent(context, Image_download_Service.class));//停止下载图片服务
            Start_update_value.saveImageUpdate(context,intent.getStringExtra("imageUpdate"));//存储判断值
        }else{
            context.stopService(new Intent(context, Provinces_download_Service.class));//停止下载省市服务
            Start_update_value.saveProvinceCityUpdate(context, intent.getStringExtra("provinceCityUpdate"));//存储判断值
         }
    }
}
