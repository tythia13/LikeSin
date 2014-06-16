package com.edwin.likesina.v4;

 
import com.edwin.likesina.R;
import com.edwin.likesina.R.id;
import com.edwin.likesina.R.layout;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class FragmentStackSupport extends FragmentActivity {
    int mStackLevel = 1;
    String TAG1 = FragmentStackSupport.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_stack);
        Log.i(TAG1,"onCreate");

        // Watch for button clicks.
        Button button = (Button)findViewById(R.id.new_fragment);
        button.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                addFragmentToStack();
            }
        });
        button = (Button)findViewById(R.id.home);
        button.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // If there is a back stack, pop it all.
                FragmentManager fm = getSupportFragmentManager();
                if (fm.getBackStackEntryCount() > 0) {
                    fm.popBackStack(fm.getBackStackEntryAt(0).getId(),
                            FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }
            }
        });

        if (savedInstanceState == null) {
            // Do first time initialization -- add initial fragment.
            Fragment newFragment = CountingFragment.newInstance(mStackLevel);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.simple_fragment, newFragment).commit();
        } else {
            mStackLevel = savedInstanceState.getInt("level");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("level", mStackLevel);
    }


    void addFragmentToStack() {
        mStackLevel++;

        // Instantiate a new fragment.
        Fragment newFragment = CountingFragment.newInstance(mStackLevel);

        // Add the fragment to the activity, pushing this transaction
        // on to the back stack.
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.simple_fragment, newFragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    protected void onStart()
    {
        Log.i(TAG1,"onStart");
        super.onStart();
    }
    
    @Override
    protected void onResume()
    {
        Log.i(TAG1,"onResume");
        super.onResume();
    }

    @Override
    protected void onPause()
    {
        Log.i(TAG1,"onPause");
        super.onPause();
    }
    
    @Override
    protected void onStop()
    {
        Log.i(TAG1,"onStop");
        super.onStop();
    }
    
    @Override
    protected void onDestroy()
    {
        Log.i(TAG1,"onDestroy");
        super.onDestroy();
    }


    public static class CountingFragment extends Fragment
    {
        int mNum;
        String TAG = CountingFragment.class.getSimpleName();

        /**
         * Create a new instance of CountingFragment, providing "num"
         * as an argument.
         */
        static CountingFragment newInstance(int num) {
            CountingFragment f = new CountingFragment();

            // Supply num input as an argument.
            Bundle args = new Bundle();
            args.putInt("num", num);
            f.setArguments(args);

            return f;
        }

        /**
         * When creating, retrieve this instance's number from its arguments.
         */
        @Override
        public void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            Log.i(TAG,"onCreate");
            mNum = getArguments() != null ? getArguments().getInt("num") : 1;
        }

        /**
         * The Fragment's UI is just a simple text view showing its
         * instance number.
         */
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) 
        {
            Log.i(TAG,"onCreateView");
            View v = inflater.inflate(R.layout.hello_world, container, false);
            View tv = v.findViewById(R.id.text);
            ((TextView)tv).setText("Fragment #" + mNum);
            tv.setBackgroundDrawable(getResources().getDrawable(android.R.drawable.gallery_thumb));
            return v;
        }
        

        @Override
        public void onStart()
        {
            Log.i(TAG,"onStart");
            super.onStart();
        }
        
        @Override
        public void onResume()
        {
            Log.i(TAG,"onResume");
            super.onResume();
        }
        
        @Override
        public void onPause()
        {
            Log.i(TAG,"onPause");
            super.onPause();
        }
        
        @Override
        public void onStop()
        {
            Log.i(TAG,"onStop");
            super.onStop();
        }
        
        @Override
        public void onDestroy()
        {
            Log.i(TAG,"onDestroy");
            super.onDestroy();
        }
    }

}
