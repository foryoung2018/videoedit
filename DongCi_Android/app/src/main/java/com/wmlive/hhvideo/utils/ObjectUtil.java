package com.wmlive.hhvideo.utils;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lsq on 9/1/2017.
 * 序列化工具
 */

public class ObjectUtil {
    /**
     * List对象持久化为字节流
     *
     * @param list 支持Parcelable的对象
     * @return true代表保存成功
     */
    public static <T extends Parcelable> byte[] toParcelBytes(List<T> list) {
        Parcel parcel = Parcel.obtain();
        try {
            if (list == null) {
                return null;
            }
            parcel.writeTypedList(list);
            return parcel.marshall();
        } catch (Exception ex) {
            return null;
        } finally {
            parcel.recycle();
        }
    }

    /**
     * Serializable对象持久化为字节流
     *
     * @param object 支持Parcelable的对象
     * @return true代表保存成功
     */
    public static <T extends Serializable> byte[] toParcelBytes(T object) {
        Parcel parcel = Parcel.obtain();
        try {
            if (object == null) {
                return null;
            }
            parcel.writeSerializable(object);
            return parcel.marshall();
        } catch (Exception ex) {
            return null;
        } finally {
            parcel.recycle();
        }
    }

    /**
     * Parcelable对象持久化为字节流
     *
     * @param object 支持Parcelable的对象
     * @return true代表保存成功
     */
    public static <T extends Parcelable> byte[] toParcelBytes(T object) {
        Parcel parcel = Parcel.obtain();
        try {
            if (object == null) {
                return null;
            }
            parcel.writeParcelable(object, 0);
            return parcel.marshall();
        } catch (Exception ex) {
            return null;
        } finally {
            parcel.recycle();
        }
    }

    /**
     * 还原持久化保存的List对象
     *
     * @param parcelBytes 持续化后的字节流
     * @param creator     Parcelable.Creator
     * @return 返回还原的持久化保存的对象
     */
    public static <T extends Parcelable> List<T> toParcelObjList(byte[] parcelBytes, Parcelable.Creator<T> creator) {
        Parcel parcel = null;
        try {
            parcel = Parcel.obtain();
            parcel.unmarshall(parcelBytes, 0, parcelBytes.length);
            parcel.setDataPosition(0);
            List<T> result = new ArrayList<>();
            parcel.readTypedList(result, creator);
            return result;
        } catch (Exception ex) {
            return null;
        } finally {
            if (null != parcel) {
                parcel.recycle();
            }
        }
    }

    /**
     * 还原持久化保存的对象
     *
     * @param parcelBytes 持续化后的字节流
     * @return
     */
    public static <T extends Parcelable> T toParcelObj(byte[] parcelBytes, ClassLoader loader) {
        Parcel parcel = null;
        try {
            parcel = Parcel.obtain();
            parcel.unmarshall(parcelBytes, 0, parcelBytes.length);
            parcel.setDataPosition(0);
            T result = parcel.readParcelable(loader);
            return result;
        } catch (Exception ex) {
            return null;
        } finally {
            if (null != parcel) {
                parcel.recycle();
            }
        }
    }

    /**
     * 字节流还原成Serializable对象
     *
     * @param parcelBytes
     * @return
     */
    public static Serializable toSerializableObj(byte[] parcelBytes) {
        Parcel parcel = null;
        try {
            parcel = Parcel.obtain();
            parcel.unmarshall(parcelBytes, 0, parcelBytes.length);
            parcel.setDataPosition(0);
            return parcel.readSerializable();
        } catch (Exception ex) {
            return null;
        } finally {
            if (null != parcel) {
                parcel.recycle();
            }
        }
    }
}
