package cgtz.com.cgwallet.utils;

import android.util.Log;

/**
 * Created by Administrator on 2015/4/10.
 */
public class LogUtils {

    public static void i(String tag,String msg){
        Log.i(tag,msg);
    }

    public static void d(String tag,String msg){
        Log.d(tag, msg);
    }

    public static void e(String tag,String msg){
        Log.e(tag,msg);
    }
}
