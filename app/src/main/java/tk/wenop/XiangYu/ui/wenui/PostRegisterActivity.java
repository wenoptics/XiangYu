package tk.wenop.XiangYu.ui.wenui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import de.hdodenhof.circleimageview.CircleImageView;
import tk.wenop.XiangYu.R;
import tk.wenop.XiangYu.bean.User;
import tk.wenop.XiangYu.config.BmobConstants;
import tk.wenop.XiangYu.ui.ActivityBase;
import tk.wenop.XiangYu.util.ImageLoadOptions;
import tk.wenop.XiangYu.util.PhotoUtil;

public class PostRegisterActivity extends ActivityBase {

    @ViewInject(R.id.btn_ok)
    Button btn_ok;
    @ViewInject(R.id.iv_avatar_male)
    CircleImageView iv_avatar_male;
    @ViewInject(R.id.iv_avatar_female)
    CircleImageView iv_avatar_female;
    @ViewInject(R.id.et_nickName)
    EditText ev_nickName;
    @ViewInject(R.id.et_userDesc)
    EditText ev_userDesc;


    ImageView nowSettingAvatarIV ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_register);
        ViewUtils.inject(this);

        User currentUser = (User) userManager.getCurrentUser(User.class);
        updateUser(currentUser);

        layout_all = getWindow().getDecorView().getRootView();

        nowSettingAvatarIV = null;
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(nowSettingAvatarIV==null) {
                    ShowToast("请选择一个性别~");
                    return;
                }
                updateStringInfoFinally();
            }
        });

        iv_avatar_male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nowSettingAvatarIV = iv_avatar_male;
                updateGenderInfo(0); //男
                iv_avatar_female.setVisibility(View.INVISIBLE);
                //设置头像
                showAvatarPop();
            }
        });

        iv_avatar_female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nowSettingAvatarIV = iv_avatar_female;
                updateGenderInfo(1); //女
                iv_avatar_male.setVisibility(View.INVISIBLE);
                //设置头像
                showAvatarPop();
            }
        });

    }

    RelativeLayout layout_choose;
    RelativeLayout layout_photo;
    View layout_all;
    PopupWindow avatarPop;
    public String filePath = "";

    @SuppressWarnings("deprecation")
    private void showAvatarPop() {
        View view = LayoutInflater.from(this).inflate(R.layout.pop_showavator,
                null);
        layout_choose = (RelativeLayout) view.findViewById(R.id.layout_choose);
        layout_photo = (RelativeLayout) view.findViewById(R.id.layout_photo);
        layout_choose.setOnClickListener(new View.OnClickListener() {
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
        layout_photo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View targetView) {
                ShowLog("点击拍照");

                layout_choose.setBackgroundColor(getResources().getColor(
                        R.color.base_color_text_white));
                targetView.setBackgroundDrawable(getResources().getDrawable(
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

        avatarPop = new PopupWindow(view, mScreenWidth, 600);
        avatarPop.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    avatarPop.dismiss();
                    return true;
                }
                return false;
            }
        });

        avatarPop.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        avatarPop.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        avatarPop.setTouchable(true);
        avatarPop.setFocusable(true);
        avatarPop.setOutsideTouchable(true);
        avatarPop.setBackgroundDrawable(new BitmapDrawable());
        // 动画效果 从底部弹起
        avatarPop.setAnimationStyle(R.style.Animations_GrowFromBottom);
        avatarPop.showAtLocation(layout_all, Gravity.BOTTOM, 0, 0);
    }


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
                if (avatarPop != null) {
                    avatarPop.dismiss();
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
                // sent to crop
                if (avatarPop != null) {
                    avatarPop.dismiss();
                }
                if (data == null) {
                    // Toast.makeText(this, "取消选择", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    saveCropAvatar(data);
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

                String url = bmobFile.getFileUrl(PostRegisterActivity.this);
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


    private void updateUser(User user) {
//        refreshAvatar(user.getAvatar());
        try {
            ev_nickName.setText(user.getNick());
        } catch (Exception e) {
        }
    }

    /**
     * 更新头像 refreshAvatar
     */
    private void refreshAvatar(String avatar) {
        if (avatar != null && !avatar.equals("")) {
            ImageLoader.getInstance().displayImage(avatar, nowSettingAvatarIV,
                    ImageLoadOptions.getOptions());
        } else {
            nowSettingAvatarIV.setImageResource(R.drawable.default_head);
        }
    }

    private void updateUserData(User user,UpdateListener listener){
        User current = (User) userManager.getCurrentUser(User.class);
        user.setObjectId(current.getObjectId());
        user.update(this, listener);
    }

    /** 修改性别
     */
    private void updateGenderInfo(int which) {
        final User u = new User();
        if(which==0){
            u.setSex(true);
        }else{
            u.setSex(false);
        }
        updateUserData(u, new UpdateListener() {

            @Override
            public void onSuccess() {
                ShowToast(String.format("您已设置性别为 %s\n上传一个头像吧~", u.getSex() ? "男" : "女"));
            }

            @Override
            public void onFailure(int arg0, String arg1) {
                ShowToast("onFailure:" + arg1);
            }
        });
    }

    private void updateStringInfoFinally() {
        final User u = new User();
        u.setUserDesc(ev_userDesc.getText().toString());
        u.setNick(ev_nickName.getText().toString());

        updateUserData(u, new UpdateListener() {

            @Override
            public void onSuccess() {
//                ShowToast("成功~");
                // 跳转到 主屏
                Intent intent = new Intent(PostRegisterActivity.this, SideActivity.class);
                startActivity(intent);
                PostRegisterActivity.this.finish();
            }

            @Override
            public void onFailure(int arg0, String arg1) {
                ShowToast("onFailure:" + arg1);
            }
        });
    }

    private void updateUserAvatar(final String url) {
        User u =new User();
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
    private void saveCropAvatar(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap bitmap = extras.getParcelable("data");
            Log.i("life", "avatar - bitmap = " + bitmap);
            if (bitmap != null) {
                bitmap = PhotoUtil.toRoundCorner(bitmap, 10);
                if (isFromCamera && degree != 0) {
                    bitmap = PhotoUtil.rotaingImageView(degree, bitmap);
                }
                nowSettingAvatarIV.setImageBitmap(bitmap);
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

    @Override
    public void onBackPressed() {
        // 禁止返回
        return;
    }
}
