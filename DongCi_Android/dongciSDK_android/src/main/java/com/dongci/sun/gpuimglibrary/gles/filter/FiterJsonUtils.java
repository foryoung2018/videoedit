package com.dongci.sun.gpuimglibrary.gles.filter;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import dalvik.system.DexFile;

public class FiterJsonUtils {

    public List<String > getClassName(Context context,String packageName){
        List<String >classNameList=new ArrayList<String >();
        try {
            Log.d("tag","classname---pre>"+context.getPackageCodePath());
            String packageCodePath = context.getPackageCodePath();
            File dir = new File(packageCodePath).getParentFile();
            for(File f:dir.listFiles()){
                Log.d("tag","classname---middle>"+f.getPath());
                if(f.getPath().endsWith("apk")){
                    DexFile df = new DexFile(f.getPath());//通过DexFile查找当前的APK中可执行文件
                    Enumeration<String> enumeration = df.entries();//获取df中的元素  这里包含了所有可执行的类名 该类名包含了包名+类名的方式
                    while (enumeration.hasMoreElements()) {//遍历
                        String className = (String) enumeration.nextElement();

                        if (className.contains(packageName)) {//在当前所有可执行的类里面查找包含有该包名的所有类
                            classNameList.add(className);
                            Log.d("tag","classname--->"+className);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  classNameList;
    }

    public void parseDetail(String className){
        className = "com.dongci.sun.gpuimglibrary.camera2.filternew.GPUImageBilateralFilter";
        try {
            Class clz = Class.forName(className);
            Field[] fields = clz.getFields();
            for(Field field : fields) {
                // 获取该属性名称与值
//                if(field.getType().equals(Integer)){
//
//                }
                Log.d("tag","parse-detail-->"+field.getName() + field.getType());
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    /**
     * 获取到类的属性，并可以修改类参数。
     *
     *
     * json 文件:
     * 内容: 各个类的变量的值，
     * 使用: 读取成要给实体类，将值保存到对象
     * 获取到对象后 将对象的值设置给各个filter
     */
}
