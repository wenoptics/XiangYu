package tk.wenop.XiangYu.network;

import android.content.Context;
import android.widget.Toast;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import tk.wenop.XiangYu.bean.CommentEntity;
import tk.wenop.XiangYu.bean.MessageEntity;

public class CommentNetwork {
    public static void save(final Context context,CommentEntity commentEntity){

        commentEntity.save(context,new SaveListener() {
                @Override
                public void onSuccess() {
                    Toast.makeText(context, "save success", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int i, String s) {
                    Toast.makeText(context,"save failure",Toast.LENGTH_SHORT).show();
                }
            });
    }

    public static void loadComment(final Context context, final OnGetCommentEntities onGetCommentEntities,MessageEntity messageEntity){

        BmobQuery<CommentEntity> query = new BmobQuery<>();
        query.include("ownerUser,owerComment.ownerUser,toUser");
        query.addWhereEqualTo("ownerMessage", messageEntity);

//        BmobQuery<CommentEntity> query2 = new BmobQuery<>();
//        query2.addWhereEqualTo("owerComment.ownerMessage", messageEntity);
//
//        List<BmobQuery<CommentEntity>> queries = new ArrayList<BmobQuery<CommentEntity>>();
//        queries.add(query);
//        queries.add(query2);


//
//        BmobQuery<CommentEntity> mainQuery = new BmobQuery<CommentEntity>();
//        mainQuery.or(queries);
//        mainQuery.include("ownerUser,owerComment.ownerUser,ownerMessage");

        query.findObjects(context,new FindListener<CommentEntity>(){
            @Override
            public void onSuccess(List<CommentEntity> commentEntities) {
                onGetCommentEntities.onGetCommentEntities(commentEntities);
            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(context,"get data failure",Toast.LENGTH_SHORT).show();
            }
        });


    }

    public interface  OnGetCommentEntities{

        public void onGetCommentEntities(List<CommentEntity> allCommentEntities);
    }


} 