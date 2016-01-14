package cgtz.com.cgwallet.utility;

import android.os.Environment;

import java.io.File;

/**
 * Created by Administrator on 2015/4/11.
 */
public class Constants {
    public static final String version = "1.1.7";
    public static final String version_ = "110";
    public static final String ENCONDING = "UTF-8";
    public static final String CGTZ_PACKAGE = "cgtz.com.cgtz";
    public static final String CGTZ_START_ACTIVITY = CGTZ_PACKAGE + ".ui.StartActivity";
    public static final String colors = "#e44d42";//手势密码提示文案颜色
    /*111111111111111111111111111111111111111111111111111111111111111111111111111111111111111*/
    public static final boolean IS_TEST = false;//判断是否为测试环境
    public static final boolean IS_DEVELOP = true;//用于在开发还是测试之间选择测试服务器路径的判断
    public static final boolean IS_28 = false;//判断是否更改为28服务器路径
    public static final String OFFLINE_HTTP_TEST = "http://192.168.10.188:45680/wallet/";//用于开发的-测试服务器路径
    public static final String OFFLINE_HTTP_DEVELOP = "http://172.16.34.188:45680/wallet2/";//用于开发的-测试服务器路径 192.168.10
    //    public static final String OFFLINE_HTTP_28 = "http://115.29.245.28:42111/wallet/";//测试服务器路径    http://115.29.245.28:42111/wallet114
    //    public static final String OFFLINE_HTTP_28 = "http://115.29.245.28:42111/wallet113/";//测试服务器路径
    public static final String OFFLINE_HTTP_28 = "http://115.29.245.28:42111/wallet115/";//测试服务器路径
    public static final String OFFLINE_HTTP = IS_28 ? OFFLINE_HTTP_28 :
            (IS_DEVELOP ? OFFLINE_HTTP_DEVELOP : OFFLINE_HTTP_TEST);//测试服务器路径
    //    public static final String ONLINE_HTTP = "https://wallet.cgtz.com/";//线上服务器路径
//    public static final String ONLINE_HTTP = "https://wallet.cgtz.com/wallet113/";//线上服务器路径
//    public static final String ONLINE_HTTP = "https://wallet.cgtz.com/wallet114/";//线上服务器路径
      public static final String ONLINE_HTTP = "https://wallet.cgtz.com/wallet115/";//1.1.6线上服务器路径
    public static final String VERSION = version + (IS_TEST ?
            (IS_28 ? "-28" : "-dev")
            : "");//版本号
    public static final String TEST_WALLET_INTRODUCE = !IS_28 ? "http://192.168.10.188:45680/mobile/e/start" : "http://115.29.245.28:42111/mobile/e/start";//草根钱包  简介  dev
    public static final String ONLINT_WALLET_INTRODUCE = "http://mobile.cgtz.com/e/start";//草根钱包  简介  线上
    public static final String WALLET_INTRODUCE = IS_TEST ? TEST_WALLET_INTRODUCE : ONLINT_WALLET_INTRODUCE;//草根钱包简介
    /**
     * 2015年11月5日12:42:43  增加精选投资和缴费服务协议     测试：http://ceshi.m.cgtz.com/    http://192.168.10.188:886/m-front/    原：http://m.cgtz.com
     */
    public static final String WALLET_INVEST = "http://m.cgtz.com";//投资H5界面 草根投资/精选投资
    public static final String WALLET_INVEST_WX = "http://m.cgtz.com/?weixin=weixin";//投资H5界面 草根投资/精选投资
//    public static final String WALLET_AGGREEMENT = "http://192.168.10.188:45680/wallet2/show/paymentAgreement";//草根钱包自助缴费服务协议
    public static final String WALLET_AGGREEMENT = "http://115.29.245.28:42111/wallet114/show/paymentAgreement";//草根钱包自助缴费服务协议

    /**
     * 2015年11月13日23:24:56 测试
     */
    public static final String TEST_MOBILE="http://m.cgtz.com/site/CheckMobile.html";//测试的需要手机号的接口
    public static final String TEST_MOBILE_PASSWORD = "http://m.cgtz.com/login2.html";//测试的需要密码的接口

