package tk.wenop.XiangYu.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import cn.bmob.v3.listener.UpdateListener;
import tk.wenop.XiangYu.R;
import tk.wenop.XiangYu.bean.User;

/**
 * 设置昵称和性别
 * 
 */
public class UpdateNickNameActivity extends ActivityBase {

	EditText edit_nick;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_nickame_info);
		initView();
	}

	private void initView() {
		initTopBar_withBackButton("修改昵称");
		edit_nick = (EditText) findViewById(R.id.edit_nick);
		findViewById(R.id.button_ok).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				String nick = edit_nick.getText().toString();
				if (nick.equals("")) {
					ShowToast("请填写昵称!");
					return;
				}
				updateInfo(nick);
			}

		});
	}

	/** 修改资料
	  * updateInfo
	  */
	private void updateInfo(String nick) {
		final User user = userManager.getCurrentUser(User.class);
		User u = new User();
		u.setNick(nick);
		u.setHight(110);
		u.setObjectId(user.getObjectId());
		u.update(this, new UpdateListener() {

			@Override
			public void onSuccess() {
				final User c = userManager.getCurrentUser(User.class);
				ShowToast("修改成功:"+c.getNick()+",height = "+c.getHight());
				finish();
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				ShowToast("onFailure:" + arg1);
			}
		});
	}
}
