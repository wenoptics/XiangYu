# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\WorkSpace\Android\sdk/tools/proguard/proguard-android.txt
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

##### 腾讯bugly
-keep public class com.tencent.bugly.**{*;}

##### Bmob
-ignorewarnings
# 这里根据具体的SDK版本修改
-libraryjars libs/BmobIM_V1.1.9beta_20150820.jar
-libraryjars libs/BmobSDK_V3.4.3_0820.jar

-keepattributes Signature
-keep class cn.bmob.v3.** {*;}

# 保证继承自BmobObject、BmobUser类的JavaBean不被混淆
-keep class tk.wenop.XiangYu.bean.** {*;}
#-keep class tk.example.bmobexample.Person{*;}