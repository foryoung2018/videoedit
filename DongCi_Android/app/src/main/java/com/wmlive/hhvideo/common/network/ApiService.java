package com.wmlive.hhvideo.common.network;

import com.wmlive.hhvideo.heihei.beans.discovery.AddFavoriteBean;
import com.wmlive.hhvideo.heihei.beans.discovery.BannerListBean;
import com.wmlive.hhvideo.heihei.beans.discovery.DiscMessageResponse;
import com.wmlive.hhvideo.heihei.beans.discovery.FollowUserResponseEntity;
import com.wmlive.hhvideo.heihei.beans.discovery.MusicCategoryBean;
import com.wmlive.hhvideo.heihei.beans.discovery.MusicResultBean;
import com.wmlive.hhvideo.heihei.beans.discovery.RecommendUserResponse;
import com.wmlive.hhvideo.heihei.beans.discovery.TopicTypeListBean;
import com.wmlive.hhvideo.heihei.beans.frame.Frames;
import com.wmlive.hhvideo.heihei.beans.gifts.GiftListResponse;
import com.wmlive.hhvideo.heihei.beans.gifts.SendGiftResultResponse;
import com.wmlive.hhvideo.heihei.beans.immessage.IMMessageResponse;
import com.wmlive.hhvideo.heihei.beans.login.LoginUserResponse;
import com.wmlive.hhvideo.heihei.beans.login.SmsResponse;
import com.wmlive.hhvideo.heihei.beans.login.WxTokenEntity;
import com.wmlive.hhvideo.heihei.beans.main.CommentDeleteResponse;
import com.wmlive.hhvideo.heihei.beans.main.DiscoverMessageBean;
import com.wmlive.hhvideo.heihei.beans.main.RecommendResponse;
import com.wmlive.hhvideo.heihei.beans.main.ShortVideoInfoResponse;
import com.wmlive.hhvideo.heihei.beans.main.ShortVideoLoveResponse;
import com.wmlive.hhvideo.heihei.beans.main.SplashResourceResponse;
import com.wmlive.hhvideo.heihei.beans.main.UpdateSystemBean;
import com.wmlive.hhvideo.heihei.beans.main.VideoCommentListResponse;
import com.wmlive.hhvideo.heihei.beans.main.VideoCommentResponse;
import com.wmlive.hhvideo.heihei.beans.main.VideoModifyOpusResponse;
import com.wmlive.hhvideo.heihei.beans.opus.OpusLikeCommentResponse;
import com.wmlive.hhvideo.heihei.beans.opus.OpusMaterialEntity;
import com.wmlive.hhvideo.heihei.beans.opus.PublishResponseEntity;
import com.wmlive.hhvideo.heihei.beans.opus.TopListResponse;
import com.wmlive.hhvideo.heihei.beans.opus.UploadMaterialResponseEntity;
import com.wmlive.hhvideo.heihei.beans.oss.OSSTokenResponse;
import com.wmlive.hhvideo.heihei.beans.personal.BlockUserResponse;
import com.wmlive.hhvideo.heihei.beans.personal.DecibelListResponse;
import com.wmlive.hhvideo.heihei.beans.personal.ListFollowerFansResponse;
import com.wmlive.hhvideo.heihei.beans.personal.ListLikeResponse;
import com.wmlive.hhvideo.heihei.beans.personal.ReportTypeResponse;
import com.wmlive.hhvideo.heihei.beans.personal.UpdateUserResponse;
import com.wmlive.hhvideo.heihei.beans.personal.UserAccountChargeCreateOrderResponse;
import com.wmlive.hhvideo.heihei.beans.personal.UserAccountChargeResponse;
import com.wmlive.hhvideo.heihei.beans.personal.UserAccountDuihuanResponse;
import com.wmlive.hhvideo.heihei.beans.personal.UserAccountResponse;
import com.wmlive.hhvideo.heihei.beans.personal.UserHomeResponse;
import com.wmlive.hhvideo.heihei.beans.personal.WeiboBindEntity;
import com.wmlive.hhvideo.heihei.beans.quickcreative.CreativeTemplateListBean;
import com.wmlive.hhvideo.heihei.beans.recordmv.MvMaterialEntity;
import com.wmlive.hhvideo.heihei.beans.recordmv.SingleTemplateBean;
import com.wmlive.hhvideo.heihei.beans.search.SearchResponse;
import com.wmlive.hhvideo.heihei.beans.splash.CheckDeviceResponse;
import com.wmlive.hhvideo.heihei.beans.splash.InitUrlResponse;
import com.wmlive.hhvideo.heihei.beans.subject.TopicCreateResponse;
import com.wmlive.networklib.entity.BaseResponse;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Created by vhawk on 2017/5/19.
 */

