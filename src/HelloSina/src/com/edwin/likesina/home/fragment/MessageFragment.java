package com.edwin.likesina.home.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.edwin.likesina.R;

public class MessageFragment extends BaseFragment
{

    int mNum;
    String TAG = MessageFragment.class.getSimpleName();

    /**
     * When creating, retrieve this instance's number from its arguments.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        mNum = getArguments() != null ? getArguments().getInt("num") : 2;
    }

    /**
     * The Fragment's UI is just a simple text view showing its instance number.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Log.i(TAG, "onCreateView");
        View v = inflater.inflate(R.layout.hello_world, container, false);
        View tv = v.findViewById(R.id.text);
        ((TextView)tv).setText("Fragment #" + mNum);
        tv.setBackgroundDrawable(getResources().getDrawable(android.R.drawable.gallery_thumb));
        return v;
    }

    @Override
    public void onStart()
    {
        Log.i(TAG, "onStart");
        super.onStart();
    }

    @Override
    public void onResume()
    {
        Log.i(TAG, "onResume");
        super.onResume();
    }

    @Override
    public void onFragmentResume(boolean isChangeTab)
    {
        Log.i(TAG, "onFragmentResume" + "->isChangeTab:" + isChangeTab);
        super.onFragmentResume(isChangeTab);
    }

    @Override
    public void onFragmentPause(boolean isChangeTab)
    {
        Log.i(TAG, "onFragmentPause" + "->isChangeTab:" + isChangeTab);
        super.onFragmentPause(isChangeTab);
    }

    @Override
    public void onPause()
    {
        Log.i(TAG, "onPause");
        super.onPause();
    }

    @Override
    public void onStop()
    {
        Log.i(TAG, "onStop");
        super.onStop();
    }

    @Override
    public void onDestroy()
    {
        Log.i(TAG, "onDestroy");
        super.onDestroy();
    }
}
