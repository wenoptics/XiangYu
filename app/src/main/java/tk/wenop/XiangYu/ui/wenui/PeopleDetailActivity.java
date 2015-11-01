package tk.wenop.XiangYu.ui.wenui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import tk.wenop.XiangYu.R;
import tk.wenop.XiangYu.bean.User;
import tk.wenop.XiangYu.ui.ChatActivity;

//import tk.wenop.testapp.R;



public class PeopleDetailActivity extends AppCompatActivity implements View.OnClickListener {

    @ViewInject(R.id.button3)
    Button sendMsgBtn;

    @ViewInject(R.id.button2)
    Button addFriendBtn;

    User targetUser;
    String targetId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people_detail);
        ViewUtils.inject(this);

        // 组装聊天对象
        targetUser = (User) getIntent().getSerializableExtra("user");
        targetId = targetUser.getObjectId();


        // 显示出返回按钮
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);

            // 在actionBar显示用户名
            actionBar.setTitle(targetUser.getUsername() + "的详细信息");
        }

        sendMsgBtn.setOnClickListener(this);
        addFriendBtn.setOnClickListener(this);

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
            intent.putExtra("user",targetUser);
            startActivity(intent);

        }else if (id == addFriendBtn.getId()){


        }

    }
}
