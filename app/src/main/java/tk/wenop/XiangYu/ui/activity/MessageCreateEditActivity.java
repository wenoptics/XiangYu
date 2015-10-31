package tk.wenop.XiangYu.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bmob.BmobProFile;
import com.flyco.animation.BounceEnter.BounceBottomEnter;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.io.File;
import java.util.List;

import cn.bmob.im.BmobRecordManager;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.inteface.OnRecordChangeListener;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import tk.wenop.XiangYu.R;
import tk.wenop.XiangYu.adapter.NewRecordPlayClickListener;
import tk.wenop.XiangYu.bean.MessageEntity;
import tk.wenop.XiangYu.config.BmobConstants;
import tk.wenop.XiangYu.network.MessageNetwork;
import tk.wenop.XiangYu.ui.ActivityBase;
import tk.wenop.XiangYu.ui.wenui.NewContentBottomDialog;
import tk.wenop.XiangYu.util.CommonUtils;

public class MessageCreateEditActivity extends ActivityBase implements View.OnClickListener, NewContentBottomDialog.SelectImageInterface {

    @ViewInject(R.id.image)
    Button btn_image;
	@ViewInject(R.id.audio)
	Button btn_speak;

    @ViewInject(R.id.upload_image)
    ImageView view_image;

    @ViewInject(R.id.view_audio)
    ImageView view_audio;

    // 语音有关
    @ViewInject(R.id.layout_record)
    RelativeLayout layout_record;
    @ViewInject(R.id.tv_voice_tips)
    TextView tv_voice_tips;
    @ViewInject(R.id.iv_record)
    ImageView iv_record;
    private Drawable[] drawable_Anims;// 话筒动画
    BmobRecordManager recordManager;

    @ViewInject(R.id.supply)
    Button supply;
    Context context;
    public static MessageEntity messageEntity = null;

    String targetId = "";
    String localCameraPath = "";

    String getImagePath = "";
    String getVoicePath = "";


    /*
        选取图片相关的声明
     */
    OnGetImageFromResoult onGetImageFromResoult = null;
    NewContentBottomDialog newContentBottomDialog;
    BounceBottomEnter bas_in;
    NewContentBottomDialog.SelectImageInterface selectImageInterface;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
//      activity_create_editmessage
        setContentView(R.layout.activity_create_editmessage);
        ViewUtils.inject(this);

        targetId = BmobUser.getCurrentUser(this).getObjectId();
        context = this;
        supply.setOnClickListener(this);
        initView();
        initVoiceView();

