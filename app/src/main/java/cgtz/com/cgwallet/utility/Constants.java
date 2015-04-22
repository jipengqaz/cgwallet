package cgtz.com.cgwallet.utility;

/**
 * Created by Administrator on 2015/4/11.
 */
public class Constants {
    public static final String ENCONDING = "UTF-8";
    public static final boolean IS_TEST = true;//判断是否为测试环境
    public static final boolean IS_DEVELOP = true;//用于在开发还是测试之间选择测试服务器路径的判断
    public static final boolean IS_28 = false;//判断是否更改为28服务器路径
    public static final String OFFLINE_HTTP_TEST = "http://192.168.10.188:45680/wallet/";//用于开发的-测试服务器路径
    public static final String OFFLINE_HTTP_DEVELOP = "http://192.168.10.188:45680/wallet2/";//用于开发的-测试服务器路径
    public static final String OFFLINE_HTTP_28 = "http://115.29.245.28:42111/wallet/";//测试服务器路径
    public static final String OFFLINE_HTTP = IS_28?OFFLINE_HTTP_28:
                                    (IS_DEVELOP?OFFLINE_HTTP_DEVELOP:OFFLINE_HTTP_TEST);//测试服务器路径
    public static final String ONLINE_HTTP = "";//线上服务器路径
    public static final String VERSION = "";//版本号
    public static final int DATA_EVENT = -1;//服务器返回的json数据解析异常
    public static final int NO_DATA = 0;//服务器没有数据返回
    public static final int IS_EVENT = 1;//服务器访问超时或者http访问异常
    public static final int NEED_LOGIN_AGAIN = 2;//需要重新登录
    public static final int SERVICE_MAINTAIN = 3;//服务器需要维护中
    public static final int OPERATION_FAIL = 4;//服务器交互操作失败
    public static final int OPERATION_SUCCESS = 5;//服务器交互操作成功
    public static final String NO_DATA_MSG = "网络不稳定";
    public static final String IS_EVENT_MSG = "由于网络原因，服务器访问超时";
    public static final String CONFIG_GESTURE = "config_gesture";//
    public static final String LOGIN_PHONE = "login_phone";//登录手机号
    public static final String CONFIG = "config";
    public static final String MOBILE_PASSWORD = "login_password";//登录密码
    public static final String ERROR_MSG_CODE = "错误码";//内容解析错误

    public static boolean GESTURES_PASSWORD = true;//用于判断是否输入手势密码

    // 手势密码点的状态
    public static final int POINT_STATE_NORMAL = 0; // 正常状态
    public static final int POINT_STATE_SELECTED = 1; // 按下状态
    public static final int POINT_STATE_WRONG = 2; // 错误状态
    //end

    public static final int WHAT_FEED_BACK = 2015042114;//意见反馈
    public static final String URL_FEED_BACK = "site/feedBack";//意见反馈
    public static final String URL_INTEREST_HISTORY = "ewallet/InterestHistory";//收益历史
}
