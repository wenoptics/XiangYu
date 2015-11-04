package tk.wenop.XiangYu.adapter.custom;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;
import java.util.List;

import tk.wenop.XiangYu.R;
import tk.wenop.XiangYu.adapter.NewRecordPlayClickListener;
import tk.wenop.XiangYu.bean.AreaEntity;
import tk.wenop.XiangYu.bean.MessageEntity;
import tk.wenop.XiangYu.ui.wenui.CommentActivity;
import tk.wenop.XiangYu.ui.wenui.PeopleDetailActivity;
import tk.wenop.XiangYu.util.ImageLoadOptions;

//import tk.wenop.testapp.Overview.MainScreenOverviewItem;
//import tk.wenop.testapp.R;
//import tk.wenop.testapp.UI.CommentActivity;
//import tk.wenop.testapp.UI.PeopleDetailActivity;

/**
 * Created by wenop_000 on 2015/10/12.
 */
public class MainScreenChatAdapter extends RecyclerView.Adapter<MainScreenChatAdapter.ViewHolder> {


    private List<MessageEntity> mDataset = new ArrayList<>();
    ImageLoader imageLoader;
//    private ArrayList<MainScreenOverviewItem> mDataset;
    protected Context mContext;
    private int lastPosition = -1;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View mView;

        public TextView  mNickName;
        public TextView  mAudioTimeSec;
        public ImageView mAvatar;
        public ImageView mContentPhoto;
        public RelativeLayout audio_msg_bubble;
        public ImageView audio_animation;
        public TextView  mLocation;
        public TextView  mCommentCount;
        public TextView  mTime;
        public View card_root_view;


        public ViewHolder(View v) {
            super(v);
            mView = v;

            card_root_view = v.findViewById(R.id.card_root_view);
            mNickName      = (TextView)  v.findViewById(R.id.tv_nickName);
            mAudioTimeSec  = (TextView)  v.findViewById(R.id.textView_audioLength     );
            mAvatar        = (ImageView) v.findViewById(R.id.imageView_avatar        );
            mContentPhoto  = (ImageView) v.findViewById(R.id.imageView_contentPhoto  );
            //按气泡播放语音
            audio_msg_bubble = (RelativeLayout) v.findViewById(R.id.audio_msg_bubble);
            //声音按钮
            audio_animation = (ImageView) v.findViewById(R.id.imageView2);

            mLocation      = (TextView)  v.findViewById(R.id.textView_location        );
            mCommentCount  = (TextView)  v.findViewById(R.id.textView_commentCount    );
            mTime          = (TextView)  v.findViewById(R.id.textView_time            );

        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MainScreenChatAdapter(Context context,  List<MessageEntity> myDataset) {
        mDataset.addAll(myDataset);
        mContext = context;
        imageLoader = ImageLoader.getInstance();

        fadeIn = new AlphaAnimation(0, 0.8f);
        fadeIn.setInterpolator(new LinearInterpolator()); //add this
        fadeIn.setDuration(500);
    }


    public void putDataSet(List<MessageEntity> myDataset){

        mDataset.clear();
        mDataset.addAll(myDataset);
        notifyDataSetChanged();

    }


    // Create new views (invoked by the layout manager)
    @Override
    public MainScreenChatAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent,
                                                   int viewType) {
        // create a new view base on type(MessageEntity msgType)
        View v;
        switch (viewType) {
            case MessageEntity.MSG_TYPE_ONLY_PHOTO:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_received_list_photo__main_screen_chat, parent, false);
                break;
            case MessageEntity.MSG_TYPE_ONLY_AUDIO:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_received_list_audio__main_screen_chat, parent, false);
                break;
            case MessageEntity.MSG_TYPE_AUDIO_wITH_PHOTO:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_received_list_both__main_screen_chat, parent, false);
                break;
            default:
                // TODO !!! debug here , should be deleted later. (wenop)
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_received_list_both__main_screen_chat, parent, false);

        }

        return new ViewHolder(v);
    }

    Animation fadeIn;

    private void setContentAudio(MessageEntity data, ViewHolder holder) {
        String path = "http://file.bmob.cn/" + data.getAudio();
        holder.audio_msg_bubble.setOnClickListener(
                new NewRecordPlayClickListener(mContext, path, holder.audio_animation));
    }

    private void setContentPhoto(MessageEntity data, ViewHolder holder) {
        imageLoader.displayImage("http://file.bmob.cn/" + data.getImage(),
                holder.mContentPhoto,
                DisplayImageOptions.createSimple(),
                new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String s, View view) {
                    }

                    @Override
                    public void onLoadingFailed(String s, View view, FailReason failReason) {

                    }

                    @Override
                    public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                        view.startAnimation(fadeIn);
                    }

                    @Override
                    public void onLoadingCancelled(String s, View view) {

                    }
                });
    }

    // add by wenop: item加载的动态效果， 防止item闪现
    private void beforeDataShown(ViewHolder holder, int position) {
//        holder.mView.setVisibility(View.INVISIBLE);
        setAnimation(holder.mView, position);
    }

    private void afterDataShown(ViewHolder holder) {

    }

    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
        if(holder.mContentPhoto!=null)
        {
            holder.mContentPhoto.setImageDrawable(null);
        }
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        beforeDataShown(holder, position);

        final MessageEntity data = mDataset.get(position);
