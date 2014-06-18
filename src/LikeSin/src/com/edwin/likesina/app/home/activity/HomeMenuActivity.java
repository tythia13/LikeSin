package com.edwin.likesina.app.home.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.edwin.likesina.R;
import com.edwin.likesina.app.home.fragment.BaseHomeFragment;
import com.edwin.likesina.app.home.fragment.DiscoverFragment;
import com.edwin.likesina.app.home.fragment.HomeFragment;
import com.edwin.likesina.app.home.fragment.MeFragment;
import com.edwin.likesina.app.home.fragment.MessageFragment;
import com.edwin.likesina.app.home.fragment.MoreFragment;
import com.edwin.likesina.common.activity.BaseActivity;

public class HomeMenuActivity extends BaseActivity implements OnClickListener
{
    private BaseHomeFragment mCurrentFragment;
    private LinearLayout mLinearLayoutHome, mLinearLayoutMessage, mLinearLayoutDiscover, mLinearLayoutMe, mLinearLayoutMore;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_menu);
        initView();
        BaseHomeFragment fragmentHome = new HomeFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.home_menu_linearlayout_content, fragmentHome, HomeFragment.class.getSimpleName()).commit();
        mCurrentFragment = fragmentHome;
    }

    private void initView()
    {
        mLinearLayoutHome = (LinearLayout)findViewById(R.id.home_menu_linearlayout_tab_home);
        mLinearLayoutMessage = (LinearLayout)findViewById(R.id.home_menu_linearlayout_tab_message);
        mLinearLayoutDiscover = (LinearLayout)findViewById(R.id.home_menu_linearlayout_tab_discover);
        mLinearLayoutMe = (LinearLayout)findViewById(R.id.home_menu_linearlayout_tab_me);
        mLinearLayoutMore = (LinearLayout)findViewById(R.id.home_menu_linearlayout_tab_more);
        mLinearLayoutHome.setOnClickListener(this);
        mLinearLayoutMessage.setOnClickListener(this);
        mLinearLayoutDiscover.setOnClickListener(this);
        mLinearLayoutMe.setOnClickListener(this);
        mLinearLayoutMore.setOnClickListener(this);
    }

    private void changeFragment(int tabIndex, String fragmentTag)
    {
        Fragment f = getSupportFragmentManager().findFragmentByTag(fragmentTag);
        if (f != null)
        {
            mCurrentFragment.onFragmentPause(true);
            getSupportFragmentManager().beginTransaction().hide(mCurrentFragment).commit();
            mCurrentFragment = (BaseHomeFragment)f;
            mCurrentFragment.onFragmentResume(true);
            getSupportFragmentManager().beginTransaction().show(mCurrentFragment).commit();
        }
        else
        {
            BaseHomeFragment basefragment = null;
            switch (tabIndex)
            {
                case 0:
                    basefragment = new HomeFragment();
                    break;
                case 1:
                    basefragment = new MessageFragment();
                    break;
                case 2:
                    basefragment = new DiscoverFragment();
                    break;
                case 3:
                    basefragment = new MeFragment();
                    break;
                case 4:
                    basefragment = new MoreFragment();
                    break;
            }
            if (basefragment != null)
            {
                mCurrentFragment.onFragmentPause(true);
                getSupportFragmentManager().beginTransaction().hide(mCurrentFragment).commit();
                mCurrentFragment = basefragment;
                getSupportFragmentManager().beginTransaction().add(R.id.home_menu_linearlayout_content, mCurrentFragment, fragmentTag)
                        .commit();
                getSupportFragmentManager().beginTransaction().show(mCurrentFragment).commit();
            }
        }
    }

    private boolean mIsFirstIn = true;

    @Override
    protected void onResume()
    {
        super.onResume();
        if (!mIsFirstIn)
        {
            mIsFirstIn = false;
            mCurrentFragment.onFragmentResume(false);
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if (!mIsFirstIn)
        {
            mIsFirstIn = false;
            mCurrentFragment.onFragmentPause(false);
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.home_menu_linearlayout_tab_home:
                changeFragment(0, HomeFragment.class.getSimpleName());
                break;
            case R.id.home_menu_linearlayout_tab_message:
                changeFragment(1, MessageFragment.class.getSimpleName());
                break;
            case R.id.home_menu_linearlayout_tab_discover:
                changeFragment(2, DiscoverFragment.class.getSimpleName());
                break;
            case R.id.home_menu_linearlayout_tab_me:
                changeFragment(3, MeFragment.class.getSimpleName());
                break;
            case R.id.home_menu_linearlayout_tab_more:
                changeFragment(4, MoreFragment.class.getSimpleName());
                break;
        }
    }
}
