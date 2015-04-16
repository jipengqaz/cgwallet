package cgtz.com.cgwallet.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.List;


/**
 * 自定义Sharedpreferences
 * Created by Administrator on 2014/10/15.
 */
public class SharedUtils {
    private Context context;
    private String configxml = "";
    private SharedPreferences sp;
    public SharedPreferences.Editor spe;
    public SharedUtils(Context context, String configxml){
        this.context = context;
        this.configxml = configxml;
        sp = context.getSharedPreferences(configxml,Context.MODE_PRIVATE);
        spe = sp.edit();
    }

    /**
     * clear记录
     */
    public void clearSp(){
        spe.clear();
        spe.commit();
    }

    /**
     * 循环删除内容
     * @param keys
     */
    public void removeList(String[] keys){
        for(String key:keys){
            remove(key);
        }
    }

    /**
     * 删除内容
     * @param key
     */
    public void remove(String key){
        spe.remove(key);
        spe.commit();
    }

    public void saveBoolean(String key,Boolean value){
        spe.putBoolean(key,value);
        spe.commit();
    }

    public Boolean getBoolean(String key,Boolean value){
        return sp.getBoolean(key,value);
    }

    public void saveFloat(String key,Float value){
        spe.putFloat(key,value);
        spe.commit();
    }

    public Float getFloat(String key,Float value){
        return sp.getFloat(key,value);
    }

    public void saveInt(String key,Integer value){
        spe.putInt(key,value);
        spe.commit();
    }

    public Integer getFloat(String key,Integer value){
        return sp.getInt(key,value);
    }

    public void saveLong(String key,Long value){
        spe.putLong(key,value);
        spe.commit();
    }

    public Long getFloat(String key,Long value){
        return sp.getLong(key,value);
    }

    public void saveString(String key,String value){
        spe.putString(key,value);
        spe.commit();
    }

    public String getString(String key,String value){
        return sp.getString(key,value);
    }

}
