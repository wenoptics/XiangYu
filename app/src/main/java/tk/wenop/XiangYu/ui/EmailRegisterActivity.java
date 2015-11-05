package tk.wenop.XiangYu.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import tk.wenop.XiangYu.R;
import tk.wenop.XiangYu.bean.User;
import tk.wenop.XiangYu.config.BmobConstants;
import tk.wenop.XiangYu.util.CommonUtils;

/**
 * Created by zysd on 15/11/6.
 */
public class EmailRegisterActivity extends BaseActivity implements View.OnClickListener {



    @ViewInject(R.id.et_email)
    EditText et_email;
    @ViewInject(R.id.et_nickName)
    EditText et_nickName;
    @ViewInject(R.id.et_password)
    EditText et_password;
    @ViewInject(R.id.et_pswAgain)
    EditText et_pswAgain;
    @ViewInject(R.id.btn_register)
    Button btn_register;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_email);
        ViewUtils.inject(this);

        init();
    }

    public void init(){
        btn_register.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == btn_register.getId()){
            doRegister();
        }

    }

    private void doRegister(){
//        String userName = et_username.getText().toString();
        String password = et_password.getText().toString();
        String pwd_again = et_pswAgain.getText().toString();
        String nickName = et_nickName.getText().toString();

        boolean isNetConnected = CommonUtils.isNetworkAvailable(this);
        if(!isNetConnected){
            ShowToast(R.string.network_tips);
            return;
        }

        if (TextUtils.isEmpty(nickName)) {
//            ShowToast(R.string.toast_error_username_null);
            ShowToast("手机号不能为空哦");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            ShowToast(R.string.toast_error_password_null);
            return;
        }
        if (!pwd_again.equals(password)) {
            ShowToast(R.string.toast_error_comfirm_password);
            return;
        }

        if (TextUtils.isEmpty(nickName)) {
            nickName = "";
        }

        final ProgressDialog progress = new ProgressDialog(EmailRegisterActivity.this);
        progress.setMessage("正在注册...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();
        //由于每个应用的注册所需的资料都不一样，故IM sdk未提供注册方法，用户可按照bmod SDK的注册方式进行注册。
        //注册的时候需要注意两点：1、User表中绑定设备id和type，2、设备表中绑定username字段
        final User bu = new User();
        bu.setUsername(nickName);
        bu.setPassword(password);
        //将user和设备id进行绑定aa
        bu.setSex(true);
        bu.setNick(nickName);
        bu.setDeviceType("android");
        bu.setInstallId(BmobInstallation.getInstallationId(this));
        bu.signUp(EmailRegisterActivity.this, new SaveListener() {

            @Override
            public void onSuccess() {

                progress.dismiss();
                ShowToast("注册成功");
                // 将设备与username进行绑定
                userManager.bindInstallationForRegister(bu.getUsername());
                //更新地理位置信息
                updateUserLocation();
                //发广播通知登陆页面退出
                sendBroadcast(new Intent(BmobConstants.ACTION_REGISTER_SUCCESS_FINISH));

                verifyOrBindEmail();
                // 启动主页
//				Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
//                Intent intent = new Intent(RegisterActivity.this, SideActivity.class);
                //todo:
//                Intent intent = new Intent(RegisterActivity.this, PostRegisterActivity.class);
//                startActivity(intent);
//                finish();

            }

            @Override
            public void onFailure(int arg0, String arg1) {

                BmobLog.i(arg1);
                ShowToast("注册失败:" + arg1);
                progress.dismiss();
            }
        });
    }

    public void verifyOrBindEmail(){


        final String email = et_email.getText().toString();
        if (email.isEmpty() || email == null){
            ShowToast("请输入邮箱");
        }

        User user = User.getCurrentUser(this, User.class);
        user.setEmail(email);
        user.update(this, new UpdateListener() {
            @Override
            public void onSuccess() {
                ShowToast("发送邮箱验证");
            }

            @Override
            public void onFailure(int i, String s) {

                ShowToast("发送邮箱验证失败");
            }
        });



//        BmobUser.requestEmailVerify(this, email, new EmailVerifyListener() {
//            @Override
//            public void onSuccess() {
//                // TODO Auto-generated method stub
//                ShowToast("请求验证邮件成功，请到" + email + "邮箱中进行激活。");
//
//                Intent intent = new Intent(EmailRegisterActivity.this, PostRegisterActivity.class);
//                startActivity(intent);
//                finish();
//            }
//
//            @Override
//            public void onFailure(int code, String e) {
//                // TODO Auto-generated method stub
//                ShowToast("请求验证邮件失败:" + e);
//            }
//        });

    }



}
