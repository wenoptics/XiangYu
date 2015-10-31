package tk.wenop.testapp.UI;

import android.content.Context;
import android.view.View;

import com.flyco.dialog.widget.base.BottomBaseDialog;
import com.skyfishjy.library.RippleBackground;

import tk.wenop.testapp.R;
import tk.wenop.testapp.Util.animatedDialogUtils.ViewFindUtils;


/*
* 主屏发消息
* */
public class NewContentBottomDialog extends BottomBaseDialog<NewContentBottomDialog> {

    RippleBackground audio_wave;
    View audio_press_region;

    public NewContentBottomDialog(Context context, View animateView) {
        super(context, animateView);
    }

    public NewContentBottomDialog(Context context) {
        super(context);
    }

    @Override
    public View onCreateView() {
//        showAnim(new FlipVerticalSwingEnter());
//        dismissAnim(null);
        View inflate = View.inflate(context, R.layout.dialog_new_content, null);
        audio_wave = ViewFindUtils.find(inflate, R.id.audio_wave);
        audio_press_region = ViewFindUtils.find(inflate, R.id.audio_press_region);

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

        // TODO
       /* ll_wechat_friend_circle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                T.showShort(context, "朋友圈");
                dismiss();
            }
        });*/
    }
}
