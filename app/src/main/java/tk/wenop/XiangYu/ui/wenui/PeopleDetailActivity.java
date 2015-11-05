package tk.wenop.XiangYu.ui.wenui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import cn.bmob.im.BmobChatManager;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.PushListener;
import de.hdodenhof.circleimageview.CircleImageView;
import tk.wenop.XiangYu.R;
import tk.wenop.XiangYu.bean.User;
import tk.wenop.XiangYu.ui.ActivityBase;
import tk.wenop.XiangYu.ui.ChatActivity;
import tk.wenop.XiangYu.ui.SetMyInfoActivity;
import tk.wenop.XiangYu.util.ImageLoadOptions;

//import tk.wenop.testapp.R;



public class PeopleDetailActivity extends ActivityBase implements View.OnClickListener {

    @ViewInject(R.id.button3)
    Button sendMsgBtn;

    @ViewInject(R.id.button2)
    Button addFriendBtn;

    @ViewInject(R.id.profile_image)
    CircleImageView mProfileImage;

    @ViewInject(R.id.tv_nickName)
    TextView mNickName;

    @ViewInject(R.id.iv_gender)
    ImageView mGender;

    @ViewInject(R.id.textView_profileText)
    TextView textView_profileText;

    User targetUser;

    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people_detail);
        ViewUtils.inject(this);

        // 组装聊天对象
        targetUser = (User) getIntent().getSerializableExtra("user");

        User currentUser = BmobUser.getCurrentUser(this, User.class);
        if (currentUser.getUsername().equals(targetUser.getUsername())) {
            // 如果是自己， 那就去设置页面
            Intent intent =  new Intent(PeopleDetailActivity.this, SetMyInfoActivity.class);
            this.startActivity(intent);
            this.finish();
        }

        // 显示出返回按钮
        actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // wenop: 在 获取(联网)得到用户之前不要 读targetUser (除了 .getUsername)
        initOtherData(targetUser.getUsername());

    }

    /**
     * 更新头像 refreshAvatar
     */
    private void refreshAvatar(String avatar) {
        if (avatar != null && !avatar.equals("")) {
            ImageLoader.getInstance().displayImage(avatar, mProfileImage,
                    ImageLoadOptions.getOptions());
        } else {
            mProfileImage.setImageResource(R.drawable.default_head);
        }
    }

    private void updateUser(User user) {

        targetUser = user;

        refreshAvatar(user.getAvatar());

        // 在actionBar显示用户名
        actionBar.setTitle(targetUser.getNick() + "的详细信息");

        sendMsgBtn.setOnClickListener(this);
        addFriendBtn.setOnClickListener(this);

        try {

//            tv_set_name.setText(user.getUsername());
            mNickName.setText(user.getNick());

            textView_profileText.setText(targetUser.getUserDesc());

//            tv_set_gender.setText(user.getSex() == true ? "男" : "女");
            int _gender_resource = user.getSex() == true ?
                    R.drawable.gender_male
                    :
                    R.drawable.gender_female;
            mGender.setImageResource(_gender_resource);

        } catch (Exception e) {

        }
    }

    private void initOtherData(String name) {
        userManager.queryUser(name, new FindListener<User>() {

            @Override
            public void onError(int arg0, String arg1) {

                ShowLog("onError onError:" + arg1);
            }

            @Override
            public void onSuccess(List<User> arg0) {

                if (arg0 != null && arg0.size() > 0) {
                    User user = arg0.get(0);

                    updateUser(user);
                } else {
                    ShowLog("onSuccess 查无此人");
                }
            }
        });
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
    public void onClick(View v) {

        int id  = v.getId();
        if (id == sendMsgBtn.getId()){

            Intent intent =  new Intent(this, ChatActivity.class);
            intent.putExtra("user", targetUser);
            startActivity(intent);

        }else if (id == addFriendBtn.getId()){

//            Intent intent =  new Intent(this, AddFriendActivity.class);
//            intent.putExtra("from", "contact");
////            intent.putExtra("user",targetUser);
//            startActivity(intent);
            final ProgressDialog progress = new ProgressDialog(this);
            progress.setMessage("正在添加...");
            progress.setCanceledOnTouchOutside(false);
            progress.show();

            BmobChatManager.getInstance(this).sendTagMessage(
                    BmobConfig.TAG_ADD_CONTACT,
                    targetUser.getObjectId(),
                    new PushListener() {

                @Override
                public void onSuccess() {
                    // TODO Auto-generated method stub
                    progress.dismiss();
                    ShowToast("发送请求成功，等待对方验证!");
                }

                @Override
                public void onFailure(int arg0, final String arg1) {
                    // TODO Auto-generated method stub
                    progress.dismiss();
                    ShowToast("发送请求失败，请重新添加!");
                    ShowLog("发送请求失败:"+arg1);
                }
            });

        }

    }
}
