package tk.wenop.XiangYu.adapter.custom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lidroid.xutils.HttpUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import tk.wenop.XiangYu.R;
import tk.wenop.XiangYu.bean.AreaEntity;

public class AreaListAdapter extends BaseAdapter implements
        AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener{

    private List<AreaEntity> allAreaEntity = new ArrayList<>();
    private List<AreaEntity> filter_AreaEntity = new ArrayList<>();
//    DBManager dbManager = DBManager.getInstance();
    ImageLoader imageLoader;
    HttpUtils http = new HttpUtils();



    public void putAreaEntity(List<AreaEntity> allMessage){

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

    public AreaListAdapter(Context context){
        this.context = context;
        imageLoader = ImageLoader.getInstance();

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

        final AreaEntity  areaEntity = (AreaEntity)getItem(position);
        final AreaHolder areaHolder;

        if(convertView == null){
            areaHolder = new AreaHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_area_list,parent,false);

            convertView.setTag(areaHolder);
        }else {
            areaHolder = (AreaHolder) convertView.getTag();
        }

        if (areaEntity.getArea()!=null)
        areaHolder.area.setText(areaEntity.getArea());

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
        putAreaEntity(filter_AreaEntity);
    }


        View.OnClickListener buttonListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            ClickHolder clickHolder = (ClickHolder) v.getTag();
             final int position =  clickHolder.position;

            }
        };
    }
    