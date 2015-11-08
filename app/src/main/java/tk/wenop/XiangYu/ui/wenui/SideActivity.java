package tk.wenop.XiangYu.ui.wenui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.flyco.animation.BaseAnimatorSet;
import com.flyco.animation.BounceEnter.BounceBottomEnter;
import com.flyco.animation.SlideExit.SlideBottomExit;
import com.flyco.animation.ZoomEnter.ZoomInEnter;
import com.flyco.animation.ZoomExit.ZoomInExit;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.NormalDialog;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.im.BmobChat;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobNotifyManager;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.db.BmobDB;
import cn.bmob.im.inteface.EventListener;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.UpdateListener;
import de.greenrobot.event.EventBus;
import de.hdodenhof.circleimageview.CircleImageView;
import tk.wenop.XiangYu.CustomApplcation;
import tk.wenop.XiangYu.MyMessageReceiver;
import tk.wenop.XiangYu.R;
import tk.wenop.XiangYu.adapter.custom.MainScreenChatAdapter;
import tk.wenop.XiangYu.bean.AreaEntity;
import tk.wenop.XiangYu.bean.MessageEntity;
import tk.wenop.XiangYu.bean.User;
import tk.wenop.XiangYu.config.BmobConstants;
import tk.wenop.XiangYu.event.ConstantEvent;
import tk.wenop.XiangYu.manager.DBManager;
import tk.wenop.XiangYu.network.AreaNetwork;
import tk.wenop.XiangYu.ui.ActivityBase;
import tk.wenop.XiangYu.ui.LoginActivity;
import tk.wenop.XiangYu.ui.NewFriendActivity;
import tk.wenop.XiangYu.ui.SetMyInfoActivity;
import tk.wenop.XiangYu.ui.activity.OnGetImageFromResoult;
import tk.wenop.XiangYu.ui.activity.OverallMessageListActivity;
import tk.wenop.XiangYu.ui.dialog.SelectAddressDialog;
import tk.wenop.XiangYu.ui.fragment.ContactFragment;
import tk.wenop.XiangYu.ui.fragment.RecentFragment;
import tk.wenop.XiangYu.ui.fragment.SettingsFragment;

import static tk.wenop.XiangYu.MyMessageReceiver.showMsgNotify;


