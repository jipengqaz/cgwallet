package cgtz.com.cgwallet.Service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import cgtz.com.cgwallet.bean.JsonBean;
import cgtz.com.cgwallet.client.Get_data;
import cgtz.com.cgwallet.utility.Constants;
import cgtz.com.cgwallet.utils.DownloadingTask;
import cgtz.com.cgwallet.utils.LogUtils;
import cgtz.com.cgwallet.utils.Utils;

/**
 * 获取启动图的服务
 * Created by Administrator on 2015/4/25 0025.
 */
public class Image_download_Service extends Service {
    private String TAG = "Image_download_Service";
    private String imageUpdate ;
    private File file = new File(Constants.IMG_FILE_PATH);
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    JsonBean jsonBean = (JsonBean) msg.obj;
                    JSONObject json = null;
                    if (!Utils.filtrateCode(Image_download_Service.this, jsonBean)) {//访问出错
                        return;
                    }
                    try {
                        json = new JSONObject(jsonBean.getJsonString());
                        String url = json.optString("url");
                        downloadingAndSave(url);
                    } catch (JSONException e) {
                        LogUtils.e(TAG, "下载启动图  错误");
                        Image_download_Service.this.stopSelf();
                    }
                    break;
                case Constants.HANDLER_RL_START:
                    if(msg.obj == null){
                        return;
                    }
                    boolean downFlag = (Boolean) msg.obj;
                    if(downFlag){
                        if(file.exists()){
                            LogUtils.e(TAG, "下载开机图片到本地结果判断: " + msg.obj.toString());
                        }
                    }
                    Intent intent = new Intent();
                    intent.setAction("provinces");
                    intent.putExtra("judge_Service", 1);
                    intent.putExtra("imageUpdate", imageUpdate);
                    sendBroadcast(intent);
                    break;
            }
        }
    };
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        imageUpdate = intent.getStringExtra("imageUpdate");
        Get_data.getImage(handler);//获取启动图  下载路径
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 下载图片并保存
     */
    private void downloadingAndSave(String url){
        //判断内存卡是否存在
        if(Utils.isHaveSD()){
//            LogUtils.e("获取开机图片", "内存卡存在,更新图片");
            DownloadingTask downTask = new DownloadingTask(
                    Constants.COMPANY_FILEDIR + "/download", Constants.IMG_FILE_NAME, url, handler);
            downTask.execute();
        }
    }
}