    public static final String TEST_VERSION_UPDATE =
            "http://192.168.10.188:45680/version/app/WalletVersion";//测试服务器，版本更新
    public static final String TWO_NIGHT_VERSION_UPDATE =
            "http://115.29.245.28:42111/version/app/WalletVersion";//28环境，版本更新
    public static final String ONLINE_VERSION_UPDATE =
            "https://d5ds88.cgtz.com/version/app/WalletVersion";//线上版本更新
    public static final String VERSION_UPDATE = IS_TEST ?
            (IS_28 ? TWO_NIGHT_VERSION_UPDATE : TEST_VERSION_UPDATE)
            : ONLINE_VERSION_UPDATE;//版本更新

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
    public static final String service_date = "version=" + version + "&mobileOS=android&";//向服务器全局数据

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
    public static final int WHAT_EWALLET_AFFIRMREDIRECT = 2015042719;//e钱包第三方转入
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

    /**
     * 2015年10月29日10:59:42   加入思伟接口的识别标识
     */
    public static final int WHAT_QUERY_LATESTTHREEORDER = 2015102901;// 点击消费界面接口，获取最新三条订单
    public static final int WHAT_QUERYORDER = 2015102902;//订单列表（全部订单）  //点击查看更多
    public static final int WHAT_QUERY_ONETYPEORDER = 2015102903;//查询某一服务的历史账单  //点击历史账单
    public static final int WHAT_QUERY_CITYSERVICE = 2015102904;//所选城市是否有水煤电缴费服务  //生活缴费界面是否支持水电服务
    public static final int WHAT_QUERY_QUERYCITYID = 2015102905;//通过城市名获取城市id(app启动时)
    public static final int WHAT_QUERY_PROVINCECITY = 2015102906;//获取城市  //点击城市时获取城市列表  暂未使用
    public static final int WHAT_QUERY_PAYMENTUNIT = 2015102907;//获取缴费单位  //水费（缴费单位界面）界面
    public static final int WHAT_QUERY_BILL = 2015102908;//查询水煤电其中一项账单(大更新)  //账单金额
    public static final int WHAT_ORDER = 2015102909;//生成渠道订单  //点击支付
    public static final int WHAT_PAYORDER = 2015102910;////支付订单  //订单详情界面
    public static final int WHAT_QUERY_EVERYDAYINTEREST = 2015102911;//利息明细
    public static final int WHAT_QUERY_ORDERDETAIL = 2015110901;//查询订单接口
    public static final int WHAT_QUERY_INTERESTDETAIL=2015111301;//查询收益详情
    //    无用接口
    public static final int WHAT_JAVASEAWAY = 2015102912;//java端思伟通用接口
    //免登陆的测试
    public static final int WHAT_MOBILE=2015111302;
    public static final int WHAT_MOBILE_PASSWORD = 2015111501;

    //UTIL_QUERY_SEVENDAYSRATES   1.1.5（6）版本七日年化收益率
    public static final int WHAT_QUERY_SEVENDAYSRATES=2015122101;
    public static final int WHAT_QUERY_IMAGEDATA=2015122801;//轮播图信息
    /**
     * 88888888888888888888888888888888888888
     **/
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

//新定义
//  public static final String E_WALLET_REDDEM="ewallet/redeem";//转出手续费
//  public static final String E_WALLET_DETAIL="ewallet/detail";//年化率

