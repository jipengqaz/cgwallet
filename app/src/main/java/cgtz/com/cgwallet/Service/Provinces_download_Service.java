package cgtz.com.cgwallet.Service;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cgtz.com.cgwallet.bean.JsonBean;
import cgtz.com.cgwallet.client.Get_data;
import cgtz.com.cgwallet.utility.Constants;
import cgtz.com.cgwallet.utils.Utils;


/**
 * 获取省市信息服务
 * Created by Administrator on 2014/11/8.
 */
public class Provinces_download_Service extends Service {
    private String TAG = "Provinces_download_Service";
    private String provinceCityUpdate;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        provinceCityUpdate = intent.getStringExtra("provinceCityUpdate");
        getProvinceAndCities();
        return super.onStartCommand(intent, flags, startId);
    }
    /**
     * 获取省和市
     * */
    private void getProvinceAndCities(){
        Get_data.getProvince(new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                JsonBean jsonBean = (JsonBean) msg.obj;
                JSONObject  json = null;
                if(!Utils.filtrateCode(Provinces_download_Service.this, jsonBean)){//访问出错
                    return;
                }
                try {
                    json = new JSONObject(jsonBean.getJsonString());
                    int success = json.optInt("success");
                    if (success == 1) {
                        SharedPreferences preferences = Provinces_download_Service.this.getSharedPreferences(Constants.PROVINCES, Context.MODE_PRIVATE);//获得存放文件
                        SharedPreferences.Editor editor = preferences.edit();
                        JSONObject provinces = json.optJSONObject("provinces");//获得所有的数据
                        JSONArray test = provinces.optJSONArray("values");//获得所有的省份
                        editor.putString(Constants.PROVINCES_XML, test + "");
                        JSONArray array = json.optJSONArray("cities");
                        int arr = array.length();
                        for (int i = 0; i < arr; i++) {
                            try {
                                JSONObject test2 = (JSONObject) array.get(i);
                                String name = test2.optString("province");
                                JSONArray test1 = test2.optJSONArray("values");
                                editor.putString(name, test1 + "");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        editor.commit();
                    } else {

                    }
                    Intent intent = new Intent();
                    intent.setAction("provinces");
                    intent.putExtra("provinceCityUpdate",provinceCityUpdate);
                    sendBroadcast(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
