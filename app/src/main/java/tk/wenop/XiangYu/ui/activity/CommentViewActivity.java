package tk.wenop.XiangYu.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import tk.wenop.XiangYu.R;
import tk.wenop.XiangYu.bean.CommentEntity;

public class CommentViewActivity extends Activity {


	@ViewInject(R.id.comment)
    ImageView comment;

    Context context;
    public static CommentEntity commentEntity = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view_comment);
        ViewUtils.inject(this);
        context = this;


        initView();

    }

    public void initView(){

        if (commentEntity == null) return;

//        ownerUser.setText(commentEntity.getOwnerUser());
//		comment.setText(commentEntity.getComment());
//		ownerMessage.setText(commentEntity.getOwnerMessage());
//		toUser.setText(commentEntity.getToUser());
		



    }


}