public class SideActivity extends ActivityBase
        implements
            NavigationView.OnNavigationItemSelectedListener,
            NewContentBottomDialog.SelectImageInterface,
            SelectAddressDialog.OnGetAddressResult,
            AreaNetwork.OnGetAreaEntity,
            AreaNetwork.OnGetFollowAreaEntities,
            EventListener {

    private MainScreenChatAdapter mainActRVAdapter;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mRVLayoutM;

//    private ArrayList<MainScreenOverviewItem> mainActDataSet;
private ArrayList<MessageEntity> mainActDataSet = new ArrayList<>();

    private BaseAnimatorSet bas_in;
    private BaseAnimatorSet bas_out;
    private ExpandableListView elv;
    DBManager dbManager;

    // 下拉刷新
    SwipeRefreshLayout mSwipeRefreshLayout;

    View view_empty_content_notice;

    /*
      选取图片相关的声明
    */
    OnGetImageFromResoult onGetImageFromResoult = null;
    NewContentBottomDialog.SelectImageInterface selectImageInterface;
    Context mContext;
    SelectAddressDialog.OnGetAddressResult onGetAddressResult;

    Menu mActionBarMenu;

    private User user;
    private Toolbar toolbar;
    private AreaEntity nowAreaEntity;



    void refreshItems() {
        // Load items
        if (nowAreaEntity == null) {
            Toast.makeText(mContext,"no_group",Toast.LENGTH_SHORT).show();
        }
        // Load complete
        onItemsLoadComplete();
    }

    void onItemsLoadComplete() {
        // Update the adapter and notify data set changed

        dbManager.refreshMessageEntities(nowAreaEntity);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_side);
        toolbar = (Toolbar) findViewById(R.id.toolbar);



        Intent intent = getIntent();
        String area = intent.getStringExtra("area");
        if (area!=null)
        if (!area.isEmpty()){
            AreaNetwork.loadArea(this, this, area);
        }

        ///
        //开启定时检测服务（单位为秒）-在这里检测后台是否还有未读的消息，有的话就取出来
        //如果你觉得检测服务比较耗流量和电量，你也可以去掉这句话-同时还有onDestory方法里面的stopPollService方法
        BmobChat.getInstance(this).startPollService(10);
        //开启广播接收器
        initNewMessageBroadCast();
        initTagMessageBroadCast();

        CustomApplcation customApplcation = CustomApplcation.getInstance();
        if (customApplcation.getLoginAreaEntity()!=null){

            String area1 = customApplcation.getLoginAreaEntity().getArea();
            if ((area1 != null)&&(!area1.equals(""))){
                toolbar.setTitle(area);
            }else {
                toolbar.setTitle("未知地点");
            }

        }else {
            toolbar.setTitle("未知地点");
        }


        dbManager = DBManager.instance(this);

        EventBus.getDefault().register(this);
        setSupportActionBar(toolbar);

        user = BmobUser.getCurrentUser(this,User.class);
        mContext =this;
        onGetAddressResult = this;
        //android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        //actionBar.setTitle("天天市");

        view_empty_content_notice = findViewById(R.id.view_empty_content_notice);

        // 下拉刷新
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.main_screen_swipe_refresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshItems();
            }
        });

        View decorView = getWindow().getDecorView();

        bas_in = new BounceBottomEnter();
        bas_out = new SlideBottomExit();

        /*
            让本activity承担起选取图片的功能
         */
        selectImageInterface = this;

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO
                //    如果没有登录，在这里跳转登录

                if (nowAreaEntity == null) {
                    Toast.makeText(mContext, "no_group", Toast.LENGTH_SHORT).show();
                }

                if (selectImageInterface != null) {
                    final NewContentBottomDialog dialog = new NewContentBottomDialog(SideActivity.this, selectImageInterface, nowAreaEntity);
                    onGetImageFromResoult = dialog;
                    dialog.showAnim(bas_in)
                            .show();
                }
            }
        });

        ///////////////////////// ActionBar ///////////////////////////////////////////

        ///////////////////////// ActionBar //end//////////////////////////////////////

        ///////////////////////// 侧滑Nav Drawer ///////////////////////////////////////////
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        CircleImageView sidebarAvatar = (CircleImageView) findViewById(R.id.nav_iv_avatar);

        ///  设置侧边栏上的用户信息
        // sidebarAvatar.setImageResource();
        ImageLoader.getInstance().displayImage(user.getAvatar(),sidebarAvatar);
        ((TextView) findViewById(R.id.nav_tv_nickName)).setText(user.getNick());
        ((TextView) findViewById(R.id.nav_tv_selfDesc)).setText(user.getUserDesc());

        sidebarAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoSettingActivity();
            }
        });

        ///////////////////////// 侧滑Nav Drawer /END///////////////////////////////////////

        //设置recyclerView
        mRecyclerView = (RecyclerView) findViewById(R.id.mainscreen_recycler_view);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        mRVLayoutM = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mRVLayoutM);


//        // wenop-test
//        //   测试增加几个item... 看看效果
//        mainActDataSet = new ArrayList<>();
//        MainScreenOverviewItem msovi_0 = new MainScreenOverviewItem();
//        msovi_0.mNickName = "wenop";
//        msovi_0.mLocation = "天津市";
//        msovi_0.mCommentCount = 11;
//        mainActDataSet.add(msovi_0);
//        MainScreenOverviewItem msovi_1 = new MainScreenOverviewItem();
//        msovi_1.mNickName = "wenop_002";
//        msovi_1.mLocation = "天津市";
//        msovi_1.mCommentCount = 2;
//        mainActDataSet.add(msovi_1);


        // specify an adapter
        //

        mainActDataSet.addAll(dbManager.getAllMessageEntities());
        mainActRVAdapter = new MainScreenChatAdapter(SideActivity.this, mainActDataSet);
        mRecyclerView.setAdapter(mainActRVAdapter);

        // wenop-add 监测，当没有内容的时候, 显示“内容空”View
        mainActRVAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
