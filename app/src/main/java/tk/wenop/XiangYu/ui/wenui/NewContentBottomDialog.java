package tk.wenop.XiangYu.ui.wenui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.flyco.dialog.widget.base.BottomBaseDialog;

import java.util.List;

import cn.bmob.im.BmobRecordManager;
import cn.bmob.im.inteface.OnRecordChangeListener;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import dmax.dialog.SpotsDialog;
import tk.wenop.XiangYu.R;
import tk.wenop.XiangYu.bean.AreaEntity;
import tk.wenop.XiangYu.bean.MessageEntity;
import tk.wenop.XiangYu.bean.User;
import tk.wenop.XiangYu.network.AreaNetwork;
import tk.wenop.XiangYu.network.MessageNetwork;
import tk.wenop.XiangYu.ui.activity.OnGetImageFromResoult;
import tk.wenop.XiangYu.util.CommonUtils;
import tk.wenop.XiangYu.util.animatedDialogUtils.T;
import tk.wenop.XiangYu.util.animatedDialogUtils.ViewFindUtils;
import tk.wenop.rippleanimation.RippleBackground;

//import tk.wenop.testapp.R;
//import tk.wenop.testapp.Util.animatedDialogUtils.ViewFindUtils;


/*
* 主屏发消息
* */
public class NewContentBottomDialog extends BottomBaseDialog<NewContentBottomDialog> implements OnGetImageFromResoult, OnGetGeoCoderResultListener, AreaNetwork.OnGetAreaEntity {

    /*
        语音有关
     */
    BmobRecordManager recordManager;


    RippleBackground audio_wave;
    View audio_press_region;
    ImageView iv_photoHolder;
    RelativeLayout viewAddPhoto;
    ImageView iv_photoTopShade;
    FloatingActionButton fab_send;
    ImageView audioControl;
    TextView textTip;
    CheckBox cb_isAnonymous;
//    FABProgressCircle fab_progress;

    SpotsDialog loadingDialog;

    SelectImageInterface selectImageInterface;


    String userID = "";


    private String AUDIO_PATH = null;
    private String IMAGE_PATH = null;
    private User loginUser;
    //消息所属的位置
    private AreaEntity areaEntity;




    //定位相关;
    LocationClient mLocClient;
    static BDLocation lastLocation = null;
    GeoCoder mSearch = null; // 搜索模块，因为百度定位sdk能够得到经纬度，但是却无法得到具体的详细地址，因此需要采取反编码方式去搜索此经纬度代表的地址
    public CommentLocationListenner myListener = new CommentLocationListenner();
    Context context;
    AreaNetwork.OnGetAreaEntity onGetAreaEntity;
    private AreaEntity nowAreaEntity = null;



    public NewContentBottomDialog(Context context,SelectImageInterface selectImageInterface,AreaEntity areaEntity) {
        super(context);
        this.context = context;
        onGetAreaEntity = this;
        this.selectImageInterface = selectImageInterface;
        this.areaEntity = areaEntity;
    }


    public NewContentBottomDialog(Context context,SelectImageInterface selectImageInterface) {
        super(context);
        this.context = context;
        this.selectImageInterface = selectImageInterface;

    }

    public NewContentBottomDialog(Context context,View animateView) {

        super(context, animateView);
        this.context = context;
    }

    public NewContentBottomDialog(Context context) {
        super(context);
        this.context = context;
    }




    public interface SelectImageInterface{
        public void toSelectImageInterface();
    }



    @Override
    public View onCreateView() {
//        showAnim(new FlipVerticalSwingEnter());
//        dismissAnim(null);


        View inflate = View.inflate(context, R.layout.dialog_new_content, null);
        com.lidroid.xutils.ViewUtils.inject(inflate);

        audio_wave = ViewFindUtils.find(inflate, R.id.audio_wave);
        audio_press_region = ViewFindUtils.find(inflate, R.id.audio_press_region);
        audioControl = ViewFindUtils.find(inflate,R.id.imageView10);
        textTip = ViewFindUtils.find(inflate,R.id.textView_textTip);
        cb_isAnonymous = ViewFindUtils.find(inflate, R.id.cb_isAnonymous);

        viewAddPhoto = ViewFindUtils.find(inflate,R.id.group_add_photo);
        iv_photoHolder = ViewFindUtils.find(inflate,R.id.iv_photoHolder);
        iv_photoTopShade = ViewFindUtils.find(inflate,R.id.iv_photoTopShade);

        fab_send = ViewFindUtils.find(inflate,R.id.fab_send);

        //录音相关
        soundWave = ViewFindUtils.find(inflate, R.id.soundWave);

        userID = BmobUser.getCurrentUser(context).getObjectId();
        loginUser  = BmobUser.getCurrentUser(context,User.class);

        //初始化定位
        initLocClient();

        initRecordManager();

        return inflate;
    }



