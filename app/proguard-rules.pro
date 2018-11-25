# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /User/KNothing/Documents/AndroidDeveloper/AndroidStudioSDK/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

#以下是release版本打包时混淆规则，请务必按照分类写在相应的区域，别乱

#~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~实体类混淆~start~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
-keep class com.wmlive.hhvideo.heihei.beans.** { *; }
-keep class com.wmlive.networklib.entity.** { *; }
-keep class * extends com.wmlive.hhvideo.common.base.BaseModel
-keep class com.wmlive.hhvideo.heihei.db.datatable.** { *; }
#...
#~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~实体类混淆~end~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


#~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~反射相关的类和方法混淆~start~~~~~~~~~~~~~~~~~~~~~~~
#
-keep class com.wmlive.hhvideo.utils.DeviceUtils { *; }
-keep class com.wmlive.hhvideo.utils.StringUtils { *; }
-keep class com.wmlive.hhvideo.utils.preferences.AesCbcWithIntegrity { *; }
-keep class com.wmlive.hhvideo.widget.** { *; }
-keep class com.wmlive.hhvideo.** { *; }
-keep class cn.wmlive.hhvideo.wxapi.** { *; }
-keep class com.example.lamemp3.MP3Recorder { *; }

-keep class com.wmlive.hhvideo.dcijkplayerlib.** { *; }
-keep class com.wmlive.networklib.** { *; }
-keep class com.danikula.videocache.** { *; }
-keep class tv.cjump.jni.** { *; }
-keep class master.flame.danmaku.** { *; }
-keep class com.yanzhenjie.recyclerview.swipe.** { *; }
-keep class android.support.v7.widget.helper.** { *; }

#~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~反射相关的类和方法混淆~end~~~~~~~~~~~~~~~~~~~~~~~~~


#~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~js互相调用混淆~start~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
#...
#~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~js互相调用混淆~end~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~



#~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~第三方包混淆~start~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

#BaseRecyclerViewAdapterHelper
-keep class com.chad.library.adapter.** {
*;
}
-keep public class * extends com.chad.library.adapter.base.BaseQuickAdapter
-keep public class * extends com.chad.library.adapter.base.BaseViewHolder
-keepclassmembers  class **$** extends com.chad.library.adapter.base.BaseViewHolder {
     <init>(...);
}
#BaseRecyclerViewAdapterHelper

##==========高德定位==========
-keep class com.amap.api.location.**{*;}
-keep class com.amap.api.fence.**{*;}
-keep class com.autonavi.aps.amapapi.model.**{*;}
##2D地图
#-keep class com.amap.api.maps2d.**{*;}
#-keep class com.amap.api.mapcore2d.**{*;}
#-dontwarn com.amap.**
#-dontwarn com.amap.api.**
##==========高德定位==========

#========================greenDao=============
-keep class org.greenrobot.** {*;}
-keep class org.greendao.** {*;}
-keepclassmembers class * extends de.greenrobot.dao.AbstractDao {
    public static Java.lang.String TABLENAME;
}
-dontwarn org.greenrobot.**
#========================greenDao=============

#==========Glide==========
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

# for DexGuard only
#-keepresourcexmlelements manifest/application/meta-data@value=GlideModule
#==========Glide==========

# OkHttp3
-dontwarn com.squareup.**
-dontwarn okio.**
-keep public class org.codehaus.* { *; }
-keep public class java.nio.* { *; }
# OkHttp3


# Retrofit
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
# Retrofit


#fastjson
-dontwarn com.alibaba.fastjson.**
-keep class com.alibaba.fastjson.**{*; }
#fastjson

#Bugtags
-keep class com.bugtags.library.** {*;}
-dontwarn com.bugtags.library.**
-keep class io.bugtags.** {*;}
-dontwarn io.bugtags.**
-dontwarn org.apache.http.**
-dontwarn android.net.http.AndroidHttpClient
# Bugtags

#jpush
-dontwarn cn.jpush.**
-keep class cn.jpush.** { *; }
#jpush

#tencent
-keep class com.tencent.mm.sdk.** {
   *;
}
#tencent

#qiniu
-keep class com.qiniu.**{*;}
-keep class com.qiniu.**{public <init>();}
#qiniu

#eventbus
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}
#eventbus

#butterknife
-keep class butterknife.** { *; }
-keepnames class * { @butterknife.InjectView *;}
-dontwarn butterknife.**
-keep class **$$ViewBinder { *; }
-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}
-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}
-dontwarn sun.misc.Unsafe
#butterknife

#RxJava
-keep public class io.reactivex.* { *; }
-keep public class com.jakewharton.rxbinding2.* { *; }
#RxJava

#shareSDk
-keep class cn.sharesdk.**{*;}
-keep class com.sina.**{*;}
-keep class **.R$* {*;}
-keep class **.R{*;}
-keep class com.mob.**{*;}
-dontwarn com.mob.**
-dontwarn cn.sharesdk.**
-dontwarn **.R$*
#shareSDk

-keepclassmembers class * {
   public <init>(org.json.JSONObject);
}

#universalimageloader
-dontwarn com.nostra13.universalimageloader.**
-keep class com.nostra13.universalimageloader.** {*;}
#universalimageloader

#greeddao
-keep class org.greenrobot.greendao.**{*;}
-keep public interface org.greenrobot.greendao.**
-keepclassmembers class * extends org.greenrobot.greendao.AbstractDao {
public static java.lang.String TABLENAME;
}
-keep class **$Properties
-keep class net.sqlcipher.database.**{*;}
-keep public interface net.sqlcipher.database.**
-dontwarn net.sqlcipher.database.**
-dontwarn org.greenrobot.greendao.**
-keep class freemarker.** { *; }

