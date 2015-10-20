package tk.wenop.XiangYu.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import tk.wenop.XiangYu.R;
import tk.wenop.XiangYu.bean.MessageEntity;
import tk.wenop.XiangYu.network.MessageNetwork;

public class MessageCreateEditActivity extends Activity implements View.OnClickListener{

    @ViewInject(R.id.image)
    ImageView image;

	@ViewInject(R.id.audio)
	ImageView audio;

	

    @ViewInject(R.id.supply)
    Button supply;
    Context context;
    public static MessageEntity messageEntity = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_editmessage);
        ViewUtils.inject(this);
        context = this;
        supply.setOnClickListener(this);

        initView();

    }

    public void initView(){

        if (messageEntity == null) return;


//        image.setText(messageEntity.getImage());
//
//		audio.setText(messageEntity.getAudio());

		

    }


    @Override
    public void onClick(View v) {
    if (v.getId() == supply.getId()){

            if (messageEntity == null){
                messageEntity = new MessageEntity();
            }

//        messageEntity.setImage(image.getText().toString());
//		messageEntity.setOwnerEntity(ownerEntity.getText().toString());
//		messageEntity.setAudio(audio.getText().toString());
//		messageEntity.setOwnerUser(ownerUser.getText().toString());
		
            MessageNetwork.save(context, messageEntity);

        }

    }

}