//        holder.mAvatar.setImageResource();

        AreaEntity areaEntity = data.getOwnerArea();
        if (areaEntity!=null)
        if (areaEntity.getArea()!=null){
            holder.mLocation.setText(data.getOwnerArea().getArea());
        }

//        Time time =  Time.valueOf(data.getCreatedAt());
        String s =data.getCreatedAt().substring(11,19);
        holder.mTime.setText(s);

        if (data.getCommentCount()!=null){
            holder.mCommentCount.setText(String.valueOf(data.getCommentCount()));
        }

        switch (data.getMsgType()) {
            case MessageEntity.MSG_TYPE_AUDIO_wITH_PHOTO:
                setContentAudio(data, holder);
                setContentPhoto(data, holder);
                break;
            case MessageEntity.MSG_TYPE_ONLY_AUDIO:
                setContentAudio(data, holder);
                break;
            case MessageEntity.MSG_TYPE_ONLY_PHOTO:
                setContentPhoto(data, holder);
                break;
            default:
                /// TODO!!! debug here, should delete later (wenop)
                setContentAudio(data, holder);
                setContentPhoto(data, holder);
                /// TODO !!! end
        }

        if (data.getOwnerUser()!=null) {

            if (data.getAnonymous() == true) {
                // 匿名消息

                holder.mNickName.setText("匿名用户");
                // 昵称样式
                holder.mNickName.setTypeface(null, Typeface.ITALIC);

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
//                    holder.card_root_view
//                            .setBackgroundColor(ContextCompat.getColor(mContext,
//                                    R.color.anonymous_card_color_male));

                } else {
                    // 女
                    holder.mAvatar.setImageResource(R.drawable.avatar_a_fm);
                    holder.mNickName
                            .setTextColor(ContextCompat.getColor(mContext,
                                    R.color.anonymous_card_color_female));
//                    holder.card_root_view
//                            .setBackgroundColor(ContextCompat.getColor(mContext,
//                                    R.color.anonymous_card_color_female));
                }
            }
            else {

                // 非匿名消息
                holder.mNickName.setText(data.getOwnerUser().getNick());
                holder.mNickName.setTypeface(null, Typeface.NORMAL);
                holder.mNickName
                        .setTextColor(ContextCompat.getColor(mContext,
                                R.color.base_color_text_black));
//                imageLoader.displayImage(data.getOwnerUser().getAvatar(), holder.mAvatar);
                refreshAvatar(data.getOwnerUser().getAvatar(), holder.mAvatar);

                // 点击头像去到用户详情
                holder.mAvatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, PeopleDetailActivity.class);
                        intent.putExtra("user", data.getOwnerUser());
                        mContext.startActivity(intent);
                    }
                });

                // 消息的owner, 要根据性别设置样式
                if (data.getOwnerUser().getSex() == true)
                {
                    // 男
                    holder.card_root_view
                            .setBackgroundColor(ContextCompat.getColor(mContext,
                                    R.color.normal_card_color_male));

                } else {
                    // 女
                    holder.card_root_view
                            .setBackgroundColor(ContextCompat.getColor(mContext,
                                    R.color.normal_card_color_female));
                }
            }
        }

        //评论按钮
        holder.mView.findViewById(R.id.group_comment)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        gotoComment(data);
                    }
                });

        holder.mLocation.setText(data.getFromLocation());

        // wenop-add
        // 点击view 去到消息 (也去到评论页面)
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoComment(data);
            }
        });

        afterDataShown(holder);
    }

    private void gotoComment(MessageEntity data) {
        Intent intent = new Intent(mContext, CommentActivity.class);
        CommentActivity.messageEntity = data;
        mContext.startActivity(intent);
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

    // wenop-add : determine what kind of View will be used
    @Override
    public int getItemViewType(int position) {
        return mDataset.get(position).getMsgType();
    }
}