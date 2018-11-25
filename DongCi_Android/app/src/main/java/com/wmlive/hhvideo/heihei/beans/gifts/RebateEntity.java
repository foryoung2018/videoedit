package com.wmlive.hhvideo.heihei.beans.gifts;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.wmlive.hhvideo.heihei.beans.record.CloneableEntity;
import com.wmlive.hhvideo.utils.CollectionUtil;

import java.util.Arrays;
import java.util.List;

/**
 * Created by lsq on 1/15/2018.11:21 AM
 *
 * @author lsq
 * @describe 添加描述
 */

public class RebateEntity extends CloneableEntity implements Parcelable {

    public static final int TYPE_NONE = 0;
    public static final int TYPE_DECIBEL = 10;  //分贝
    public static final int TYPE_REBATE = 20;   //返钻
    private static final String KEY_HIT = "point";     //分贝
    private static final String KEY_GOLD = "gold";  //返钻
    public long activity_id;
    public String activity_key;
    public long prize_id;
    public String prize_key;
    public String prize_name;
    public String prize_type;
    public String sequence;
    public String sequence_key;
    public int count;

    private List<String> sequenceList;

    public int getDecibelRebateCount() {
        if (!TextUtils.isEmpty(prize_type) && KEY_HIT.equalsIgnoreCase(prize_type)) {
            return count;
        }
        return 0;
    }

    public int getGoldRebateCount() {
        if (!TextUtils.isEmpty(prize_type) && KEY_GOLD.equalsIgnoreCase(prize_type)) {
            return count;
        }
        return 0;
    }

    public int getDecibelRebate(int clickCount) {
        if (clickCount > 0 && !TextUtils.isEmpty(prize_type)) {
            if (KEY_HIT.equalsIgnoreCase(prize_type)) {
                return getSequenceValue(clickCount);
            }
        }
        return 0;
    }

    public int getDecibelRebateCount(int clickCount) {
        if (clickCount > 0 && !TextUtils.isEmpty(prize_type)) {
            if (KEY_HIT.equalsIgnoreCase(prize_type)) {
                if (!TextUtils.isEmpty(sequence)) {
                    getSequenceList();
                    if (!CollectionUtil.isEmpty(sequenceList)) {
                        int all = 0;
                        for (int i = 1; i <= clickCount; i++) {
                            all += getValue(i);
                        }
                        return all;
                    }
                }
            }
        }
        return 0;
    }

    private void getSequenceList() {
        if (sequenceList == null) {
            String[] positions = sequence.split(",");
            if (positions.length > 0) {
                sequenceList = Arrays.asList(positions);
            }
        }
    }

    public int getGoldRebate(int clickCount) {
        if (clickCount > 0 && !TextUtils.isEmpty(prize_type)) {
            if (KEY_GOLD.equalsIgnoreCase(prize_type)) {
                return getSequenceValue(clickCount);
            }
        }
        return 0;
    }

    private int getSequenceValue(int clickCount) {
        if (!TextUtils.isEmpty(sequence)) {
            getSequenceList();
            return getValue(clickCount);
        }
        return 0;
    }

    private int getValue(int clickCount) {
        if (clickCount > 0 && !CollectionUtil.isEmpty(sequenceList)) {
            int index = (clickCount - 1) % sequenceList.size();
            String value = sequenceList.get(index);
            return TextUtils.isDigitsOnly(value) ? Integer.parseInt(value) : 0;
        } else {
            return 0;
        }
    }

    @Override
    public RebateEntity clone() {
        RebateEntity giftPriceEntity = null;
        try {
            giftPriceEntity = (RebateEntity) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return giftPriceEntity;
    }

    @Override
    public String toString() {
        return "RebateEntity{" +
                "activity_id=" + activity_id +
                ", activity_key='" + activity_key + '\'' +
                ", prize_id=" + prize_id +
                ", prize_key='" + prize_key + '\'' +
                ", prize_name='" + prize_name + '\'' +
                ", prize_type='" + prize_type + '\'' +
                ", sequence='" + sequence + '\'' +
                ", sequence_key='" + sequence_key + '\'' +
                ", count=" + count +
                ", sequenceList=" + sequenceList +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.activity_id);
        dest.writeString(this.activity_key);
        dest.writeLong(this.prize_id);
        dest.writeString(this.prize_key);
        dest.writeString(this.prize_name);
        dest.writeString(this.prize_type);
        dest.writeString(this.sequence);
        dest.writeString(this.sequence_key);
        dest.writeInt(this.count);
        dest.writeStringList(this.sequenceList);
    }

    public RebateEntity() {
    }

    protected RebateEntity(Parcel in) {
        this.activity_id = in.readLong();
        this.activity_key = in.readString();
        this.prize_id = in.readLong();
        this.prize_key = in.readString();
        this.prize_name = in.readString();
        this.prize_type = in.readString();
        this.sequence = in.readString();
        this.sequence_key = in.readString();
        this.count = in.readInt();
        this.sequenceList = in.createStringArrayList();
    }

    public static final Creator<RebateEntity> CREATOR = new Creator<RebateEntity>() {
        @Override
        public RebateEntity createFromParcel(Parcel source) {
            return new RebateEntity(source);
        }

        @Override
        public RebateEntity[] newArray(int size) {
            return new RebateEntity[size];
        }
    };
}