    SoundWave soundWave;

    void onVolumnChangedH(int volume) {

//        Log.d("valueOf volume", String.valueOf(volume));
        soundWave.setCurrentVolume(volume);
    }



    @Override
    public void setUiBeforShow() {

        audio_wave.setRippleDurationTime(3500);
        audio_wave.setRippleAmount(1);
        audio_wave.setRippleRepeatCount(0);
        audio_wave.reloadAnimator();
        audio_wave.startRippleAnimation();

        audioControl.setOnTouchListener(new VoiceTouchListen());

        fab_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog = new SpotsDialog(context);
                loadingDialog.show();
                sendMessage();
            }
        });

        viewAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectImageInterface != null) selectImageInterface.toSelectImageInterface();
            }
        });

    }

    /*
        此接口接受订阅的图片地址
     */
    @Override
    public void onGetImageFromResoult(String path) {

        IMAGE_PATH = path;
        // 取原来占位图片的宽高
        int tWidth = iv_photoHolder.getWidth();
        int tHeight = iv_photoHolder.getHeight();
        iv_photoHolder.setMinimumHeight(tHeight);
        iv_photoHolder.setMaxHeight(tHeight);
        iv_photoHolder.setMinimumWidth(tWidth);
        iv_photoHolder.setMaxWidth(tWidth);

        try {

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(path, options);

//        int bitmapWidth = bitmap.getWidth();
//        int bitmapHeight = bitmap.getHeight();

            iv_photoHolder.setScaleType(ImageView.ScaleType.CENTER_CROP);
            iv_photoHolder.setImageBitmap(bitmap);
            iv_photoHolder.setScaleType(ImageView.ScaleType.CENTER_CROP);
            iv_photoTopShade.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            T.show(context, "增加图片失败", 1500);
            IMAGE_PATH = null;
        }

    }


    /*
        录音功能
     */

    private void initRecordManager(){
        // 语音相关管理器
        recordManager = BmobRecordManager.getInstance(context);
        // 设置音量大小监听--在这里开发者可以自己实现：当剩余10秒情况下的给用户的提示，类似微信的语音那样
        recordManager.setOnRecordChangeListener(new OnRecordChangeListener() {

            @Override
            public void onVolumnChanged(int value) {
                // TODO Auto-generated method stub
//                iv_record.setImageDrawable(drawable_Anims[value]);
                onVolumnChangedH(value);
            }

            @Override
            public void onTimeChanged(int recordTime, String localPath) {
                // TODO Auto-generated method stub
                BmobLog.i("voice", "已录音长度:" + recordTime);
                if (recordTime >= BmobRecordManager.MAX_RECORD_TIME) {// 1分钟结束，发送消息

                    AUDIO_PATH = localPath;
                    // 发送语音消息
//                    sendVoiceMessage(localPath, recordTime);
                    //是为了防止过了录音时间后，会多发一条语音出去的情况。

//                    handler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            // TODO Auto-generated method stub
//                            btn_speak.setClickable(true);
//                        }
//                    }, 1000);

                } else {

                }
            }
        });
    }

    void setViewIsRecording(boolean isRecording) {
        if(isRecording) {
            soundWave.setVisibility(View.VISIBLE);
        } else {
            soundWave.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 长按说话
     */
    class VoiceTouchListen implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (!CommonUtils.checkSdCard()) {
                            T.show(context, "发送语音需要sdcard支持！", 1500);
                        return false;
                    }
                    try {
                        v.setPressed(true);
                        // 开始录音
                        recordManager.startRecording(userID);
                        setViewIsRecording(true);
                    } catch (Exception e) {}
                    return true;
                case MotionEvent.ACTION_MOVE: {
                    if (event.getY() < 0) {
                        //提醒用户往上滑可以取消录音
                        textTip.setText("松手取消录音");

                    } else {
                        // 取消录音
                        textTip.setText("正在录音...\n不松手上移\n取消录音");

                    }
                    return true;
                }
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    v.setPressed(false);
                    setViewIsRecording(false);
                    try {

                        if (event.getY() < 0) {// 放弃录音
                            recordManager.cancelRecording();
                            BmobLog.i("voice", "放弃语音");
                            textTip.setText("长按录音…");
                        } else {
                            int recordTime = recordManager.stopRecording();
                            if (recordTime > 1) {
                                //获得录音文件的路径
                                AUDIO_PATH = recordManager.getRecordFilePath(userID);
                                BmobLog.i("voice", AUDIO_PATH);
                                textTip.setText("已录音, 待发送");

                            } else {
                                // 录音时间过短，则提示录音过短的提示
//                                T.show(context, "录音时间过短", 700);
                                Snackbar.make(v, "录音时间过短", Snackbar.LENGTH_SHORT)
                                        .setAction("Action", null).show();

                                textTip.setText("长按录音…");
                            }
                        }
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                    return true;

                default:
                    return false;
            }
        }
    }


    private void doWhenSendError() {
        T.show(context, "发送失败", 1500);
        loadingDialog.dismiss();
    }

    private void onSendProgress() {

    }

    /*
        保存并且发送消息
     */
    public void sendMessage(){

        /*if (IMAGE_PATH == null){
            Toast.makeText(context,"请添加图片信息",Toast.LENGTH_SHORT).show();
            return;
        }

        if (AUDIO_PATH == null){
            Toast.makeText(context,"请添加语音信息",Toast.LENGTH_SHORT).show();
            return;
        }*/

        if (AUDIO_PATH != null && IMAGE_PATH == null)
        {
            // 只发送语音消息
            String[] files = new String[]{AUDIO_PATH};

            Bmob.uploadBatch(context, files, new cn.bmob.v3.listener.UploadBatchListener() {
                @Override
                public void onSuccess(List<BmobFile> list, List<String> list1) {

                    if (list.size() > 0) {
                        MessageEntity messageEntity = new MessageEntity();
                        if (areaEntity!=null) messageEntity.setOwnerArea(areaEntity);
                        messageEntity.setMsgType(MessageEntity.MSG_TYPE_ONLY_AUDIO);
                        messageEntity.setAudio(list.get(0).getUrl());
                        messageEntity.setOwnerUser(loginUser);
                        messageEntity.setCommentCount(0);
                        messageEntity.setAnonymous(cb_isAnonymous.isChecked());
                        if (nowAreaEntity!=null){
                            messageEntity.setFromLocation(nowAreaEntity.getArea());
                        }

                        MessageNetwork.save(context, messageEntity);
                        loadingDialog.dismiss();
                        dismiss();
                    }

                }

                @Override
                public void onProgress(int i, int i1, int i2, int i3) {

                }

                @Override
                public void onError(int i, String s) {
                    doWhenSendError();
                }
            });
        }

        else if (IMAGE_PATH != null && AUDIO_PATH == null)
        {
            // 只发送图片消息
            String[] files = new String[]{IMAGE_PATH};

            Bmob.uploadBatch(context, files, new cn.bmob.v3.listener.UploadBatchListener() {
                @Override
                public void onSuccess(List<BmobFile> list, List<String> list1) {

                    if (list.size() > 0) {
                        MessageEntity messageEntity = new MessageEntity();
                        if (areaEntity!=null) messageEntity.setOwnerArea(areaEntity);
                        messageEntity.setMsgType(MessageEntity.MSG_TYPE_ONLY_PHOTO);
                        messageEntity.setImage(list.get(0).getUrl());
                        messageEntity.setOwnerUser(loginUser);
                        messageEntity.setCommentCount(0);
                        messageEntity.setAnonymous(cb_isAnonymous.isChecked());
                        if (nowAreaEntity!=null){
                            messageEntity.setFromLocation(nowAreaEntity.getArea());
                        }
                        MessageNetwork.save(context, messageEntity);
                        loadingDialog.dismiss();
                        dismiss();
                    }

                }

                @Override
                public void onProgress(int i, int i1, int i2, int i3) {
                    Log.d("wenop-debug", String.format(":i=%d;i1=%d;i2=%d;i3=%d", i, i1, i2, i3));
                }

                @Override
                public void onError(int i, String s) {
                    doWhenSendError();
                }
            });
        }

        else if (IMAGE_PATH != null && AUDIO_PATH != null)
        {
            //语音+附图消息
            String[] files = new String[]{IMAGE_PATH, AUDIO_PATH};

            Bmob.uploadBatch(context, files, new cn.bmob.v3.listener.UploadBatchListener() {
                @Override
                public void onSuccess(List<BmobFile> list, List<String> list1) {

                    if (list.size() > 0) {

                    }
                    if (list.size() > 1) {
                        MessageEntity messageEntity = new MessageEntity();
                        if (areaEntity!=null) messageEntity.setOwnerArea(areaEntity);
                        messageEntity.setMsgType(MessageEntity.MSG_TYPE_AUDIO_wITH_PHOTO);
                        messageEntity.setAudio(list.get(1).getUrl());
                        messageEntity.setImage(list.get(0).getUrl());
                        messageEntity.setOwnerUser(loginUser);
                        messageEntity.setCommentCount(0);
                        messageEntity.setAnonymous(cb_isAnonymous.isChecked());
                        if (nowAreaEntity!=null){
                            messageEntity.setFromLocation(nowAreaEntity.getArea());
                        }
                        if (nowAreaEntity!=null){
                            messageEntity.setFromLocation(nowAreaEntity.getArea());
                        }

                        MessageNetwork.save(context, messageEntity);
                        loadingDialog.dismiss();
                        dismiss();
                    }

                }

                @Override
                public void onProgress(int i, int i1, int i2, int i3) {

                }

                @Override
                public void onError(int i, String s) {
                    doWhenSendError();
                }
            });

        }

        else {
            T.show(context, "您可以发语音或者一张图片", 1000);
            loadingDialog.dismiss();
        }


    }