public interface ApiService {

    //测试接口http://baobab.kaiyanapp.com/api/v4/categories/all
    @GET()
    Observable<Response<String>> testUrl(@Url String url);

    @GET
    Observable<Response<BaseResponse>> login(@Url String url, @Query("phone_no") String mobile, @Query("verify_code") String smsCode);

    /**
     * init接口，每次app启动时调用
     *
     * @return
     */
    @GET
    Observable<Response<InitUrlResponse>> init(@Url String url, @QueryMap Map<String, String> options);

    @GET
    Observable<Response<CheckDeviceResponse>> checkDeviceId(@Url String url, @Query("device_uuid") String newId, @Query("old_device_uuid") String oldId);


    /**
     * 获取验证码
     *
     * @param mobile
     * @param url
     * @return
     */
    @FormUrlEncoded
    @POST
    Observable<Response<SmsResponse>> sendSmsCode(@Url String url, @Field("phone_no") String mobile);


    /**
     * 手机号码登录
     *
     * @param mobile
     * @param smsCode
     * @param url
     * @return
     */
    @GET
    Observable<Response<LoginUserResponse>> mobileLogin(@Url String url, @Query("phone_no") String mobile, @Query("verify_code") String smsCode);

    /**
     * 第三方登录
     *
     * @param url
     * @param account_source
     * @param account_token
     * @param open_id
     * @param unionid
     * @return
     */
    @FormUrlEncoded
    @POST
    Observable<Response<LoginUserResponse>> signIn(@Url String url, @Field("account_source") String account_source, @Field("account_token") String account_token, @Field("open_id") String open_id, @Field("unionid") String unionid);

    /**
     * 获取微信的accessToken
     *
     * @param url
     * @return
     */
    @GET
    Observable<Response<WxTokenEntity>> getWxAccessToken(@Url String url);

    /**
     * 获取推荐视频列表
     *
     * @param url
     * @param offset
     * @return
     */
    @GET
    Observable<Response<RecommendResponse>> fetchRecommendVideo(@Url String url, @Query("offset") int offset);

    /**
     * 获取关注用户的视频列表
     *
     * @param url
     * @param offset
     * @return
     */
    @GET
    Observable<Response<RecommendResponse>> fetchAttentionVideo(@Url String url, @Query("offset") int offset);

    /**
     * 获取单个视频的详细信息
     *
     * @param url
     * @param shortVideoId
     * @param barrage      是否加载弹幕 0：不加载，1：加载。 默认0
     * @return
     */
    @GET
    Observable<Response<ShortVideoInfoResponse>> fetchVideoInfo(@Url String url, @Query("opus_id") long shortVideoId, @Query("barrage") int barrage);

    /**
     * 点赞与取消赞
     *
     * @param url
     * @param videoId
     * @return
     */
    @GET
    Observable<Response<ShortVideoLoveResponse>> loveVideo(@Url String url, @Query("opus_id") long videoId);

    /**
     * 关注主播
     *
     * @param url
     * @return
     */
    @GET
    Observable<Response<FollowUserResponseEntity>> followAnchor(@Url String url, @QueryMap Map<String, String> options);

