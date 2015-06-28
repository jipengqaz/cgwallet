package cgtz.com.cgwallet.utility;

import android.os.Environment;

import java.io.File;

/**
 * Created by Administrator on 2015/4/11.
 */
public class Constants {
    public static final String version = "1.0.0";
    public static final String version_ = "100";
    public static final String ENCONDING = "UTF-8";
    public static final String CGTZ_PACKAGE = "cgtz.com.cgtz";
    public static final String CGTZ_START_ACTIVITY = CGTZ_PACKAGE+".ui.StartActivity";
    public static final String colors  ="#e44d42";//手势密码提示文案颜色
    public static final boolean IS_TEST = true;//判断是否为测试环境
    public static final boolean IS_DEVELOP = true;//用于在开发还是测试之间选择测试服务器路径的判断
    public static final boolean IS_28 = true;//判断是否更改为28服务器路径
    public static final String OFFLINE_HTTP_TEST = "http://192.168.10.188:45680/wallet/";//用于开发的-测试服务器路径
    public static final String OFFLINE_HTTP_DEVELOP = "http://192.168.10.188:45680/wallet2/";//用于开发的-测试服务器路径
    public static final String OFFLINE_HTTP_28 = "http://115.29.245.28:42111/wallet/";//测试服务器路径
    public static final String OFFLINE_HTTP = IS_28?OFFLINE_HTTP_28:
                                    (IS_DEVELOP?OFFLINE_HTTP_DEVELOP:OFFLINE_HTTP_TEST);//测试服务器路径
    public static final String ONLINE_HTTP = "https://wallet.cgtz.com/";//线上服务器路径
    public static final String VERSION = version+(IS_TEST?
            (IS_28?"-28":"-dev")
            :"");//版本号
    public static final String WALLET_INTRODUCE = "http://192.168.10.188:45680/mobile/e/start";//草根钱包简介
    public static final String TEST_VERSION_UPDATE =
            "http://192.168.10.188:45680/version/app/WalletVersion";//测试服务器，版本更新
    public static final String TWO_NIGHT_VERSION_UPDATE =
            "http://115.29.245.28:42111/version/app/WalletVersion";//28环境，版本更新
    public static final String ONLINE_VERSION_UPDATE =
            "https://d5ds88.cgtz.com/version/app/WalletVersion";//线上版本更新
    public static final String VERSION_UPDATE = IS_TEST?
                                    (IS_28?TWO_NIGHT_VERSION_UPDATE:TEST_VERSION_UPDATE)
                                    :ONLINE_VERSION_UPDATE;//版本更新

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
    public static final String service_date = "version="+version+"&mobileOS=android&";//向服务器全局数据

    public static boolean GESTURES_PASSWORD = true;//用于判断是否输入手势密码

