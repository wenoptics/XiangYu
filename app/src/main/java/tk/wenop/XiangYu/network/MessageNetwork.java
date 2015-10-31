package tk.wenop.XiangYu.network;

import android.content.Context;
import android.widget.Toast;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import tk.wenop.XiangYu.bean.MessageEntity;
import tk.wenop.XiangYu.manager.DBManager;

public class MessageNetwork {
    public static void save(final Context context, final MessageEntity messageEntity){

        messageEntity.save(context,new SaveListener() {
                @Override
                public void onSuccess() {
                    Toast.makeText(context, "save success", Toast.LENGTH_SHORT).show();

                    /*
                        网络存储成功后更新本地的数据
                     */

                    DBManager.instance(context).add2MessageEntities(messageEntity);

                }

                @Override
                public void onFailure(int i, String s) {
                    Toast.makeText(context,"save failure",Toast.LENGTH_SHORT).show();
                }
            });
    }

    public static void loadMessage(final Context context, final OnGetMessageEntities onGetMessageEntities){

        BmobQuery<MessageEntity> query = new BmobQuery<>();
        query.include("ownerUser");
        query.findObjects(context,new FindListener<MessageEntity>(){
            @Override
            public void onSuccess(List<MessageEntity> messageEntities) {
                onGetMessageEntities.onGetMessageEntities(messageEntities);
            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(context,"get data failure",Toast.LENGTH_SHORT).show();
            }
        });


    }

    public interface  OnGetMessageEntities{

        public void onGetMessageEntities(List<MessageEntity> allMessageEntities);
    }


} 