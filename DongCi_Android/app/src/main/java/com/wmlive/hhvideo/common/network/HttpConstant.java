package com.wmlive.hhvideo.common.network;

/**
 * 网络请求接口的requestCode
 */

public interface HttpConstant {

    //登录，参数以16递进
    int TYPE_INIT = 0x10000;
    int TYPE_MOBILE_LOGIN = 0x10001;
    int TYPE_SMS_CODE = 0x10002;
    int TYPE_HOME_LIST = 0x10010;

    // 我的主页模块
    int TYPE_FETCH_COMMENT = 0x20000;
    int TYPE_FETCH_MORE_COMMENT = 0x20010;
    int TYPE_EXPLOSION_VIDEO = 0x20020;
    int TYPE_RECOMMEND_VIDEO_LIST = 0x20030;
    int TYPE_VIDEO_INFO = 0x20040;
    int TYPE_VIDEO_LOVE = 0x20050;
    int TYPE_USER_FOLLOW = 0x20060;
    int TYPE_USER_FOLLOW_ALL = 0x20064;
    int TYPE_COMMENT_LIST = 0x20070;
    int TYPE_DECIBEL_LIST = 0x20074;
    int TYPE_COMMENT_DELETE = 0x20080;
    int TYPE_REPORT_TYPE_LIST = 0x20090;
    int TYPE_REPORT = 0x20100;
    int TYPE_VIDEO_DELETE = 0x20110;
    int TYPE_DOWNLOAD = 0x20120;
    int TYPE_USER_BEHAVIOR = 0x20130;
    int TYPE_FOLLOW_VIDEO_LIST = 0x20140;//关注视频列表
    int TYPE_DISCOVER_MESSAGE = 0x20150;//发现的消息提醒
    int TYPE_GIFT_LIST = 0x20160;//获取礼物列表
    int TYPE_SEND_GIFT = 0x20170;//送礼物
    int TYPE_COMMENT_CLICK_STARS = 0x20180;//评论 点赞
    int TYPE_EDIT_OPUS_INFO = 0x20190; // 共同创作权限与描述
    int TYPE_DOWNLOAD_MATERIAL = 0x20200;//素材下载
    int TYPE_DOWNLOAD_VIDEO = 0x20210;//视频下载
    int TYPE_UPLOAD_VIDEO = 0x20220;//视频上传
    String SUCCESS = "success";
    String Fail = "fail";
    String Cancel = "cancel";

    //个人中心模块
    int TYPE_PERSONAL_HOME = 0x30000;//用戶主页
    int TYPE_PERSONAL_PRODUCT_LIST = 0x30010;//用户作品列表
    int TYPE_PERSONAL_LIKE_LIST = 0x30020;//用户喜欢列表
    int TYPE_PERSONAL_FOCUS_LIST = 0x30030;//关注列表
    int TYPE_PERSONAL_FANS_LIST = 0x300040;//粉丝列表
    int TYPE_PERSONAL_UPDATE_USER = 0x30050;//更新用户
    int TYPE_PERSONAL_FOCUS_UNFOLLOW = 0x30060;//取消关注
    int TYPE_PERSONAL_FOCUS_FOLLOW = 0x30070;//关注用户
    int TYPE_PERSONAL_FANS_UNFOLLOW = 0x30080;//粉丝--取消关注
    int TYPE_PERSONAL_FANS_FOLLOW = 0x30090;//粉丝--关注用户
    int TYPE_PERSONAL_OTHER_HOME = 0x30100;//他人主页
    int TYPE_PERSONAL_REPORT = 0x30110;//举报
    int TYPE_PERSONAL_REPORT_LIST = 0x30120;//举报用户列表
    int TYPE_PERSONAL_SIGNOUT = 0x30130;//登出
    int TYPE_PERSONAL_POST_USER_HEAD = 0x30140;//上传头像到阿里云服务器上
    //极光 绑定User
    int TYPE_JPUSH_BIND_USER = 0X30150;
    //账户
    int TYPE_USER_ACCOUNT_INFO = 0X30160;//账户信息
    int TYPE_USER_ACCOUNT_CHARGE = 0X30170;//充值列表
    int TYPE_USER_ACCOUNT_DUIHUAN = 0X30180;//兑换列表
    int TYPE_USER_ACCOUNT_DUIHUAN_JINBI = 0X30190;//兑换列表
    int TYPE_USER_ACCOUNT_ChARGE_CREATE_ORDER = 0X30200;//兑换列表
    int TYPE_USER_VERIFY_INVITATION_CODE = 0X30210;//验证邀请码
    int TYPE_USER_USER_INFO = 0X30220;//获取用户信息
    int TYPE_SPLASH_IMAGE = 0X30230;//开屏动画

