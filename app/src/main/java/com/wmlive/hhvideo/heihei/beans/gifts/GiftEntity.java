package com.wmlive.hhvideo.heihei.beans.gifts;

import android.os.Parcel;
import android.os.Parcelable;

import com.wmlive.hhvideo.heihei.beans.personal.UserAccountEntity;
import com.wmlive.hhvideo.heihei.beans.record.CloneableEntity;
import com.wmlive.hhvideo.utils.CollectionUtil;
import com.wmlive.hhvideo.utils.CommonUtils;

import java.util.List;

/**
 * Created by lsq on 1/5/2018.3:55 PM
 *
 * @author lsq
 * @describe 添加描述
 */

public class GiftEntity extends CloneableEntity implements Parcelable {
    public String id;
    public String name;
    public int gift_status; //0:online , 1:offline
    public String attr_file; //zip文件下载地址
    public String unique_id;
    //文件前缀unique_id说明
    //    six_icon.webp	小图标
    //    six_bg_music.aac	背景音乐
    //    six_icon_music.mp3	音效
    //    six_image.webp	礼物大图
    public String descs;
    public int visible;
    public String attr_md5;
    public String bg_music_url;  //礼物音效
    public int exp;//经验
    public String gift_orders;   //礼物排序， 倒序， 最大的在前边
    public int gold; //价格,为0是免费礼物
    public String icon_music_url; //音符音效
    public String icon_url;  //	弹出音符icon
    public String image_url; //礼物面板展示url
    public int show_second;

    public List<RebateEntity> prizes;
    public UserAccountEntity user_gold_account;

    public int getDecibelRebateCount(int clickCount) {
        int all = 0;
        if (!CollectionUtil.isEmpty(prizes)) {
            for (RebateEntity prize : prizes) {
                if (prize != null) {
                    all += prize.getDecibelRebateCount(clickCount);
                }
            }
        }
        return all;
    }

    @Override
    public GiftEntity clone() {
        GiftEntity giftEntity = null;
        try {
            giftEntity = (GiftEntity) super.clone();
            if (giftEntity != null) {
                if (!CollectionUtil.isEmpty(prizes)) {
                    giftEntity.prizes = CommonUtils.cloneList(prizes);
                }
                giftEntity.user_gold_account = user_gold_account;
            }
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return giftEntity;
    }

    public boolean isFree() {
        return gold <= 0;
    }

    @Override
    public String toString() {
        return "GiftEntity{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", gift_status=" + gift_status +
                ", attr_file='" + attr_file + '\'' +
                ", unique_id='" + unique_id + '\'' +
                ", descs='" + descs + '\'' +
                ", visible=" + visible +
                ", attr_md5='" + attr_md5 + '\'' +
                ", bg_music_url='" + bg_music_url + '\'' +
                ", exp=" + exp +
                ", gift_orders='" + gift_orders + '\'' +
                ", gold=" + gold +
                ", icon_music_url='" + icon_music_url + '\'' +
                ", icon_url='" + icon_url + '\'' +
                ", image_url='" + image_url + '\'' +
                ", prizes=" + CommonUtils.printList(prizes) +
                ", user_gold_account=" + user_gold_account +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeInt(this.gift_status);
        dest.writeString(this.attr_file);
        dest.writeString(this.unique_id);
        dest.writeString(this.descs);
        dest.writeInt(this.visible);
        dest.writeString(this.attr_md5);
        dest.writeString(this.bg_music_url);
        dest.writeInt(this.exp);
        dest.writeString(this.gift_orders);
        dest.writeInt(this.gold);
        dest.writeString(this.icon_music_url);
        dest.writeString(this.icon_url);
        dest.writeString(this.image_url);
        dest.writeInt(this.show_second);
        dest.writeTypedList(this.prizes);
        dest.writeParcelable(this.user_gold_account, flags);
    }

    public GiftEntity() {
    }

    protected GiftEntity(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.gift_status = in.readInt();
        this.attr_file = in.readString();
        this.unique_id = in.readString();
        this.descs = in.readString();
        this.visible = in.readInt();
        this.attr_md5 = in.readString();
        this.bg_music_url = in.readString();
        this.exp = in.readInt();
        this.gift_orders = in.readString();
        this.gold = in.readInt();
        this.icon_music_url = in.readString();
        this.icon_url = in.readString();
        this.image_url = in.readString();
        this.show_second = in.readInt();
        this.prizes = in.createTypedArrayList(RebateEntity.CREATOR);
        this.user_gold_account = in.readParcelable(UserAccountEntity.class.getClassLoader());
    }

    public static final Creator<GiftEntity> CREATOR = new Creator<GiftEntity>() {
        @Override
        public GiftEntity createFromParcel(Parcel source) {
            return new GiftEntity(source);
        }

        @Override
        public GiftEntity[] newArray(int size) {
            return new GiftEntity[size];
        }
    };
}
