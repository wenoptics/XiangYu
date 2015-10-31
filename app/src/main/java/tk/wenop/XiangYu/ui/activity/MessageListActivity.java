package tk.wenop.XiangYu.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.baoyz.widget.PullRefreshLayout;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import de.greenrobot.event.EventBus;
import tk.wenop.XiangYu.R;
import tk.wenop.XiangYu.adapter.custom.MessageListAdapter;
import tk.wenop.XiangYu.event.ConstantEvent;
import tk.wenop.XiangYu.manager.DBManager;
import tk.wenop.XiangYu.ui.dialog.SelectAddressDialog;

/**
 * Created by zysd on 15/10/22.
 */
public class MessageListActivity extends Activity implements View.OnClickListener, SelectAddressDialog.OnGetAddressResult {


    @ViewInject(R.id.message_list)
    ListView message_list;
    @ViewInject(R.id.message_add)
    Button message_add;
    @ViewInject(R.id.address)
    Button address;



    @ViewInject(R.id.swipeRefreshLayout)
    PullRefreshLayout swipeRefreshLayout;

    DBManager dbManager;
    MessageListAdapter messageListAdapter;
    SelectAddressDialog selectAddressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);
        ViewUtils.inject(this);
        EventBus.getDefault().register(this);
        dbManager = DBManager.instance(this);
        initList();
        message_add.setOnClickListener(this);
        //下拉刷新;
        swipeRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                dbManager.getAllMessageEntities();
            }
        });
        selectAddressDialog = new SelectAddressDialog(this,this);
        address.setOnClickListener(this);

    }


    public void initList(){

        messageListAdapter = new MessageListAdapter(this);
        messageListAdapter.putMessageEntity(dbManager.getAllMessageEntities());
        message_list.setAdapter(messageListAdapter);

    }


    public void onEventMainThread(ConstantEvent event){

        swipeRefreshLayout.setRefreshing(false);
        switch (event){
            case MESSAGE_LOAD:
                messageListAdapter.putMessageEntity(dbManager.getAllMessageEntities());
                break;

            case MESSAGE_REFRESH:
                messageListAdapter.putMessageEntity(dbManager.getAllMessageEntities());
                break;

        }

    }


    @Override
    public void onClick(View v) {

        if (v.getId() == message_add.getId()){

            Intent intent = new Intent(MessageListActivity.this,MessageCreateEditActivity.class);
            startActivity(intent);

        }else if (v.getId() == address.getId()){

            selectAddressDialog.show();
        }

    }


    @Override
    public void onGetResult(String province, String city, String district) {
        if (district != null){
            address.setText(district);
        }else if (city!= null){
            address.setText(city);
        }

    }

}