    //登录
    int TYPE_LOGIN_WECHAT = 0x30240;//wechat登录
    //登录
    int TYPE_LOGIN_SINA = 0x30250;//sina登录
    int TYPE_PERSONAL_BLOCK = 0x30260; //拉黑

    //发现的搜索
    int TYPE_DISCOVERY_SEARCH = 0x40100;
    //发现顶部轮播图
    int TYPE_DISCOVERY_BANNER = 0x40110;
    //发现的话题和音乐
    int TYPE_DISCOVERY_TOPIC_HOME = 0x40120;
    //获取音乐的分类
    int TYPE_DISCOVERY_GET_CATEGORY = 0x40130;
    //按照分类搜索音乐
    int TYPE_DISCOVERY_SEARCH_BY_CAT = 0x40140;
    //收藏音乐
    int TYPE_DISCOVERY_ADD_FAVORITE = 0x40150;
    //话题列表
    int TYPE_DISCOVERY_TOPIC_LIST = 0x40160;
    //音乐列表
    int TYPE_DISCOVERY_MUSIC_LIST = 0x40170;
    //我的收藏列表
    int TYPE_DISCOVERY_COLLECT_LIST = 0x40180;

    //话题模块的code
    int TYPE_TOPIC_ADD_CODE = 0x50000;//添加话题
    int TYPE_TOPIC_LIST_CODE = 0x50010;//话题列表
    int TYPE_TOPIC_SEARCH_LIST_CODE = 0x50020;//搜索话题列表

    //搜索模块
    int TYPE_SEARCH_TOPI_CODE = 0x60010;//搜索中话题
    int TYPE_SEARCH_USER_CODE = 0x60020;//搜索中用户
    int TYPE_SEARCH_MUSIC_CODE = 0x60030;//搜索中音乐

    int TYPE_OSSTOKEN_TOKEN_CODE = 0x70000;//获取osstoken
    int TYPE_OSSTOKEN_RETOKEN_CODE = 0X70001;//osstoken失效,重新获取

    //作品模块
    int TYPE_OPUS_UPLOAD_CODE = 0x80000;//错误信息

    //升级
    int TYPE_CHECK_SYSTEM_UPLOAD_CODE = 0x90000;//错误信息
    int TYPE_UPLOAD_LOG = 0x90010;//上传Log
    int TYPE_FRAME_LAYOUT = 0X90020; //获取画框列表

    //礼物
    int TYPE_GIFT_INOF = 0X110000;//礼物列表

    int TYPE_UPLOAD_MATERIAL = 0X110010;//上传素材
    int TYPE_PUBLISH_PRODUCT = 0X110020;//发布作品
    int TYPE_GET_MATERIAL = 0X110030; // 获取作品素材列表
    int TYPE_GET_TEMPLATE = 0X110031; // 获取作品使用的模板

    int TYPE_GET_RECOMMEND_USER = 0X110040; // 获取推荐用户列表

    //获取微信token
    int TYPE_GET_WECHAT_TOKEN = 0X110050;


    //获取所有礼物资源
    int TYPE_GET_ALL_GIFT_RESOURCE = 0X120050;
    //获取某个作品的礼物资源
    int TYPE_GET_GIFT_LIST = 0X120070;
    //获取分贝榜单
    int TYPE_GET_DECIBEL_LIST = 0X120080;

    //绑定微博
    int TYPE_BIND_WEIBO = 0X120090;

    //解除绑定微博
    int TYPE_UNBIND_WEIBO = 0X120100;

    // IM消息
    int TYPE_IM_SEND_MESSAGE = 0X130000; // 发送IM消息
    int TYPE_IM_BUY_GIFT = 0X130010; // IM中购买礼物
    int TYPE_IM_BANNER = 0X130020; // im页banner

    int TYPE_IM_SEARCH_FOLLOW = 0X130030; // 搜索关注的人
    int TYPE_BLOCK_LIST = 0X130040; // 黑名单列表

    int TYPE_CHECK_DEVICE_ID = 0X130050;

    int TYPE_DISC_MESSAGE_LIST = 0X130055; // 发现消息列表
    int TYPE_TOP_LIST = 0X130060;

    //快速创意短视频
    int TYPE_CREATIVELIST = 0X140001;//获取模板列表


}
