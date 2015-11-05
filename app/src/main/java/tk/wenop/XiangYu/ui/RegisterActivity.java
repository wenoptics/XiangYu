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
import android.widget.Toast;

import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
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

    Button btn_register,tv_send,tv_bind;
    EditText et_username, et_password, et_pswAgain, et_nickName,et_smsCode;
    BmobChatUser currentUser;

    MyCountTimer timer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        
        initTopBar_withBackButton("注册");

        et_username = (EditText) findViewById(R.id.et_phone);
        et_password = (EditText) findViewById(R.id.et_password);
        et_pswAgain = (EditText) findViewById(R.id.et_pswAgain);
        et_nickName = (EditText) findViewById(R.id.et_nickName);
        et_smsCode = (EditText) findViewById(R.id.et_smsCode);

        tv_bind = (Button) findViewById(R.id.tv_bind);
        tv_send = (Button) findViewById(R.id.tv_send);

        btn_register = (Button) findViewById(R.id.btn_register);
        btn_register.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                doRegister();
            }
        });



        tv_send.setOnClickListener(this);
        tv_bind.setOnClickListener(this);
    }
    
    private void doRegister(){
        String userName = et_username.getText().toString();
        String password = et_password.getText().toString();
        String pwd_again = et_pswAgain.getText().toString();
        String nickName = et_nickName.getText().toString();

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

        if (TextUtils.isEmpty(nickName)) {
            nickName = "";
        }
        
        final ProgressDialog progress = new ProgressDialog(RegisterActivity.this);
        progress.setMessage("正在注册...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();
        //由于每个应用的注册所需的资料都不一样，故IM sdk未提供注册方法，用户可按照bmod SDK的注册方式进行注册。
        //注册的时候需要注意两点：1、User表中绑定设备id和type，2、设备表中绑定username字段
        final User bu = new User();
        bu.setUsername(userName);
        bu.setPassword(password);
        //将user和设备id进行绑定aa
        bu.setSex(true);
        bu.setNick(nickName);
        bu.setDeviceType("android");
        bu.setInstallId(BmobInstallation.getInstallationId(this));
        bu.signUp(RegisterActivity.this, new SaveListener() {

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

                verifyOrBind();
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

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.tv_send:
                requestSMSCode();
                break;
            case R.id.tv_bind:
                verifyOrBind();
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
                    // TODO Auto-generated method stub
                    if (ex == null) {// 验证码发送成功
                        showToast("验证码发送成功");// 用于查询本次短信发送详情
                    } else {//如果验证码发送错误，可停止计时
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
            tv_send.setText((millisUntilFinished / 1000) +"秒后重发");
        }
        @Override
        public void onFinish() {
            tv_send.setText("重新发送验证码");
        }
    }

    private void verifyOrBind(){
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
                // TODO Auto-generated method stub
                progress.dismiss();
                if(ex==null){
                    showToast("手机号码已验证");
                    bindMobilePhone(phone);
                }else{
                    showToast("验证失败：code=" + ex.getErrorCode() + "，错误描述：" + ex.getLocalizedMessage());
                }
            }
        });
    }

    private void bindMobilePhone(String phone){
        //开发者在给用户绑定手机号码的时候需要提交两个字段的值：mobilePhoneNumber、mobilePhoneNumberVerified
        User user =new User();
        user.setMobilePhoneNumber(phone);
        user.setMobilePhoneNumberVerified(true);
        User cur = BmobUser.getCurrentUser(RegisterActivity.this, User.class);
        user.update(RegisterActivity.this, cur.getObjectId(), new UpdateListener() {

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                showToast("手机号码绑定成功");

                //todo:绑定好手机号后才跳转
                Intent intent = new Intent(RegisterActivity.this, PostRegisterActivity.class);
                startActivity(intent);
                finish();

            }

            @Override
            public void onFailure(int arg0, String arg1) {
                // TODO Auto-generated method stub
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