//                Log.d("====== onChanged",
//                        String.format("count:%d", mainActRVAdapter.getItemCount()));
                refreshContentEmptyCheck();
            }
        });
    }


    // wenop-add
    //   当列表没有item的时候，显示"空"View
    void refreshContentEmptyCheck() {
        if (mainActRVAdapter.getItemCount() == 0) {
            view_empty_content_notice.setVisibility(View.VISIBLE);
        } else {
            view_empty_content_notice.setVisibility(View.GONE);
        }
    }


    private void gotoSettingActivity() {
        Intent intent =  new Intent(SideActivity.this, SetMyInfoActivity.class);
//        intent.putExtra("user",data.getOwnerUser());
        SideActivity.this.startActivity(intent);
    }


    private static long firstTime=0;

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            // 连续按两次返回键就退出
            if (firstTime + 2000 > System.currentTimeMillis()) {
                super.onBackPressed();
            } else {
                ShowToast("再按一次退出程序");
            }
            firstTime = System.currentTimeMillis();
        }

    }

    MenuItem favItem;

    // 设置关注地点的按钮状态
    private void setFollowAreaBtnIcon(Boolean followed) {

        if(followed) {
            favItem.setIcon(R.drawable.ic_heart_white_24dp);
        } else {
            favItem.setIcon(R.drawable.ic_heart_broken_white_24dp);
        }
    }

    @Override
    public void onGetFollowAreaEntities(List<AreaEntity> areaEntities) {

        if (nowAreaEntity == null) return;

        ///areaEntities.contains(nowAreaEntity)
        //遍历查找
        Boolean found = false;
        for(AreaEntity ae:areaEntities) {
            if (ae.getObjectId().equals( nowAreaEntity.getObjectId() )) {
                found = true;
                break;
            }
        }

        // 设置关注图标样式
        setFollowAreaBtnIcon(found);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_actionbar_main_screen, menu);

        mActionBarMenu = menu;
        favItem = mActionBarMenu.findItem(R.id.action_toggleFav);

        //  在menu创建好之后  去查关注列表
        AreaNetwork.loadFollowAreas(mContext, this, user);

        // Associate searchable configuration with the SearchView
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search_place).getActionView();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_toggleFav) {
            //关注地理位置的按钮
            if (nowAreaEntity!=null)
            addFollowArea(nowAreaEntity);

            return true;
        }else if (id == R.id.search_place){

            SelectAddressDialog selectAddressDialog = new SelectAddressDialog(mContext,onGetAddressResult);
            selectAddressDialog.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /*
        添加地点关注
     */
    public void  addFollowArea(final AreaEntity areaEntity){

        BmobRelation relation = new BmobRelation();
        relation.add(areaEntity);

        user.setFollowAreas(relation);
//        user.setFollowingUsers(relation);
        user.update(mContext, new UpdateListener() {
            @Override
            public void onSuccess() {
                ShowToast(String.format("关注%s成功", areaEntity.getArea()));
                setFollowAreaBtnIcon(true);
            }

            @Override
            public void onFailure(int i, String s) {
                Toast.makeText(mContext, "关注地点失败", Toast.LENGTH_SHORT).show();
            }
        });

    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_main_screen) {

        } else if (id == R.id.nav_message_list) {
            //消息
            Intent intent = new Intent(this, OverallMessageListActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_setting) {

            gotoSettingActivity();

        } else if (id == R.id.nav_logout) {
            // 退出登录
            confirmLogout();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    protected void confirmLogout() {
        final NormalDialog dialog = new NormalDialog(SideActivity.this);
        dialog.isTitleShow(false)//
                .bgColor(Color.parseColor("#383838"))//
                .cornerRadius(5)//
                .content("真的要退出登录吗?")//
                .contentGravity(Gravity.CENTER)//
                .contentTextColor(Color.parseColor("#ffffff"))//
                .dividerColor(Color.parseColor("#222222"))//
                .btnTextSize(15.5f, 15.5f)//
                .btnTextColor(Color.parseColor("#ffffff"), Color.parseColor("#ffffff"))//
                .btnPressColor(Color.parseColor("#2B2B2B"))//
                .widthScale(0.85f)//
                .showAnim(new ZoomInEnter())//
                .dismissAnim(new ZoomInExit())//
                .show();

        dialog.setOnBtnClickL(
                new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        dialog.dismiss();
                        //T.showShort(SideActivity.this, "取消");

                    }
                },
                new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        dialog.dismiss();
                        // wenop: 使用原版的Logout方法，不必自己写。也不必退出App
                        CustomApplcation.getInstance().logout();
                        startActivity(new Intent(SideActivity.this, LoginActivity.class));
                        SideActivity.this.finish();
//                        BmobUser.logOut(mContext);
//                        finish();
//                        T.showShort(SideActivity.this, "确定");
                    }
                });
    }


    public void onEventMainThread(ConstantEvent event){


        switch (event){
            case MESSAGE_LOAD:
                mainActRVAdapter.putDataSet(dbManager.getAllMessageEntities());
                // Stop refresh animation
                mSwipeRefreshLayout.setRefreshing(false);
                break;

            case MESSAGE_REFRESH:
                mainActRVAdapter.putDataSet(dbManager.getAllMessageEntities());
                // Stop refresh animation
                mSwipeRefreshLayout.setRefreshing(false);
                break;

            // TODO llwoll: 刷新失败时候 没有办法停止刷新动画
            case LOGIN_LOCATION_GET:
                if (nowAreaEntity == null){
                    CustomApplcation applcation = CustomApplcation.getInstance();
                    String area =  applcation.getLoginAreaEntity().getArea();
                    toolbar.setTitle(area);
                    DBManager.instance(this).refreshMessageEntities(applcation.getLoginAreaEntity());
                }

        }

    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case BmobConstants.REQUESTCODE_TAKE_CAMERA:// 当取到值的时候才上传path路径下的图片到服务器
//                    ShowLog("本地图片的地址：" + localCameraPath);
//                    sendImageMessage(localCameraPath);
                    break;
                case BmobConstants.REQUESTCODE_TAKE_LOCAL:
                    if (data != null) {
                        Uri selectedImage = data.getData();
                        if (selectedImage != null) {
                            Cursor cursor = getContentResolver().query(
                                    selectedImage, null, null, null, null);
                            cursor.moveToFirst();
                            int columnIndex = cursor.getColumnIndex("_data");
                            String localSelectPath = cursor.getString(columnIndex);
                            cursor.close();
                            if (localSelectPath == null
                                    || localSelectPath.equals("null")) {
//                                ShowToast("找不到您想要的图片");
                                return;
                            }

//                            getImagePath = localSelectPath;

                            /*
                                获得图片,然后发送事件给dialog
                             */
                            if (onGetImageFromResoult != null) onGetImageFromResoult.onGetImageFromResoult(localSelectPath);


//
                        }
                    }
                    break;
                case BmobConstants.REQUESTCODE_TAKE_LOCATION:// 地理位置
                    double latitude = data.getDoubleExtra("x", 0);// 维度
                    double longtitude = data.getDoubleExtra("y", 0);// 经度
                    String address = data.getStringExtra("address");
                    if (address != null && !address.equals("")) {
//                        sendLocationMessage(address, latitude, longtitude);
                    } else {
//                        ShowToast("无法获取到您的位置信息!");
                    }

                    break;
            }
        }
    }