    @GET
    Observable<Response<FollowUserResponseEntity>> followAll(@Url String url, @Query("user_ids") String videoId);

    /**
     * 获取评论列表
     *
     * @param url
     * @param videoId
     * @param offset
     * @return
     */
    @GET
    Observable<Response<VideoCommentListResponse>> fetchCommentList(@Url String url, @Query("opus_id") long videoId, @Query("offset") int offset);

    /**
     * 评论、回复评论
     *
     * @param url
     * @param videoId
     * @param title
     * @param remindUserId
     * @param replayUserId
     * @return
     */
    @FormUrlEncoded
    @POST
    Observable<Response<VideoCommentResponse>> commentVideo(@Url String url, @Field("opus_id") long videoId, @Field("title") String title, @Field("at_user_ids") String remindUserId, @Field("reply_user_id") long replayUserId);

    /**
     * 删除评论
     *
     * @param url
     * @param id
     * @param commentId @return
     */
    @GET
    Observable<Response<CommentDeleteResponse>> deleteComment(@Url String url, @Query("comment_id") long commentId, @Query("opus_id") long id);

    /**
     * 评论 点赞
     *
     * @param url
     * @param commentId
     * @param is_cancel
     * @return
     */
    @GET
    Observable<Response<OpusLikeCommentResponse>> likeComment(@Url String url, @Query("comment_id") long commentId, @Query("is_cancel") int is_cancel);

    /**
     * 获取最新视频列表
     *
     * @param url
     * @param offset
     * @return
     */
    @GET
    Observable<Response<RecommendResponse>> explosionVideo(@Url String url, @Query("offset") int offset);


    /**
     * 个人主页
     *
     * @param url
     * @param user_id
     * @return
     */
    @GET
    Observable<Response<UserHomeResponse>> getUserHome(@Url String url, @Query("user_id") long user_id);


    /**
     * 极光 绑定用户
     *
     * @param url
     * @param device_uuid
     * @param apns_token
     * @return
     */
    @GET
    Observable<Response<BaseResponse>> bindUser(@Url String url, @Query("device_uuid") String device_uuid,
                                                @Query("apns_token") String apns_token,
                                                @Query("certype") int certype,
                                                @Query("apns_type") String apns_type);

    /**
     * 关注列表
     *
     * @param url
     * @param user_id
     * @param offset
     * @return
     */
    @GET
    Observable<Response<ListFollowerFansResponse>> getListFollower(@Url String url, @Query("user_id") long user_id, @Query("offset") int offset);

    /**
     * 修改关注列表--关注状态
     *
     * @param url
     * @param user_id
     * @return
     */
    @GET
    Observable<Response<BaseResponse>> updateFollowerState(@Url String url, @Query("user_id") long user_id);

    /**
     * 修改粉丝列表--关注状态
     *
     * @param url
     * @param user_id
     * @return
     */
    @GET
    Observable<Response<BaseResponse>> updateFansState(@Url String url, @Query("user_id") long user_id);

    /**
     * 粉丝列表
     *
     * @param url
     * @param user_id
     * @param offset
     * @return
     */
    @GET
    Observable<Response<ListFollowerFansResponse>> getListFans(@Url String url, @Query("user_id") long user_id, @Query("offset") int offset);

    /**
     * 更改用户信息
     *
     * @param url
     * @param name
     * @param gender
     * @param birth_day
     * @param region
     * @param description
     * @param cover_ori
     * @param cover_ori_file_sign
     * @return
     */
    @FormUrlEncoded
    @POST
    Observable<Response<UpdateUserResponse>> updateUser(@Url String url, @Field("name") String name, @Field("gender") String gender, @Field("birth_day") String birth_day, @Field("region") String region, @Field("description") String description, @Field("cover_ori") String cover_ori, @Field("cover_ori_file_sign") String cover_ori_file_sign);

