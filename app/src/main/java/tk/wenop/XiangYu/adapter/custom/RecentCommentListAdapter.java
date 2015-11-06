package tk.wenop.XiangYu.adapter.custom;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
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
        final AreaHolder areaHolder;

        if(convertView == null){
            areaHolder = new AreaHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_area_list,parent,false);
            areaHolder.area = (TextView) convertView.findViewById(R.id.area);

            convertView.setTag(areaHolder);
        }else {
            areaHolder = (AreaHolder) convertView.getTag();
        }

        String usermame = jsonObject.optString(RouteConfig.FROM_USER_NAME);
        areaHolder.area.setText(usermame);


        final String message = jsonObject.optString(RouteConfig.OWN_MESSAGE_ID);


        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MessageNetwork.loadOneMessage(context, onGetOneMessageEntitiy, message);
            }
        });

        return convertView;
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



    public static class AreaHolder{

        public TextView  area;

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
    