    public static final String AGAINLOGIN_URL = "account/checkPass";//判断登录密码接口
    public static final String URL_WALLET_DETAIL = "ewallet/detail";//我的钱包接口
    public static final String URL_WALLET_DEPOSIT = "ewallet/deposit";//进入存钱页面获取数据
    public static final String URL_STARTUP = "api/startUp";//(启动调用接口)获取是否更新数据判断值
    public static final String URL_KEFUTIP = "api/getKefuTip";//客服文案
    public static final String URL_PROVINCES_CITIES_UPDATE = "api/getProvinceAndCities";//获取省市数据
    public static final String URL_API_STARTIMAGE = "api/startImage";//获取开机图片
    public static final String URL_BEFORE_PAY = "pay/BeforePay";//判断银行卡是否绑定接口
    public static final String URL_SHARE_TIP = "account/GetShareTip";//获取分享内容和二维码
    public static final String URL_SELECTED_BANK = "api/GetAvailableBanks";//获取连连支付支持的银行卡接口
    public static final String URL_EWALLET_AFFIRMDO = "ewallet/Affirmdo";//草根钱包余额转入
    public static final String URL_EWALLET_AFFIRMREDIRECT = "ewallet/Affirmredirect";//e钱包第三方转入
    public static final String UTL_SIGNPORT = "pay/sign";//连连支付支付签名接口
    public static final String URL_BANKCARD_LLBIND = "Bankcard/Llbind";//预绑成功之后调用 用来银行卡绑定连连的接口
    public static final String URL_PAYSTATUS = "order/payStatus";//投资时银行卡充值成功后，获取投资记录是否生成
    public static final String URL_PAY_SETPASS = "pay/SetPass";//设置交易密码
    public static final String URL_GET_CODE = "pay/GetMobileCode";//获取修改交易密码的是的短信验证码
    public static final String URL_RESETPAYPASS = "pay/ResetPayPass";//重置交易密码
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
    public static final String IMG_FILE_NAME = "loading" + version_ + ".jpg";
    public static final int HANDLER_RL_START = 10000010;//handler判断值，下载图片成功返回值
    //下载的欢迎图片，保存路径
    public static final String IMG_FILE_PATH = Environment.getExternalStorageDirectory()
            + File.separator + Constants.COMPANY_FILEDIR + "/download/" + IMG_FILE_NAME;
    public static final String PROVINCES = "provinces";//存储省市的文件
    public static final String PROVINCES_XML = "provinces_xml";//存储省的key
    public static final String TITLE_BIND_BANK = "银行卡绑定";//
    public static final String TITLE_EDIT_NAME = "实名认证";//


    /**
     * 2015年10月28日17:56:40  目前思伟所有接口
     */

    public static final String SEAWAY_QUERY_LATESTTHREEORDER = "seaway/queryLatestThreeOrder";// 点击消费界面接口，获取最新三条订单
    public static final String SEAWAY_QUERYORDER = "seaway/queryOrder";//订单列表（全部订单）  //点击查看更多
    public static final String SEAWAY_QUERY_ONETYPEORDER = "seaway/queryOneTypeOrder";//查询某一服务的历史账单  //点击历史账单
    public static final String SEAWAY_QUERY_CITYSERVICE = "seaway/queryCityService";//所选城市是否有水煤电缴费服务  //生活缴费界面是否支持水电服务
    public static final String SEAWAY_QUERY_QUERYCITYID = "seaway/queryCityId";//通过城市名获取城市id(app启动时)
    public static final String SEAWAY_QUERY_PROVINCECITY = "seaway/queryProvinceCity";//获取城市  //点击城市时获取城市列表  暂未使用
    public static final String SEAWAY_QUERY_PAYMENTUNIT = "seaway/queryPaymentUnit";//获取缴费单位  //水费（缴费单位界面）界面
    public static final String SEAWAY_QUERY_BILL = "seaway/queryBill";//查询水煤电其中一项账单(大更新)  //账单金额
    public static final String SEAWAY_ORDER = "seaway/order";//生成渠道订单  //点击支付
    public static final String SEAWAY_PAYORDER = "seaway/payOrder";////支付订单  //订单详情界面
    public static final String SEAWAY_QUERY_EVERYDAYINTEREST = "seaway/queryEverydayInterest";//利息明细
    public static final String SEAWAY_QUERY_ORDERDETAIL = "seaway/queryOrderDetail";//查询订单接口
    public static final String SEAWAY_QUERY_INTERESTDETAIL="seaway/queryInterestDetail";//每日利息明细的接口
    //    无用接口
    public static final String SEAWAY_JAVASEAWAY = "seaway/javaSeaway";//java端思伟通用接口
    /**
     * 2015年12月21日10:11:09  七日年化收益率接口
     * public static final String QUERY_IMAGEDATA="https://cgwallet.sinaapp.com/index.php/Home/Index/index";//1.1.5（6）版本轮播图信息  线上
     *                                              http://cgwallet.sinaapp.com/index.php/Home/Index/test
     */
    public static final String UTIL_QUERY_SEVENDAYSRATES="util/querySevenDaysRates";//1.1.5（6）版本七日年化收益率
    public static final String QUERY_IMAGEDATA="https://cgwallet.sinaapp.com/index.php/Home/Index/test";//1.1.5（6）版本轮播图信息
}
