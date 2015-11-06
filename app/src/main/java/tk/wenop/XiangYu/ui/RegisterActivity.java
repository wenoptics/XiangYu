package tk.wenop.XiangYu.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.RequestSMSCodeListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.VerifySMSCodeListener;
import tk.wenop.XiangYu.R;
import tk.wenop.XiangYu.bean.User;
import tk.wenop.XiangYu.config.BmobConstants;
import tk.wenop.XiangYu.ui.wenui.PostRegisterActivity;
import tk.wenop.XiangYu.util.CommonUtils;

public class RegisterActivity extends BaseActivity implements OnClickListener {

    Button btn_register,tv_send;
    EditText et_username, et_password, et_pswAgain, et_nickName,et_smsCode;
    TextView tv_RegisterUserEmail;
    BmobChatUser currentUser;

    MyCountTimer timer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        
        initTopBar_withBackButton("欢迎加入乡语!");

        et_username = (EditText) findViewById(R.id.et_phone);
        et_password = (EditText) findViewById(R.id.et_password);
        et_pswAgain = (EditText) findViewById(R.id.et_pswAgain);
        et_nickName = (EditText) findViewById(R.id.et_nickName);
        et_smsCode = (EditText) findViewById(R.id.et_smsCode);
        tv_RegisterUserEmail = (TextView) findViewById(R.id.tv_RegisterUserEmail);

        tv_send = (Button) findViewById(R.id.tv_send);

