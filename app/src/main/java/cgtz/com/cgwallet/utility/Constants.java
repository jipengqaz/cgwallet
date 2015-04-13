package cgtz.com.cgwallet.utility;

/**
 * Created by Administrator on 2015/4/11.
 */
public class Constants {
    public static final boolean IS_TEST = true;//判断是否为测试环境
    public static final String OFFLINE_HTTP = "";//测试服务器路径
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
}
