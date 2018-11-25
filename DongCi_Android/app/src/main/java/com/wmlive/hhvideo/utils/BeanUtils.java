package com.wmlive.hhvideo.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by wenlu on 2017/8/31.
 */

public class BeanUtils {

    public static Object deepClone(Object obj) {
        try {
            // 将对象写到流里
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ObjectOutputStream oo = new ObjectOutputStream(bo);
            oo.writeObject(obj);
            // 从流里读出来
            ByteArrayInputStream bi = new ByteArrayInputStream(bo.toByteArray());
            ObjectInputStream oi = new ObjectInputStream(bi);
            return (oi.readObject());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
