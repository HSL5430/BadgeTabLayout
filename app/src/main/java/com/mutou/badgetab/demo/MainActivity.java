package com.mutou.badgetab.demo;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.SparseArrayCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.mutou.badgetab.BadgeTabLayout;
import com.mutou.badgetab.demo.ui.dashboard.DashboardFragment;
import com.mutou.badgetab.demo.ui.home.HomeFragment;
import com.mutou.badgetab.demo.ui.notifications.NotificationsFragment;

import java.util.List;

public class MainActivity extends AppCompatActivity implements BadgeTabLayout.OnCheckedChangeListener {

    /**
     * 切换了BottomTab 的 item 时才会 赋值 否则为null
     */
    private Fragment mSelectedFragment;
    private SparseArrayCompat<Fragment> mFragments = new SparseArrayCompat<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initFragments();

        BadgeTabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setOnCheckedChangeListener(new BadgeTabLayout.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(BadgeTabLayout group, int checkedId) {

            }
        });
    }


    private void initFragments() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();

        Fragment homeFragment = new HomeFragment();
        ft.add(R.id.contentLayout, homeFragment, String.valueOf(R.id.homeTabView));
        mFragments.put(R.id.homeTabView, homeFragment);


        Fragment dashboardFragment = new DashboardFragment();
        ft.add(R.id.contentLayout, dashboardFragment, String.valueOf(R.id.dashboardTabView));
        mFragments.put(R.id.dashboardTabView, dashboardFragment);

        Fragment notificationsFragment = new NotificationsFragment();
        ft.add(R.id.contentLayout, notificationsFragment, String.valueOf(R.id.notificationsTabView));
        mFragments.put(R.id.notificationsTabView, notificationsFragment);

        mSelectedFragment = homeFragment;
        ft.show(homeFragment).hide(dashboardFragment).hide(notificationsFragment).commitNowAllowingStateLoss();
    }


    @Override
    public void onCheckedChanged(BadgeTabLayout group, int checkedId) {
        selectFragmentById(checkedId);
    }

    private boolean selectFragmentById(int checkedId) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();

        // 有可能因为被回收或者某种原因丢失mFragments的数据
        if (mFragments.size() < 1) {
            List<Fragment> fragments = fragmentManager.getFragments();
            for (int i = 0; i < fragments.size(); i++) {
                Fragment f = fragments.get(i);
                if (f != null) {
                    int tabId = Integer.valueOf(f.getTag());
                    mFragments.put(tabId, f);
                }
            }
        }

        Fragment fragment = mFragments.get(checkedId);
        if (fragment == null) {
            fragment = fragmentManager.findFragmentByTag(String.valueOf(checkedId));
        }
        if (fragment != null) {
            if (mSelectedFragment != null) {
                ft.hide(mSelectedFragment);
            } else {
                // 首次 切换按钮
                for (int i = 0; i < mFragments.size(); i++) {
                    if (i != mFragments.indexOfKey(checkedId)) {
                        ft.hide(mFragments.valueAt(i));
                    }
                }
            }
            boolean hidden = fragment.isHidden();
            if (!hidden) {
                fragment.onHiddenChanged(false);
            }

            mSelectedFragment = fragment;
            ft.show(fragment).commitNowAllowingStateLoss();
            return true;
        }
        return false;
    }
}
