package tk.wenop.XiangYu.DEPRESSED;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import tk.wenop.XiangYu.R;
import tk.wenop.XiangYu.bean.MessageEntity;

public class MessageViewActivity extends Activity {

    @ViewInject(R.id.image)
    TextView image;
	@ViewInject(R.id.audio)
	TextView audio;

	



    Context context;
    public static MessageEntity messageEntity = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view_message);
        ViewUtils.inject(this);
        context = this;


        initView();

    }

    public void initView(){

        if (messageEntity == null) return;

//        image.setText(messageEntity.getImage());
//		audio.setText(messageEntity.getAudio());
//		ownerEntity.setText(messageEntity.get());
//		ownerUser.setText(messageEntity.getOwnerUser());
		



    }


}