# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/ldx/Development/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:


#################[sdk>tools>proguard>proguard-android.txt文件中已经有了>]start#################
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

    #指定代码的压缩级别
    -optimizationpasses 5
    #忽略警告
    -ignorewarning


    #包明不混合大小写
#    -dontusemixedcaseclassnames

    #不去忽略非公共的库类
#    -dontskipnonpubliclibraryclasses

     #混淆时是否记录日志
#    -verbose

     #优化  不优化输入的类文件
#    -dontoptimize

     #预校验
#    -dontpreverify

    #保护注解
#    -keepattributes *Annotation*

#   -keep public class com.google.vending.licensing.ILicensingService
#   -keep public class com.android.vending.licensing.ILicensingService

    #保持 native 方法不被混淆
#    -keepclasseswithmembernames class * {
#        native <methods>;
#    }

#-keepclassmembers public class * extends android.view.View {
#   void set*(***);
#   *** get*();
#}

    #保持自定义控件类不被混淆
#    -keepclassmembers class * extends android.app.Activity {
#       public void *(android.view.View);
#    }

    #保持枚举 enum 类不被混淆 如果混淆报错，建议直接使用上面的 -keepclassmembers class * implements java.io.Serializable即可
#    -keepclassmembers enum * {
#      public static **[] values();
#      public static ** valueOf(java.lang.String);
#    }

    #保持 Parcelable 不被混淆
#    -keep class * implements android.os.Parcelable {
#      public static final android.os.Parcelable$Creator *;
#    }

  #不混淆资源类
#    -keepclassmembers class **.R$* {
#        public static <fields>;
#    }

    #如果引用了v4或者v7包
#    -dontwarn android.support.**

#-keep class android.support.annotation.Keep

#-keep @android.support.annotation.Keep class * {*;}

#-keepclasseswithmembers class * {
#    @android.support.annotation.Keep <methods>;
#}

#
#-keepclasseswithmembers class * {
#    @android.support.annotation.Keep <fields>;
#}

#-keepclasseswithmembers class * {
#    @android.support.annotation.Keep <init>(...);
#}

