package tk.wenop.XiangYu.adapter.custom;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
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

    private AtUserCallback mAtUserCallback;

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
        public TextView  mCallUser;

        public RelativeLayout audio_msg_bubble;
        public ImageView audio_animation;

        public ViewHolder(View v) {
            super(v);
            mView = v;

            mNickName      = (TextView)  v.findViewById(R.id.tv_nickName);
            mAudioTimeSec  = (TextView)  v.findViewById(R.id.textView_audioLength     );
            mAvatar        = (ImageView) v.findViewById(R.id.iv_comment_comment_item_avatar         );
            mTime          = (TextView)  v.findViewById(R.id.textView_audioLength           );
            mCallUser      = (TextView)  v.findViewById(R.id.textView_callUser            );

            //按气泡播放语音
            audio_msg_bubble = (RelativeLayout) v.findViewById(R.id.audio_msg_bubble_item_in_comment);
            //声音按钮
            audio_animation = (ImageView) v.findViewById(R.id.imageView2);
        }

    }


    public CommentAdapter(Context context, ArrayList<CommentEntity> myDataset){

        this(context);
        mDataset = myDataset;

    }
    public CommentAdapter(Context context){

        mContext = context;
        inflater = LayoutInflater.from(mContext);
        imageLoader = ImageLoader.getInstance();

        // wenop-add
        try {
            this.mAtUserCallback = ((AtUserCallback) context);
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement mAtUserCallback.");
        }
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

        return new ViewHolder(v);
    }


    private String setViewUserNickName(User user, TextView targetTV, Boolean isAnonymous) {
        String setStr = "";
        if (isAnonymous) {
            setStr = "匿名用户";
            // 昵称样式
            targetTV.setTypeface(null, Typeface.ITALIC);
            if (user.getSex() == true)
            {
                // 男
                targetTV
                    .setTextColor(ContextCompat.getColor(mContext,
                            R.color.anonymous_card_color_male));

            } else {
                // 女
                targetTV
                    .setTextColor(ContextCompat.getColor(mContext,
                            R.color.anonymous_card_color_female));
            }
        } else {
            setStr = user.getNick();
            targetTV.setTypeface(null, Typeface.NORMAL);
            targetTV
                    .setTextColor(ContextCompat.getColor(mContext,
                            R.color.base_color_text_black));
        }
        targetTV.setText(setStr);
        return setStr;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final CommentEntity data = mDataset.get(position);
//        Object data = mDataset.get(position);
//        holder.mAvatar.setImageResource(  );
//        holder.mNickName.setText(data.mNickName);

        final User user = data.getOwnerUser();
        if (user!=null) {

            Boolean isAnonymous = data.getAnonymous();
            if (isAnonymous==null) isAnonymous=false;

            // 设置用户名样式
            setViewUserNickName(user, holder.mNickName, isAnonymous);

            if (isAnonymous) {
                // 这条评论是匿名的

                // 匿名点击头像设置 点击事件为空（必须要有，不然view被recycle后重用会不一致）
                holder.mAvatar.setOnClickListener(null);

                // 消息的owner, 要根据性别设置样式
                if (data.getOwnerUser().getSex() == true)
                {
                    // 男
                    holder.mAvatar.setImageResource(R.drawable.avatar_a_m);
                    holder.mNickName
                            .setTextColor(ContextCompat.getColor(mContext,
                                    R.color.anonymous_card_color_male));

                } else {
                    // 女
                    holder.mAvatar.setImageResource(R.drawable.avatar_a_fm);
                    holder.mNickName
                            .setTextColor(ContextCompat.getColor(mContext,
                                    R.color.anonymous_card_color_female));
                }

            } else {

                // 这条评论不是匿名的

                refreshAvatar(data.getOwnerUser().getAvatar(), holder.mAvatar);

                holder.mAvatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(mContext, PeopleDetailActivity.class);
                        intent.putExtra("user", user);
                        mContext.startActivity(intent);
                    }
                });
            }

            // 长按头像可以at人
            final Boolean finalIsAnonymous = isAnonymous;
            holder.mAvatar.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    mAtUserCallback.onAtUserCallback(data.getOwnerUser(), finalIsAnonymous);
                    return true;
                }
            });
            User userBeingAt = data.getToUser();
            if (userBeingAt != null) {
                //有人被at
                String showName
                        = setViewUserNickName(userBeingAt, holder.mCallUser, data.getIsToUserAnonymous());
                holder.mCallUser.setText(String.format("@%s", showName));
            } else {
                // 没人被at
                holder.mCallUser.setText("");
            }

            String path = "http://file.bmob.cn/" + data.getComment();
            holder.audio_msg_bubble.setOnClickListener(new NewRecordPlayClickListener(mContext, path, holder.audio_animation));
            // 设置语音时长
            holder.mTime.setText(String.format("%d\'\'", data.getAudioLength()));
        }
    }

    public interface AtUserCallback {
        void onAtUserCallback(User toUser, Boolean isUserAnonymous);
    }

    private void refreshAvatar(String avatar, ImageView avatarView) {
        if (avatar != null && !avatar.equals("")) {
            ImageLoader.getInstance().displayImage(avatar, avatarView,
                    ImageLoadOptions.getOptions());
        } else {
            avatarView.setImageResource(R.drawable.default_head);
        }
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {

        // wenop-add: Cancel loading in case of error image display after reused.
        ImageLoader.getInstance().cancelDisplayTask(holder.mAvatar);
        super.onViewRecycled(holder);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}