package com.wmlive.hhvideo.common;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.wmlive.hhvideo.common.manager.MyAppActivityManager;
import com.wmlive.hhvideo.common.manager.TransferDataManager;
import com.wmlive.hhvideo.heihei.beans.main.MultiTypeVideoBean;
import com.wmlive.hhvideo.heihei.mainhome.activity.MainActivity;
import com.wmlive.hhvideo.heihei.mainhome.activity.VideoDetailListActivity;
import com.wmlive.hhvideo.heihei.mainhome.activity.VideoListActivity;
import com.wmlive.hhvideo.heihei.mainhome.fragment.RecommendFragment;
import com.wmlive.hhvideo.heihei.personal.activity.UserHomeActivity;
import com.wmlive.hhvideo.heihei.personal.activity.WebViewActivity;
import com.wmlive.networklib.util.EventHelper;

/**
 * Created by lsq on 12/12/2017.5:18 PM
 *
 * @author lsq
 * @describe DeepLink页面跳转
 */

public class DcRouter {

    public static final String KEY_ROUTE_KEY = "id";

    /**
     * DeepLink的跳转
     * 例如    hhvideo://opus/detail?id=10001
     * scheme：hhvideo
     * host：opus
     * path：/detail
     * Query：id=10001
     * QueryParameter:10001
     *
     * @param deepLink
     * @return false 未处理 true 正常处理
     */
    public static boolean linkTo(Context ctx, String deepLink, boolean needBackMainPage, String pageFrom) {
        if (!TextUtils.isEmpty(deepLink)) {
            Uri uri = Uri.parse(deepLink);
            if (uri != null && "hhvideo".equals(uri.getScheme())) {
                switch (uri.getHost()) {
                    case "home"://首页
                        MyAppActivityManager.getInstance().finishAllActivityBefore(MainActivity.class);
                        break;
                    case "opus"://作品详情
                        String worksId = uri.getQueryParameter("id");
                        if (!TextUtils.isEmpty(worksId)) {
//                            VideoDetailActivity.startVideoDetailActivity(ctx, Long.parseLong(worksId), 0, needBackMainPage ? worksId : null);
                            MultiTypeVideoBean bean = new MultiTypeVideoBean();
                            bean.videoId = Long.parseLong(worksId);
                            bean.pageFrom = pageFrom;
                            TransferDataManager.get().setVideoListData(null);
                            VideoDetailListActivity.startVideoDetailListActivity(ctx, 0, RecommendFragment.TYPE_SINGLE_WORK, bean,null,null);
                        }
                        break;
                    case "topic"://话题详情
                        String topicId = uri.getQueryParameter("id");
                        if (!TextUtils.isEmpty(topicId)) {
                            VideoListActivity.startVideoListActivity(ctx, RecommendFragment.TYPE_TOPIC, MultiTypeVideoBean.createTopicParma(Long.parseLong(topicId), 0, null), needBackMainPage ? topicId : null);
                        }
                        break;
                    case "music"://音乐详情
                        String musicId = uri.getQueryParameter("id");
                        if (!TextUtils.isEmpty(musicId)) {
                            VideoListActivity.startVideoListActivity(ctx, RecommendFragment.TYPE_TOPIC, MultiTypeVideoBean.createTopicParma(Long.parseLong(musicId), 0, null), needBackMainPage ? musicId : null);
                        }
                        break;
                    case "user"://个人主页
                        String userId = uri.getQueryParameter("id");
                        if (!TextUtils.isEmpty(userId)) {
                            UserHomeActivity.startUserHomeActivity(ctx, Long.valueOf(userId), needBackMainPage ? userId : null);
                        }
                        break;
                    case "webpage"://打开网页
                        String pageUrl = uri.getQueryParameter("url");   //页面的url
                        String type = uri.getQueryParameter("type");
                        if (!TextUtils.isEmpty(pageUrl)) {
                            if ("1".equalsIgnoreCase(type)) {
                                Intent intent = new Intent();//type=1默认浏览器打开
                                intent.setAction(Intent.ACTION_VIEW);
                                intent.setData(Uri.parse(pageUrl));
                                ctx.startActivity(intent);
                            } else {//type=0内嵌浏览器打开
                                WebViewActivity.startWebActivity(ctx, pageUrl, "");
                            }
                        }
                        break;
                    case "message"://进入铃铛消息
                        EventHelper.post(GlobalParams.EventType.TYPE_SHOW_MAIN_FIRST, 3);
                        break;
                    default:
                        MyAppActivityManager.getInstance().finishAllActivityBefore(MainActivity.class);
                        break;
                }
                return true;
            }
        }
        return false;
    }

    public static void linkTo(Context ctx, String deepLink) {
        linkTo(ctx, deepLink, false,"");
    }
}
