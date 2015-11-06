package tk.wenop.XiangYu.adapter.custom;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import tk.wenop.XiangYu.R;
import tk.wenop.XiangYu.adapter.NewRecordPlayClickListener;
import tk.wenop.XiangYu.bean.CommentEntity;
import tk.wenop.XiangYu.bean.User;
import tk.wenop.XiangYu.ui.wenui.PeopleDetailActivity;
import tk.wenop.XiangYu.util.ImageLoadOptions;

//import tk.wenop.XiangYu.R;

//import tk.wenop.testapp.R;
//import tk.wenop.testapp.UI.PeopleDetailActivity;

/**
 * Created by wenop_000 on 2015/10/12.
 */
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private ArrayList<CommentEntity> mDataset = new ArrayList<>();
    protected Context mContext;
    OnAtSomeOne onAtSomeOne;


    ImageLoader imageLoader;
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View mView;

        public TextView  mNickName;
        public TextView textView_callUser;

        public TextView  mAudioTimeSec;
        public ImageView mAvatar;
        public TextView  mTime;

        public RelativeLayout audio_msg_bubble;
        public ImageView audio_animation;

        public ViewHolder(View v) {
            super(v);
            mView = v;

            mNickName      = (TextView)  v.findViewById(R.id.tv_nickName);
            textView_callUser = (TextView) v.findViewById(R.id.textView_callUser);
            mAudioTimeSec  = (TextView)  v.findViewById(R.id.textView_audioLength     );
            mAvatar        = (ImageView) v.findViewById(R.id.iv_comment_comment_item_avatar         );
            mTime          = (TextView)  v.findViewById(R.id.textView_time            );




            //按气泡播放语音
            audio_msg_bubble = (RelativeLayout) v.findViewById(R.id.audio_msg_bubble_item_in_comment);
            //声音按钮
            audio_animation = (ImageView) v.findViewById(R.id.imageView2);
        }

    }


    public CommentAdapter(Context context,ArrayList<CommentEntity> myDataset){

        mDataset = myDataset;
        mContext = context;

        inflater = LayoutInflater.from(mContext);
    }
    public CommentAdapter(Context context,OnAtSomeOne onAtSomeOne){

        mContext = context;
        this.onAtSomeOne = onAtSomeOne;
        inflater = LayoutInflater.from(mContext);
        imageLoader = ImageLoader.getInstance();
    }

    public void putDataSet(List<CommentEntity> myDataset){

        if (myDataset == null) return;
        mDataset.clear();
        mDataset.addAll(myDataset);
        notifyDataSetChanged();
    }

    public void addData(CommentEntity commentEntity){
        if (commentEntity == null) return;
        mDataset.add(commentEntity);
        notifyDataSetChanged();
    }



    // Create new views (invoked by the layout manager)
    @Override
    public CommentAdapter.ViewHolder onCreateViewHolder(final ViewGroup viewGroup,
                                                   int viewType) {
        // create a new view
        View v = inflater.inflate(R.layout.item_comment_list__comment, viewGroup, false);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: onClick在这里
            }
        });

        return new ViewHolder(v);
    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final CommentEntity data = mDataset.get(position);
        // TODO wenop 把对应数据取出来, 然后设置view
//        Object data = mDataset.get(position);
//        holder.mAvatar.setImageResource(  );
//        holder.mNickName.setText(data.mNickName);

        User user = data.getOwnerUser();
        if (user!=null) {
            refreshAvatar(data.getOwnerUser().getAvatar(), holder.mAvatar);
            holder.mNickName.setText(user.getUsername());
        }
        if (data.getOwerComment()!=null){
            holder.textView_callUser.setText("@"+data.getOwerComment().getOwnerUser().getUsername());
        }


        holder.mAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, PeopleDetailActivity.class);
                intent.putExtra("user", data.getOwnerUser());
                mContext.startActivity(intent);
            }
        });


        String path = "http://file.bmob.cn/" + data.getComment();
        holder.audio_msg_bubble.setOnClickListener(new NewRecordPlayClickListener(mContext, path, holder.audio_animation));

        // TODO 长按头像可以at人     holder.mAvatar.setOnLongClickListener(null);
        holder.mAvatar.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onAtSomeOne.onAtSomeOne(data);

                return false;
            }
        });
    }

    public interface OnAtSomeOne{

        public void onAtSomeOne(CommentEntity commentEntity);


    }



    private void refreshAvatar(String avatar, ImageView avatarView) {
        if (avatar != null && !avatar.equals("")) {
            ImageLoader.getInstance().displayImage(avatar, avatarView,
                    ImageLoadOptions.getOptions());
        } else {
            avatarView.setImageResource(R.drawable.default_head);
        }
    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }





    //todo: 识别用户点击头像的动作,思路如下
    /*
            1:区别开用户单击时间而不影响计算录音的计时;(首次点击事计时开始，但是还不妨碍时间短时的单击时间触发跳转到用户详情页面)
            2:通过接口将adapter avatar 获取的点击时间传递到activity,处理
     */

    class VoiceTouchListen implements View.OnTouchListener {

        public VoiceTouchListen(){}

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {


                case MotionEvent.ACTION_DOWN:

                    return true;
                case MotionEvent.ACTION_MOVE: {

                    return true;
                }
                case MotionEvent.ACTION_UP:

                default:
                    return false;
            }
        }
    }



    class MyGestureListener extends GestureDetector.SimpleOnGestureListener{




    }















}