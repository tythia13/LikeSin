package com.edwin.likesina.app.more.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.edwin.likesina.R;
import com.edwin.likesina.common.fragment.BaseFragment;

public class AboutFragment extends BaseFragment
{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.activity_setting, container, false);
        TextView about = (TextView)view.findViewById(R.id.setting_activity_textview_about);
        about.setText("我是关于页面");
        return view;
    }
}
