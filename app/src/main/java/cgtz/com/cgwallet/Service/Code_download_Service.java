package cgtz.com.cgwallet.Service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import org.json.JSONObject;

import cgtz.com.cgwallet.bean.JsonBean;
import cgtz.com.cgwallet.client.Get_share_content;
import cgtz.com.cgwallet.utils.HttpUtils;
import cgtz.com.cgwallet.utils.LogUtils;
import cgtz.com.cgwallet.utils.Start_update_value;
import cgtz.com.cgwallet.utils.Utils;

/**
 * Created by Administrator on 2015/5/9 0009.
 */
public class Code_download_Service extends Service{
    private String TAG="Code_download_Service";
    private String qrcode;//获取二维码的路径
    private JSONObject json ;
    private String res;//二维码
    private Handler handler = new Handler(){//获取分享内容的
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    JsonBean jsonBean = (JsonBean) msg.obj;
                    int code = jsonBean.getCode();
                    String errorMsg = jsonBean.getError_msg();
                    json = jsonBean.getJsonObject();
                    LogUtils.e(TAG,json+"");
                    if(json.optInt("success") == 1) {
                        qrcode = json.optString("qrcode");
                        new Thread(connectNet).start();
                    }
                    break;
                case 1:
                    Intent intent = new Intent();
                    intent.setAction("provinces");
                    intent.putExtra("judge_Service", 3);
                    sendBroadcast(intent);
//                    if (bitmap != null) {
//                        Qr_code.setImageBitmap(bitmap);// 显示获取的二维码
//                    }
                    break;
            }
        }
    };

    //获取二维码的线程
    private Runnable connectNet = new Runnable(){
        @Override
        public void run() {
            //取得的是byte数组, 从byte数组生成bitmap
            try {
                byte[] data = HttpUtils.getImage(qrcode);
                LogUtils.e(TAG,data.length+"");
                res = new String(data,"ISO8859-1");
                Start_update_value.saveShare(Code_download_Service.this, json + "", res);
//                if (data != null) {
//                    bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);// bitmap
//                } else {
//
//                }
                LogUtils.e(TAG, "下载二维码  ");
                handler.sendEmptyMessage(1);
            } catch (Exception e) {
                LogUtils.e(TAG, "下载二维码  错误");
                Code_download_Service.this.stopSelf();
            }
        }
    };
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String userId = Utils.getUserId();
        String token = Utils.getToken();
        Get_share_content.getContent(handler,userId,token);
        return super.onStartCommand(intent, flags, startId);
    }
}
