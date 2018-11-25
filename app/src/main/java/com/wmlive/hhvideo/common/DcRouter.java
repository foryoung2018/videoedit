package com.wmlive.hhvideo.common;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.wmlive.hhvideo.common.base.BaseCompatActivity;
import com.wmlive.hhvideo.common.manager.MyAppActivityManager;
import com.wmlive.hhvideo.common.manager.TransferDataManager;
import com.wmlive.hhvideo.heihei.beans.main.MultiTypeVideoBean;
import com.wmlive.hhvideo.heihei.beans.record.TopicInfoEntity;
import com.wmlive.hhvideo.heihei.beans.recordmv.MvMaterialEntity;
import com.wmlive.hhvideo.heihei.login.AccountUtil;
import com.wmlive.hhvideo.heihei.mainhome.activity.MainActivity;
import com.wmlive.hhvideo.heihei.mainhome.activity.VideoDetailListActivity;
import com.wmlive.hhvideo.heihei.mainhome.activity.VideoListActivity;
import com.wmlive.hhvideo.heihei.mainhome.fragment.RecommendFragment;
import com.wmlive.hhvideo.heihei.mainhome.presenter.Block;
import com.wmlive.hhvideo.heihei.personal.activity.UserHomeActivity;
import com.wmlive.hhvideo.heihei.personal.activity.WebViewActivity;
import com.wmlive.hhvideo.heihei.record.activity.RecordMvActivity;
import com.wmlive.hhvideo.heihei.record.activity.SelectFrameActivity;
import com.wmlive.hhvideo.heihei.record.activity.SelectFrameActivity2;
import com.wmlive.hhvideo.heihei.record.activitypresenter.RecordMvActivityHelper;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.networklib.util.EventHelper;

import java.util.List;

