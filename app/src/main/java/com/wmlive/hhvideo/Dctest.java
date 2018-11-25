package com.wmlive.hhvideo;

import java.io.IOException;

public class Dctest {
    public static void main(String[] args) {
        String[] name = {
                "develop", "_360", "baidu", "chuizi",
                "flyme", "guanwang", "huawei", "lenovo",
                "leshi", "ppzs", "qita", "vivo",
                "wandoujia", "xiaomi", "yyb", "qcheng",
                "beiyong01", "beiyong02", "beiyong03",
                "beiyong04", "beiyong05", "beiyong06",
                "beiyong07", "beiyong08", "beiyong09",
                "beiyong10", "googleplay", "oppo",
                "bilibili01", "bilibili02", "bilibili03", "bilibili04", "bilibili05",
                "bilibili06", "bilibili07", "bilibili08", "bilibili09", "bilibili010"};
        for (int i = 0; i < name.length; i++) {
//            String path = "C:\\Users\\Administrator\\Desktop\\dongci_android\\DongCi_Android\\app\\build\\outputs\\apk\\release\\dc_3.4.0_19582_20180926__360_release.apk";
            String path = "C:\\Users\\Administrator\\Desktop\\dongci_android\\DongCi_Android\\app\\build\\outputs\\apk\\release\\dc_3.4.0_19582_20180926_" + name[i] + "_release.apk";
            try {
                Process exec = Runtime.getRuntime().exec("python ./addChannelToApk.py " + path + " " + name[i]);
            } catch (IOException e) {
                System.out.print(e);
                e.printStackTrace();
            }
        }

//        String path = "C:\\Users\\Administrator\\Desktop\\dongci_android\\DongCi_Android\\app\\build\\outputs\\apk\\release\\dc_3.4.0_19580_20180925_oppo_release.apk";
//        try {
//            Process exec = Runtime.getRuntime().exec("python ./addChannelToApk.py " + path + " oppo");
//        } catch (IOException e) {
//            System.out.print(e);
//            e.printStackTrace();
//        }

    }
}
