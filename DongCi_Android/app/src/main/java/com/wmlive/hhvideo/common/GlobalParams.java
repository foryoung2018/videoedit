package com.wmlive.hhvideo.common;


import com.wmlive.hhvideo.DCApplication;
import com.wmlive.hhvideo.utils.DeviceUtils;

import cn.wmlive.hhvideo.BuildConfig;

/**
 * 全局配置相关常量和变量
 */
public class GlobalParams {

    /**
     * 全局静态变量，一般数量较少，有些在切换用户时可能需要重置!!!
     */
    public static class StaticVariable {

        public static long sStartTimestamp;

        public static boolean sReleaseEnvironment = false;//这个变量只用作切换url环境使用，不可用作判断是否是release和debug版本

        public static boolean sInitFromLocal = true;//使用本地的接口启动

        public static int sCurrentNetwork = 0;//当前网络类型，0：wifi  1:移动网络   2:无网络

        public static boolean sSupportWebp = true;

        public static long sPublishingProductId = 0;

        public static int sStatusBarHeight = DeviceUtils.getStatusBarHeight(DCApplication.getDCApp());

        //api
        public static String sApiServiceVersion = "1.0";

        //设备唯一ID
        public static String sUniqueDeviceId;

        public static String sLocalPublicIp;//本地公网ip
        public static String sAliyunUploadIp;//阿里云上传ip

        public static boolean sHasShowedRemind;

        public static String CHANNEL_CODE = "develop";

        public static int sDiscoverUnreadCount;

        public static String netName;//运营商名称

        public static String ipRegion;//ip地域

        public static String ipCity;//ip城市
        public static boolean ispublishing;

    }

    /**
     * 配置相关的常量
     */
    public interface Config {

        boolean IS_DEBUG = BuildConfig.DEBUG_SWITCH;

        long APP_FILE_CACHE_MXI_SIZE = 500 << 20;

        String APP_DEBUG_URL = "http://api.dongci-test.wmlives.com/";   //这个是测试环境的baseUrl，如果服务端有修改，务必再次同步修改!!!

        String APP_RELEASE_URL = "https://api-02.wmlive.cn/";   //这个是正式环境的baseUrl，如果服务端有修改，务必再次同步修改!!!

        String CHECK_DEVICE_ID_URL = "api/sys/re-check-device";

        String APP_VERSION = BuildConfig.APP_VERSION;


        //log
        String APP_LOG_TAG = "DcAppLogMain";

        //最小点击间隔，防止快速点击
        long MINIMUM_CLICK_DELAY = 1000;

        //视频缩略帧的时间
        float VIDEO_COVER_CLIP_SECOND = 0.3f;//第0.2秒

        //app_key
        String WMLIVE_RD_APP_KEY = "4f9e1d6555bb56b0";

        //AppSecret
        String WMLIVE_RD_APP_SECRET = "3802546827b23964b214c1f3d31f4178daszf5wZGDqPYrUzmsQ0FUgVywiLMZendRqG7Qryp/m03TNfhWrjywuSVFuH5jqX40PexKwfj/ZU2dTHSIVcR5Dt6P4VTnqsOvJyV8gec+o=";

        String GREENDAO_DB_NAME = "db_dongci";

        String CHN_360 = "_360";
        String CHN_BAIDU = "baidu";
        String CHN_HUAWEI = "huawei";

    }

    public static class Utils {
        // 两次点击按钮之间的点击间隔不能少于1000毫秒
        private static final int MIN_CLICK_DELAY_TIME = 1000;
        private static long lastClickTime;