    /**
     * 作品列表
     *
     * @param url
     * @param user_id
     * @param offset
     * @return
     */
    @GET
    Observable<Response<ListLikeResponse>> getPersonalProductList(@Url String url, @Query("user_id") long user_id, @Query("offset") int offset);

    /**
     * 喜欢的作品列表
     *
     * @param url
     * @param user_id
     * @param offset
     * @return
     */
    @GET
    Observable<Response<ListLikeResponse>> getPersonalLoveList(@Url String url, @Query("user_id") long user_id, @Query("offset") int offset);

    /**
     * 举报列表
     *
     * @param url
     * @return
     */
    @GET
    Observable<Response<ReportTypeResponse>> getListReportType(@Url String url);

    /**
     * 举报用户
     *
     * @param url
     * @return
     */
    @FormUrlEncoded
    @POST
    Observable<Response<BaseResponse>> reportUser(@Url String url, @Field("user_id") long user_id, @Field("report_type_id") int report_type_id);

    /**
     * 拉黑用户
     *
     * @param url
     * @return
     */
    @GET
    Observable<Response<BlockUserResponse>> blockUser(@Url String url, @Query("user_id") long user_id, @Query("type") int blockType);

    /**
     * 登出
     *
     * @param url
     * @return
     */
    @FormUrlEncoded
    @POST
    Observable<Response<BaseResponse>> logout(@Url String url, @Field("token") String token);

    //添加话题
    @FormUrlEncoded
    @POST
    Observable<Response<TopicCreateResponse>> createTopic(@Url String url, @Field("name") String name, @Field("desc") String desc);

    //搜索功能（用户，话题，音乐）
    @FormUrlEncoded
    @POST
    Observable<Response<SearchResponse>> searchSearch(@Url String url, @Field("keyword") String keyword, @Field("doc_type") String doc_type, @Field("offset") int offset);

    //发现模块顶部的banner
    @GET
    Observable<Response<BannerListBean>> banner(@Url String url);

    //发现模块话题列表
    @GET
    Observable<Response<TopicTypeListBean>> topicList(@Url String url, @Query("offset") int offset);

    //发现模块音乐分类
    @GET
    Observable<Response<MusicCategoryBean>> musicCategory(@Url String url);

    //发现模块按分类搜索音乐
    @GET
    Observable<Response<MusicResultBean>> searchMusicByCat(@Url String url, @Query("cat_id") long cat_id, @Query("offset") int offset);

    //发现模块添加到收藏
    @FormUrlEncoded
    @POST
    Observable<Response<AddFavoriteBean>> addFavorite(@Url String url, @Field("music_id") long cat_id);

    //发现模块话题内容列表
    @GET
    Observable<Response<RecommendResponse>> topicOpusList(@Url String url, @Query("topic_id") long topic_id, @Query("offset") int offset);

    /**
     * 获取osstoken
     *
     * @param url
     * @param module 模块：message，avatar，opus
     * @param format message’: [‘png’, ‘mp3’, ‘jpg’, ‘mp4’]，
     *               avatar’: [‘png’, ‘jpg’] ‘
     *               opus: [‘mp4’, ‘jpg’, ‘webp’, ‘jpeg’]
     * @return
     */
    @FormUrlEncoded
    @POST
    Observable<Response<OSSTokenResponse>> getOssToken(@Url String url, @Field("module") String module, @Field("format") String format);

    //osstoken失效,重新获取osstoken
    @GET
    Observable<Response<OSSTokenResponse>> reGetOssToken(@Url String url, @Query("token") String token, @Query("access_key_id") String access_key_id);

    //发现模块音乐内容列表
    @GET
    Observable<Response<RecommendResponse>> musicOpusList(@Url String url, @Query("music_id") long music_id, @Query("offset") int offset);


    /**
     * 作品举报列表
     *
     * @param url
     * @return
     */
    @GET
    Observable<Response<ReportTypeResponse>> fetchReportType(@Url String url);


