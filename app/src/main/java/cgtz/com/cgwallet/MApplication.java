package cgtz.com.cgwallet;

import android.app.Activity;
import android.app.Application;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/4/22.
 */
public class MApplication extends Application {
    private static ArrayList<Activity> activities = new ArrayList<>();


    @Override
    public void onCreate() {
        super.onCreate();
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
    public Activity getActivityByActivity(Activity activity){
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


}
