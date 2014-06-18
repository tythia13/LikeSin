package com.edwin.likesina;

import com.edwin.likesina.app.home.activity.HomeMenuActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class SplashActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // Intent intent = new Intent(this, MainActivity.class);
        Intent intent = new Intent(this, HomeMenuActivity.class);
        startActivity(intent);
        finish();
    }
}
