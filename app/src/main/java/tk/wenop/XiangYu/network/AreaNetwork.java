package tk.wenop.XiangYu.network;

import android.content.Context;
import android.widget.Toast;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import tk.wenop.XiangYu.bean.AreaEntity;
import tk.wenop.XiangYu.bean.User;

public class AreaNetwork {
    public static void save(final Context context,AreaEntity entity){

        entity.save(context,new SaveListener() {
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

    public static void loadAreas(final Context context, final OnGetAreaEntities onGetAreaEntities,AreaEntity areaEntity){

        BmobQuery<AreaEntity> query = new BmobQuery<>();
        query.findObjects(context, new FindListener<AreaEntity>() {
            @Override
            public void onSuccess(List<AreaEntity> commentEntities) {
                onGetAreaEntities.onGetAreaEntities(commentEntities);
            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(context, "get data failure", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public interface  OnGetAreaEntities{

        public void onGetAreaEntities(List<AreaEntity> allCommentEntities);
    }


    /*
       查找指定的位置信息
     */


    public static void loadArea(final Context context, final OnGetAreaEntity onGetAreaEntity, final String area){

        BmobQuery<AreaEntity> query = new BmobQuery<>();
        query.addWhereEqualTo("area",area);
        query.findObjects(context,new FindListener<AreaEntity>(){
            @Override
            public void onSuccess(List<AreaEntity> areaEntities) {
                if (areaEntities.size()>=1){
                    onGetAreaEntity.onGetAreaEntity(areaEntities.get(0));
                }else {


                    final AreaEntity areaEntity = new AreaEntity();
                    areaEntity.setArea(area);
                    areaEntity.save(context, new SaveListener() {
                        @Override
                        public void onSuccess() {
                            onGetAreaEntity.onGetAreaEntity(areaEntity);
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            onGetAreaEntity.onGetAreaEntity(null);
                        }
                    });
                }


            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(context,"get data failure",Toast.LENGTH_SHORT).show();

                /*
                    如果没有查找到就新建一个位置信息
                 */
                AreaEntity areaEntity = new AreaEntity();
                areaEntity.setArea(area);
                areaEntity.save(context, new SaveListener() {
                    @Override
                    public void onSuccess() {
                        final AreaEntity areaEntity = new AreaEntity();
                        areaEntity.setArea(area);
                        areaEntity.save(context, new SaveListener() {
                            @Override
                            public void onSuccess() {
                                onGetAreaEntity.onGetAreaEntity(areaEntity);
                            }

                            @Override
                            public void onFailure(int i, String s) {
                                onGetAreaEntity.onGetAreaEntity(null);
                            }
                        });
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        onGetAreaEntity.onGetAreaEntity(null);
                    }
                });

            }
        });
    }

    public interface  OnGetAreaEntity{
        public void onGetAreaEntity(AreaEntity  areaEntity);
    }



    public static void loadFollowAreas(final Context context, final OnGetFollowAreaEntities onGetAreaEntities,User user){

        BmobQuery<AreaEntity> query = new BmobQuery<>();

        query.addWhereRelatedTo("relation",new BmobPointer(user));

        query.findObjects(context, new FindListener<AreaEntity>() {
            @Override
            public void onSuccess(List<AreaEntity> areaEntities) {
                onGetAreaEntities.onGetFollowAreaEntities(areaEntities);
            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(context, "get data failure", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public interface  OnGetFollowAreaEntities{

        public void onGetFollowAreaEntities(List<AreaEntity> areaEntities);

    }






} 