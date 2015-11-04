package tk.wenop.XiangYu.adapter.custom;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
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

    ImageLoader imageLoader;
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View mView;

        public TextView  mNickName;
        public TextView  mAudioTimeSec;
        public ImageView mAvatar;
        public TextView  mTime;

        public RelativeLayout audio_msg_bubble;
        public ImageView audio_animation;

        public ViewHolder(View v) {
            super(v);
            mView = v;

            mNickName      = (TextView)  v.findViewById(R.id.tv_nickName);
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
    public CommentAdapter(Context context){

        mContext = context;
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

    // 点击头像要跳转到用户详情页
    private final View.OnClickListener onAvatarClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(mContext, PeopleDetailActivity.class);
            mContext.startActivity(intent);
        }
    };

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
        }

        holder.mAvatar.setOnClickListener(onAvatarClickListener);

        // TODO 长按头像可以at人     holder.mAvatar.setOnLongClickListener(null);
        String path = "http://file.bmob.cn/" + data.getComment();
        holder.audio_msg_bubble.setOnClickListener(new NewRecordPlayClickListener(mContext, path, holder.audio_animation));

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
}