//    public void finish(){
//        dismiss();
//    }

    /*

        定位相关的代码

     */
    private void initLocClient() {
        // 定位初始化
        mLocClient = new LocationClient(getContext());
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setProdName("bmobim");// 设置产品线
        option.setOpenGps(true);// 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        option.setOpenGps(true);
        option.setIsNeedAddress(true);
        option.setIgnoreKillProcess(true);
        mLocClient.setLocOption(option);
        mLocClient.start();
        if (mLocClient != null && mLocClient.isStarted())
            mLocClient.requestLocation();

//        if (lastLocation != null) {
//             显示在地图上
//            LatLng ll = new LatLng(lastLocation.getLatitude(),
//                    lastLocation.getLongitude());
//            MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
//            mBaiduMap.animateMapStatus(u);

        //设置反地理编码问题
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(this);
    }

    /**
     * 定位SDK监听函数
     */
    public class CommentLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null)
                return;
            String str =  location.getCity();

            if (lastLocation != null) {
                if (lastLocation.getLatitude() == location.getLatitude()
                        && lastLocation.getLongitude() == location
                        .getLongitude()) {
                    BmobLog.i("获取坐标相同");// 若两次请求获取到的地理位置坐标是相同的，则不再定位

                    //todo:设置当前的位置
                    lastLocation.setAddrStr(location.getDistrict());
                    AreaNetwork.loadArea(context, onGetAreaEntity, location.getDistrict());

                    mLocClient.stop();
                    return;
                }
            }
            lastLocation = location;
            //todo:设置当前的位置
            lastLocation.setAddrStr(location.getDistrict());
            AreaNetwork.loadArea(context, onGetAreaEntity, location.getDistrict());


            BmobLog.i("lontitude = " + location.getLongitude() + ",latitude = "
                    + location.getLatitude() + ",地址 = "
                    + lastLocation.getAddrStr());

            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                            // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
//            mBaiduMap.setMyLocationData(locData);
            LatLng ll = new LatLng(location.getLatitude(),
                    location.getLongitude());

            String address = location.getAddrStr();
            if (address != null && !address.equals("")) {
                lastLocation.setAddrStr(address);

            } else {
                // 反Geo搜索
                //todo 需要测试是否真正需要反地理编码的需要
//                mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(ll));
            }
            // 显示在地图上
//            MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
//            mBaiduMap.animateMapStatus(u);
//            设置按钮可点击
//            mHeaderLayout.getRightImageButton().setEnabled(true);
        }

    }
    @Override
    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {

        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
//            ShowToast("抱歉，未能找到结果");
            BmobLog.i("抱歉，未能找到结果：");
            return;
        }
        BmobLog.i("反编码得到的地址：" + result.getAddress());
        lastLocation.setAddrStr(result.getAddress());

        //todo:确认当前得到的地理位置是 result.getAddress()  并补充在这里
    }

    /*
        从 bmob拉取数据
     */
    @Override
    public void onGetAreaEntity(AreaEntity areaEntity) {
        if (areaEntity != null) nowAreaEntity = areaEntity;
    }



}
