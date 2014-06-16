package com.edwin.likesina.home.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.edwin.likesina.R;
import com.edwin.likesina.home.fragment.BaseFragment;
import com.edwin.likesina.home.fragment.DiscoverFragment;
import com.edwin.likesina.home.fragment.HomeFragment;
import com.edwin.likesina.home.fragment.MeFragment;
import com.edwin.likesina.home.fragment.MessageFragment;
import com.edwin.likesina.home.fragment.MoreFragment;

public class HomeMenuActivity extends FragmentActivity implements OnClickListener
{
    private BaseFragment mCurrentFragment;
    private LinearLayout mLinearLayoutHome, mLinearLayoutMessage, mLinearLayoutDiscover, mLinearLayoutMe, mLinearLayoutMore;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_menu);
        initView();
        BaseFragment fragmentHome = new HomeFragment();
        getFragmentManager().beginTransaction()
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
        Fragment f = getFragmentManager().findFragmentByTag(fragmentTag);
        if (f != null)
        {
            mCurrentFragment.onFragmentPause(true);
            getFragmentManager().beginTransaction().hide(mCurrentFragment).commit();
            mCurrentFragment = (BaseFragment)f;
            mCurrentFragment.onFragmentResume(true);
            getFragmentManager().beginTransaction().show(mCurrentFragment).commit();
        }
        else
        {
            BaseFragment basefragment = null;
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
                getFragmentManager().beginTransaction().hide(mCurrentFragment).commit();
                mCurrentFragment = basefragment;
                getFragmentManager().beginTransaction().add(R.id.home_menu_linearlayout_content, mCurrentFragment, fragmentTag).commit();
                getFragmentManager().beginTransaction().show(mCurrentFragment).commit();
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
