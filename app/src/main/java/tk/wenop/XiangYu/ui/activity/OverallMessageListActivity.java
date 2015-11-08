package tk.wenop.XiangYu.ui.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import tk.wenop.XiangYu.R;
import tk.wenop.XiangYu.ui.ActivityBase;
import tk.wenop.XiangYu.ui.fragment.AreaListFragment;
import tk.wenop.XiangYu.ui.fragment.CommentListFragment;
import tk.wenop.XiangYu.ui.fragment.ContactFragment;
import tk.wenop.XiangYu.ui.fragment.RecentFragment;

/**
 * Created by zysd on 15/11/1.
 *
 * tab
 *
 */
public class OverallMessageListActivity extends ActivityBase implements AreaListFragment.OnFragmentInteractionListener
        ,CommentListFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actitity_overall_tab);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initTopBar_withBackButton("我的消息");

        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add("评论", CommentListFragment.class)
                .add("私信", RecentFragment.class)
                .add("好友", ContactFragment.class)
                .add("关注", AreaListFragment.class)
                .create());


        String str = getIntent().getStringExtra("message");

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);

        SmartTabLayout viewPagerTab = (SmartTabLayout) findViewById(R.id.viewpagertab);
        viewPagerTab.setViewPager(viewPager);

//        viewPager.getCurrentItem()

    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

}
