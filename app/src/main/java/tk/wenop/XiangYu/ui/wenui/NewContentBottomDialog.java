package tk.wenop.XiangYu.ui.wenui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.flyco.dialog.widget.base.BottomBaseDialog;

import tk.wenop.XiangYu.R;
import tk.wenop.XiangYu.ui.activity.OnGetImageFromResoult;
import tk.wenop.XiangYu.util.animatedDialogUtils.ViewFindUtils;
import tk.wenop.rippleanimation.RippleBackground;

//import tk.wenop.testapp.R;
//import tk.wenop.testapp.Util.animatedDialogUtils.ViewFindUtils;


/*
* 主屏发消息
* */
public class NewContentBottomDialog extends BottomBaseDialog<NewContentBottomDialog> implements OnGetImageFromResoult {

    RippleBackground audio_wave;
    View audio_press_region;
    ImageView iv_photoHolder;
    RelativeLayout viewAddPhoto;
    ImageView iv_photoTopShade;

    SelectImageInterface selectImageInterface;
    public NewContentBottomDialog(Context context,SelectImageInterface selectImageInterface) {
        super(context);
        this.selectImageInterface = selectImageInterface;
    }

    public NewContentBottomDialog(Context context,View animateView) {
        super(context, animateView);
    }

    public NewContentBottomDialog(Context context) {
        super(context);
    }


    public interface SelectImageInterface{
        public void toSelectImageInterface();
    }



    @Override
    public View onCreateView() {
//        showAnim(new FlipVerticalSwingEnter());
//        dismissAnim(null);
        View inflate = View.inflate(context, R.layout.dialog_new_content, null);
        audio_wave = ViewFindUtils.find(inflate, R.id.audio_wave);
        audio_press_region = ViewFindUtils.find(inflate, R.id.audio_press_region);

        viewAddPhoto = ViewFindUtils.find(inflate,R.id.group_add_photo);
        iv_photoHolder = ViewFindUtils.find(inflate,R.id.iv_photoHolder);
        iv_photoTopShade = ViewFindUtils.find(inflate,R.id.iv_photoTopShade);


        return inflate;
    }


    /// TODO llwoll
    void onVolumnChanged____TODO_llwoll_录音音量变化动效(int volume) {

        audio_wave.setRippleDurationTime(1000);
        audio_wave.setRippleAmount(2);
        audio_wave.setRippleRepeatCount(0);
        audio_wave.reloadAnimator();
        audio_wave.startRippleAnimation();
    }



    @Override
    public void setUiBeforShow() {

        audio_wave.setRippleDurationTime(3500);
        audio_wave.setRippleAmount(1);
        audio_wave.setRippleRepeatCount(0);
        audio_wave.reloadAnimator();
        audio_wave.startRippleAnimation();

        audio_press_region.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        viewAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectImageInterface != null) selectImageInterface.toSelectImageInterface();
            }
        });

        // TODO
       /* ll_wechat_friend_circle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                T.showShort(context, "朋友圈");
                dismiss();
            }
        });*/
    }

    /*
        此接口接受订阅的图片地址
     */
    @Override
    public void onGetImageFromResoult(String path) {

        // 取原来占位图片的宽高
        int tWidth = iv_photoHolder.getWidth();
        int tHeight = iv_photoHolder.getHeight();
        iv_photoHolder.setMinimumHeight(tHeight);
        iv_photoHolder.setMaxHeight(tHeight);
        iv_photoHolder.setMinimumWidth(tWidth);
        iv_photoHolder.setMaxWidth(tWidth);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);

//        int bitmapWidth = bitmap.getWidth();
//        int bitmapHeight = bitmap.getHeight();

        iv_photoHolder.setScaleType(ImageView.ScaleType.CENTER_CROP);
        iv_photoHolder.setImageBitmap(bitmap);
        iv_photoHolder.setScaleType(ImageView.ScaleType.CENTER_CROP);

        iv_photoTopShade.setVisibility(View.VISIBLE);


    }


    /*


     */


}
