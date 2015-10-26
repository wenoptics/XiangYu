package tk.wenop.XiangYu.adapter.custom;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.HttpUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import tk.wenop.XiangYu.R;
import tk.wenop.XiangYu.adapter.NewRecordPlayClickListener;
import tk.wenop.XiangYu.bean.MessageEntity;
import tk.wenop.XiangYu.ui.activity.CommentCreateEditActivity;

public class MessageListAdapter extends BaseAdapter implements
        AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener{

    private List<MessageEntity> allMessageEntity = new ArrayList<>();
    private List<MessageEntity> filter_MessageEntity = new ArrayList<>();
//    DBManager dbManager = DBManager.getInstance();
    ImageLoader imageLoader;
    HttpUtils http = new HttpUtils();



    public void putMessageEntity(List<MessageEntity> allMessage){

        if (allMessage == null) return;
        if (allMessage.size() < 0) return;

        if (allMessage.size()<=allMessageEntity.size()) {
            filter_MessageEntity.addAll(allMessageEntity);
            notifyDataSetChanged();
            return;
        }else if (allMessage.size() > allMessageEntity.size()){
            allMessageEntity.clear();
            allMessageEntity.addAll(allMessage);
            filter_MessageEntity.clear();
            filter_MessageEntity.addAll(allMessage);
        }

        notifyDataSetChanged();
    }

    Context context;

    public MessageListAdapter(Context context){
        this.context = context;
        imageLoader = ImageLoader.getInstance();

    }

    @Override
    public int getCount() {
        return filter_MessageEntity.size();
    }

    @Override
    public Object getItem(int position) {
        return filter_MessageEntity.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final MessageEntity  messageEntity = (MessageEntity)getItem(position);
        final MessageHolder messageHolder;

        if(convertView == null){
            messageHolder = new MessageHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_message_list,parent,false);

            messageHolder.audio = (ImageView) convertView.findViewById(R.id.audio);
			messageHolder.image = (ImageView) convertView.findViewById(R.id.image);
            messageHolder.item_action_comment = (TextView) convertView.findViewById(R.id.item_action_comment);
//            messageHolder.comment = (EditText) convertView.findViewById(R.id.comment);


            convertView.setTag(messageHolder);
        }else {
            messageHolder = (MessageHolder) convertView.getTag();
        }

		imageLoader.displayImage("http://file.bmob.cn/" + messageEntity.getImage(), messageHolder.image);
        String path = "http://file.bmob.cn/" + messageEntity.getAudio();
        messageHolder.audio.setOnClickListener(new NewRecordPlayClickListener(context,path, messageHolder.audio));


        messageHolder.item_action_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent  intent = new Intent(context, CommentCreateEditActivity.class);
                CommentCreateEditActivity.messageEntity = messageEntity;
                context.startActivity(intent);

            }
        });


        //if (dbManager.messageEntityCarFileHashMap.containsKey(messageEntity.getObjectId())){
        //    CarFile file = dbManager.carEntityCarFileHashMap.get(carEntity.getObjectId());
        //    file.getFile().loadImage(context,carHolder.imageView);
        //}


        /*
            查看项目详情
         */
//        convertView.setClickable(true);
//        convertView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(context, MessageViewActivity.class);
//                MessageViewActivity.messageEntity = messageEntity;
//                context.startActivity(intent);
//            }
//        });




        return convertView;
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

    public static class MessageHolder{


        public ImageView image;
        public ImageView audio;
        public EditText comment;
        public TextView item_action_comment;

    }

    public static class ClickHolder{
        int position;
        TextView numberTextView;
    }


    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        filter_MessageEntity.clear();
        if (charText.length() == 0) {
            filter_MessageEntity.addAll(allMessageEntity);
        }
        else
        {
//            for (MessageEntity wp : allMessageEntity)
//            {
//                if (wp.getMessageType().toLowerCase(Locale.getDefault()).contains(charText))
//                {
//                    filter_MessageEntity.add(wp);
//                }
//            }
        }
        putMessageEntity(filter_MessageEntity);
    }


        View.OnClickListener buttonListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            ClickHolder clickHolder = (ClickHolder) v.getTag();
             final int position =  clickHolder.position;

            }
        };
    }
    