package com.edwin.likesina;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.edwin.likesina.app.home.activity.HomeMenuActivity;

public class SplashActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // Intent intent = new Intent(this, MainActivity.class);
        String a = "test";
        Log.e("edwin", "a="+a);
        a = "treat";
        Log.e("edwin", "a="+a);
        String b = a.substring(0,2);
        Log.e("edwin", "b="+b);
        
        Intent intent = new Intent(this, HomeMenuActivity.class);
        startActivity(intent);
        finish();
    }
}
