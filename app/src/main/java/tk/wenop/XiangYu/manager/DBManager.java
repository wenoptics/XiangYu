package tk.wenop.XiangYu.manager;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;
import tk.wenop.XiangYu.bean.AreaEntity;
import tk.wenop.XiangYu.bean.CommentEntity;
import tk.wenop.XiangYu.bean.MessageEntity;
import tk.wenop.XiangYu.event.ConstantEvent;
import tk.wenop.XiangYu.network.CommentNetwork;
import tk.wenop.XiangYu.network.MessageNetwork;

/**
 * Created by zysd on 15/10/22.
 */
public class DBManager implements MessageNetwork.OnGetMessageEntities {

    private static Context mContext;
    private static DBManager dbManager = null;
    private DBManager(Context context){
        mContext = context;
    }

    public static DBManager instance(Context context){


        if (dbManager != null){
            return dbManager;
        }else {
            mContext = context;
            return new DBManager(context);
        }

    }

    private static List<MessageEntity> allMessageEntities = new ArrayList<>();
//    private static HashMap<String,List<CommentEntity>> messageId2CommentListMap = new HashMap<>();
    private static Map<String,ArrayList<CommentEntity>> messageId2CommentListMap = new HashMap<String,ArrayList<CommentEntity>>();

    public void initNetLogin(){
        //todo:开机定位，初始化位置后调用此方法并把位置传进来
//        MessageNetwork.loadMessage(mContext,this,areaEntity);

    }


    public void refreshMessageEntities(AreaEntity areaEntity){
        MessageNetwork.loadMessage(mContext,this,areaEntity);
    }

    @Override
    public void onGetMessageEntities(List<MessageEntity> allMessageEntities) {
            this.allMessageEntities.clear();
            this.allMessageEntities.addAll(allMessageEntities);

        EventBus.getDefault().post(ConstantEvent.MESSAGE_LOAD);
    }

    public List<MessageEntity> getAllMessageEntities() {
        return allMessageEntities;
    }

    public void setAllMessageEntities(List<MessageEntity> allMessageEntities) {
        this.allMessageEntities = allMessageEntities;
    }

    public void add2MessageEntities(MessageEntity entity){
        this.allMessageEntities.add(entity);
        EventBus.getDefault().postSticky(ConstantEvent.MESSAGE_REFRESH);
    }


    /*
        todo: 1:如何映射单个messageID to 多个CommentEntitty
        todo: 2:将数据提交到云端并通知其他用户
     */
    public void add2Message2CommentMap(CommentEntity commentEntity){


        if (commentEntity == null) return;
        String str = commentEntity.getOwnerMessage().getObjectId();
        if (messageId2CommentListMap.containsKey(str)){
            ArrayList<CommentEntity> commentEntities = messageId2CommentListMap.get(str);
            commentEntities.add(commentEntity);
            messageId2CommentListMap.put(str,commentEntities);

        }else {

            ArrayList<CommentEntity> commentEntities =new ArrayList<>();
            commentEntities.add(commentEntity);
            messageId2CommentListMap.put(str,commentEntities);

        }


    }

    /*

        todo;从网络中加载都的CommentEntity 应该先存在DBManager里然后返回 通过enentbus  通知其他地方在这里加载数据;

     */
    public void getComments(MessageEntity messageEntity,CommentNetwork.OnGetCommentEntities onGetCommentEntities){

        if (messageId2CommentListMap.containsKey(messageEntity.getObjectId()))
        {
            onGetCommentEntities.onGetCommentEntities(messageId2CommentListMap.get(messageEntity.getObjectId()));
        }
        else
        {
            CommentNetwork.loadComment(mContext,onGetCommentEntities,messageEntity);
//            .instance(mContext).getComments(messageEntity,onGetCommentEntities);
        }

    }




}
