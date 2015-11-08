package tk.wenop.XiangYu.ui;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.SDKInitializer;

import java.util.List;

import cn.bmob.im.BmobChat;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.listener.FindListener;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;
import tk.wenop.XiangYu.CustomApplcation;
import tk.wenop.XiangYu.R;
import tk.wenop.XiangYu.bean.User;
import tk.wenop.XiangYu.config.Config;
import tk.wenop.XiangYu.ui.wenui.SideActivity;

/**
 * 引导页
 * 
 * @ClassName: SplashActivity
 * @Description: TODO
 * @author smile
 * @date 2014-6-4 上午9:45:43
 */
public class SplashActivity extends BaseActivity {

    private static final int GO_HOME = 100;
    private static final int GO_LOGIN = 200;

    // 定位获取当前用户的地理位置
    private LocationClient mLocationClient;

    private BaiduReceiver mReceiver;// 注册广播接收器，用于监听网络以及验证key

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        GifImageView gif_iv = (GifImageView) findViewById(R.id.gif_splashbg);
        GifDrawable gif_drawable = (GifDrawable) gif_iv.getDrawable();
        gif_drawable.setLoopCount(1);
        gif_drawable.start();

        //可设置调试模式，当为true的时候，会在logcat的BmobChat下输出一些日志，包括推送服务是否正常运行，如果服务端返回错误，也会一并打印出来。方便开发者调试
        BmobChat.DEBUG_MODE = true;
        //BmobIM SDK初始化--只需要这一段代码即可完成初始化
        //请到Bmob官网(http://www.bmob.cn/)申请ApplicationId,具体地址:http://docs.bmob.cn/android/faststart/index.html?menukey=fast_start&key=start_android
        BmobChat.getInstance(this).init(Config.applicationId);
//        BmobSMS.initialize(this, Config.applicationId);

        // 开启定位
        initLocClient();
        // 注册地图 SDK 广播监听者
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
        iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
        mReceiver = new BaiduReceiver();
        registerReceiver(mReceiver, iFilter);


    }

    private void gotoHome() {
        mHandler.sendEmptyMessageDelayed(GO_HOME, 2000);
    }

    private void gotoLogin() {
        mHandler.sendEmptyMessageDelayed(GO_LOGIN, 2000);
    }

    // by wenop
    private void checkUser() {
        User currUser = BmobUserManager.getInstance(this).getCurrentUser(User.class);
        if (currUser==null) {
            gotoLogin();
            return;
        }
        BmobUserManager.getInstance(this).queryUserById(currUser.getObjectId(),
                new FindListener<BmobChatUser>() {
                    @Override
                    public void onSuccess(List<BmobChatUser> list) {
                        if (list.size() == 0) {

                            Toast.makeText(
                                    SplashActivity.this,
                                    "登录失效，请您重新登陆",
                                    Toast.LENGTH_SHORT
                            ).show();

                            gotoLogin();

                        } else {
                            // 每次自动登陆的时候就需要更新下当前位置和好友的资料，因为好友的头像，昵称啥的是经常变动的
                            updateUserInfos();
                            gotoHome();
                        }
                    }

                    @Override
                    public void onError(int i, String s) {
                        Toast.makeText(SplashActivity.this, s, Toast.LENGTH_LONG).show();
                    }
                }
        );

    }

    @Override
    protected void onResume() {

        super.onResume();
        checkUser();
        /*if (userManager.getCurrentUser() != null) {
            // 每次自动登陆的时候就需要更新下当前位置和好友的资料，因为好友的头像，昵称啥的是经常变动的
            updateUserInfos();
            mHandler.sendEmptyMessageDelayed(GO_HOME, 2000);
        } else {
            mHandler.sendEmptyMessageDelayed(GO_LOGIN, 2000);
        }*/
    }

    /**
     * 开启定位，更新当前用户的经纬度坐标
     */
    private void initLocClient() {
        mLocationClient = CustomApplcation.getInstance().mLocationClient;
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationMode.Hight_Accuracy);// 设置定位模式:高精度模式
        option.setCoorType("bd09ll"); // 设置坐标类型:百度经纬度
        option.setScanSpan(1000);// 设置发起定位请求的间隔时间为1000ms:低于1000为手动定位一次，大于或等于1000则为定时定位
        option.setIsNeedAddress(false);// 不需要包含地址信息
        mLocationClient.setLocOption(option);
        mLocationClient.start();
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case GO_HOME:
//				startAnimActivity(MainActivity.class);
//				startAnimActivity(MessageListActivity.class);
                startAnimActivity(SideActivity.class);
                finish();
                break;
            case GO_LOGIN:
                startAnimActivity(LoginActivity.class);
                finish();
                break;
            }
        }
    };

    /**
     * 构造广播监听类，监听 SDK key 验证以及网络异常广播
     */
    public class BaiduReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            String s = intent.getAction();
            if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
//                ShowToast("key 验证出错! 请在 AndroidManifest.xml 文件中检查 key 设置");
                BmobLog.e("BaiduReceiver", "key 验证出错! 请在 AndroidManifest.xml 文件中检查 key 设置");
            } else if (s
                    .equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
                ShowToast("当前网络连接不稳定，请检查您的网络设置!");
            }
        }
    }

    @Override
    protected void onDestroy() {
        // 退出时销毁定位
        if (mLocationClient != null && mLocationClient.isStarted()) {
            mLocationClient.stop();
        }
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

}