    // 手势密码点的状态
    public static final int POINT_STATE_NORMAL = 0; // 正常状态
    public static final int POINT_STATE_SELECTED = 1; // 按下状态
    public static final int POINT_STATE_WRONG = 2; // 错误状态
    //end
    public static final int HANDLER_SERVER_MAINTAIN = 2015011914;//服务器维护判断值
    public static final int WHAT_WALLET_MAIN = 88888888;//钱包首页
    public static final int WHAT_FEED_BACK = 2015042114;//意见反馈
    public static final int WHAT_LOGIN = 2015042315;//登录
    public static final int WHAT_GET_SECURITY_CODE = 2015042411;//验证码
    public static final int WHAT_REGISTER = 2015042414;//注册
    public static final int WHAT__WALLET_DETAIL = 2015042418;//我的钱包
    public static final int WHAT_STARTUP = 2015042511;//(启动调用接口)获取是否更新数据
    public static final int WHAT_KE_FU = 2015042515;//(启动调用接口)获取客服文案数据
    public static final int WHAT_PROVINCES = 2015042517;//获取省市数据
    public static final int WHAT_WALLET_DETAIL = 2015042418;//我的钱包
    public static final int WHAT_WALLET_DEPOSIT = 2015042709;////进入存钱页面获取数据
    public static final int WHAT_BEFORE_PAY = 2015042714;//银行卡是否绑定
    public static final int WHAT_SELECTED_BANK = 2015042716;//获取银行列表
    public static final int WHAT_EWALLET_AFFIRMDO = 2015042717;//草根钱包余额存入
    public static final int WHAT_EWALLET_AFFIRMREDIRECT= 2015042719;//e钱包第三方转入
    public static final int WHAT_SIGNPORT = 2015042758;//连连支付支付签名接口
    public static final int WHAT_BANKCARD_LLBIND = 2015042720;//预绑成功之后调用 用来银行卡绑定连连
    public static final int WHAT_PAYSTATUS = 2015042759;//投资时银行卡充值成功后，获取投资记录是否生成
    public static final int WHAT_ACCOUNT_INFO = 2015042822;//获取个人信息及安全中心内容
    public static final int WHAT_WITHDRAW = 2015042817;//取钱页面进入判断识别码
    public static final int WHAT_UPDATE_BANK = 2015042823;//完善银行卡信息
    public static final int WHAT_BRANCH = 2015042843;//获取城市分行
    public static final int WHAT_BANK_LIST_CASE = 10011;//修改登录密码Handler的what判断值
    public static final int WHAT_BANKCARD_UNBIND = 2014121515;//注销银行卡数据返回handler判断值
    public static final int WHAT_VERSION_UPDATE = 20150000;//版本更新
    public static final int WHAT_IS_MY = 1;//用于用户存钱 或取钱  返回主页面  好跳到我的钱包
    /**88888888888888888888888888888888888888**/
    public static final String URL_WALLET_SLOGAN = "https://d5ds88.cgtz.com/version/notice/wallet";//草根钱包介绍
    public static final String URL_SERVER_MAINTAIN = "android/Maintain";//服务器是否维护的判断接口
    public static final String URL_CG_WALLET_PROTOCOL = "http://d5ds88.cgtz.com/site/agreement/";
    public static final String URL_FEED_BACK = "site/feedBack";//意见反馈
    public static final String URL_INTEREST_HISTORY = "ewallet/InterestHistory";//收益历史
    public static final String URL_LOGIN = "site/login";//登录接口
    public static final String URL_GET_SECURITY_CODE = "site/getvcode";//注册时获取验证码
    public static final String URL_FORGET_PWD_CODE = "site/mbcode";//忘记密码时，获取验证码
    public static final String URL_PASSWORD = "site/password1";//忘记密码时，验证码密码的提交
    public static final String URL_REGISTER = "site/register";//注册
    public static final String E_WALLET_LIST = "ewallet/list";//草根钱包转入转出数据
    public static final String AGAINLOGIN_URL = "account/checkPass";//判断登录密码接口
    public static final String URL_WALLET_DETAIL = "ewallet/detail";//我的钱包接口
    public static final String URL_WALLET_DEPOSIT = "ewallet/deposit";//进入存钱页面获取数据
    public static final String URL_STARTUP = "api/startUp";//(启动调用接口)获取是否更新数据判断值
    public static final String URL_KEFUTIP = "api/getKefuTip";//客服文案
    public static final String URL_PROVINCES_CITIES_UPDATE = "api/getProvinceAndCities";//获取省市数据
    public static final String URL_API_STARTIMAGE = "api/startImage";//获取开机图片
    public static final String URL_BEFORE_PAY = "pay/BeforePay";//判断银行卡是否绑定接口
    public static final String URL_SHARE_TIP ="account/GetShareTip";//获取分享内容和二维码
    public static final String URL_SELECTED_BANK= "api/GetAvailableBanks";//获取连连支付支持的银行卡接口
    public static final String URL_EWALLET_AFFIRMDO= "ewallet/Affirmdo";//草根钱包余额转入
    public static final String URL_EWALLET_AFFIRMREDIRECT= "ewallet/Affirmredirect";//e钱包第三方转入
    public static final String UTL_SIGNPORT = "pay/sign";//连连支付支付签名接口
    public static final String URL_BANKCARD_LLBIND = "Bankcard/Llbind";//预绑成功之后调用 用来银行卡绑定连连的接口
    public static final String URL_PAYSTATUS = "order/payStatus";//投资时银行卡充值成功后，获取投资记录是否生成
    public static final String URL_PAY_SETPASS = "pay/SetPass";//设置交易密码
    public static final String URL_GET_CODE = "pay/GetMobileCode";//获取修改交易密码的是的短信验证码
    public static final String URL_RESETPAYPASS= "pay/ResetPayPass";//重置交易密码
    public static final String URL_CHANGE_LOGINPWD = "account/resetPassword";//修改登录密码
    public static final String URL_ACCOUNT_INFO = "account/info";//获取个人信息及安全中心内容
    public static final String URL_E_WALLET_REDEEM = "ewallet/redeem";//进入取钱界面判断接口
    public static final String URL_DO_WITHDRAW = "ewallet/RedeemDo";//取钱接口
    public static final String URL_UPDATE_BANK = "Bankcard/update";//银行卡信息完善接口
    public static final String URL_BRANCH = "api/getBranch";//获取分行信息
    public static final String URL_BANK_LIST = "Bankcard/index";//获取绑定银行卡列表
    public static final String URL_BANKCARD_UNBIND = "bankcard/Unbind";//注销银行卡接口


    /**
     *
     */
    public static final String NEED_LOGIN = "请先登录";
    public static final String COMPANY_FILEDIR = "com.cgwallet";//用于保存app下载的文件根目录
    public static final String IMG_FILE_NAME = "loading"+version_+".jpg";
    public static final int HANDLER_RL_START = 10000010;//handler判断值，下载图片成功返回值
    //下载的欢迎图片，保存路径
    public static final String IMG_FILE_PATH = Environment.getExternalStorageDirectory()
            + File.separator + Constants.COMPANY_FILEDIR + "/download/"+IMG_FILE_NAME;
    public static final String PROVINCES = "provinces";//存储省市的文件
    public static final String PROVINCES_XML = "provinces_xml";//存储省的key
    public static final String TITLE_BIND_BANK = "银行卡绑定";//
    public static final String TITLE_EDIT_NAME = "实名认证";//
}
