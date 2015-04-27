package cgtz.com.cgwallet.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

/**
 * Created by Administrator on 2015/4/25 0025.
 */
public class Ke_Fu_data {
    private static String KE_FU = "ke_fu";
    public static String KEY_CONTENT="content";
    public static String KEY_WORK_TIME="work_time";
    public static String KEY_PHONE ="phone_number";
    public static String KEY_SAFE ="possession_safe";

    /**
     * 存储客服信息文案
     * @param context
     * @param map
     */
    public static void saveKe_fu_data(Context context,Map<String ,String> map){
        SharedPreferences sharedPreferences = context.getSharedPreferences(KE_FU,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if(!map.get(KEY_CONTENT).isEmpty()){
            editor.putString(KEY_CONTENT,map.get(KEY_CONTENT));
        }
        if(!map.get(KEY_WORK_TIME).isEmpty()){
            editor.putString(KEY_WORK_TIME,map.get(KEY_WORK_TIME));
        }
        if(!map.get(KEY_PHONE).isEmpty()){
            editor.putString(KEY_PHONE,map.get(KEY_PHONE));
        }
        if(!map.get(KEY_SAFE).isEmpty()){
            editor.putString(KEY_SAFE,map.get(KEY_SAFE));
        }
        editor.commit();
    }

    /**
     *  客服工作时间
     * @param context
     * @return
     */
    public static String getWorkTime(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(KE_FU, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_WORK_TIME,"");
    }
    /**
     *  客服电话  客服工作时间
     * @param context
     * @return
     */
    public static String getContent(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(KE_FU, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_CONTENT,"");
    }
    /**
     *  客服电话
     * @param context
     * @return
     */
    public static String getPhone(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(KE_FU, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_PHONE,"");
    }
    /**
     *  平台账户资金由国有银行实时监管
     * @param context
     * @return
     */
    public static String getSafe(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(KE_FU, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_SAFE,"");
    }
}
