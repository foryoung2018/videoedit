package com.wmlive.hhvideo.heihei.beans.login;

import android.text.TextUtils;

import com.wmlive.hhvideo.common.base.BaseModel;
import com.wmlive.hhvideo.heihei.beans.main.ShareInfo;
import com.wmlive.hhvideo.heihei.beans.personal.UserHomeData;
import com.wmlive.hhvideo.heihei.beans.personal.UserHomeRelation;

import java.util.List;

/**
 * Created by vhawk on 2017/5/24.
 */

public class UserInfo extends BaseModel {
    public static final String UNSPECIFIED = "Unspecified";// 未知
    public static final String MALE = "Male";// 男
    public static final String FEMALE = "Female";// 女

    /**
     * user_status : Normal
     * dc_num :
     * name :
     * level : 0
     * gender : Unspecified
     * region :
     * user_type : Normal
     * cover_url :
     * cover_ori :
     * experience : 0
     * need_regist : true
     * birth_day :
     * created_time : 1495628086
     * honours : []
     * constellation :
     * id : 10020
     * description :
     * <p>
     * 基本信息
     */
    public boolean is_auth_user;//是否有管理作品权限
    private String user_status;
    private String dc_num;
    private String name;
    private int level;
    private String gender;
    private String region;
    private String user_type;
    private String cover_url;
    private String cover_ori;
    private int experience;
    private boolean need_regist;
    private String birth_day;
    private int created_time;
    private String constellation;
    private long id;
    private String description;
    private List<?> honours;
    public int invite_verify; // 是否需要邀请码验证
    public boolean is_first_login; // 是否用户第一次登录
    /**
     * 个人主页 数据
     */
    private UserHomeRelation relation;
    private UserHomeData data;
    private ShareInfo share_info;
    private ShareInfo share_home;

    private WeiboEntity bind_weibo;
    private VerifyEntity verify;
    private OfficialWebsiteBean official_website;

    public boolean isBlock = true;//黑名单列表使用

    public void replace(UserInfo userEntity) {
        this.name = userEntity.getName();
        this.cover_url = userEntity.getCover_url();
        this.dc_num = userEntity.getDc_num();
        this.id = userEntity.getId();
        this.description = userEntity.getDescription();
        this.honours = userEntity.getHonours();
        UserHomeRelation userHomeRelation = new UserHomeRelation();
        if (userEntity.getRelation() != null) {
            userHomeRelation.is_fans = userEntity.getRelation().is_fans;
            userHomeRelation.is_follow = userEntity.getRelation().is_follow;
            userHomeRelation.is_block = userEntity.getRelation().is_block;
            userHomeRelation.follow_count = userEntity.getRelation().follow_count;
            userHomeRelation.fans_count = userEntity.getRelation().fans_count;
        }
        this.relation = userHomeRelation;
        this.verify = userEntity.getVerify();
    }


    public String getUser_status() {
        return user_status;
    }

    public void setUser_status(String user_status) {
        this.user_status = user_status;
    }

    public String getDc_num() {
        return dc_num;
    }

    public void setDc_num(String dc_num) {
        this.dc_num = dc_num;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getGender() {
        return  gender;
    }

    public boolean isFemale() {
        if (!TextUtils.isEmpty(gender)) {
            return UserInfo.FEMALE.equalsIgnoreCase(gender);
        }
        return false;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }

    public String getCover_url() {
        return cover_url;
    }

    public void setCover_url(String cover_url) {
        this.cover_url = cover_url;
    }

    public String getCover_ori() {
        return cover_ori;
    }

    public void setCover_ori(String cover_ori) {
        this.cover_ori = cover_ori;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public boolean isNeed_regist() {
        return need_regist;
    }

    public void setNeed_regist(boolean need_regist) {
        this.need_regist = need_regist;
    }

    public String getBirth_day() {
        return birth_day;
    }

    public void setBirth_day(String birth_day) {
        this.birth_day = birth_day;
    }

    public int getCreated_time() {
        return created_time;
    }

    public void setCreated_time(int created_time) {
        this.created_time = created_time;
    }

    public String getConstellation() {
        return constellation;
    }

    public void setConstellation(String constellation) {
        this.constellation = constellation;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<?> getHonours() {
        return honours;
    }

    public void setHonours(List<?> honours) {
        this.honours = honours;
    }

    public boolean getIs_auth_user() {
        return is_auth_user;
    }

    public void setIs_auth_user(boolean is_auth_user) {
        this.is_auth_user = is_auth_user;
    }

    public UserHomeData getData() {
        return data;
    }

    public int getTogetherCount() {
        return data == null ? 0 : data.getCo_create_count();
    }

    public OfficialWebsiteBean getOfficial_website() {
        return official_website;
    }

    public void setOfficial_website(OfficialWebsiteBean official_website) {
        this.official_website = official_website;
    }

    public void setData(UserHomeData data) {
        this.data = data;
    }

    public ShareInfo getShare_info() {
        return share_info;
    }

    public void setShare_info(ShareInfo share_info) {
        this.share_info = share_info;
    }

    public ShareInfo getShare_home() {
        return share_home;
    }

    public void setShare_home(ShareInfo share_home) {
        this.share_home = share_home;
    }

    public UserHomeRelation getRelation() {
        if (relation == null) {
            relation = new UserHomeRelation();
        }
        return relation;
    }

    public boolean isFollowed() {
        return getRelation().is_follow;
    }

    public void setFollowed(boolean followed) {
        getRelation().is_follow = followed;
    }

    public int getFansCount() {
        return getRelation().fans_count;
    }

    public void setRelation(UserHomeRelation relation) {
        this.relation = relation;
    }


    public WeiboEntity getBind_weibo() {
        return bind_weibo;
    }

    public void setBind_weibo(WeiboEntity bind_weibo) {
        this.bind_weibo = bind_weibo;
    }

    public VerifyEntity getVerify() {
        return verify;
    }

    public void setVerify(VerifyEntity verify) {
        this.verify = verify;
    }

    public static class WeiboEntity extends BaseModel {
        public String weibo_id;
        public String weibo_name;
        public String weibo_url;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "is_auth_user=" + is_auth_user +
                ", user_status='" + user_status + '\'' +
                ", dc_num='" + dc_num + '\'' +
                ", name='" + name + '\'' +
                ", level=" + level +
                ", gender='" + gender + '\'' +
                ", region='" + region + '\'' +
                ", user_type='" + user_type + '\'' +
                ", cover_url='" + cover_url + '\'' +
                ", cover_ori='" + cover_ori + '\'' +
                ", experience=" + experience +
                ", need_regist=" + need_regist +
                ", birth_day='" + birth_day + '\'' +
                ", created_time=" + created_time +
                ", constellation='" + constellation + '\'' +
                ", id=" + id +
                ", description='" + description + '\'' +
                ", honours=" + honours +
                ", invite_verify=" + invite_verify +
                ", data=" + data +
                ", share_info=" + share_info +
                ", share_home=" + share_home +
                ", relation=" + relation +
                '}';
    }
}
