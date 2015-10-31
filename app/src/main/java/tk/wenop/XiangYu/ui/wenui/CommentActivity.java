package tk.wenop.XiangYu.ui.wenui;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import java.util.ArrayList;

import tk.wenop.XiangYu.R;
import tk.wenop.XiangYu.adapter.custom.CommentAdapter;
import tk.wenop.XiangYu.adapter.custom.MainScreenOverviewItem;
import tk.wenop.XiangYu.util.WrappingRecyclerViewLayoutManager;

//import tk.wenop.testapp.Adapter.CommentAdapter;
//import tk.wenop.testapp.Overview.MainScreenOverviewItem;
//import tk.wenop.testapp.R;
//import tk.wenop.testapp.Util.WrappingRecyclerViewLayoutManager;

public class CommentActivity extends AppCompatActivity {

    private CommentAdapter commentAdapter;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mRVLayoutM;

    protected ArrayList<MainScreenOverviewItem> commentDataSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);


        // 显示出返回按钮
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // 在actionBar显示用户名
        actionBar.setTitle("wenop" + "说");
//        actionBar.setTitle("");


        /// 设置评论数据
        //设置recyclerView
        mRecyclerView = (RecyclerView) findViewById(R.id.comment_recyclerview);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
//        mRecyclerView.setHasFixedSize(true);

        // 用这个WrappingRecyclerViewLayoutManager改版的LayoutManager是因为我需要让
        //    这个recyclerView的内容展开(全局滚动)
        mRVLayoutM = new WrappingRecyclerViewLayoutManager(this);
        mRecyclerView.setLayoutManager(mRVLayoutM);

        commentDataSet = new ArrayList<>();
        // TODO 在这里插入评论数据
        // ...
        commentDataSet.add(new MainScreenOverviewItem());
        commentDataSet.add(new MainScreenOverviewItem());
        commentDataSet.add(new MainScreenOverviewItem());

        commentAdapter = new CommentAdapter(CommentActivity.this, commentDataSet);
        mRecyclerView.setAdapter(commentAdapter);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // 如果是返回按钮，那就finish这个activity
        if (id==android.R.id.home) {
            finish();
        }

        return true;
    }
}