        initNewMessageDialog();
    }

    /*
        实现新建消息的dialog
     */
    public void initNewMessageDialog(){

        selectImageInterface = this;
         bas_in = new BounceBottomEnter();
         newContentBottomDialog = new NewContentBottomDialog(MessageCreateEditActivity.this,selectImageInterface);
         onGetImageFromResoult = newContentBottomDialog;


    }

    public void initView(){

        if (messageEntity == null) return;
//        image.setText(messageEntity.getImage());
//		  audio.setText(messageEntity.getAudio());
    }


    /**
     * 初始化语音布局
     *------------------
     * @Title: initVoiceView
     * @Description: TODO
     * @param
     * @return void
     * @throws
     */

    private void initVoiceView() {

        layout_record = (RelativeLayout) findViewById(R.id.layout_record);
        tv_voice_tips = (TextView) findViewById(R.id.tv_voice_tips);
        iv_record = (ImageView) findViewById(R.id.iv_record);
        btn_speak.setOnTouchListener(new VoiceTouchListen());
        btn_image.setOnClickListener(this);

        initVoiceAnimRes();
        initRecordManager();
    }
    /**
     * 初始化语音动画资源
     * @Title: initVoiceAnimRes
     * @Description: TODO
     * @param
     * @return void
     * @throws
     */
    private void initVoiceAnimRes() {

        drawable_Anims = new Drawable[] {
                getResources().getDrawable(R.drawable.chat_icon_voice2),
                getResources().getDrawable(R.drawable.chat_icon_voice3),
                getResources().getDrawable(R.drawable.chat_icon_voice4),
                getResources().getDrawable(R.drawable.chat_icon_voice5),
                getResources().getDrawable(R.drawable.chat_icon_voice6) };
    }

    private void initRecordManager(){
        // 语音相关管理器
        recordManager = BmobRecordManager.getInstance(this);
        // 设置音量大小监听--在这里开发者可以自己实现：当剩余10秒情况下的给用户的提示，类似微信的语音那样
        recordManager.setOnRecordChangeListener(new OnRecordChangeListener() {

            @Override
            public void onVolumnChanged(int value) {
                // TODO Auto-generated method stub
                iv_record.setImageDrawable(drawable_Anims[value]);
            }

            @Override
            public void onTimeChanged(int recordTime, String localPath) {
                // TODO Auto-generated method stub
                BmobLog.i("voice", "已录音长度:" + recordTime);
                if (recordTime >= BmobRecordManager.MAX_RECORD_TIME) {// 1分钟结束，发送消息
                    // 需要重置按钮
                    btn_speak.setPressed(false);
                    btn_speak.setClickable(false);
                    // 取消录音框
                    layout_record.setVisibility(View.INVISIBLE);
                    // 发送语音消息
//                    sendVoiceMessage(localPath, recordTime);
                    //是为了防止过了录音时间后，会多发一条语音出去的情况。


//                    handler.postDelayed(new Runnable() {
//
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




    /**
     * 长按说话
     * @ClassName: VoiceTouchListen
     * @Description: TODO
     * @author smile
     * @date 2014-7-1 下午6:10:16
     */
    class VoiceTouchListen implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (!CommonUtils.checkSdCard()) {
                        ShowToast("发送语音需要sdcard支持！");

                        view_audio.setVisibility(View.VISIBLE);
                        return false;
                    }
                    try {
                        v.setPressed(true);
                        layout_record.setVisibility(View.VISIBLE);
                        tv_voice_tips.setText(getString(R.string.voice_cancel_tips));
                        // 开始录音
                        recordManager.startRecording(targetId);
                    } catch (Exception e) {
                    }
                    return true;
                case MotionEvent.ACTION_MOVE: {
                    if (event.getY() < 0) {
                        tv_voice_tips
                                .setText(getString(R.string.voice_cancel_tips));
                        tv_voice_tips.setTextColor(Color.RED);
                    } else {
                        tv_voice_tips.setText(getString(R.string.voice_up_tips));
                        tv_voice_tips.setTextColor(Color.WHITE);
                    }
                    return true;
                }
                case MotionEvent.ACTION_UP:
                    v.setPressed(false);
                    layout_record.setVisibility(View.INVISIBLE);
                    try {

                        float aa = event.getY();
                        if (event.getY() < 0) {// 放弃录音
                            recordManager.cancelRecording();
                            BmobLog.i("voice", "放弃发送语音");
                        } else {
                            int recordTime = recordManager.stopRecording();
                            if (recordTime > 1) {
                                // 发送语音文件
                                BmobLog.i("voice", "发送语音");
                                getVoicePath = recordManager.getRecordFilePath(targetId);
                                Log.d("voice", "file path: " + getVoicePath);
                                view_audio.setVisibility(View.VISIBLE);
                                view_audio.setOnClickListener(new NewRecordPlayClickListener(context, getVoicePath, view_audio));

                            } else {// 录音时间过短，则提示录音过短的提示
                                layout_record.setVisibility(View.GONE);
//                                showShortToast().show();
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


    /**
     * 发送语音消息
     * @Title: sendImageMessage
     * @Description: TODO
     * @param @param localPath
     * @return void
     * @throws
     */
    private void sendVoiceMessage(String local, int length) {


        BmobMsg bmobMsg = new BmobMsg();

        BmobProFile.getInstance(this).upload(local, new com.bmob.btp.callback.UploadListener() {
            @Override
            public void onSuccess(String filename, String url, BmobFile bmobFile) {

                Log.i("bmob",filename+"upload success");


            }

            @Override
            public void onProgress(int i) {

            }

            @Override
            public void onError(int i, String s) {

            }

        });

    }



    /**
     * 选择图片
     * @Title: selectImage
     * @Description: TODO
     * @param
     * @return void
     * @throws
     */
    public void selectImageFromLocal() {

        File dir = new File(BmobConstants.BMOB_PICTURE_PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, String.valueOf(System.currentTimeMillis())
                + ".jpg");
        localCameraPath = file.getPath();


        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
        } else {
            intent = new Intent(
                    Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        startActivityForResult(intent, BmobConstants.REQUESTCODE_TAKE_LOCAL);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case BmobConstants.REQUESTCODE_TAKE_CAMERA:// 当取到值的时候才上传path路径下的图片到服务器
                    ShowLog("本地图片的地址：" + localCameraPath);
                    sendImageMessage(localCameraPath);
                    break;
                case BmobConstants.REQUESTCODE_TAKE_LOCAL:
                    if (data != null) {
                        Uri selectedImage = data.getData();
                        if (selectedImage != null) {
                            Cursor cursor = getContentResolver().query(
                                    selectedImage, null, null, null, null);
                            cursor.moveToFirst();
                            int columnIndex = cursor.getColumnIndex("_data");
                            String localSelectPath = cursor.getString(columnIndex);
                            cursor.close();
                            if (localSelectPath == null
                                    || localSelectPath.equals("null")) {
                                ShowToast("找不到您想要的图片");
                                return;
                            }

                            getImagePath = localSelectPath;

                            /*
                                获得图片,然后发送事件给dialog
                             */
                            if (onGetImageFromResoult != null) onGetImageFromResoult.onGetImageFromResoult(getImagePath);


                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                            Bitmap bitmap = BitmapFactory.decodeFile(getImagePath, options);
                            view_image.setImageBitmap(bitmap);

                        }
                    }
                    break;
                case BmobConstants.REQUESTCODE_TAKE_LOCATION:// 地理位置
                    double latitude = data.getDoubleExtra("x", 0);// 维度
                    double longtitude = data.getDoubleExtra("y", 0);// 经度
                    String address = data.getStringExtra("address");
                    if (address != null && !address.equals("")) {
//                        sendLocationMessage(address, latitude, longtitude);
                    } else {
                        ShowToast("无法获取到您的位置信息!");
                    }

                    break;
            }
        }
    }

    /*
        需要知道回调过来的图片路径的时候，实现此接口
     */
//    public  interface OnGetImageFromResoult{
//        public void onGetImageFromResoult(String path);
//    }
    /*
        发布选择图片通知
     */
    @Override
    public void toSelectImageInterface() {
            selectImageFromLocal();
    }


    /**
     * 默认先上传本地图片，之后才显示出来 sendImageMessage
     * @Title: sendImageMessage
     * @Description: TODO
     * @param @param localPath
     * @return void
     * @throws
     */
    private void sendImageMessage(String local) {
//        if (layout_more.getVisibility() == View.VISIBLE) {
//            layout_more.setVisibility(View.GONE);
//            layout_add.setVisibility(View.GONE);
//            layout_emo.setVisibility(View.GONE);
//        }
//        manager.sendImageMessage(targetUser, local, new UploadListener() {
//
//            @Override
//            public void onStart(BmobMsg msg) {
//                // TODO Auto-generated method stub
//                ShowLog("开始上传onStart：" + msg.getContent() + ",状态："
//                        + msg.getStatus());
//                refreshMessage(msg);
//            }
//
//            @Override
//            public void onSuccess() {
//                // TODO Auto-generated method stub
//                mAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onFailure(int error, String arg1) {
//                // TODO Auto-generated method stub
//                ShowLog("上传失败 -->arg1：" + arg1);
//                mAdapter.notifyDataSetChanged();
//            }
//        });
    }

//    @SuppressLint("HandlerLeak")
//    private Handler handler = new Handler() {
//        public void handleMessage(Message msg) {
//            if (msg.what == NEW_MESSAGE) {
//                BmobMsg message = (BmobMsg) msg.obj;
//                String uid = message.getBelongId();
//                BmobMsg m = BmobChatManager.getInstance(ChatActivity.this)
//                        .getMessage(message.getConversationId(), message.getMsgTime());
//                if (!uid.equals(targetId))// 如果不是当前正在聊天对象的消息，不处理
//                    return;
//                mAdapter.add(m);
//                // 定位
//                mListView.setSelection(mAdapter.getCount() - 1);
//                //取消当前聊天对象的未读标示
//                BmobDB.create(ChatActivity.this).resetUnread(targetId);
//            }
//        }
//    };

    @Override
    public void onClick(View v) {
    if (v.getId() == supply.getId()){

            if (getVoicePath == ""){
                Toast.makeText(context,"请输入语音信息",Toast.LENGTH_SHORT).show();
                return;
            }else if (getImagePath == ""){
                Toast.makeText(context,"请输入图片信息",Toast.LENGTH_SHORT).show();
                return;
            }

        Toast.makeText(context,"信息完整,马上上传",Toast.LENGTH_SHORT).show();

        String[] files = new String[]{getImagePath,getVoicePath};

        Bmob.uploadBatch(context, files, new cn.bmob.v3.listener.UploadBatchListener() {
            @Override
            public void onSuccess(List<BmobFile> list, List<String> list1) {

                if (list.size() > 0){

                }
                if (list.size() > 1){

                    messageEntity.setAudio(list.get(1).getUrl());
                    messageEntity.setImage(list.get(0).getUrl());
                    MessageNetwork.save(context, messageEntity);

                }

            }

            @Override
            public void onProgress(int i, int i1, int i2, int i3) {

            }

            @Override
            public void onError(int i, String s) {

            }
        });

            if (messageEntity == null){
                messageEntity = new MessageEntity();
            }

        }else if (v.getId() == btn_image.getId()){

        /*
            todo:用来测试选取图片的dialog
         */
        newContentBottomDialog.showAnim(bas_in)
                .show();
//            selectImageFromLocal();
        }

    }

}