    @FormUrlEncoded
    @POST
    Observable<Response<BaseResponse>> reportWorks(@Url String url, @Field("opus_id") long videoId, @Field("report_type_id") long reportTypeId);

    /**
     * 删除作品
     *
     * @param url
     * @param videoId
     * @return
     */
    @GET
    Observable<Response<BaseResponse>> deleteVideo(@Url String url, @Query("opus_id") long videoId);

    /**
     * 修改作品共同创作权限与描述
     *
     * @param url
     * @param videoId
     * @return
     */
    @GET
    Observable<Response<VideoModifyOpusResponse>> modifyOpus(@Url String url, @Query("opus_id") long videoId, @Query("title") String title, @Query("switch") String creationFlag);

    @Streaming
    @GET
    Observable<ResponseBody> downLoadVideo(@Url String fileUrl);

    //我的收藏音乐
    @GET
    Observable<Response<MusicResultBean>> myCollect(@Url String url, @Query("offset") int offset);

    //检查系统升级
    @GET
    Observable<Response<UpdateSystemBean>> checkSystemAppUpdate(@Url String urk, @Query("version") String version, @Query("device_platform") String device_platform, @Query("app_name") String app_name, @Query("channel") String channel);

    /**
     * 用户观看行为
     *
     * @param url
     * @param videoId
     * @param viewLength
     * @param shareCount
     * @param downLoadCount
     * @return
     */
    @FormUrlEncoded
    @POST
    Observable<Response<BaseResponse>> sendUserBehavior(@Url String url, @Field("opus_id") long videoId, @Field("view_length") long viewLength, @Field("share_count") long shareCount, @Field("download_count") long downLoadCount);

    /**
     * @param url
     * @param videoId
     * @param viewLength
     * @param shareCount
     * @param downLoadCount
     * @return
     */
    @FormUrlEncoded
    @POST
    Observable<Response<BaseResponse>> sendPlayDownloadLog(@Url String url, @Field("opus_id") long videoId, @Field("view_length") long viewLength, @Field("share_count") long shareCount, @Field("download_count") long downLoadCount);

    /**
     * 素材下载
     *
     * @param url
     * @param url1
     * @param material_id
     * @param viewLength
     * @param shareCount
     * @param downLoadCount
     * @param speed
     * @param res
     * @return
     */
    @GET
    Observable<Response<BaseResponse>> sendMaterialDownLoad(@Url String url, @Query("url") String url1, @Query("material_id") String material_id,
                                                            @Query("file_len") String viewLength,
                                                            @Query("download_len") String shareCount, @Query("download_duration") String downLoadCount,
                                                            @Query("download_speed") String speed, @Query("res") String res, @QueryMap Map<String, String> options);

    /**
     * 视频下载
     *
     * @param url
     * @param url1
     * @param opus_id
     * @param viewLength
     * @param shareCount
     * @param downLoadCount
     * @param speed
     * @param buffer_duration
     * @return
     */
    @GET
    Observable<Response<BaseResponse>> sendVideoDownLoad(@Url String url, @Query("url") String url1, @Query("opus_id") String opus_id,
                                                         @Query("file_len") String viewLength, @Query("download_len") String shareCount,
                                                         @Query("download_duration") String downLoadCount, @Query("download_speed") String speed,
                                                         @Query("buffer_duration") String buffer_duration, @QueryMap Map<String, String> options);

    /**
     * 视频上传
     *
     * @param url
     * @param file_len
     * @param upoload_duration
     * @param speed
     * @param res
     * @return
     */
    @GET
    Observable<Response<BaseResponse>> sendVideoUpload(@Url String url, @Query("file_len") String file_len, @Query("upoload_duration") String upoload_duration,
                                                       @Query("upload_speed") String speed, @Query("res") String res, @QueryMap Map<String, String> options);

