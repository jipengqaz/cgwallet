package cgtz.com.cgwallet;

import android.app.Activity;
import android.app.Application;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/4/22.
 */
public class MApplication extends Application {
    private static ArrayList<Activity> activities = new ArrayList<>();
    private static String token ="";
    private static String user_id ="";
    public static boolean goLogin = true;//登录页面是否按了返回键
    public static boolean isSetTrade = false;//记录登录后，返回的是否设置了交易密码

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static boolean isGoLogin() {
        return goLogin;
    }

    public static void setGoLogin(boolean goLogin) {
        MApplication.goLogin = goLogin;
    }

    public static boolean isSetTrade() {
        return isSetTrade;
    }

    public static void setIsSetTrade(boolean isSetTrade) {
        MApplication.isSetTrade = isSetTrade;
    }

    /**
     * 获取token
     * @return
     */
    public static String getToken() {
        return token;
    }

    /**
     * 设置token
     * @param token
     */
    public static void setToken(String token) {
        MApplication.token = token;
    }

    /**
     * 获取id
     * @return
     */
    public static String getUser_id() {
        return user_id;
    }

    /**
     * 设置id
     * @param user_id
     */
    public static void setUser_id(String user_id) {
        MApplication.user_id = user_id;
    }

    /**
     * 存储打开的activity
     * @param activity
     */
    public synchronized static void registActivities(Activity activity){
        int size = activities.size();
        for(int i=0;i<size;i++){
            Activity activity_ = activities.get(i);
            if(activity_.getClass().getName() == activity.getClass().getName()){
                activities.remove(i);
                if(!activity_.isFinishing()){
                    activity_.finish();
                }
                break;
            }
        }
        activities.add(activity);
    }

    /**
     * 杀掉打开的Activity
     * @param activity
     */
    public synchronized static void destroyActivity(Activity activity){
        int size = activities.size();
        if(activity != null && activities != null && size != 0){
            activities.remove(activity);
            if(!activity.isFinishing()){
                activity.finish();
            }
        }
    }

    /**
     * 根据Activity获取想要的Activity
     * @param activity
     * @return
     */
    public synchronized static Activity getActivityByActivity(Activity activity){
        String name = activity.getClass().getName();
        int size = activities.size();
        for(int i=0;i<size;i++){
            Activity activity_ = activities.get(i);
            if(activity_.isFinishing()){
                continue;
            }
            if(activity_.getClass().getName() == name){
                return activity_;
            }
        }
        return null;
    }

    /**
     * 关掉所有Activity
     */
    public synchronized static void finishAllActivitys(){
        if(activities != null){
            int size = activities.size();
            for(int i=0;i<size;i++){
                Activity activity = activities.get(i);
                if(!activity.isFinishing()){
                    activity.finish();
                }
            }
        }
    }


}
