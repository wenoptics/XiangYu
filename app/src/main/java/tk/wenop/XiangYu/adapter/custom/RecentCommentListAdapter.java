package tk.wenop.XiangYu.adapter.custom;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.HttpUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import tk.wenop.XiangYu.R;
import tk.wenop.XiangYu.bean.MessageEntity;
import tk.wenop.XiangYu.config.RouteConfig;
import tk.wenop.XiangYu.network.MessageNetwork;
import tk.wenop.XiangYu.ui.wenui.CommentActivity;
import tk.wenop.XiangYu.util.ImageLoadOptions;


public class RecentCommentListAdapter extends BaseAdapter implements
        AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener, MessageNetwork.OnGetOneMessageEntitiy {

    private List<JSONObject> allAreaEntity = new ArrayList<>();
    private List<JSONObject> filter_AreaEntity = new ArrayList<>();
    ImageLoader imageLoader;
    HttpUtils http = new HttpUtils();


    MessageNetwork.OnGetOneMessageEntitiy onGetOneMessageEntitiy;



    public void putDatas(List<JSONObject> allMessage){

        if (allMessage == null) return;
        if (allMessage.size() < 0) return;

        if (allMessage.size()<=allAreaEntity.size()) {
            filter_AreaEntity.addAll(allAreaEntity);
            notifyDataSetChanged();
            return;
        }else if (allMessage.size() > allAreaEntity.size()){
            allAreaEntity.clear();
            allAreaEntity.addAll(allMessage);
            filter_AreaEntity.clear();
            filter_AreaEntity.addAll(allMessage);
        }

        notifyDataSetChanged();
    }

    Context context;

    public RecentCommentListAdapter(Context context){
        this.context = context;
        imageLoader = ImageLoader.getInstance();
        onGetOneMessageEntitiy = this;

    }

    @Override
    public int getCount() {
        return filter_AreaEntity.size();
    }

    @Override
    public Object getItem(int position) {
        return filter_AreaEntity.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final JSONObject  jsonObject = (JSONObject)getItem(position);
        final RecentCommentHolder recentCommentHolder;

        if(convertView == null){
            recentCommentHolder = new RecentCommentHolder();
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.item_recent_comment_list, parent, false);
            recentCommentHolder.content = (TextView) convertView.findViewById(R.id.tv_content);
            recentCommentHolder.avatar = (ImageView) convertView.findViewById(R.id.iv_avatar);

            convertView.setTag(recentCommentHolder);
        }else {
            recentCommentHolder = (RecentCommentHolder) convertView.getTag();
        }

        String username = jsonObject.optString(RouteConfig.FROM_NICK_NAME);
        String fromAvatar = jsonObject.optString(RouteConfig.FROM_AVATAR_URL);
        recentCommentHolder.content.setText(String.format("%s@了你", username));
        refreshAvatar(fromAvatar, recentCommentHolder.avatar);

        final String message = jsonObject.optString(RouteConfig.OWN_MESSAGE_ID);


        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MessageNetwork.loadOneMessage(context, onGetOneMessageEntitiy, message);
            }
        });

        return convertView;
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
    public void onGetMessageEntity(MessageEntity messageEntity) {

        Intent intent = new Intent(context, CommentActivity.class);
        CommentActivity.messageEntity = messageEntity;
        context.startActivity(intent);

    }



    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {



    }

    public void delete(final int pos){


    }


    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {



        return false;
    }



    public static class RecentCommentHolder {

        public TextView content;
        public ImageView avatar;

    }

    public static class ClickHolder{
        int position;
        TextView numberTextView;
    }


    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        filter_AreaEntity.clear();
        if (charText.length() == 0) {
            filter_AreaEntity.addAll(allAreaEntity);
        }
        else
        {
        }

    }


        View.OnClickListener buttonListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            ClickHolder clickHolder = (ClickHolder) v.getTag();
             final int position =  clickHolder.position;
            }
        };

    }
    