    /**
     * 网络检测日志上传
     *
     * @param url
     * @param content
     * @param device_id
     * @param os_platform
     * @param app_version
     * @param device_model
     * @return
     */
    @FormUrlEncoded
    @POST
    Observable<Response<BaseResponse>> sendNetCheckLog(@Url String url, @Field("content") String content, @Field("device_id") String device_id, @Field("os_platform") String os_platform, @Field("app_version") String app_version, @Field("device_model") String device_model);


    //发现的消息提醒
    @GET
    Observable<Response<DiscoverMessageBean>> getDiscoveryMessage(@Url String url, @Query("latest_time") long latestTime, @Query("latest_news_time") long latestNewsTime);

    /**
     * 账户信息
     *
     * @param url
     * @return
     */
    @GET
    Observable<Response<UserAccountResponse>> getAccountInfo(@Url String url);

    /**
     * 获取充值列表
     *
     * @param url
     * @return
     */
    @GET
    Observable<Response<UserAccountChargeResponse>> getAccountChargeList(@Url String url);

    /**
     * 获取兑换列表
     *
     * @param url
     * @return
     */
    @GET
    Observable<Response<UserAccountDuihuanResponse>> getAccountDuihuanList(@Url String url);

    /**
     * 兑换钻石
     *
     * @param url
     * @param package_id
     * @return
     */
    @GET
    Observable<Response<UserAccountResponse>> getDuiHuanJinbi(@Url String url, @Query("package_id") int package_id);

    /**
     * 创建订单
     *
     * @param url
     * @return
     */
    @FormUrlEncoded
    @POST
    Observable<Response<UserAccountChargeCreateOrderResponse>> getCreateOrder(@Url String url, @Field("package_id") long package_id);


    //送礼物
    @FormUrlEncoded
    @POST
    Observable<Response<UserAccountResponse>> sendGift(@Url String url, @Field("opus_id") long opus_id, @Field("gift_id") String gift_id, @Field("amount") String amount);

    //送礼物
    @FormUrlEncoded
    @POST
    Observable<Response<SendGiftResultResponse>> sendGiftV2(@Url String url,
                                                            @Field("opus_id") long opusId,
                                                            @Field("gift_id") String giftIds,
                                                            @Field("amount") String amount);


    //上传日志接口
    @Multipart
    @POST
    Observable<Response<BaseResponse>> uploadLog(@Url String url, @Part MultipartBody.Part file);

    //多文件上传接口
    @Multipart
    @POST
    Observable<Response<BaseResponse>> fileUpload(@Url String url, @PartMap Map<String, RequestBody> map);


    //上传素材接口
    @FormUrlEncoded
    @POST
    Observable<Response<UploadMaterialResponseEntity>> uploadMaterial(@Url String url,
                                                                      @Field("path") String path,
                                                                      @Field("sign") String sign,
                                                                      @Field("length") long length,
                                                                      @Field("original_id") long originalId,
                                                                      @Field("music_id") long musicId,
                                                                      @Field("crc64") String crc64,
                                                                      @Field("quality") String quality);

    //上传作品接口
    @FormUrlEncoded
    @POST
    Observable<Response<PublishResponseEntity>> publishProduct(@Url String url, @FieldMap Map<String, String> map);

    // 得到作品素材信息
    @GET
    Observable<Response<OpusMaterialEntity>> getOpusMaterial(@Url String url, @Query("opus_id") long opusId);

    // 得到创意Mv作品素材信息
    @GET
    Observable<Response<MvMaterialEntity>> getMvOpusMaterial(@Url String url, @Query("opus_id") long opusId);

    // 得到作作品使用的模板
    @GET
    Observable<Response<SingleTemplateBean>> getOpusTemplate(@Url String url, @Query("opus_id") long opusId, @Query("only_template") String onlyTemplate);

    //获取画框布局数据
    @GET
    Observable<Response<Frames>> getFrameLayoutList(@Url String url);

    @GET
    Observable<Response<RecommendUserResponse>> getRecommendUserList(@Url String url, @Query("offset") int offset);