-dontwarn freemarker.**
#greeddao

# fresco
-keep,allowobfuscation @interface com.facebook.common.internal.DoNotStrip

# Do not strip any method/class that is annotated with @DoNotStrip
-keep @com.facebook.common.internal.DoNotStrip class *
-keepclassmembers class * {
    @com.facebook.common.internal.DoNotStrip *;
}
-dontwarn okio.**
-dontwarn com.squareup.okhttp.**
-dontwarn okhttp3.**
-dontwarn javax.annotation.**
-dontwarn com.android.volley.toolbox.**
-dontwarn com.facebook.**
# fresco

#guava
-dontwarn com.google.auto.**
#guava

#阿里推送
-keepclasseswithmembernames class ** {
    native <methods>;
}
-keepattributes Signature
-keep class sun.misc.Unsafe { *; }
-keep class com.taobao.** {*;}
-keep class com.alibaba.** {*;}
-keep class com.alipay.** {*;}
-dontwarn com.taobao.**
-dontwarn com.alibaba.**
-dontwarn com.alipay.**
-keep class com.ut.** {*;}
-dontwarn com.ut.**
-keep class com.ta.** {*;}
-dontwarn com.ta.**
-keep class anet.**{*;}
-keep class org.android.spdy.**{*;}
-keep class org.android.agoo.**{*;}
-dontwarn anet.**
-dontwarn org.android.spdy.**
-dontwarn org.android.agoo.**
-keep class com.google.firebase.**{*;}
-dontwarn com.google.firebase.**

# 推送辅助通道
# 小米通道
-keep class com.xiaomi.** {*;}
-dontwarn com.xiaomi.**
# 华为通道
-keep class com.huawei.** {*;}
-dontwarn com.huawei.**
# GCM/FCM通道
-keep class com.google.firebase.**{*;}
#阿里推送

#华为推送
-keep class com.huawei.android.pushagent.**{*;}
-keep class com.huawei.android.pushselfshow.**{*;}
-keep class com.huawei.android.microkernel.**{*;}
-keep class com.baidu.mapapi.**{*;}

#-dontwarn class com.huawei.android.pushagent.**
#-dontwarn class com.huawei.android.pushselfshow.**
#-dontwarn class com.huawei.android.microkernel.**
#华为推送

# talkingdata
-dontwarn com.tendcloud.tenddata.**
-keep class com.tendcloud.** {*;}
-keep public class com.tendcloud.tenddata.** { public protected *;}
-keepclassmembers class com.tendcloud.tenddata.**{
public void *(***);
}
-keep class com.talkingdata.sdk.TalkingDataSDK {public *;}
-keep class com.apptalkingdata.** {*;}
-keep class dice.** {*; }
-dontwarn dice.**
# talkingdata

# umeng
-keep class com.umeng.commonsdk.** {*;}
-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
# umeng

# magicWindow
-keep class com.tencent.mm.sdk.** {*;}
-keep class cn.magicwindow.** {*;}
-dontwarn cn.magicwindow.**
# magicWindow

#~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~第三方包混淆~end~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~



#~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~严重警告!!!~start~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
#~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~以下代码没有特殊情况，千万别动!!!!~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-optimizationpasses 5                   #代码混淆的压缩比例，值在0-7之间
-dontoptimize                           #是否优化输入的类文件
-dontusemixedcaseclassnames             #不使用大小写混合
-dontskipnonpubliclibraryclasses        #不跳过jars中的非public classes。在proguard4.5时，是默认选项
-dontskipnonpubliclibraryclassmembers   #不跳过jars中的非public classes的成员变量。在proguard4.5时，是默认选项
-dontpreverify                          #混淆时是否做预校验
-verbose                                #混淆时是否记录日志
-dontwarn                               #忽略警告
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*  #混淆算法
-keepattributes InnerClasses,LineNumberTable  #保持源文件和行号的信息,用于混淆后定位错误位置
-keepattributes *Annotation*   #保持含有Annotation字符串的 attributes
-keep class * extends java.lang.annotation.Annotation { *; }
-keepattributes Exceptions,Signature,Deprecated,SourceFile,LocalVariable*Table,Synthetic,EnclosingMethod

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.app.View
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService

#保持本化方法及其类声明
-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclassmembers class * extends android.app.Activity{
    public void *(android.view.View);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

-keep class **.R$* {
 *;
}

-keepclassmembers class * {
    void *(**On*Event);
}

#保持 任意包名.R类的类成员属性。  即保护R文件中的属性名不变
-keepclassmembers class **.R$* {
    public static <fields>;
}

-dontwarn org.apache.**
-keep class org.apache.** { *; }
-keep class java.nio.** { *; }

-dontwarn org.junit.**

-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

#WebView相关
-keepclassmembers class fqcn.of.javascript.interface.for.Webview {
   public *;
}
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
    public boolean *(android.webkit.WebView, java.lang.String);
}
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, jav.lang.String);
}
#WebView相关

###########################混淆日志###########################
-dump class_files.txt        #apk所有class的内部结构
-printseeds seeds.txt           #未混淆的class和成员变量
-printusage unused.txt          #打印未被使用的嗲吗
-printmapping mapping.txt       #混淆前后的映射
###########################混淆日志###########################

#~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~警告结束!!!~end~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~