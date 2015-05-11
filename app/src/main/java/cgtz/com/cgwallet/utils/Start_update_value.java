package cgtz.com.cgwallet.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/4/25 0025.
 */
public class Start_update_value {
    private  static String PREFS_FILE_NAME = "start_update";
    public final static  String KEY_IMAGE_UPDATE = "ImageUpdate";//启动图
    public final static String KEY_KEFU_UPDATE = "kefuUpdate";//客服
    public final static String KEY_CITY_UPDATE = "ProvinceCityUpdate";//支行信息

    public final static String KEY_SHARE = "share";//分享信息
    public final static String KEY_QR_CODE ="Qr_code";//二维码  信息



    /**
     * 返回所有的更新时间数据
     * @param context
     * @return
     */
    public static Map<String ,String > getUpdateTime(Context context){
        SharedPreferences  preferences = context.getSharedPreferences(PREFS_FILE_NAME,Context.MODE_PRIVATE);
        Map <String ,String > map = new HashMap<String ,String >();
        map.put(KEY_IMAGE_UPDATE,preferences.getString(KEY_IMAGE_UPDATE,""));
        map.put(KEY_KEFU_UPDATE, preferences.getString(KEY_KEFU_UPDATE, ""));
        map.put(KEY_CITY_UPDATE, preferences.getString(KEY_CITY_UPDATE, ""));
        return map;
    }

    public static HashMap<String ,String > getShare(Context context){
        SharedPreferences  preferences = context.getSharedPreferences(PREFS_FILE_NAME,Context.MODE_PRIVATE);
        HashMap <String ,String > map = new HashMap<String ,String >();
        map.put(KEY_SHARE, preferences.getString(Utils.getUserPhone(context)+KEY_SHARE, ""));
        map.put(KEY_QR_CODE, preferences.getString(Utils.getUserPhone(context)+KEY_QR_CODE, ""));
        return map;
    }

    /**
     * 存储分享数据
     * @param context
     * @param share   分享文案
     * @param Qr_code 二维码
     */
    public static void saveShare(Context context,String share,String  Qr_code){
        SharedPreferences preferences = context.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Utils.getUserPhone(context)+KEY_SHARE, share);
        editor.putString(Utils.getUserPhone(context)+KEY_QR_CODE, Qr_code);
        editor.commit();
    }
    /**
     * 存储启动图最后更新时间
     * @param time 时间
     */
    public static void saveImageUpdate(Context context,String time){
        SharedPreferences preferences = context.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_IMAGE_UPDATE, time);
        editor.commit();
    }

    /**
     * 存储客服信息最后更新时间
     * @param time 时间
     */
    public static void saveKeFuUpdate(Context context,String time){
        SharedPreferences preferences = context.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_KEFU_UPDATE,time);
        editor.commit();
    }
    /**
     * 存储银行信息最后更新时间
     * @param time 时间
     */
    public static void saveProvinceCityUpdate(Context context,String time){
        SharedPreferences preferences = context.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_CITY_UPDATE,time);
        editor.commit();
    }
}
