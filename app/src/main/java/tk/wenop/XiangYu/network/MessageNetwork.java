package tk.wenop.XiangYu.network;

import android.content.Context;
import android.widget.Toast;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetListener;
import cn.bmob.v3.listener.SaveListener;
import tk.wenop.XiangYu.bean.AreaEntity;
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

    public static void loadMessage(final Context context, final OnGetMessageEntities onGetMessageEntities,AreaEntity areaEntity){

        BmobQuery<MessageEntity> query = new BmobQuery<>();
        query.addWhereEqualTo("ownerArea",areaEntity);
        query.include("ownerUser,ownerArea");
//        query.include("");
        query.findObjects(context, new FindListener<MessageEntity>() {
            @Override
            public void onSuccess(List<MessageEntity> messageEntities) {
                onGetMessageEntities.onGetMessageEntities(messageEntities);
            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(context, "get data failure", Toast.LENGTH_SHORT).show();
            }
        });
     }

    public interface  OnGetMessageEntities{
        public void onGetMessageEntities(List<MessageEntity> allMessageEntities);
    }


    public static void loadOneMessage(final Context context, final OnGetOneMessageEntitiy onGetOneMessageEntity,String objectID){

        BmobQuery<MessageEntity> query = new BmobQuery<>();
        query.getObject(context, objectID, new GetListener<MessageEntity>() {
            @Override
            public void onSuccess(MessageEntity messageEntity) {
                onGetOneMessageEntity.onGetMessageEntity(messageEntity);
            }

            @Override
            public void onFailure(int i, String s) {

            }
        });
    }

    public interface  OnGetOneMessageEntitiy{
        public void onGetMessageEntity(MessageEntity messageEntity);
    }






} 