package com.dongci.sun.gpuimglibrary.api.apiTest;

import android.util.Log;

import java.io.File;

/**
 * 类相关的工具类
 *
 * @author <a href="mailto:ohergal@gmail.com">ohergal</a>
 *
 */
public class ClassUtil {

    private static String path = "C:\\Users\\sunqimin\\workspaceNew\\dongciSdk_android\\gpuimglibrary\\src\\main\\java\\com\\dongci\\sun\\gpuimglibrary\\camera2\\filternew";

    public static void main(){
        File file = new File(path);
        for(File f:file.listFiles()){
            Log.d("tag","filess--->"+f.getPath());
        }
    }

    public ClassUtil(){}

}