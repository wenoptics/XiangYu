package tk.wenop.XiangYu.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import tk.wenop.XiangYu.R;
import tk.wenop.XiangYu.bean.CommentEntity;
import tk.wenop.XiangYu.network.CommentNetwork;

public class CommentCreateEditActivity extends Activity implements View.OnClickListener{


	@ViewInject(R.id.comment)
    Button comment;
	

    @ViewInject(R.id.supply)
    Button supply;
    Context context;
    public static CommentEntity commentEntity = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_editcomment);
        ViewUtils.inject(this);
        context = this;
        supply.setOnClickListener(this);

        initView();

    }

    public void initView(){

        if (commentEntity == null) return;


//        comment.setText(commentEntity.getComment());
		

    }


    @Override
    public void onClick(View v) {
    if (v.getId() == supply.getId()){

            if (commentEntity == null){
                commentEntity = new CommentEntity();
            }

//            commentEntity.setToUser(toUser.getText().toString());
//		commentEntity.setOwnerMessage(ownerMessage.getText().toString());
//		commentEntity.setOwnerUser(ownerUser.getText().toString());
//		commentEntity.setComment(comment.getText().toString());
		
            CommentNetwork.save(context, commentEntity);

        }

    }

}