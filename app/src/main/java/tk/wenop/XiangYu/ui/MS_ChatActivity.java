package tk.wenop.XiangYu.ui;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.File;
import java.util.List;

import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobNotifyManager;
import cn.bmob.im.BmobRecordManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.db.BmobDB;
import cn.bmob.im.inteface.EventListener;
import cn.bmob.im.inteface.OnRecordChangeListener;
import cn.bmob.im.inteface.UploadListener;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.listener.PushListener;
import tk.wenop.XiangYu.MyMessageReceiver;
import tk.wenop.XiangYu.R;
import tk.wenop.XiangYu.adapter.MainScreen_MessageChatAdapter;
import tk.wenop.XiangYu.adapter.NewRecordPlayClickListener;
import tk.wenop.XiangYu.config.BmobConstants;
import tk.wenop.XiangYu.util.CommonUtils;
import tk.wenop.XiangYu.view.HeaderLayout;
import tk.wenop.XiangYu.view.dialog.DialogTips;
import tk.wenop.XiangYu.view.xlist.XListView;
import tk.wenop.XiangYu.view.xlist.XListView.IXListViewListener;

/**
 * 聊天界面
 * 
 * @ClassName: ChatActivity
 * @Description: TODO
 * @author smile
 * @date 2014-6-3 下午4:33:11
 */

/**
 * @ClassName: ChatActivity
 * @Description: TODO
 * @author smile
 * @date 2014-6-23 下午3:28:49
 */
@SuppressLint({ "ClickableViewAccessibility", "InflateParams" })
public class MS_ChatActivity extends ActivityBase implements OnClickListener,
        IXListViewListener, EventListener {

    private Button
            btn_chat_send,
            btn_speak;

    private ImageButton btn_picture;

    private ToggleButton btn_isAnonymous;

    XListView mListView;

//    EmoticonsEditText edit_user_comment;

    String targetId = "";

    BmobChatUser targetUser;

    private static int MsgPagerNum;

    private ViewPager pager_emo;

//    private TextView tv_picture, tv_camera, tv_location;

    // 语音有关
    RelativeLayout layout_record;
    TextView tv_voice_tips;
    ImageView iv_record;

    private Drawable[] drawable_Anims;// 话筒动画

    BmobRecordManager recordManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ms_chat);
        manager = BmobChatManager.getInstance(this);
        MsgPagerNum = 0;
        // 组装聊天对象
        targetUser = (BmobChatUser) getIntent().getSerializableExtra("user");
        targetId = targetUser.getObjectId();
