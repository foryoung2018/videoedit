package com.wmlive.hhvideo.heihei.beans.splash;

import com.wmlive.hhvideo.heihei.beans.log.Logss;
import com.wmlive.hhvideo.heihei.beans.personal.ReportEntry;
import com.wmlive.networklib.entity.BaseResponse;

/**
 * init接口中所有的url
 */
public class InitUrlResponse extends BaseResponse {
// "version": "1.0",
//         "comment": "初始化接口"

    private String version;
    private String comment;

    private UserUrl user;
    private OpusUrl opus;//作品
    private MusicUrl music;//音乐
    private SocialUrl social;//素材
    private TopicUrl topic;
    private SearchUrl search;
    private SysUrl sys;
    private Function function;
    private ReportEntry conf_data;
    private MessageUrl message;//消息
    private PayUrl pay;
    private TipsUrl tips;
    public Logss log;


    public String getVersion() {
        return version;
    }

    public InitUrlResponse setVersion(String version) {
        this.version = version;
        return this;
    }

    public String getComment() {
        return comment;
    }

    public InitUrlResponse setComment(String comment) {
        this.comment = comment;
        return this;
    }

    public UserUrl getUser() {
        return user;
    }

    public InitUrlResponse setUser(UserUrl user) {
        this.user = user;
        return this;
    }

    public OpusUrl getOpus() {
        return opus;
    }

    public InitUrlResponse setOpus(OpusUrl opus) {
        this.opus = opus;
        return this;
    }

    public MusicUrl getMusic() {
        return music;
    }

    public InitUrlResponse setMusic(MusicUrl music) {
        this.music = music;
        return this;
    }

    public SocialUrl getSocial() {
        return social;
    }

    public InitUrlResponse setSocial(SocialUrl social) {
        this.social = social;
        return this;
    }

    public TopicUrl getTopic() {
        return topic;
    }

    public InitUrlResponse setTopic(TopicUrl topic) {
        this.topic = topic;
        return this;
    }

    public SearchUrl getSearch() {
        return search;
    }

    public InitUrlResponse setSearch(SearchUrl search) {
        this.search = search;
        return this;
    }

    public SysUrl getSys() {
        return sys;
    }

    public InitUrlResponse setSys(SysUrl sys) {
        this.sys = sys;
        return this;
    }

    public Function getFunction() {
        return function;
    }

    public InitUrlResponse setFunction(Function function) {
        this.function = function;
        return this;
    }

    public ReportEntry getConf_data() {
        return conf_data;
    }

    public void setConf_data(ReportEntry conf_data) {
        this.conf_data = conf_data;
    }

    public MessageUrl getMessage() {
        return message;
    }

    public void setMessage(MessageUrl message) {
        this.message = message;
    }

    public PayUrl getPay() {
        return pay;
    }

    public void setPayUrl(PayUrl pay) {
        this.pay = pay;
    }

    public void setPay(PayUrl pay) {
        this.pay = pay;
    }

    public TipsUrl getTips() {
        return tips;
    }

    public Logss getLog() {
        return log;
    }

    public void setTips(TipsUrl tips) {
        this.tips = tips;
    }

    @Override
    public String toString() {
        return "InitUrlResponse{" +
                "version='" + version + '\'' +
                ", comment='" + comment + '\'' +
                ", user=" + (user == null ? "null" : user.toString()) +
                ", opus=" + (opus == null ? "null" : opus.toString()) +
                ", music=" + (music == null ? "null" : music.toString()) +
                ", social=" + (social == null ? "null" : social.toString()) +
                ", topic=" + (topic == null ? "null" : topic.toString()) +
                ", search=" + (search == null ? "null" : search.toString()) +
                ", sys=" + (sys == null ? "null" : sys.toString()) +
                ", function=" + (function == null ? "null" : function.toString()) +
                ", conf_data=" + (conf_data == null ? "null" : conf_data.toString()) +
                ", message=" + (message == null ? "null" : message.toString()) +
                ", pay=" + (pay == null ? "null" : pay.toString()) +
                ", tips=" + (tips == null ? "null" : tips.toString()) +
                '}';
    }
}
