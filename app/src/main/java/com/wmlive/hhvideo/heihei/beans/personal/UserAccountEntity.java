package com.wmlive.hhvideo.heihei.beans.personal;

import android.os.Parcel;
import android.os.Parcelable;

import com.wmlive.hhvideo.heihei.beans.record.CloneableEntity;

/**
 * Created by XueFei on 2017/8/1.
 * <p>
 * 账户信息类
 */

public class UserAccountEntity extends CloneableEntity  implements Parcelable {
    private long user_id;
    private int gold;
    private int point;
    private int all_outlay_gold;
    private double point_worth;
    private int all_earn_point;

    public UserAccountEntity() {
    }

    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public int getAll_outlay_gold() {
        return all_outlay_gold;
    }

    public void setAll_outlay_gold(int all_outlay_gold) {
        this.all_outlay_gold = all_outlay_gold;
    }

    public double getPoint_worth() {
        return point_worth;
    }

    public void setPoint_worth(double point_worth) {
        this.point_worth = point_worth;
    }

    public int getAll_earn_point() {
        return all_earn_point;
    }

    public void setAll_earn_point(int all_earn_point) {
        this.all_earn_point = all_earn_point;
    }

    @Override
    public UserAccountEntity clone() {
        UserAccountEntity accountEntity = null;
        try {
            accountEntity = (UserAccountEntity) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return accountEntity;
    }

    @Override
    public String toString() {
        return "UserAccountEntity{" +
                "user_id=" + user_id +
                ", gold=" + gold +
                ", point=" + point +
                ", all_outlay_gold=" + all_outlay_gold +
                ", point_worth=" + point_worth +
                ", all_earn_point=" + all_earn_point +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.user_id);
        dest.writeInt(this.gold);
        dest.writeInt(this.point);
        dest.writeInt(this.all_outlay_gold);
        dest.writeDouble(this.point_worth);
        dest.writeInt(this.all_earn_point);
    }

    protected UserAccountEntity(Parcel in) {
        this.user_id = in.readLong();
        this.gold = in.readInt();
        this.point = in.readInt();
        this.all_outlay_gold = in.readInt();
        this.point_worth = in.readDouble();
        this.all_earn_point = in.readInt();
    }

    public static final Creator<UserAccountEntity> CREATOR = new Creator<UserAccountEntity>() {
        @Override
        public UserAccountEntity createFromParcel(Parcel source) {
            return new UserAccountEntity(source);
        }

        @Override
        public UserAccountEntity[] newArray(int size) {
            return new UserAccountEntity[size];
        }
    };
}
