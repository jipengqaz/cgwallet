/**
 *
 */
package cgtz.com.cgwallet.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class AppUtil {

    /**
     * 获取屏幕分辨率
     * @param context
     * @return
     */
    public static int[] getScreenDispaly(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        //获取屏幕信息
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;;// 手机屏幕的宽度
        int height =dm.heightPixels;// 手机屏幕的高度
        int result[] = { width, height };
        return result;
    }

}
