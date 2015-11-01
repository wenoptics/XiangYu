package tk.wenop.XiangYu.ui.wenui;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;

import com.flyco.animation.BaseAnimatorSet;
import com.flyco.animation.BounceEnter.BounceBottomEnter;
import com.flyco.animation.SlideExit.SlideBottomExit;
import com.flyco.animation.ZoomEnter.ZoomInEnter;
import com.flyco.animation.ZoomExit.ZoomInExit;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.NormalDialog;

import java.io.File;
import java.util.ArrayList;

import cn.bmob.v3.BmobUser;
import de.greenrobot.event.EventBus;
import de.hdodenhof.circleimageview.CircleImageView;
import tk.wenop.XiangYu.R;
import tk.wenop.XiangYu.adapter.custom.MainScreenChatAdapter;
import tk.wenop.XiangYu.bean.MessageEntity;
import tk.wenop.XiangYu.config.BmobConstants;
import tk.wenop.XiangYu.event.ConstantEvent;
import tk.wenop.XiangYu.manager.DBManager;
import tk.wenop.XiangYu.ui.SetMyInfoActivity;
import tk.wenop.XiangYu.ui.activity.OnGetImageFromResoult;
import tk.wenop.XiangYu.ui.activity.OverallMessageListActivity;
import tk.wenop.XiangYu.util.animatedDialogUtils.T;

//import tk.wenop.XiangYu.R;
//import tk.wenop.XiangYu.adapter.custom.MainScreenChatAdapter;
//import tk.wenop.XiangYu.adapter.custom.MainScreenOverviewItem;
//import tk.wenop.XiangYu.util.animatedDialogUtils.ViewFindUtils;
//import tk.wenop.testapp.Util.animatedDialogUtils.ViewFindUtils;
//
//import tk.wenop.testapp.Adapter.MainScreenChatAdapter;
//import tk.wenop.testapp.Overview.MainScreenOverviewItem;
//import tk.wenop.testapp.R;

public class SideActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, NewContentBottomDialog.SelectImageInterface {

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

    /*
      选取图片相关的声明
   */
    OnGetImageFromResoult onGetImageFromResoult = null;
    NewContentBottomDialog.SelectImageInterface selectImageInterface;
    Context mContext;


    void refreshItems() {
        // Load items
        //TODO llwoll
        dbManager.refreshMessageEntities();
        // Load complete
        onItemsLoadComplete();
    }

    void onItemsLoadComplete() {
        // Update the adapter and notify data set changed
        //TODO llwoll
        dbManager.refreshMessageEntities();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_side);
        dbManager = DBManager.instance(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("天津市");
        EventBus.getDefault().register(this);
        setSupportActionBar(toolbar);

        mContext =this;
        //android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        //actionBar.setTitle("天天市");


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
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();

                // TODO
                //    如果没有登录，在这里跳转登录

                //  在此处允许用户发内容
                //    弹出窗口

                if (selectImageInterface!= null){
                    final NewContentBottomDialog dialog = new NewContentBottomDialog(SideActivity.this,selectImageInterface);
                    onGetImageFromResoult = dialog;
                            dialog.showAnim(bas_in)
                            .show();
                }

            }
        });

        ///////////////////////// 侧滑Nav Drawer ///////////////////////////////////////////
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        CircleImageView sidebarAvatar = (CircleImageView) findViewById(R.id.nav_iv_avatar);

        /// TODO 设置侧边栏上的用户头像
        // sidebarAvatar.setImageResource();

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
    }


    private void gotoSettingActivity() {
        Intent intent =  new Intent(SideActivity.this, SetMyInfoActivity.class);
//        intent.putExtra("user",data.getOwnerUser());
        SideActivity.this.startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_actionbar_main_screen, menu);

        // Associate searchable configuration with the SearchView
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search_place).getActionView();

        // TODO llwoll:     searchView就是那个搜索框， 用来搜索、进入地区群组

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
                        // TODO llwoll
                        BmobUser.logOut(mContext);
                        finish();
                        T.showShort(SideActivity.this, "确定");
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

}
