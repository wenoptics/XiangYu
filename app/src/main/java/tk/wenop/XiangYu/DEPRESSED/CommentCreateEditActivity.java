package tk.wenop.XiangYu.DEPRESSED;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import cn.bmob.im.BmobRecordManager;
import cn.bmob.im.inteface.OnRecordChangeListener;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import tk.wenop.XiangYu.R;
import tk.wenop.XiangYu.adapter.NewRecordPlayClickListener;
import tk.wenop.XiangYu.bean.CommentEntity;
import tk.wenop.XiangYu.bean.MessageEntity;
import tk.wenop.XiangYu.bean.User;
import tk.wenop.XiangYu.manager.DBManager;
import tk.wenop.XiangYu.network.CommentNetwork;
import tk.wenop.XiangYu.ui.ActivityBase;
import tk.wenop.XiangYu.util.CommonUtils;

public class CommentCreateEditActivity extends ActivityBase implements View.OnClickListener, View.OnLongClickListener, CommentNetwork.OnGetCommentEntities {


	@ViewInject(R.id.comment)
    Button comment;
    //audio commit
    @ViewInject(R.id.btn_speak)
    Button btn_speak;
    @ViewInject(R.id.image)
    ImageView image;
    @ViewInject(R.id.audio)
    ImageView audio;

    @ViewInject(R.id.comment_list)
    ListView comment_list;

    // 语音有关
    @ViewInject(R.id.layout_record)
    RelativeLayout layout_record;
    @ViewInject(R.id.tv_voice_tips)
    TextView tv_voice_tips;
    @ViewInject(R.id.iv_record)
    ImageView iv_record;
    private Drawable[] drawable_Anims;// 话筒动画
    BmobRecordManager recordManager;


    User currentUser;
    String targetId;
    String getVoicePath;
    Context context;
    public static CommentEntity commentEntity = null;
    public static MessageEntity messageEntity = null;

    ImageLoader imageLoader;
    CommentListAdapter commentListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_editcomment);
        ViewUtils.inject(this);
        context = this;

        imageLoader = ImageLoader.getInstance();
        initView();
        currentUser = BmobUser.getCurrentUser(this, User.class);
        targetId = BmobUser.getCurrentUser(this).getObjectId();
        commentListAdapter = new CommentListAdapter(this);
        comment_list.setAdapter(commentListAdapter);

        //加载评论信息
        DBManager.instance(context).getComments(messageEntity, this);

    }

    public void initView(){

        initVoiceView();

        if (messageEntity == null) return;
        imageLoader.displayImage("http://file.bmob.cn/" + messageEntity.getImage(), image);
        String path = "http://file.bmob.cn/" + messageEntity.getAudio();
        audio.setOnClickListener(new NewRecordPlayClickListener(context, path, audio));

    }


    @Override
    public void onClick(View v) {


     if (v.getId() == audio.getId()){


     }

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

    @Override
    public boolean onLongClick(View v) {
        return false;
    }

    @Override
    public void onGetCommentEntities(List<CommentEntity> allCommentEntities) {
        commentListAdapter.putCommentEntity(allCommentEntities);
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

                                saveAudioFile(getVoicePath);

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



    public void saveAudioFile(String getVoicePath){



        String[] files = new String[]{getVoicePath};

        Bmob.uploadBatch(context, files, new cn.bmob.v3.listener.UploadBatchListener() {
            @Override
            public void onSuccess(List<BmobFile> list, List<String> list1) {

                if (list.size() > 0) {
                    CommentEntity commentEntity = new CommentEntity();
                    commentEntity.setComment(list.get(0).getUrl());
                    commentEntity.setOwnerMessage(messageEntity);
                    commentEntity.setOwnerUser(currentUser);
                    commentEntity.save(context);//todo 检测更多动作
                    //todo:save comment to comment list;
                    commentListAdapter.addCommentEntity(commentEntity);

                }
            }

            @Override
            public void onProgress(int i, int i1, int i2, int i3) {

            }

            @Override
            public void onError(int i, String s) {

            }
        });

    }




}