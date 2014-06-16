package com.edwin.likesina.home.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class HomeTabhost extends FrameLayout
{

    private OnTabChangeListener mOnTabChangedListener;

    public HomeTabhost(Context context)
    {
        super(context);
        init(context);
    }

    public HomeTabhost(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }

    public HomeTabhost(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context)
    {
        
    }

    public void setOnTabChangedListener(OnTabChangeListener listner)
    {
        mOnTabChangedListener = listner;
    }

    public interface OnTabChangeListener
    {
        void onTabChanged(int tabIndex);
    }

}
