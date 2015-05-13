package cgtz.com.cgwallet.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import cgtz.com.cgwallet.utility.Constants;


public class ChangeLogHelper {

    final static String PREFS_FILE_NAME = "app-version";
    final static String PREFS_KEY = "app_version"+ Constants.version;
    final static String FINANCIAL_KEY = "financial";
    final static String ASSETS_KEY ="assets";

    public static boolean isTheSameVersion(Context context) {
        String savedVersion = getSaveVersion(context);

        return getVersionName(context).equals(savedVersion);
    }

    public static String getSaveVersion(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE);
        return preferences.getString(PREFS_KEY, null);
    }

    public static void saveAppVersion(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PREFS_KEY, getVersionName(context));
        editor.commit();
    }

    public static String getVersionName(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            if (pm != null) {
                PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
                return packageInfo.versionName;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

}
