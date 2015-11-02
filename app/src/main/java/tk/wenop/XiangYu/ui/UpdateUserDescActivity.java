package tk.wenop.XiangYu.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import cn.bmob.v3.listener.UpdateListener;
import tk.wenop.XiangYu.R;
import tk.wenop.XiangYu.bean.User;

/**
 * 设置个性签名
 * 
 */
public class UpdateUserDescActivity extends ActivityBase {

    EditText edit_userDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_userdesc_info);
        initView();
    }

    private void initView() {
        initTopBar_withBackButton("修改个性签名");
        edit_userDesc = (EditText) findViewById(R.id.edit_userDesc);

		edit_userDesc.setText(getUserDesc());

        findViewById(R.id.button_ok).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				String userDesc = edit_userDesc.getText().toString();
				updateInfo(userDesc);
			}

		});
    }

    private String getUserDesc()
	{
		final User user = userManager.getCurrentUser(User.class);
		String d = user.getUserDesc();
		if (d != null)
		{ return d; }
		return "";
	}

    /** 修改资料
      * updateInfo
      */
    private void updateInfo(String userDesc) {
        final User user = userManager.getCurrentUser(User.class);
        User u = new User();
        u.setUserDesc(userDesc);
        u.setHight(110);
        u.setObjectId(user.getObjectId());
        u.update(this, new UpdateListener() {

            @Override
            public void onSuccess() {
                final User c = userManager.getCurrentUser(User.class);
//				ShowToast("修改成功:"+c.getUserDesc()+",height = "+c.getHight());
                ShowToast("修改成功");
                finish();
            }

            @Override
            public void onFailure(int arg0, String arg1) {
                ShowToast("onFailure:" + arg1);
            }
        });
    }
}
