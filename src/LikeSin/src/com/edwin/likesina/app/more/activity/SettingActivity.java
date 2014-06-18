package com.edwin.likesina.app.more.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edwin.likesina.R;
import com.edwin.likesina.app.more.fragment.AboutFragment;
import com.edwin.likesina.app.more.fragment.RecommandFragment;
import com.edwin.likesina.common.activity.BaseActivity;
import com.edwin.likesina.common.fragment.BaseFragment;

public class SettingActivity extends BaseActivity
{
    private TextView mTextViewAbount, mTextViewRecommand;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        mTextViewAbount = (TextView)findViewById(R.id.setting_activity_textview_about);
        mTextViewRecommand = (TextView)findViewById(R.id.setting_activity_textview_recommand);
        mTextViewAbount.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                BaseFragment f = new AboutFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.setting_activity_linearlayout_content, f).commit();
            }
        });
        mTextViewRecommand.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                BaseFragment f = new RecommandFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.setting_activity_linearlayout_content, f).commit();
            }
        });
    }
}