        btn_register = (Button) findViewById(R.id.btn_register);
        btn_register.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                doRegister();
            }
        });

        tv_send.setOnClickListener(this);
        tv_RegisterUserEmail.setOnClickListener(this);
    }

    String userName;
    String password;
    String pwd_again;
    String nickName;
    String smsCode ;

    private void doRegister(){

        // 要走p1 p2 p3 3步

        userName = et_username.getText().toString();
        password = et_password.getText().toString();
        pwd_again = et_pswAgain.getText().toString();
        nickName = et_nickName.getText().toString();
        smsCode = et_smsCode.getText().toString();

        boolean isNetConnected = CommonUtils.isNetworkAvailable(this);
        if(!isNetConnected){
            ShowToast(R.string.network_tips);
            return;
        }
        
        if (TextUtils.isEmpty(userName)) {
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

        if (TextUtils.isEmpty(smsCode)) {
            ShowToast("请输入短信验证码");
            return;
        }

        if (TextUtils.isEmpty(nickName)) {
            nickName = "";
        }

        // 验证短信验证码
        p1_verifySMS();

    }

    // 创建账号
    private void p2_newAccount(final String phoneNum) {

        final ProgressDialog progress = new ProgressDialog(RegisterActivity.this);
        progress.setMessage("正在注册...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();
        //由于每个应用的注册所需的资料都不一样，故IM sdk未提供注册方法，用户可按照bmod SDK的注册方式进行注册。
        //注册的时候需要注意两点：1、User表中绑定设备id和type，2、设备表中绑定username字段
        final User bu = new User();
        bu.setUsername(userName);
        bu.setPassword(password);
        //将user和设备id进行绑定
        bu.setSex(true);
        bu.setNick(nickName);
        bu.setDeviceType("android");
        bu.setInstallId(BmobInstallation.getInstallationId(this));
        bu.signUp(RegisterActivity.this, new SaveListener() {

            @Override
            public void onSuccess() {

                progress.dismiss();
//                ShowToast("注册成功");
                BmobLog.i("创建账号成功");
                // 将设备与username进行绑定
                userManager.bindInstallationForRegister(bu.getUsername());
                //更新地理位置信息
                updateUserLocation();
                //发广播通知登陆页面退出
                sendBroadcast(new Intent(BmobConstants.ACTION_REGISTER_SUCCESS_FINISH));

                // 启动主页
//				Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
//                Intent intent = new Intent(RegisterActivity.this, SideActivity.class);

//                Intent intent = new Intent(RegisterActivity.this, PostRegisterActivity.class);
//                startActivity(intent);
//                finish();

                // 绑定手机号
                p3_bindMobilePhone(bu, phoneNum);

            }

            @Override
            public void onFailure(int arg0, String arg1) {

                BmobLog.i(arg1);
                ShowToast("注册失败:" + arg1);
                progress.dismiss();
            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.tv_send:
                requestSMSCode();
                break;

            case R.id.tv_RegisterUserEmail:
                Intent intent = new Intent(RegisterActivity.this,EmailRegisterActivity.class);
                startActivity(intent);
                break;
        }

    }

    private void requestSMSCode() {
        String number = et_username.getText().toString();
        if (!TextUtils.isEmpty(number)) {
            timer = new MyCountTimer(60000, 1000);
            timer.start();
            BmobSMS.requestSMSCode(this, number, "register", new RequestSMSCodeListener() {

                @Override
                public void done(Integer smsId, BmobException ex) {

                    if (ex == null) {// 验证码发送成功
                        showToast("验证码已经发送啦"); // 用于查询本次短信发送详情
                        tv_send.setClickable(false);
                    } else { //如果验证码发送错误，可停止计时
                        BmobLog.i(ex.toString());
                        timer.cancel();
                    }
                }
            });


        } else {
            showToast("请输入手机号码");
        }
    }

    class MyCountTimer extends CountDownTimer {

        public MyCountTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }
        @Override
        public void onTick(long millisUntilFinished) {
            tv_send.setText((millisUntilFinished / 1000) +"秒可重发");
        }
        @Override
        public void onFinish() {
            tv_send.setText("重发验证码");
            tv_send.setClickable(true);
        }
    }

    private void p1_verifySMS(){
        final String phone = et_username.getText().toString();
        String code = et_smsCode.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            showToast("手机号码不能为空");
            return;
        }

        if (TextUtils.isEmpty(code)) {
            showToast("验证码不能为空");
            return;
        }
        final ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage("正在验证短信验证码...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();
        // V3.3.9提供的一键注册或登录方式，可传手机号码和验证码
        BmobSMS.verifySmsCode(this,phone, code, new VerifySMSCodeListener() {

            @Override
            public void done(BmobException ex) {

                progress.dismiss();
                if(ex==null) {
                    BmobLog.i("手机号码已验证");
                    p2_newAccount(phone);

                } else {
                    if(ex.getErrorCode()==9019) {
                        showToast("您输入的验证码有误");
                    } else {
                        showToast("手机验证失败");
                    }
                    BmobLog.i("验证失败：code=" + ex.getErrorCode() + "，错误描述：" + ex.getLocalizedMessage());
                }
            }
        });
    }

    private void p3_bindMobilePhone(User user, String phone){
        //开发者在给用户绑定手机号码的时候需要提交两个字段的值：mobilePhoneNumber、mobilePhoneNumberVerified
        User tmpUser =new User();
        tmpUser.setMobilePhoneNumber(phone);
        tmpUser.setMobilePhoneNumberVerified(true);
        tmpUser.update(RegisterActivity.this, user.getObjectId(), new UpdateListener() {

            @Override
            public void onSuccess() {

                BmobLog.i("手机号码绑定成功");
                showToast("注册成功~");

                // 走完3步，终于注册成功了。 可以去下一个页面了
                Intent intent = new Intent(RegisterActivity.this, PostRegisterActivity.class);
                startActivity(intent);
                finish();

            }

            @Override
            public void onFailure(int arg0, String arg1) {

                showToast("手机号码绑定失败：" + arg0 + "-" + arg1);
            }
        });
    }



    public void showToast(String text) {
        if (!TextUtils.isEmpty(text)) {
            if (mToast == null) {
                mToast = Toast.makeText(getApplicationContext(), text,
                        Toast.LENGTH_SHORT);
            } else {
                mToast.setText(text);
            }
            mToast.show();
        }
    }

}