import static com.wmlive.hhvideo.heihei.record.activitypresenter.RecordMvActivityHelper.EXTRA_RECORD_TYPE_FROM_JUMP;

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
        KLog.d("uri", "linkTo: deepLink==" + deepLink);
        if (!TextUtils.isEmpty(deepLink)) {
            Uri uri = Uri.parse(deepLink);
//  uri.getHost()==opus
// uri.getPath()==/action/jointeamwork
// uri.getAuthority()==opus
// uri.getFragment()==null
// uri.getScheme()==hhvideo
// uri.getSchemeSpecificPart()==//opus/action/jointeamwork?id=1578558082&topic_id=341&topic_name=topic_name&mw_ck=微信&mw_dc=&mw_dc_order=&mw_ios_dc=&mw_android_dc=&mw_mk=ABfw&mw_slk=ABf9&mw_tags=&mw_ulp=JTdCJTIyaWQlMjIlM0ElMjIlMjIlMkMlMjJ0b3BpY19pZCUyMiUzQSUyMiUyMiU3RA==&mw_tk=b5c3dd3929c97ab7ed4e1fd25d13cf5236490f72
// uri.getQuery()==id=1578558082&topic_id=341&topic_name=topic_name&mw_ck=微信&mw_dc=&mw_dc_order=&mw_ios_dc=&mw_android_dc=&mw_mk=ABfw&mw_slk=ABf9&mw_tags=&mw_ulp=JTdCJTIyaWQlMjIlM0ElMjIlMjIlMkMlMjJ0b3BpY19pZCUyMiUzQSUyMiUyMiU3RA==&mw_tk=b5c3dd3929c97ab7ed4e1fd25d13cf5236490f72

            KLog.d("uri", "linkTo: uri.getHost()==" + uri.getHost() +
                    "  uri.getPath()==" + uri.getPath() +
                    "  uri.getAuthority()==" + uri.getAuthority() +
                    "  uri.getFragment()==" + uri.getFragment() +
                    "  uri.getSchemeSpecificPart()==" + uri.getSchemeSpecificPart() +
                    "  uri.getScheme()==" + uri.getScheme() +
                    " uri.getQuery()==" + uri.getQuery());
            if (uri != null && "hhvideo".equals(uri.getScheme())) {
                switch (uri.getHost()) {
                    case "home"://首页
                        MyAppActivityManager.getInstance().finishAllActivityBefore(MainActivity.class);
                        break;
                    case "opus":
                        String path = uri.getPath();
                        switch (path) {
                            case "/detail"://详情页
                                String worksId = uri.getQueryParameter("id");
                                if (!TextUtils.isEmpty(worksId)) {
                                    MultiTypeVideoBean bean = new MultiTypeVideoBean();
                                    bean.videoId = Long.parseLong(worksId);
                                    bean.pageFrom = pageFrom;
                                    TransferDataManager.get().setVideoListData(null);
                                    VideoDetailListActivity.startVideoDetailListActivity(ctx, 0, RecommendFragment.TYPE_SINGLE_WORK, bean, null, null);
                                }
                                break;
                            case "/action/creative"://打开速拍
                                if (!TextUtils.isEmpty(AccountUtil.getToken())) {
                                    String template_name = uri.getQueryParameter("template_name");
                                    String bg_name = uri.getQueryParameter("bg_name");
                                    TopicInfoEntity topicInfo2 = new TopicInfoEntity();
                                    topicInfo2.topicTitle = uri.getQueryParameter("topic_name");
                                    topicInfo2.topicId = Long.valueOf(uri.getQueryParameter("topic_id") == null ? "0" : uri.getQueryParameter("topic_id"));
                                    RecordMvActivityHelper.startRecordActivity((BaseCompatActivity) ctx, EXTRA_RECORD_TYPE_FROM_JUMP, 0, template_name, bg_name, topicInfo2);
                                } else {
                                    BaseCompatActivity activity = (BaseCompatActivity) ctx;
                                    activity.showReLogin();
                                }
                                break;
                            case "/action/multigrid"://打开多格拍摄
                                if (!TextUtils.isEmpty(AccountUtil.getToken())) {
                                    String topicid = uri.getQueryParameter("topic_id");
                                    String topicname = uri.getQueryParameter("topic_name");
                                    String opus_layout = uri.getQueryParameter("opus_layout");
                                    TopicInfoEntity topicInfo = new TopicInfoEntity();
                                    if (topicid != null && topicname != null) {
                                        topicInfo.topicTitle = topicname;
                                        topicInfo.topicId = Long.valueOf(topicid);
                                    }
                                    SelectFrameActivity.startSelectFrameActivity((BaseCompatActivity) ctx, topicInfo, SelectFrameActivity.VIDEO_TYPE_RECORD);
                                } else {
                                    BaseCompatActivity activity = (BaseCompatActivity) ctx;
                                    activity.showReLogin();
                                }
                                break;
                            case "/action/jointeamwork"://踹跪
                                if (!TextUtils.isEmpty(AccountUtil.getToken())) {
                                    String id = uri.getQueryParameter("id");
                                    String tid = uri.getQueryParameter("topic_id");
                                    String tname = uri.getQueryParameter("topic_name");
                                    TopicInfoEntity topicInfo = new TopicInfoEntity();
                                    if (tid != null && tname != null) {
                                        topicInfo.topicTitle = tname;
                                        topicInfo.topicId = Long.valueOf(tid);
                                    }
                                    getOpusInfo(ctx, Long.parseLong(id), topicInfo);
                                } else {
                                    BaseCompatActivity activity = (BaseCompatActivity) ctx;
                                    activity.showReLogin();
                                }
                                break;

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

    private static void getOpusInfo(Context ctx, long opusID, TopicInfoEntity topicInfo) {
        NetModel.getOpusMaterial(opusID, new NetModel.NetCallback() {
            @Override
            public void onRequestOK(Object o) {
                MvMaterialEntity mvMaterialEntity = (MvMaterialEntity) o;
                if (mvMaterialEntity.opus != null) {//多格拍摄踹跪
                    SelectFrameActivity2.startSelectFrameActivity2((BaseCompatActivity) ctx, SelectFrameActivity2.VIDEO_TYPE_TEAMWORK, topicInfo, opusID, mvMaterialEntity.opus.frame_layout);
                } else {//速拍踹跪
                    if (mvMaterialEntity.template != null)
                        RecordMvActivityHelper.startRecordActivity((BaseCompatActivity) ctx, RecordMvActivityHelper.EXTRA_RECORD_TYPE_REPLACE, opusID, "", "", topicInfo);
                }
            }

            @Override
            public void onRequestError(int requestCode, Object o) {

            }
        });
    }

    public static void linkTo(Context ctx, String deepLink) {
        linkTo(ctx, deepLink, false, "");
    }
}
