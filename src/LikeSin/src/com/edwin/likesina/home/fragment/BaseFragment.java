package com.edwin.likesina.home.fragment;

import android.app.Fragment;

public class BaseFragment extends Fragment
{
    public void onFragmentResume(boolean isChangeTab)
    {

    }

    public void onFragmentPause(boolean isChangeTab)
    {

    }

    /**
     * @return default false
     */
    public boolean isBackKeyEventConsumed()
    {
        return false;
    }
}
