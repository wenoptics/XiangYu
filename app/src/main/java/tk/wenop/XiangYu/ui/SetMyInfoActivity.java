package tk.wenop.XiangYu.ui;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.bmob.im.BmobChatManager;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.PushListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import tk.wenop.XiangYu.R;
import tk.wenop.XiangYu.bean.Blog;
import tk.wenop.XiangYu.bean.User;
import tk.wenop.XiangYu.config.BmobConstants;
import tk.wenop.XiangYu.util.ImageLoadOptions;
import tk.wenop.XiangYu.util.PhotoUtil;

/**
 * 个人资料页面
 * 
 * @ClassName: SetMyInfoActivity
 * @author smile
 * @date 2014-6-10 下午2:55:19
 */
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
@SuppressLint({ "SimpleDateFormat", "ClickableViewAccessibility", "InflateParams" })
public class SetMyInfoActivity extends ActivityBase implements OnClickListener {

    TextView tv_set_name, tv_set_nick, tv_set_gender;
    ImageView iv_set_avator, iv_arraw, iv_nickarraw;
    LinearLayout layout_all;

    RelativeLayout layout_head, layout_nick, layout_gender, layout_userDesc;

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);
        // 因为魅族手机下面有三个虚拟的导航按钮，需要将其隐藏掉，不然会遮掉拍照和相册两个按钮，且在setContentView之前调用才能生效
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= 14) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
        setContentView(R.layout.activity_set_info);
        
        initView();
    }

    private void initView() {
        layout_all = (LinearLayout) findViewById(R.id.layout_all);
        iv_set_avator = (ImageView) findViewById(R.id.iv_set_avator);
        iv_arraw = (ImageView) findViewById(R.id.iv_arraw);
        iv_nickarraw = (ImageView) findViewById(R.id.iv_nickarraw);
        tv_set_name = (TextView) findViewById(R.id.tv_set_name);
        tv_set_nick = (TextView) findViewById(R.id.tv_set_nick);
        layout_head = (RelativeLayout) findViewById(R.id.layout_head);
        layout_nick = (RelativeLayout) findViewById(R.id.layout_nick);
        layout_gender = (RelativeLayout) findViewById(R.id.layout_gender);
        layout_userDesc = (RelativeLayout) findViewById(R.id.layout_userDesc);

        tv_set_gender = (TextView) findViewById(R.id.tv_set_gender);

        initTopBar_withBackButton("个人资料");
        layout_head.setOnClickListener(this);
        layout_nick.setOnClickListener(this);
        layout_gender.setOnClickListener(this);
        layout_userDesc.setOnClickListener(this);
        iv_nickarraw.setVisibility(View.VISIBLE);
        iv_arraw.setVisibility(View.VISIBLE);

    }

    private void initMeData() {
        User user = userManager.getCurrentUser(User.class);
        BmobLog.i("hight = "+user.getHight()+",sex= "+user.getSex());
        initOtherData(user.getUsername());
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
                    user = arg0.get(0);

                    updateUser(user);
                } else {
                    ShowLog("onSuccess 查无此人");
                }
            }
        });
    }

    private void updateUser(User user) {
        // 更改
        refreshAvatar(user.getAvatar());
        try {
            tv_set_name.setText(user.getUsername());
            tv_set_nick.setText(user.getNick());
            tv_set_gender.setText(user.getSex() == true ? "男" : "女");
        } catch (Exception e) {
        }
    }

    /**
     * 更新头像 refreshAvatar
     */
    private void refreshAvatar(String avatar) {
        if (avatar != null && !avatar.equals("")) {
            ImageLoader.getInstance().displayImage(avatar, iv_set_avator,
                    ImageLoadOptions.getOptions());
        } else {
            iv_set_avator.setImageResource(R.drawable.default_head);
        }
    }

    @Override
    public void onResume() {
        
        super.onResume();
        initMeData();
    }

    @Override
    public void onClick(View v) {
        
        switch (v.getId()) {

        case R.id.layout_head:
            showAvatarPop();
            break;
        case R.id.layout_nick:
            startAnimActivity(UpdateNickNameActivity.class);
//			addBlog();
            break;
        case R.id.layout_gender:// 性别
            showSexChooseDialog();
            break;
        case R.id.layout_userDesc:
            /// 个性签名
            startAnimActivity(UpdateUserDescActivity.class);
            break;
        }
    }

    String[] sexs = new String[]{ "男", "女" };
    private void showSexChooseDialog() {
        new AlertDialog.Builder(this)
        .setTitle("性别...")
//        .setIcon(android.R.drawable.ic_dialog_info)
        .setSingleChoiceItems(sexs, 0,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                            int which) {
                        BmobLog.i("点击的是"+sexs[which]);
                        updateInfo(which);
                        dialog.dismiss();
                    }
                })
        .setNegativeButton("取消", null)
        .show();
    }


    /** 修改资料
      */
    private void updateInfo(int which) {
        final User u = new User();
        if(which==0){
            u.setSex(true);
        }else{
            u.setSex(false);
        }
        updateUserData(u,new UpdateListener() {

            @Override
            public void onSuccess() {

                ShowToast("修改成功");
                tv_set_gender.setText(u.getSex() == true ? "男" : "女");
            }

            @Override
            public void onFailure(int arg0, String arg1) {

                ShowToast("onFailure:" + arg1);
            }
        });
    }

    /**
     * 添加好友请求
     *
     */
    private void addFriend() {
        final ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage("正在添加...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();
        // 发送tag请求
        BmobChatManager.getInstance(this).sendTagMessage(BmobConfig.TAG_ADD_CONTACT,
                user.getObjectId(), new PushListener() {

                    @Override
                    public void onSuccess() {
                        
                        progress.dismiss();
                        ShowToast("发送请求成功，等待对方验证！");
                    }

                    @Override
                    public void onFailure(int arg0, final String arg1) {
                        
                        progress.dismiss();
                        ShowToast("发送请求成功，等待对方验证！");
                        ShowLog("发送请求失败:" + arg1);
                    }
                });
    }

    RelativeLayout layout_choose;
    RelativeLayout layout_photo;
    PopupWindow avatorPop;

    public String filePath = "";

    @SuppressWarnings("deprecation")
    private void showAvatarPop() {
        View view = LayoutInflater.from(this).inflate(R.layout.pop_showavator,
                null);
        layout_choose = (RelativeLayout) view.findViewById(R.id.layout_choose);
        layout_photo = (RelativeLayout) view.findViewById(R.id.layout_photo);
        layout_photo.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                ShowLog("点击拍照");
                
                layout_choose.setBackgroundColor(getResources().getColor(
                        R.color.base_color_text_white));
                layout_photo.setBackgroundDrawable(getResources().getDrawable(
                        R.drawable.pop_bg_press));
                File dir = new File(BmobConstants.MyAvatarDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                // 原图
                File file = new File(dir, new SimpleDateFormat("yyMMddHHmmss")
                        .format(new Date()));
                filePath = file.getAbsolutePath();// 获取相片的保存路径
                Uri imageUri = Uri.fromFile(file);

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent,
                        BmobConstants.REQUESTCODE_UPLOADAVATAR_CAMERA);
            }
        });
        layout_choose.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                
                ShowLog("点击相册");
                layout_photo.setBackgroundColor(getResources().getColor(
                        R.color.base_color_text_white));
                layout_choose.setBackgroundDrawable(getResources().getDrawable(
                        R.drawable.pop_bg_press));
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent,
                        BmobConstants.REQUESTCODE_UPLOADAVATAR_LOCATION);
            }
        });

        avatorPop = new PopupWindow(view, mScreenWidth, 600);
        avatorPop.setTouchInterceptor(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    avatorPop.dismiss();
                    return true;
                }
                return false;
            }
        });

        avatorPop.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        avatorPop.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        avatorPop.setTouchable(true);
        avatorPop.setFocusable(true);
        avatorPop.setOutsideTouchable(true);
        avatorPop.setBackgroundDrawable(new BitmapDrawable());
        // 动画效果 从底部弹起
        avatorPop.setAnimationStyle(R.style.Animations_GrowFromBottom);
        avatorPop.showAtLocation(layout_all, Gravity.BOTTOM, 0, 0);
    }

    /**
     * @Title: startImageAction
     * @return void
     * @throws
     */
    private void startImageAction(Uri uri, int outputX, int outputY,
            int requestCode, boolean isCrop) {
        Intent intent = null;
        if (isCrop) {
            intent = new Intent("com.android.camera.action.CROP");
        } else {
            intent = new Intent(Intent.ACTION_GET_CONTENT, null);
        }
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra("return-data", true);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        startActivityForResult(intent, requestCode);
    }

    Bitmap newBitmap;
    boolean isFromCamera = false;// 区分拍照旋转
    int degree = 0;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
        case BmobConstants.REQUESTCODE_UPLOADAVATAR_CAMERA:// 拍照修改头像
            if (resultCode == RESULT_OK) {
                if (!Environment.getExternalStorageState().equals(
                        Environment.MEDIA_MOUNTED)) {
                    ShowToast("SD不可用");
                    return;
                }
                isFromCamera = true;
                File file = new File(filePath);
                degree = PhotoUtil.readPictureDegree(file.getAbsolutePath());
                Log.i("life", "拍照后的角度：" + degree);
                startImageAction(Uri.fromFile(file), 200, 200,
                        BmobConstants.REQUESTCODE_UPLOADAVATAR_CROP, true);
            }
            break;
        case BmobConstants.REQUESTCODE_UPLOADAVATAR_LOCATION:// 本地修改头像
            if (avatorPop != null) {
                avatorPop.dismiss();
            }
            Uri uri = null;
            if (data == null) {
                return;
            }
            if (resultCode == RESULT_OK) {
                if (!Environment.getExternalStorageState().equals(
                        Environment.MEDIA_MOUNTED)) {
                    ShowToast("SD不可用");
                    return;
                }
                isFromCamera = false;
                uri = data.getData();
                startImageAction(uri, 200, 200,
                        BmobConstants.REQUESTCODE_UPLOADAVATAR_CROP, true);
            } else {
                ShowToast("照片获取失败");
            }

            break;
        case BmobConstants.REQUESTCODE_UPLOADAVATAR_CROP:// 裁剪头像返回
            // TODO sent to crop
            if (avatorPop != null) {
                avatorPop.dismiss();
            }
            if (data == null) {
                // Toast.makeText(this, "取消选择", Toast.LENGTH_SHORT).show();
                return;
            } else {
                saveCropAvator(data);
            }
            // 初始化文件路径
            filePath = "";
            // 上传头像
            uploadAvatar();
            break;
        default:
            break;

        }
    }

    private void uploadAvatar() {
        BmobLog.i("头像地址：" + path);
        final BmobFile bmobFile = new BmobFile(new File(path));
        bmobFile.upload(this, new UploadFileListener() {

            @Override
            public void onSuccess() {
                
                String url = bmobFile.getFileUrl(SetMyInfoActivity.this);
                // 更新BmobUser对象
                updateUserAvatar(url);
            }

            @Override
            public void onProgress(Integer arg0) {
                

            }

            @Override
            public void onFailure(int arg0, String msg) {
                
                ShowToast("头像上传失败：" + msg);
            }
        });
    }

    private void updateUserAvatar(final String url) {
        User  u =new User();
        u.setAvatar(url);
        updateUserData(u,new UpdateListener() {
            @Override
            public void onSuccess() {
                
                ShowToast("头像更新成功！");
                // 更新头像
                refreshAvatar(url);
            }

            @Override
            public void onFailure(int code, String msg) {
                
                ShowToast("头像更新失败：" + msg);
            }
        });
    }

    String path;

    /**
     * 保存裁剪的头像
     */
    private void saveCropAvator(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap bitmap = extras.getParcelable("data");
            Log.i("life", "avatar - bitmap = " + bitmap);
            if (bitmap != null) {
                bitmap = PhotoUtil.toRoundCorner(bitmap, 10);
                if (isFromCamera && degree != 0) {
                    bitmap = PhotoUtil.rotaingImageView(degree, bitmap);
                }
                iv_set_avator.setImageBitmap(bitmap);
                // 保存图片
                String filename = new SimpleDateFormat("yyMMddHHmmss")
                        .format(new Date())+".png";
                path = BmobConstants.MyAvatarDir + filename;
                PhotoUtil.saveBitmap(BmobConstants.MyAvatarDir, filename,
                        bitmap, true);
                // 上传头像
                if (bitmap != null && bitmap.isRecycled()) {
                    bitmap.recycle();
                }
            }
        }
    }

    /** 测试关联关系是否可用
      */
    public void addBlog(){
        //		BmobRelation relation = new BmobRelation();
        //		blog.setObjectId("c7a9ca9c0c");
        //		relation.add(blog);
        //		user.setBlogs(relation);
        final Blog blog = new Blog();
        blog.setBrief("你好");
        blog.save(this, new SaveListener() {

            @Override
            public void onSuccess() {
                
                BmobLog.i("blog保存成功");
                User  u =new User();
                u.setBlog(blog);
                updateUserData(u, new UpdateListener() {

                    @Override
                    public void onSuccess() {
                        
                        BmobLog.i("user更新成功");
                    }

                    @Override
                    public void onFailure(int code, String msg) {
                        
                        BmobLog.i("code = "+code+",msg = "+msg);
                    }
                });

            }

            @Override
            public void onFailure(int arg0, String arg1) {
                

            }
        });
    }

    private void updateUserData(User user,UpdateListener listener){
        User current = (User) userManager.getCurrentUser(User.class);
        user.setObjectId(current.getObjectId());
        user.update(this, listener);
    }

}
