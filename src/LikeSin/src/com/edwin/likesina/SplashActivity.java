package com.edwin.likesina;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        Log.e("edwin", "a=" + a);
        a = "treat";
        Log.e("edwin", "a=" + a);
        String b = a.substring(0, 2);
        Log.e("edwin", "b=" + b);
        test("hv5m5w540h540qhip");
        Log.e("edwin", "end");
        test("hv5m5w320qhip");
        Log.e("edwin", "end");
        test("hv5m5h320qhip");
        Log.e("edwin", "end");
        test("hv5m5w320qmid");
        Log.e("edwin", "end");
        test("hv5m5qhip");
        Log.e("edwin", "end");
        Intent intent = new Intent(this, HomeMenuActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * decode the key value from a giving unique string
     * 
     * @author edwin li 2014-10-20
     * @param str
     */
    private void test(String str)
    {
        Pattern datePatt = Pattern.compile("[hv]{2}[m]{1}[0-9]{1}[m]{1}[0-9]{3}[h]{0,1}[0-9]{0,3}[q]{1}[a-z]{3}");
        Matcher m = datePatt.matcher(str);
        if (m.matches())
        {
            String str1 = str.replace("hv", "");
            int w = str1.indexOf("w");
            int h = str1.indexOf("h");
            int q = str1.indexOf("q");
            if (w == -1)
            {
                if (h > q || h == -1)
                {
                    Log.e("splsh", "use default w, h");
                }
                else
                {
                    String height = str1.substring(h + 1, q);
                    Log.e("splsh", "height is:" + height);
                }
            }
            else
            {
                if (h > q || h == -1)
                {
                    String width = str1.substring(w + 1, q);
                    Log.e("splsh", "width is:" + width);
                }
                else
                {
                    String height = str1.substring(w + 1, h);
                    Log.e("splsh", "height is:" + height);
                    String width = str1.substring(h + 1, q);
                    Log.e("splsh", "width is:" + width);
                }
            }
        }
        else
        {
            Log.e("edwin", "zifuchuan you wenti");
        }
    }
}
