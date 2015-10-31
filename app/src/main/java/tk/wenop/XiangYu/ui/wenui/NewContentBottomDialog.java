package tk.wenop.XiangYu.ui.wenui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;

import com.flyco.dialog.widget.base.BottomBaseDialog;
import com.skyfishjy.library.RippleBackground;

import tk.wenop.XiangYu.R;
import tk.wenop.XiangYu.ui.activity.OnGetImageFromResoult;
import tk.wenop.XiangYu.util.animatedDialogUtils.ViewFindUtils;

//import tk.wenop.testapp.R;
//import tk.wenop.testapp.Util.animatedDialogUtils.ViewFindUtils;


/*
* 主屏发消息
* */
public class NewContentBottomDialog extends BottomBaseDialog<NewContentBottomDialog> implements OnGetImageFromResoult {

    RippleBackground audio_wave;
    View audio_press_region;
    ImageView group_add_photo;
    ImageView imageSelect;

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

        imageSelect = ViewFindUtils.find(inflate,R.id.imageView7);
        group_add_photo = ViewFindUtils.find(inflate,R.id.group_add_photo);


        return inflate;
    }

    @Override
    public void setUiBeforShow() {

        audio_wave.startRippleAnimation();


        audio_press_region.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



            }
        });

        imageSelect.setOnClickListener(new View.OnClickListener() {
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

                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                            Bitmap bitmap = BitmapFactory.decodeFile(path, options);
                            group_add_photo.setImageBitmap(bitmap);

    }

}
