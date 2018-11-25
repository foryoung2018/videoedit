package com.wmlive.hhvideo.heihei.beans.record;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by lsq on 9/7/2017.
 * 作品的扩展字段，保留
 */

public class ProductExtendEntity extends CloneableEntity implements Parcelable {
    public long extendId;
    public String test_upgrade = "test1";
    public int videoWidth;//导出作品的宽
    public int videoHeight;//导出作品的高
    public boolean allowTeam = true;//是否允许共同创作
    public boolean isLocalUploadVideo = false;//本地上传的单个作品
    public int productCreateType;
    public String template_name;
    public String bg_name;

    @Override
    public String toString() {
        return "ProductExtendEntity{" +
                "extendId=" + extendId +
                ", test_upgrade='" + test_upgrade + '\'' +
                ", videoWidth=" + videoWidth +
                ", videoHeight=" + videoHeight +
                ", allowTeam=" + allowTeam +
                '}';
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        ProductExtendEntity infoEntity = null;
        try {
            infoEntity = (ProductExtendEntity) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return infoEntity;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.extendId);
        dest.writeString(this.test_upgrade);
        dest.writeInt(this.videoWidth);
        dest.writeInt(this.videoHeight);
        dest.writeByte(this.allowTeam ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isLocalUploadVideo ? (byte) 1 : (byte) 0);
        dest.writeInt(this.productCreateType);
        dest.writeString(this.bg_name);
        dest.writeString(this.template_name);
    }

    public ProductExtendEntity() {
    }

    protected ProductExtendEntity(Parcel in) {
        this.extendId = in.readLong();
        this.test_upgrade = in.readString();
        this.videoWidth = in.readInt();
        this.videoHeight = in.readInt();
        this.allowTeam = in.readByte() != 0;
        this.isLocalUploadVideo = in.readByte() != 0;
        this.bg_name = in.readString();
        this.template_name = in.readString();
        this.productCreateType = in.readInt();
    }

    public static final Creator<ProductExtendEntity> CREATOR = new Creator<ProductExtendEntity>() {
        @Override
        public ProductExtendEntity createFromParcel(Parcel source) {
            return new ProductExtendEntity(source);
        }

        @Override
        public ProductExtendEntity[] newArray(int size) {
            return new ProductExtendEntity[size];
        }
    };
}