#################[sdk>tools>proguard>proguard-android.txt文件中已经有了]end#################


     # 混淆时所采用的算法
    -optimizations !code/simplification/arithmetic,!field/*,!class/merging/*


    # 保持哪些类不被混淆
    -keep public class * extends android.app.Fragment
    -keep public class * extends android.app.Activity
    -keep public class * extends android.app.Application
    -keep public class * extends android.app.Service
    -keep public class * extends android.content.BroadcastReceiver
    -keep public class * extends android.content.ContentProvider
    -keep public class * extends android.app.backup.BackupAgentHelper
    -keep public class * extends android.preference.Preference
    -keep public class com.android.vending.licensing.ILicensingService
    #如果有引用v4包可以添加下面这行
    -keep public class * extends android.support.v4.app.Fragment

    ##记录生成的日志数据,gradle build时在本项目根目录输出-start##
    #apk 包内所有 class 的内部结构
    -dump proguard/class_files.txt
    #未混淆的类和成员
    -printseeds proguard/seeds.txt
    #列出从 apk 中删除的代码
    -printusage proguard/unused.txt
    #混淆前后的映射
    -printmapping proguard/mapping.txt
    ##记录生成的日志数据，gradle build时 在本项目根目录输出-end##

    #保持自定义控件类不被混淆
    -keepclasseswithmembers class * {
        public <init>(android.content.Context, android.util.AttributeSet);
    }

    #保持自定义控件类不被混淆
    -keepclasseswithmembers class * {
        public <init>(android.content.Context, android.util.AttributeSet, int);
    }

    -keep public class * extends android.view.View {
            public <init>(android.content.Context);
            public <init>(android.content.Context, android.util.AttributeSet);
            public <init>(android.content.Context, android.util.AttributeSet, int);
            public void set*(...);
     }

    #保持 Serializable 不被混淆
    -keepnames class * implements java.io.Serializable

    #保持 Serializable 不被混淆并且enum 类也不被混淆
    -keepclassmembers class * implements java.io.Serializable {
        static final long serialVersionUID;
        private static final java.io.ObjectStreamField[] serialPersistentFields;
        !static !transient <fields>;
        !private <fields>;
        !private <methods>;
        private void writeObject(java.io.ObjectOutputStream);
        private void readObject(java.io.ObjectInputStream);
        java.lang.Object writeReplace();
        java.lang.Object readResolve();
    }

    #避免混淆泛型 如果混淆报错建议关掉
    #–keepattributes Signature

    #移除log 测试了下没有用还是建议自己定义一个开关控制是否输出日志
    #-assumenosideeffects class android.util.Log {
    #    public static boolean isLoggable(java.lang.String, int);
    #    public static int v(...);
    #    public static int i(...);
    #    public static int w(...);
    #    public static int d(...);
    #    public static int e(...);
    #}


#############################################################################################
########################                 以上通用           ##################################
#############################################################################################

    #gson
    -keepattributes Signature
    -keep class sun.misc.Unsafe { *; }
    -keep class com.google.gson.** { *; }
    -keep class com.google.gson.stream.** { *; }
#    -keep interface com.google.gson.** {*;}
#    -dontwarn com.google.gson.**

    #butterknife
    -keep class butterknife.** { *; }
    -dontwarn butterknife.internal.**
    -keep class **$$ViewBinder { *; }

    -keepclasseswithmembernames class * {
        @butterknife.* <fields>;
    }

    -keepclasseswithmembernames class * {
        @butterknife.* <methods>;
    }

    ######引用的其他Module可以直接在app的这个混淆文件里配置

    # 如果使用了Gson之类的工具要使被它解析的JavaBean类即实体类不被混淆。
    -keep class com.lptiyu.tanke.entity.** { *; }
    -keep class com.lptiyu.tanke.enums.** { *; }
    -keep class com.lptiyu.tanke.global.** { *; }
    -keep class com.lptiyu.tanke.pojo.** { *; }
    -keep class com.lptiyu.tanke.utils.** { *; }

    ######所有的jar包需要keep######
    #avoscloud-feedback
    -keep class com.avos.avoscloud.feedback.** { *; }
    #avoscloud-push
    -keep class com.avos.** { *; }
    #avoscloud-sdk
    -keep class com.avos.avoscoud.** { *; }
    #baidu
    -keep class com.baidu.** { *; }
    -keep class vi.com.gdi.bgl.android.java.** { *; }
    -keep class com.baidu.trace.** { *; }
    #easyar
    -keep class cn.easyar.engine.** { *; }
    #fastjson
    -keep class com.alibaba.fastjson.** { *; }
    #WebSocket
    -keep class com.avos.avoscloud.java_websocket
     #mob & shareSDk
    -keep class cn.sharesdk.**{*;}
    -keep class com.sina.**{*;}
    -keep class **.R$* {*;}
    -keep class **.R{*;}
    -keep class com.mob.**{*;}
    -dontwarn com.mob.**
    -dontwarn cn.sharesdk.**
    -dontwarn **.R$*
    #okhttp
    -keep class com.avos.avoscloud.okhttp.** { *; }
    -keep class com.avoscloud.okhttp.internal.** { *; }
    #okio
    -keep class com.avos.avoscloud.okio.** { *; }
    #protobuf
    -keep class com.google.protobuf.** { *; }
    #ZipEntry
    -keep class com.file.zip

    #roundedimageview圆角图片库
    -keep class com.makeramen.roundedimageview.** { *; }

    #retrofit
#    -dontwarn retrofit.**
    -keep class retrofit.** { *; }
#    -keepattributes Signature
    -keepattributes Exceptions

    #square的产品
    -keep class com.squareup.** { *;}
    #rx产品,因为rx用到反射，不keep反射会出问题
    -keep class rx.** { *;}

    #3D 地图
    -keep   class com.amap.api.mapcore.**{*;}
    -keep   class com.amap.api.maps.**{*;}
    -keep   class com.autonavi.amap.mapcore.*{*;}

    #定位
    -keep class com.amap.api.location.**{*;}
    -keep class com.amap.api.fence.**{*;}
    -keep class com.autonavi.aps.amapapi.model.**{*;}

    #搜索
#    -keep   class com.amap.api.services.**{*;}

    #2D地图
#    -keep class com.amap.api.maps2d.**{*;}
#    -keep class com.amap.api.mapcore2d.**{*;}

    #导航
#    -keep class com.amap.api.navi.**{*;}
#    -keep class com.autonavi.**{*;}

