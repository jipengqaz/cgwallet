package cgtz.com.cgwallet.utility;

/**
 * Created by Administrator on 2015/4/11.
 */
public class Constants {
    public static final boolean IS_TEST = true;//判断是否为测试环境
    public static final String OFFLINE_HTTP = "";//线下路径
    public static final String ONLINE_HTTP = "";//线上路径
    public static final String VERSION = "";//版本号
    public static final int DATA_EVENT = -1;//数据解析错误
    public static final int NO_DATA = 0;//没有数据或者数据为空
    public static final int IS_EVENT = 1;//访问服务器超时，或者http访问异常
    public static final int NEED_LOGIN_AGAIN = 2;//账号需要重新登录
    public static final int SERVICE_MAINTAIN = 3;//服务器维护中
    public static final int OPERATION_FAIL = 4;//操作失败
    public static final int OPERATION_SUCCESS = 5;//操作成功
    public static final String NO_DATA_MSG = "当前网络不给力";
    public static final String IS_EVENT_MSG = "当前网络不给力";
}