/*
    通过其他dialog 发起选取图片的通知
 */
    @Override
    public void toSelectImageInterface() {
        selectImageFromLocal();
    }
    /**
     * 选择图片
     */
    public void selectImageFromLocal() {

        File dir = new File(BmobConstants.BMOB_PICTURE_PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, String.valueOf(System.currentTimeMillis())
                + ".jpg");
//        localCameraPath = file.getPath();


        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
        } else {
            intent = new Intent(
                    Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        startActivityForResult(intent, BmobConstants.REQUESTCODE_TAKE_LOCAL);
    }

    @Override
    public void onGetResult(String province, String city, String district) {


        if (district != null){
            //查询地理位置信息;
            AreaNetwork.loadArea(mContext, this, district);
            return;
        }

        if (city!=null){
            AreaNetwork.loadArea(mContext,this,city);
            return;
        }

        if (province!= null){
            AreaNetwork.loadArea(mContext,this,province);
            return;
        }

    }


    @Override
    public void onGetAreaEntity(AreaEntity areaEntity) {
        /*
            获得位置信息后 todo:更新当前列表
         */

        if (areaEntity== null){
            //todo: 获取地理位置信息失败

            return;
        }
        nowAreaEntity = areaEntity;

        // 去查关注列表, 因为要知道有没有关注这个地点，因为要改followIcon的状态
        AreaNetwork.loadFollowAreas(mContext, this, user);

        toolbar.setTitle(areaEntity.getArea());
        DBManager.instance(this).refreshMessageEntities(areaEntity);
    }





    /////////////// move from bmobIM original MainActivity


    private Button[] mTabs;
    private ContactFragment contactFragment;
    private RecentFragment recentFragment;
    private SettingsFragment settingFragment;
    private Fragment[] fragments;
    private int index;
//    private int currentTabIndex;

//    ImageView iv_recent_tips,iv_contact_tips;//消息提示

    @Override
    protected void onResume() {
        
        super.onResume();
        //小圆点提示
        if(BmobDB.create(this).hasUnReadMsg()){
            // TODO wenop
//            iv_recent_tips.setVisibility(View.VISIBLE);
        }else{
            // TODO wenop
//            iv_recent_tips.setVisibility(View.GONE);
        }
        if(BmobDB.create(this).hasNewInvite()){
            // TODO wenop
//            iv_contact_tips.setVisibility(View.VISIBLE);
        }else{
            // TODO wenop
//            iv_contact_tips.setVisibility(View.GONE);
        }
        MyMessageReceiver.ehList.add(this);// 监听推送的消息
        //清空
        MyMessageReceiver.mNewNum=0;

    }

    @Override
    protected void onPause() {
        
        super.onPause();
        MyMessageReceiver.ehList.remove(this);// 取消监听推送的消息
    }

    @Override
    public void onMessage(BmobMsg message) {
        
        refreshNewMsg(message);
    }


    /** 刷新界面
     */
    private void refreshNewMsg(BmobMsg message){
        // 声音提示
        boolean isAllow = CustomApplcation.getInstance().getSpUtil().isAllowVoice();
        if(isAllow){
            CustomApplcation.getInstance().getMediaPlayer().start();
        }
        // todo wenop 未读消息红点
//        iv_recent_tips.setVisibility(View.VISIBLE);
        //也要存储起来
        if(message!=null){
            BmobChatManager.getInstance(SideActivity.this).saveReceiveMessage(true,message);
        }

        // wenop-add 如果是SideActivity接收到消息了，那就要在通知栏显示了
        showMsgNotify(this, message);

        /*if(currentTabIndex==0){
            //当前页面如果为会话页面，刷新此页面
            if(recentFragment != null){
                recentFragment.refresh();
            }
        }*/
    }

    NewBroadcastReceiver  newReceiver;

    private void initNewMessageBroadCast(){
        // 注册接收消息广播
        newReceiver = new NewBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(BmobConfig.BROADCAST_NEW_MESSAGE);
        //优先级要低于ChatActivity
        intentFilter.setPriority(3);
        registerReceiver(newReceiver, intentFilter);
    }

    /**
     * 新消息广播接收者
     *
     */
    private class NewBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //刷新界面
            refreshNewMsg(null);
            // 记得把广播给终结掉
            abortBroadcast();
        }
    }

    TagBroadcastReceiver  userReceiver;

    private void initTagMessageBroadCast(){
        // 注册接收消息广播
        userReceiver = new TagBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(BmobConfig.BROADCAST_ADD_USER_MESSAGE);
        //优先级要低于ChatActivity
        intentFilter.setPriority(3);
        registerReceiver(userReceiver, intentFilter);
    }


    /**
     * 标签消息广播接收者
     */
    private class TagBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            BmobInvitation message = (BmobInvitation) intent.getSerializableExtra("invite");
            refreshInvite(message);
            // 记得把广播给终结掉
            abortBroadcast();
        }
    }

    @Override
    public void onNetChange(boolean isNetConnected) {
        
        if(isNetConnected){
            ShowToast(R.string.network_tips);
        }
    }

    @Override
    public void onAddUser(BmobInvitation message) {
        
        refreshInvite(message);
    }

    /** 刷新好友请求
     */
    private void refreshInvite(BmobInvitation message){
        boolean isAllow = CustomApplcation.getInstance().getSpUtil().isAllowVoice();
        if(isAllow){
            CustomApplcation.getInstance().getMediaPlayer().start();
        }
        // todo wenop 未读消息红点
//        iv_contact_tips.setVisibility(View.VISIBLE);
        //同时提醒通知
        String tickerText = message.getFromname()+"请求添加好友";
        boolean isAllowVibrate = CustomApplcation.getInstance().getSpUtil().isAllowVibrate();
        BmobNotifyManager.getInstance(this).showNotify(isAllow,isAllowVibrate,R.drawable.ic_launcher, tickerText, message.getFromname(), tickerText.toString(),NewFriendActivity.class);

    }

    @Override
    public void onOffline() {
        
        showOfflineDialog(this);
    }

    @Override
    public void onReaded(String conversionId, String msgTime) {
        
    }

    @Override
    protected void onDestroy() {
        
        super.onDestroy();
        try {
            unregisterReceiver(newReceiver);
        } catch (Exception e) {
        }
        try {
            unregisterReceiver(userReceiver);
        } catch (Exception e) {
        }
        //取消定时检测服务
        BmobChat.getInstance(this).stopPollService();
    }







}
