package cgtz.com.cgwallet.utility;

import android.os.Environment;

import java.io.File;

/**
 * Created by Administrator on 2015/4/11.
 */
public class Constants {
    public static final String version_ = "100";
    public static final String ENCONDING = "UTF-8";
    public static final String colors  ="#e44d42";//手势密码提示文案颜色
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
    public static final int DATA_EVENT = 201501;//服务器返回的json数据解析异常
    public static final int NO_DATA = 201502;//服务器没有数据返回
    public static final int IS_EVENT = 201503;//服务器访问超时或者http访问异常
    public static final int NEED_LOGIN_AGAIN = 201504;//需要重新登录
    public static final int SERVICE_MAINTAIN = 201505;//服务器需要维护中
    public static final int OPERATION_FAIL = 201506;//服务器交互操作失败
    public static final int OPERATION_SUCCESS = 201507;//服务器交互操作成功
    public static final String NO_DATA_MSG = "网络不稳定";
    public static final String IS_EVENT_MSG = "由于网络原因，服务器访问超时";
    public static final String CONFIG_GESTURE = "config_gesture";//
    public static final String CONFIG = "config";
    public static final String LOGIN_PHONE = "login_phone";//登录手机号
    public static final String LOGIN_PASSWORD = "login_password";//登录密码
    public static final String LOGIN_USER_ID = "user_id";//登录后的userid
    public static final String LOGIN_TOKEN = "token";//登录后的token
    public static final String ERROR_MSG_CODE = "错误码";//内容解析错误
    public static final String service_date = "mobileOS=android&";//向服务器全局数据

    public static boolean GESTURES_PASSWORD = true;//用于判断是否输入手势密码

    // 手势密码点的状态
    public static final int POINT_STATE_NORMAL = 0; // 正常状态
    public static final int POINT_STATE_SELECTED = 1; // 按下状态
    public static final int POINT_STATE_WRONG = 2; // 错误状态
    //end
    public static final int HANDLER_SERVER_MAINTAIN = 2015011914;//服务器维护判断值
    public static final int WHAT_FEED_BACK = 2015042114;//意见反馈
    public static final int WHAT_LOGIN = 2015042315;//登录
    public static final int WHAT_GET_SECURITY_CODE = 2015042411;//验证码
    public static final int WHAT_REGISTER = 2015042414;//注册
    public static final String URL_CG_WALLET_PROTOCOL = "http://d5ds88.cgtz.com/site/agreement/";
    public static final String URL_FEED_BACK = "site/feedBack";//意见反馈
    public static final String URL_INTEREST_HISTORY = "ewallet/InterestHistory";//收益历史
    public static final String URL_LOGIN = "site/login";//登录接口
    public static final String URL_GET_SECURITY_CODE = "site/getvcode";//获取验证码
    public static final String URL_REGISTER = "site/register";//注册
    public static final String E_WALLET_LIST = "ewallet/list";//草根钱包转入转出数据
    public static final String AGAINLOGIN_URL = "account/checkPass";//判断登录密码接口


    /**
     *
     */
    public static final String COMPANY_FILEDIR = "com.cgtz";//用于保存app下载的文件根目录
    public static final String IMG_FILE_NAME = "loading"+version_+".jpg";
    //下载的欢迎图片，保存路径
    public static final String IMG_FILE_PATH = Environment.getExternalStorageDirectory()
            + File.separator + Constants.COMPANY_FILEDIR + "/download/"+IMG_FILE_NAME;
}
