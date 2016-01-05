package cgtz.com.cgwallet;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import org.json.JSONObject;
import org.ryan.LiteApplication;

import java.util.ArrayList;
import java.util.HashMap;

import cgtz.com.cgwallet.bean.JsonBean;
import cgtz.com.cgwallet.utility.Constants;
import cgtz.com.cgwallet.utils.CustomTask;
import cgtz.com.cgwallet.utils.LogUtils;
import cgtz.com.cgwallet.utils.Utils;
import cn.jpush.android.api.JPushInterface;

/**
 * 自定义Application
 * Created by Administrator on 2015/4/22.
 */
public class MApplication extends LiteApplication {
    private static ArrayList<Activity> activities = new ArrayList<>();
    private static String token = "";
    private static String user_id = "";
    public static boolean goLogin = true;//登录页面是否按了返回键
    public static boolean isSetTrade = false;//记录登录后，返回的是否设置了交易密码
    public static String channeel;//渠道类别
    public static String jpushRegistid;//极光推送注册id
    public static String imiId;//手机设备号
    public static MApplication instance;
    public static int mLocationId = -1; // 定位城市ID
    public String lngCityName;
    private String newprovince;
    private LocationClient locationClient = null;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            try {
                JsonBean jsonBean = (JsonBean) msg.obj;
                int code = jsonBean.getCode();
                String errorMsg = jsonBean.getError_msg();
                if (code == Constants.DATA_EVENT) {
//                    Utils.makeToast(getApplicationContext(), Constants.ERROR_MSG_CODE + code);
                    return;
                }
                int action = msg.what;
                switch (action) {
                    case Constants.WHAT_QUERY_QUERYCITYID:
//                        boolean flag = Utils.filtrateCode(getApplicationContext(), jsonBean);
/*                        if (flag && code == Constants.OPERATION_FAIL) {//数据交互失败
//                            Utils.makeToast(getApplicationContext(), errorMsg);
                        } else if (flag && code == Constants.OPERATION_SUCCESS) {//数据交互成功
                            JSONObject jsonObject = new JSONObject(jsonBean.getJsonString());
                            mLocationId = jsonObject.getInt("result");
                            LogUtils.e("MApplocation", "mLocationId:" + mLocationId);
                        }*/
                        if (code == Constants.OPERATION_FAIL) {//数据交互失败
                        } else if (code == Constants.OPERATION_SUCCESS) {//数据交互成功
                            JSONObject jsonObject = new JSONObject(jsonBean.getJsonString());
                            mLocationId = jsonObject.getInt("result");
                            LogUtils.e("MApplocation", "mLocationId:" + mLocationId);
                        }
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                LogUtils.e("MApplication", "handler 异常");
            }
        }
    };

    // 是否是第一次定位
    private boolean isFirstLocation = true;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        JPushInterface.setDebugMode(Constants.IS_TEST);//true 设置开启日志，发布时请关闭日志(false)
        JPushInterface.init(this);// 初始化 JPush
        initLocation();
        /*if (lngCityName==null&&mLocationId==-1) {
            // 定位用户当前位置  lngCityName==null
            initLocation();
        }*/

        //初始化ImageLoader
        initImageLoader(getContext());
    }

    public void initLocation() {
//    private void initLocation() {
        try {
            MyLocationListenner myListener = new MyLocationListenner();
            locationClient = new LocationClient(this);
            locationClient.registerLocationListener(myListener);
            LocationClientOption option = new LocationClientOption();
            option.setOpenGps(true);
            option.setAddrType("all");
            option.setCoorType("bd09ll");
//          设置发起定位请求的间隔时间5000
            option.setScanSpan(3 * 1000);
            option.disableCache(true);
            // 	option.setPoiNumber(5);
            // 	option.setPoiDistance(1000);
            // option.setPoiExtraInfo(true);
            option.setPriority(LocationClientOption.GpsFirst);
            locationClient.setLocOption(option);
            locationClient.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //  用于定位的部分
    //    public class MyLocationListenner implements BDLocationListener {
    private class MyLocationListenner implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null)
                return;
            StringBuffer sb = new StringBuffer(256);
            if (location.getLocType() == BDLocation.TypeGpsLocation) {
//				sb.append(location.getAddrStr());
                sb.append(location.getCity());
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
                sb.append(location.getCity());
            }
            if (sb.toString() != null && sb.toString().length() > 0) {
//              String lngCityName = sb.toString();
                if (isFirstLocation) {
                    lngCityName = sb.toString();
                    lngCityName = lngCityName.substring(0, lngCityName.length() - 1);
                    Log.e("截取后城市", lngCityName);
                    isFirstLocation = false;
                }
                Log.e("定位定位", lngCityName);
                Log.e("省份省份省份", location.getProvince().toString());
/*                String province = location.getProvince().toString();
//                String split = str.substring(0,str.length()-1);
                int index = province.lastIndexOf('市');
                if (index > 0) {
                    newprovince = province.substring(0, province.length() - 1);
                }*/

                // 传给服务器拿到ID
//                    updateLocation("", lngCityName);
            }
        }
    }

    /**
     * 上传城市信息到服务器
     *
     * @param lngCityName
     */
    private void updateLocation(String deCityName, String lngCityName) {
        HashMap<String, String> params1 = new HashMap<>();
        params1.put("user_id", Utils.getUserId());
        params1.put("token", Utils.getToken());
        params1.put("provinceName", deCityName);
        params1.put("cityName", lngCityName);
        CustomTask task = new CustomTask(mHandler, Constants.WHAT_QUERY_QUERYCITYID
                , Constants.SEAWAY_QUERY_QUERYCITYID, true, params1, true);
        /*CustomTask task = new CustomTask(mHandler, Constants.WHAT_QUERY_PROVINCECITY
                , Constants.SEAWAY_QUERY_PROVINCECITY, true, params1, true);*/
        task.execute();
    }

    public static synchronized MApplication getInstance() {
        if (instance == null) {
            instance = new MApplication();
        }
        return instance;
    }


    public static String getImiId() {
        return imiId;
    }

    public static void setImiId(String imiId) {
        MApplication.imiId = imiId;
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

    public static String getChannel() {
        return channeel;
    }

    public static void setChannel(String channeel) {
        MApplication.channeel = channeel;
    }

    public static String getJpushRegistid() {
        return jpushRegistid;
    }

    public static void setJpushRegistid(String jpushRegistid) {
        MApplication.jpushRegistid = jpushRegistid;
    }

    /**
     * 获取token
     *
     * @return
     */
    public static String getToken() {
        return token;
    }

    /**
     * 设置token
     *
     * @param token
     */
    public static void setToken(String token) {
        MApplication.token = token;
    }

    /**
     * 获取id
     *
     * @return
     */
    public static String getUser_id() {
        return user_id;
    }

    /**
     * 设置id
     *
     * @param user_id
     */
    public static void setUser_id(String user_id) {
        MApplication.user_id = user_id;
    }

    /**
     * 存储打开的activity
     *
     * @param activity
     */
    public synchronized static void registActivities(Activity activity) {
        int size = activities.size();
        for (int i = 0; i < size; i++) {
            Activity activity_ = activities.get(i);
            if (activity_.getClass().getName() == activity.getClass().getName()) {
                activities.remove(i);
                if (!activity_.isFinishing()) {
                    activity_.finish();
                }
                break;
            }
        }
        activities.add(activity);
    }

    /**
     * 杀掉打开的Activity
     *
     * @param activity
     */
    public synchronized static void destroyActivity(Activity activity) {
        int size = activities.size();
        if (activity != null && activities != null && size != 0) {
            activities.remove(activity);
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }

    /**
     * 根据Activity获取想要的Activity
     *
     * @param activity
     * @return
     */
    public synchronized static Activity getActivityByActivity(Activity activity) {
        String name = activity.getClass().getName();
        return getActivityByName(name);
    }

    /**
     * 根据Acivity  name 获取想要的activity
     *
     * @param name
     * @return
     */
    public synchronized static Activity getActivityByName(String name) {
        int size = activities.size();
        for (int i = 0; i < size; i++) {
            Activity activity_ = activities.get(i);
            if (activity_.isFinishing()) {
                continue;
            }
            if (activity_.getClass().getName() == name) {
                return activity_;
            }
        }
        return null;
    }

    /**
     * 关掉  传入activity  name以外的所有的activity
     *
     * @param name
     */
    public synchronized static void finishAllActivitys(String name) {
        if (activities != null) {
            int size = activities.size();
            for (int i = 0; i < size; i++) {
                Activity activity = activities.get(i);
                if (!activity.isFinishing() && activity.getClass().getName() != name) {
                    activity.finish();
                }
            }
        }
    }

    /**
     * 关掉所有Activity
     */
    public synchronized static void finishAllActivitys() {
        if (activities != null) {
            int size = activities.size();
            for (int i = 0; i < size; i++) {
                Activity activity = activities.get(i);
                if (!activity.isFinishing()) {
                    activity.finish();
                }
            }
        }
    }

    //    2015年12月29日07:44:47   imageloader
    public static void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
//		  ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();//不会在内存中缓存多个大小的图片
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());//为了保证图片名称唯一
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        //内存缓存大小默认是：app可用内存的1/8
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs(); // Remove for release app

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());
//		ImageLoader.getInstance().init( ImageLoaderConfiguration.createDefault(this));
    }

}