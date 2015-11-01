package tk.wenop.XiangYu.ui.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import tk.wenop.XiangYu.R;
import tk.wenop.XiangYu.ui.fragment.ContactFragment;
import tk.wenop.XiangYu.ui.fragment.RecentFragment;
import tk.wenop.XiangYu.ui.fragment.SettingsFragment;

/**
 * Created by zysd on 15/11/1.
 */
public class OverallMessageListActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actitity_overall_tab);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        toolbar.setTitle("llwoll");
//        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add(R.string.fragment_recent, RecentFragment.class)
                .add(R.string.fragment_contact, ContactFragment.class)
                .add(R.string.fragment_setting, SettingsFragment.class)
                .create());

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);

        SmartTabLayout viewPagerTab = (SmartTabLayout) findViewById(R.id.viewpagertab);
        viewPagerTab.setViewPager(viewPager);


//
//        @Override
//        public boolean onOptionsItemSelected(MenuItem item) {
//            int id = item.getItemId();
//            // 如果是返回按钮，那就finish这个activity
//            if (id==android.R.id.home) {
//                finish();
//            }
//
//            return true;
//        }




    }
}