//		BmobLog.i("聊天对象：" + targetUser.getUsername() + ",targetId = "
//				+ targetId);
        //注册广播接收器
        initNewMessageBroadCast();
        initView();
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
                    sendVoiceMessage(localPath, recordTime);
                    //是为了防止过了录音时间后，会多发一条语音出去的情况。
                    handler.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            btn_speak.setClickable(true);
                        }
                    }, 1000);
                }else{

                }
            }
        });
    }

    private void initView() {
        mHeaderLayout = (HeaderLayout) findViewById(R.id.common_actionbar);
        mListView = (XListView) findViewById(R.id.mListView);

        // wenop-mod
        // TODO 但是这个图片显示效果很丑
        initTopBarForBoth("乡语", R.drawable.base_action_bar_user_bg_selector,
			new HeaderLayout.onRightImageButtonClickListener() {

				// 单击去用户详情
				@Override
				public void onClick() {

				}
			});
        //initTopBar_withBackButton("与" + targetUser.getUsername() + "对话");

        initBottomView();
        initXListView();
        initVoiceView();
    }

    /**
     * 初始化语音布局
     *
     * @Title: initVoiceView
     * @Description:
     * @param
     * @return void
     * @throws
     */
    private void initVoiceView() {
        layout_record = (RelativeLayout) findViewById(R.id.layout_record);
        tv_voice_tips = (TextView) findViewById(R.id.tv_voice_tips);
        iv_record = (ImageView) findViewById(R.id.iv_record);
        btn_speak.setOnTouchListener(new VoiceTouchListen());
        initVoiceAnimRes();
        initRecordManager();
    }

    /**
     * 长按说话
     * @ClassName: VoiceTouchListen
     * @Description: TODO
     * @author smile
     * @date 2014-7-1 下午6:10:16
     */
    class VoiceTouchListen implements OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!CommonUtils.checkSdCard()) {
                    ShowToast("发送语音需要sdcard支持！");
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

                // 此处的 x,y 是相对于按钮控件(0,0)的
                // 因为 getX()得到是相对的， getRawX()得到是绝对的
                if (event.getY() < 0) {
                    /*tv_voice_tips
                            .setText(getString(R.string.voice_cancel_tips));
                    tv_voice_tips.setTextColor(Color.RED);*/
                    // TODO: 2015/10/14  左滑增加图片，右滑取消发送

                } else {
                    /*tv_voice_tips.setText(getString(R.string.voice_up_tips));
                    tv_voice_tips.setTextColor(Color.WHITE);*/
                }

                //wenop-debug
                //Log.d("Action_move", "X:"+String.valueOf(event.getX())+";Y:"+String.valueOf(event.getY()));
                //tv_voice_tips.setText(
                //        "X:"+String.valueOf(event.getX())+";Y:"+String.valueOf(event.getY())
                //);

                return true;
            }
            case MotionEvent.ACTION_UP:
                v.setPressed(false);
                layout_record.setVisibility(View.INVISIBLE);
                try {
                    if (event.getY() < 0) {// 放弃录音
                        recordManager.cancelRecording();
                        BmobLog.i("voice", "放弃发送语音");
                    } else {
                        int recordTime = recordManager.stopRecording();
                        if (recordTime > 1) {
                            // 发送语音文件
                            BmobLog.i("voice", "发送语音");
                            sendVoiceMessage(
                                    recordManager.getRecordFilePath(targetId),
                                    recordTime);
                        } else {// 录音时间过短，则提示录音过短的提示
                            layout_record.setVisibility(View.GONE);
                            showShortToast().show();
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
        manager.sendVoiceMessage(targetUser, local, length,
                new UploadListener() {

                    @Override
                    public void onStart(BmobMsg msg) {
                        // TODO Auto-generated method stub
                        refreshMessage(msg);
                    }

                    @Override
                    public void onSuccess() {
                        // TODO Auto-generated method stub
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(int error, String arg1) {
                        // TODO Auto-generated method stub
                        ShowLog("上传语音失败 -->arg1：" + arg1);
                        mAdapter.notifyDataSetChanged();
                    }
                });
    }

    Toast toast;

    /**
     * 显示录音时间过短的Toast
     * @Title: showShortToast
     * @return void
     * @throws
     */
    private Toast showShortToast() {
        if (toast == null) {
            toast = new Toast(this);
        }
        View view = LayoutInflater.from(this).inflate(
                R.layout.include_chat_voice_short, null);
        toast.setView(view);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        return toast;
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

    /**
     * 加载消息历史，从数据库中读出
     */
    private List<BmobMsg> initMsgData() {
        List<BmobMsg> list = BmobDB.create(this).queryMessages(targetId,MsgPagerNum);
        return list;
    }

    /**
     * 界面刷新
     * @Title: initOrRefresh
     * @Description: TODO
     * @param
     * @return void
     * @throws
     */
    private void initOrRefresh() {
        if (mAdapter != null) {
            if (MyMessageReceiver.mNewNum != 0) {// 用于更新当在聊天界面锁屏期间来了消息，这时再回到聊天页面的时候需要显示新来的消息
                int news=  MyMessageReceiver.mNewNum;//有可能锁屏期间，来了N条消息,因此需要倒叙显示在界面上
                int size = initMsgData().size();
                for(int i=(news-1);i>=0;i--){
                    mAdapter.add(initMsgData().get(size-(i+1)));// 添加最后一条消息到界面显示
                }
                mListView.setSelection(mAdapter.getCount() - 1);
            } else {
                mAdapter.notifyDataSetChanged();
            }
        } else {
            mAdapter = new MainScreen_MessageChatAdapter(this, initMsgData());
            mListView.setAdapter(mAdapter);
        }
    }



    private void initBottomView() {

        btn_chat_send = (Button) findViewById(R.id.btn_chat_send);
        btn_chat_send.setOnClickListener(this);

        // 最中间
        // 语音框
        btn_speak = (Button) findViewById(R.id.btn_speak);

        //增加图片按钮
        btn_picture = (ImageButton) findViewById(R.id.btn_picture);


        btn_chat_send.setVisibility(View.VISIBLE);

    }


    MainScreen_MessageChatAdapter mAdapter;

    private void initXListView() {
        // 不允许加载更多
        mListView.setPullLoadEnable(false);
        // 允许下拉
        mListView.setPullRefreshEnable(true);
        // 设置监听器
        mListView.setXListViewListener(this);
        mListView.pullRefreshing();

        //wenop: 要显示divider
//		mListView.setDividerHeight(0);

        // 加载数据
        initOrRefresh();
        mListView.setSelection(mAdapter.getCount() - 1);
        mListView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
//                hideSoftInputView();
                return false;
            }
        });

        // 重发按钮的点击事件
        mAdapter.setOnInViewClickListener(R.id.iv_fail_resend,
                new MainScreen_MessageChatAdapter.onInternalClickListener() {

                    @Override
                    public void OnClickListener(View parentV, View v,
                            Integer position, Object values) {
                        // 重发消息
                        showResendDialog(parentV, v, values);
                    }
                });
    }

    /**
     * 显示重发按钮 showResendDialog
     * @Title: showResendDialog
     * @Description: TODO
     * @param @param recent
     * @return void
     * @throws
     */
    public void showResendDialog(final View parentV, View v, final Object values) {
        DialogTips dialog = new DialogTips(this, "确定重发该消息", "确定", "取消", "提示",
                true);
        // 设置成功事件
        dialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int userId) {
                if (((BmobMsg) values).getMsgType() == BmobConfig.TYPE_IMAGE
                        || ((BmobMsg) values).getMsgType() == BmobConfig.TYPE_VOICE) {// 图片和语音类型的采用
                    resendFileMsg(parentV, values);
                } else {
                    resendTextMsg(parentV, values);
                }
                dialogInterface.dismiss();
            }
        });
        // 显示确认对话框
        dialog.show();
        dialog = null;
    }

    /**
     * 重发文本消息
     */
    private void resendTextMsg(final View parentV, final Object values) {
        BmobChatManager.getInstance(MS_ChatActivity.this).resendTextMessage(
                targetUser, (BmobMsg) values, new PushListener() {

                    @Override
                    public void onSuccess() {
                        // TODO Auto-generated method stub
                        ShowLog("发送成功");
                        ((BmobMsg) values)
                                .setStatus(BmobConfig.STATUS_SEND_SUCCESS);
                        parentV.findViewById(R.id.progress_load).setVisibility(
                                View.INVISIBLE);
                        parentV.findViewById(R.id.iv_fail_resend)
                                .setVisibility(View.INVISIBLE);
                        parentV.findViewById(R.id.tv_send_status)
                                .setVisibility(View.VISIBLE);
                        ((TextView) parentV.findViewById(R.id.tv_send_status))
                                .setText("已发送");
                    }

                    @Override
                    public void onFailure(int arg0, String arg1) {
                        // TODO Auto-generated method stub
                        ShowLog("发送失败:" + arg1);
                        ((BmobMsg) values)
                                .setStatus(BmobConfig.STATUS_SEND_FAIL);
                        parentV.findViewById(R.id.progress_load).setVisibility(
                                View.INVISIBLE);
                        parentV.findViewById(R.id.iv_fail_resend)
                                .setVisibility(View.VISIBLE);
                        parentV.findViewById(R.id.tv_send_status)
                                .setVisibility(View.INVISIBLE);
                    }
                });
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 重发图片消息
     * @Title: resendImageMsg
     * @Description: TODO
     * @param @param parentV
     * @param @param values
     * @return void
     * @throws
     */
    private void resendFileMsg(final View parentV, final Object values) {
        BmobChatManager.getInstance(MS_ChatActivity.this).resendFileMessage(
                targetUser, (BmobMsg) values, new UploadListener() {

                    @Override
                    public void onStart(BmobMsg msg) {
                        // TODO Auto-generated method stub
                    }

                    @Override
                    public void onSuccess() {
                        // TODO Auto-generated method stub
                        ((BmobMsg) values)
                                .setStatus(BmobConfig.STATUS_SEND_SUCCESS);
                        parentV.findViewById(R.id.progress_load).setVisibility(
                                View.INVISIBLE);
                        parentV.findViewById(R.id.iv_fail_resend)
                                .setVisibility(View.INVISIBLE);
                        if (((BmobMsg) values).getMsgType() == BmobConfig.TYPE_VOICE) {
                            parentV.findViewById(R.id.tv_send_status)
                                    .setVisibility(View.GONE);
                            parentV.findViewById(R.id.tv_voice_length)
                                    .setVisibility(View.VISIBLE);
                        } else {
                            parentV.findViewById(R.id.tv_send_status)
                                    .setVisibility(View.VISIBLE);
                            ((TextView) parentV
                                    .findViewById(R.id.tv_send_status))
                                    .setText("已发送");
                        }
                    }

                    @Override
                    public void onFailure(int arg0, String arg1) {
                        // TODO Auto-generated method stub
                        ((BmobMsg) values)
                                .setStatus(BmobConfig.STATUS_SEND_FAIL);
                        parentV.findViewById(R.id.progress_load).setVisibility(
                                View.INVISIBLE);
                        parentV.findViewById(R.id.iv_fail_resend)
                                .setVisibility(View.VISIBLE);
                        parentV.findViewById(R.id.tv_send_status)
                                .setVisibility(View.INVISIBLE);
                    }
                });
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
        case R.id.btn_chat_send:// 发送文本
            /*final String msg = edit_user_comment.getText().toString();
            if (msg.equals("")) {
                ShowToast("请输入发送消息!");
                return;
            }*/
            boolean isNetConnected = CommonUtils.isNetworkAvailable(this);
            if (!isNetConnected) {
                ShowToast(R.string.network_tips);
                // return;
            }
            // 组装BmobMessage对象
            BmobMsg message = BmobMsg.createTextSendMsg(this, targetId, "blablabla2333");

            // wenop: what if we don't set the Extra
//            message.setExtra("Bmob");


            // 默认发送完成，将数据保存到本地消息表和最近会话表中
            manager.sendTextMessage(targetUser, message);
            // 刷新界面
            refreshMessage(message);

            break;
        case R.id.tv_camera:// 拍照
            selectImageFromCamera();
            break;
        case R.id.tv_picture:// 图片
            selectImageFromLocal();
            break;
        case R.id.tv_location:// 位置
            selectLocationFromMap();
            break;
        default:
            break;
        }
    }

    /**
     * 启动地图
     *
     * @Title: selectLocationFromMap
     * @Description: TODO
     * @param
     * @return void
     * @throws
     */
    private void selectLocationFromMap() {
        Intent intent = new Intent(this, LocationActivity.class);
        intent.putExtra("type", "select");
        startActivityForResult(intent, BmobConstants.REQUESTCODE_TAKE_LOCATION);
    }

    private String localCameraPath = "";// 拍照后得到的图片地址

    /**
     * 启动相机拍照 startCamera
     *
     * @Title: startCamera
     * @throws
     */
    public void selectImageFromCamera() {
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File dir = new File(BmobConstants.BMOB_PICTURE_PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, String.valueOf(System.currentTimeMillis())
                + ".jpg");
        localCameraPath = file.getPath();
        Uri imageUri = Uri.fromFile(file);
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(openCameraIntent,
                BmobConstants.REQUESTCODE_TAKE_CAMERA);
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
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
        } else {
            intent = new Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
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
                        sendImageMessage(localSelectPath);
                    }
                }
                break;
            case BmobConstants.REQUESTCODE_TAKE_LOCATION:// 地理位置
                double latitude = data.getDoubleExtra("x", 0);// 维度
                double longtitude = data.getDoubleExtra("y", 0);// 经度
                String address = data.getStringExtra("address");
                if (address != null && !address.equals("")) {
                    sendLocationMessage(address, latitude, longtitude);
                } else {
                    ShowToast("无法获取到您的位置信息!");
                }

                break;
            }
        }
    }

    /**
     * 发送位置信息
     * @Title: sendLocationMessage
     * @Description: TODO
     * @param @param address
     * @param @param latitude
     * @param @param longtitude
     * @return void
     * @throws
     */
    private void sendLocationMessage(String address, double latitude,
            double longtitude) {

        // 组装BmobMessage对象
        BmobMsg message = BmobMsg.createLocationSendMsg(this, targetId,
                address, latitude, longtitude);
        // 默认发送完成，将数据保存到本地消息表和最近会话表中
        manager.sendTextMessage(targetUser, message);
        // 刷新界面
        refreshMessage(message);
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

        manager.sendImageMessage(targetUser, local, new UploadListener() {

            @Override
            public void onStart(BmobMsg msg) {
                // TODO Auto-generated method stub
                ShowLog("开始上传onStart：" + msg.getContent() + ",状态："
                        + msg.getStatus());
                refreshMessage(msg);
            }

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int error, String arg1) {
                // TODO Auto-generated method stub
                ShowLog("上传失败 -->arg1：" + arg1);
                mAdapter.notifyDataSetChanged();
            }
        });
    }



    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        // 新消息到达，重新刷新界面
        initOrRefresh();
        MyMessageReceiver.ehList.add(this);// 监听推送的消息
        // 有可能锁屏期间，在聊天界面出现通知栏，这时候需要清除通知和清空未读消息数
        BmobNotifyManager.getInstance(this).cancelNotify();
        BmobDB.create(this).resetUnread(targetId);
        //清空消息未读数-这个要在刷新之后
        MyMessageReceiver.mNewNum=0;
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        MyMessageReceiver.ehList.remove(this);// 监听推送的消息
        // 停止录音
        if (recordManager.isRecording()) {
            recordManager.cancelRecording();
            layout_record.setVisibility(View.GONE);
        }
        // 停止播放录音
        if (NewRecordPlayClickListener.isPlaying
                && NewRecordPlayClickListener.currentPlayListener != null) {
            NewRecordPlayClickListener.currentPlayListener.stopPlayRecord();
        }

    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == NEW_MESSAGE) {
                BmobMsg message = (BmobMsg) msg.obj;
                String uid = message.getBelongId();
                BmobMsg m = BmobChatManager.getInstance(MS_ChatActivity.this)
                        .getMessage(message.getConversationId(), message.getMsgTime());
                if (!uid.equals(targetId))// 如果不是当前正在聊天对象的消息，不处理
                    return;
                mAdapter.add(m);
                // 定位
                mListView.setSelection(mAdapter.getCount() - 1);
                //取消当前聊天对象的未读标示
                BmobDB.create(MS_ChatActivity.this).resetUnread(targetId);
            }
        }
    };

    public static final int NEW_MESSAGE = 0x001;// 收到消息

    NewBroadcastReceiver  receiver;

    private void initNewMessageBroadCast(){
        // 注册接收消息广播
        receiver = new NewBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(BmobConfig.BROADCAST_NEW_MESSAGE);
        //设置广播的优先级别大于Mainacitivity,这样如果消息来的时候正好在chat页面，直接显示消息，而不是提示消息未读
        intentFilter.setPriority(5);
        registerReceiver(receiver, intentFilter);
    }

    /**
     * 新消息广播接收者
     *
     */
    private class NewBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String from = intent.getStringExtra("fromId");
            String msgId = intent.getStringExtra("msgId");
            String msgTime = intent.getStringExtra("msgTime");
            // 收到这个广播的时候，message已经在消息表中，可直接获取
            if(TextUtils.isEmpty(from)&&TextUtils.isEmpty(msgId)&&TextUtils.isEmpty(msgTime)){
                BmobMsg msg = BmobChatManager.getInstance(MS_ChatActivity.this).getMessage(msgId, msgTime);
                if (!from.equals(targetId))// 如果不是当前正在聊天对象的消息，不处理
                    return;
                //添加到当前页面
                mAdapter.add(msg);
                // 定位
                mListView.setSelection(mAdapter.getCount() - 1);
                //取消当前聊天对象的未读标示
                BmobDB.create(MS_ChatActivity.this).resetUnread(targetId);
            }
            // 记得把广播给终结掉
            abortBroadcast();
        }
    }

    /**
     * 刷新界面
     * @Title: refreshMessage
     * @Description: TODO
     * @param @param message
     * @return void
     * @throws
     */
    private void refreshMessage(BmobMsg msg) {
        // 更新界面
        mAdapter.add(msg);
        mListView.setSelection(mAdapter.getCount() - 1);
//        edit_user_comment.setText("");
    }


    @Override
    public void onMessage(BmobMsg message) {
        // TODO Auto-generated method stub
        Message handlerMsg = handler.obtainMessage(NEW_MESSAGE);
        handlerMsg.obj = message;
        handler.sendMessage(handlerMsg);
    }

    @Override
    public void onNetChange(boolean isNetConnected) {
        // TODO Auto-generated method stub
        if (!isNetConnected) {
            ShowToast(R.string.network_tips);
        }
    }

    @Override
    public void onAddUser(BmobInvitation invite) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onOffline() {
        // TODO Auto-generated method stub
        showOfflineDialog(this);
    }

    @Override
    public void onReaded(String conversionId, String msgTime) {
        // TODO Auto-generated method stub
        // 此处应该过滤掉不是和当前用户的聊天的回执消息界面的刷新
        if (conversionId.split("&")[1].equals(targetId)) {
            // 修改界面上指定消息的阅读状态
            for (BmobMsg msg : mAdapter.getList()) {
                if (msg.getConversationId().equals(conversionId)
                        && msg.getMsgTime().equals(msgTime)) {
                    msg.setStatus(BmobConfig.STATUS_SEND_RECEIVERED);
                }
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    public void onRefresh() {
        // TODO Auto-generated method stub
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                MsgPagerNum++;
                int total = BmobDB.create(MS_ChatActivity.this).queryChatTotalCount(targetId);
                BmobLog.i("记录总数：" + total);
                int currents = mAdapter.getCount();
                if (total <= currents) {
                    ShowToast("记录加载完了哦!");
                } else {
                    List<BmobMsg> msgList = initMsgData();
                    mAdapter.setList(msgList);
                    mListView.setSelection(mAdapter.getCount() - currents - 1);
                }
                mListView.stopRefresh();
            }
        }, 1000);
    }

    @Override
    public void onLoadMore() {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(false) {
                return false;
            /*if (layout_more.getVisibility() == 0) {
                layout_more.setVisibility(View.GONE);
                return false;*/
            } else {
                return super.onKeyDown(keyCode, event);
            }
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideSoftInputView();
        try {
            unregisterReceiver(receiver);
        } catch (Exception ignored) {
        }

    }

}