        public static boolean isFastClick() {
            boolean flag = false;
            long curClickTime = System.currentTimeMillis();
            if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
                flag = true;
            }
            lastClickTime = curClickTime;
            return flag;
        }
    }

    /**
     * EventBus的Type，以10递进
     */
    public interface EventType {
        //IM 消息通知notification
        int IM_NOTIFICATION_NEW_MSG = 10010;
        //action 消息通知notification
        int ACTION_NOTIFICATION_NEW_MSG = 10020;

        //私信
        int TYPE_IM_CHAT_MSG = 10030;
        //不是自己发的消息
        int TYPE_IM_CHAT_NOT_SELF_MSG = 10032;
        //系统消息
        int TYPE_IM_SYSTEM_MSG = 10040;
        //系统弹窗消息
        int TYPE_ALERT_SYSTEM_MSG = 10050;
        //刷新首页im消息的小红点
        int TYPE_REFRESH_HOME_IM_COUNT = 10060;

        //token失效重新登录
        int TYPE_RELOGIN = 30001;
        int TYPE_LOGOUT = 30010;//退出登录
        int TYPE_SCROLL = 30020;//首页是否能滑动
        int TYPE_GET_USER_INFO = 30030;//获取用户信息
        int TYPE_REFRESH_COMMENT = 30050;//刷新评论数
        int TYPE_VIDEO_DELETE = 30060;//视频被删除
        int TYPE_SWITCH_DANMA = 30070;//弹幕开关
        //拍摄完成后回到关注页面
        int TYPE_SHOW_MAIN_FIRST = 400001;

        //网络切换
        int TYPE_NETWORK_CHANGE = 500080;

        //作品删除了
        int TYPE_WORK_DELETED = 500090;

        //作品观看行为
        int TYPE_USER_BEHAVIOR = 500100;
        int TYPE_SHARE_EVENT = 500110;
        int TYPE_SHARE_CANCEL_EVENT = 500120;

        int TYPE_PLAY_DOWNLOAD = 500130;//观看缓存下载
        int TYPE_CREATE_DOWNLOAD = 500140;//共同创作下载
        int TYPE_UPLOAD = 500150;//上传视频

        int TYPE_TRIM_FINISH = 600010;//从录制中选取本地视频裁剪完成
        int TYPE_EFFECT_FINISH = 600020;//从录制中编辑特效完成
        int TYPE_PUBLISH_PRODUCT = 600030;//发布作品
        int TYPE_PUBLISH_PRODUCT_OK = 600040;//发布作品成功
        int TYPE_EDIT_FINISH = 600050;//编辑完成
        int TYPE_WEIBO_AUTH_OK = 600060;//微博认证成功
        int TYPE_REFRESH_COUNTDOWN = 600070;//刷新倒计时

        //登录成功
        int TYPE_LOGIN_OK = 700010;
        int TYPE_FOLLOW_OK = 700020;
        int TYPE_LIKE_OK = 700021;
        int TYPE_MODIFY_PERMISSION_OK = 700022;
        int TYPE_UPDATE_USER_INFO = 700030;//刷新消息数据库中的个人信息

        int TYPE_VIDEO_LIST = 700040;//同步详情页和列表页数据
        int TYPE_SYN_VIDEO_LIST = 700050;//同步详情页和列表页位置


        //作品发布状态
        int TYPE_PUBLISH_START = 80010;//作品发布开始
        int TYPE_PUBLISH_PROGRESS = 80020;//作品发布进度跟新
        int TYPE_PUBLISH_FINISH = 80030;//作品发布成功
        int TYPE_PUBLISH_ERRER = 80040;//作品发布失败
        int TYPE_PUBLISH_RETRY = 80050;//作品发布失败
    }

    public interface Social {
        String WECHAT_APP_ID = "wx4efac1ce792d3ce5";
        String WECHAT_APP_SECRET = "7cc7bdcf4d14a8dce09a0eb64efebfd8";
        String WECHAT_AUTH_STATE = "wechat_dc_auth";
        String WECHAT_AUTH_SCOPE = "snsapi_userinfo";

        String WEIBO_APP_KEY = "154282396";
        String WEIBO_APP_SECRET = "ffa4398b39beda24d9ee41c2e7a5fd1c";
        String WEIBO_REDIRECT_URL = "http://open.weibo.com/apps/154282396";
        String WEIBO_SCOPE = "email,direct_messages_read,direct_messages_write,friendships_groups_read," +
                "friendships_groups_write,statuses_to_me_read,follow_app_official_microblog,invitation_write";

        String QQ_APP_ID = "1106135591";
        String QQ_SCOPE = "all";
        String QQ_APP_SECRET = "KEYGEcnI8mJqD10dfWe";
    }

    /**
     * 魔窗跳转key
     */
    public interface MWConfig {
        String LINK_KEY_HOME = "home";
        String LINK_KEY_TOPIC = "topic";
        String LINK_KEY_USER = "user";
        String LINK_KEY_OPUS = "opus";
    }


    public interface DanmuConfig {
        // 弹幕库文字长度调节值,超过此大小进行显示时间调节
        int DANMU_TITLE_LENGTH = 30;
        // 弹幕调节显示时间文字长度基准值
        float DANMU_TITLE_DURATION_BASE = 30.0f;
    }

    /**
     * http请求错误码
     */
    public interface ErrorCode {

        /**
         * 网络错误
         */
        int TYPE_NET_ERROR = -1;
        /**
         * token失效重新登录
         */
        int TYPE_RELOGIN = 30001;
        /**
         * IM发送失败
         */
        int TYPE_IM_SEND_ERROR = 30004;
        /**
         * IM拉黑
         */
        int TYPE_IM_SNED_BLOCK = 30007;
    }

}