    @GET
    Observable<Response<ListLikeResponse>> getTogetherList(@Url String url, @Query("user_id") long user_id, @Query("offset") int offset);

    /**
     * 验证邀请码
     */
    @FormUrlEncoded
    @POST
    Observable<Response<LoginUserResponse>> getVerifyInvitationCode(@Url String url, @Field("code") String code);

    /**
     * 获取用户信息
     */
    @FormUrlEncoded
    @POST
    Observable<Response<LoginUserResponse>> getUserInfo(@Url String url, @Field("user_id") long user_id);

    /**
     * 获取所有的礼物资源
     *
     * @param url
     * @return
     */
    @GET
    Observable<Response<GiftListResponse>> getGiftResource(@Url String url);

    /**
     * 获取作品的礼物资源列表
     *
     * @param url
     * @return
     */
    @GET
    Observable<Response<GiftListResponse>> getGiftListV2(@Url String url, @Query("opus_id") long videoId);

    /**
     * im送礼物列表
     *
     * @param url
     * @param userId
     * @return
     */
    @GET
    Observable<Response<GiftListResponse>> getImGiftList(@Url String url, @Query("user_id") long userId);


    @GET
    Observable<Response<CreativeTemplateListBean>> getCreativeList(@Url String url, @Query("offset") int offset);

    /**
     * 分贝列表
     *
     * @param url
     * @return
     */
    @GET
    Observable<Response<DecibelListResponse>> getDecibelList(@Url String url, @Query("user_id") long userId, @Query("offset") int offset);


    /**
     * 解除绑定微博
     *
     * @param url
     * @return
     */
    @FormUrlEncoded
    @POST
    Observable<Response<BaseResponse>> unbindWeibo(@Url String url, @Field("token") String token);

    /**
     * 绑定微博
     *
     * @param url
     * @return
     */
    @FormUrlEncoded
    @POST
    Observable<Response<WeiboBindEntity>> bindWeibo(@Url String url, @Field("account_token") String token, @Field("weibo_id") String weibo_id);

    /**
     * 作品分贝列表
     *
     * @param url
     * @return
     */
    @GET
    Observable<Response<DecibelListResponse>> getVideoDecibelList(@Url String url, @Query("opus_id") long opusId, @Query("offset") int offset);

    /**
     * 开屏动画
     *
     * @param url
     * @return
     */
    @GET
    Observable<Response<SplashResourceResponse>> getLoadSplash(@Url String url);

    /**
     * 发送IM 信息
     *
     * @param url
     * @return
     */
    @FormUrlEncoded
    @POST
    Observable<Response<IMMessageResponse>> sendIMMessage(@Url String url, @Field("user_id") long userId, @Field("msg_type") String msgType, @Field("local_msg_id") String msgId, @Field("content") String content);

    /**
     * IM中购买礼物
     *
     * @param url
     * @return
     */
    @FormUrlEncoded
    @POST
    Observable<Response<SendGiftResultResponse>> buyIMGift(@Url String url, @Field("user_id") long userId, @Field("gift_id") String giftId, @Field("amount") String amount);

    @GET
    Observable<Response<BannerListBean>> getImBanner(@Url String url);

    /**
     * 搜索关注的人
     *
     * @param url
     * @return
     */
    @GET
    Observable<Response<SearchResponse>> searchFollow(@Url String url, @Query("key_word") String key_word, @Query("offset") int offset);


    @GET
    Observable<Response<SearchResponse>> getBlockList(@Url String url, @Query("offset") int offset);

    /**
     * 上传分享行为数据
     *
     * @param url
     * @return
     */
    @GET
    Observable<Response<BaseResponse>> getShareLog(@Url String url, @QueryMap Map<String, String> options);

    @GET
    Observable<Response<DiscMessageResponse>> getListSystemNews(@Url String url, @Query("offset") int offset);

    @GET
    Observable<Response<TopListResponse>> getOpusTopList(@Url String url, @Query("offset") int offset);

}
