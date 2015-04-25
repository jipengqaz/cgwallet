package cgtz.com.cgwallet.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2015/4/25 0025.
 */
public class Start_update_value {
    private static String PREFS_FILE_NAME = "start_update";
    private static  String KEY_IMAGE_UPDATE = "ImageUpdate";
    private static String KEY_KEFU_UPDATE = "kefuUpdate";
    private static String KEY_CITY_UPDATE = "ProvinceCityUpdate";
    /**
     * 存储启动图最后更新时间
     * @param time 时间
     */
    public static void saveImageUpdate(Context context,String time){
        SharedPreferences preferences = context.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_IMAGE_UPDATE,time);
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
