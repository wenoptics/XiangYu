package tk.wenop.XiangYu.ui.wenui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.im.BmobRecordManager;
import cn.bmob.im.inteface.OnRecordChangeListener;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import tk.wenop.XiangYu.R;
import tk.wenop.XiangYu.adapter.custom.CommentAdapter;
import tk.wenop.XiangYu.adapter.custom.MainScreenOverviewItem;
import tk.wenop.XiangYu.bean.CommentEntity;
import tk.wenop.XiangYu.bean.MessageEntity;
import tk.wenop.XiangYu.bean.User;
import tk.wenop.XiangYu.manager.DBManager;
import tk.wenop.XiangYu.network.CommentNetwork;
import tk.wenop.XiangYu.ui.ChatActivity;
import tk.wenop.XiangYu.util.CommonUtils;
import tk.wenop.XiangYu.util.WrappingRecyclerViewLayoutManager;


public class CommentActivity extends AppCompatActivity implements CommentNetwork.OnGetCommentEntities {

    private CommentAdapter commentAdapter;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mRVLayoutM;

    protected ArrayList<MainScreenOverviewItem> commentDataSet;


    public static MessageEntity messageEntity = null;
    ActionBar actionBar;

    @ViewInject(R.id.imageView_contentPhoto)
    ImageView contentPhoto;
    @ViewInject(R.id.imageView_avatar)
    ImageView avatar;

    @ViewInject(R.id.btn_speak)
    Button btn_speak;

    ImageLoader imageLoader;
    User currentUser;
    String userID;
    Context context;


    // 语音有关
    @ViewInject(R.id.layout_record)
    RelativeLayout layout_record;
    @ViewInject(R.id.tv_voice_tips)
    TextView tv_voice_tips;
    @ViewInject(R.id.iv_record)
    ImageView iv_record;
    private Drawable[] drawable_Anims;// 话筒动画
    BmobRecordManager recordManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        ViewUtils.inject(this);

        // 显示出返回按钮
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        currentUser = BmobUser.getCurrentUser(this, User.class);
        userID = currentUser.getObjectId();
        context = this;

        imageLoader = ImageLoader.getInstance();

        initView();


        /// 设置评论数据
        //设置recyclerView
        mRecyclerView = (RecyclerView) findViewById(R.id.comment_recyclerview);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
//        mRecyclerView.setHasFixedSize(true);

        // 用这个WrappingRecyclerViewLayoutManager改版的LayoutManager是因为我需要让
        //    这个recyclerView的内容展开(全局滚动)
        mRVLayoutM = new WrappingRecyclerViewLayoutManager(this);
        mRecyclerView.setLayoutManager(mRVLayoutM);



        commentDataSet = new ArrayList<>();
        commentAdapter = new CommentAdapter(CommentActivity.this);
        mRecyclerView.setAdapter(commentAdapter);

        //加载评论信息
        DBManager.instance(this).getComments(messageEntity, this);


    }

    public void initData(){




    }

    public void initView(){

        if (messageEntity == null) return;
        // 在actionBar显示用户名

        if (messageEntity.getOwnerUser() != null){
            final User user = messageEntity.getOwnerUser();

             if (user.getAvatar()!=null){
                //渲染用户头像
                 imageLoader.displayImage(user.getAvatar(), avatar);
                 avatar.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {
//                         Intent intent = new Intent(context, PeopleDetailActivity.class);
                         Intent intent =  new Intent(context, ChatActivity.class);
                         intent.putExtra("user", user);
                         context.startActivity(intent);
                     }
                 });
             }

            actionBar.setTitle(user.getUsername());
        }

        if (messageEntity == null) return;
        imageLoader.displayImage("http://file.bmob.cn/" + messageEntity.getImage(), contentPhoto);
        String path = "http://file.bmob.cn/" + messageEntity.getAudio();

//        audio.setOnClickListener(new NewRecordPlayClickListener(context, path, audio));
        initVoiceView();

    }
    private void initVoiceView() {

        layout_record = (RelativeLayout) findViewById(R.id.layout_record);
        tv_voice_tips = (TextView) findViewById(R.id.tv_voice_tips);
        iv_record = (ImageView) findViewById(R.id.iv_record);
        btn_speak.setOnTouchListener(new VoiceTouchListen());

        initVoiceAnimRes();
        initRecordManager();
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // 如果是返回按钮，那就finish这个activity
        if (id==android.R.id.home) {
            finish();
        }

        return true;
    }


    @Override
    public void onGetCommentEntities(List<CommentEntity> allCommentEntities) {
        commentAdapter.putDataSet(allCommentEntities);
    }



    /*
        录音功能
     */


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
//                        ShowToast("发送语音需要sdcard支持！");
                        return false;
                    }
                    try {
                        v.setPressed(true);
                        layout_record.setVisibility(View.VISIBLE);
                        tv_voice_tips.setText(getString(R.string.voice_cancel_tips));
                        // 开始录音
                        recordManager.startRecording(userID);
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
//                case MotionEvent.ACTION_CANCEL:
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
                                saveAudioFile(recordManager.getRecordFilePath(userID));

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
                    commentAdapter.addData(commentEntity);
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
