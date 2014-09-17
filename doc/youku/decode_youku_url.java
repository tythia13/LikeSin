package com.example.helloyouku;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;

public class MainActivity extends Activity {
	private boolean mIsFirstIn = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		int seed = 625;
		String key1 = "bd733d2d";
		String key2 = "06371c3de8bb3869";
		// String fileid =
		// "51*25*51*51*51*12*51*40*51*51*61*51*16*25*63*37*40*61*33*0*61*28*51*56*58*55*51*12*37*16*0*13*25*33*63*13*25*33*5*61*28*16*33*5*55*16*33*22*5*16*28*12*56*5*61*22*55*51*12*25*51*61*0*55*16*25*";
		String fileid = "51*25*51*51*51*33*51*40*51*51*61*51*16*25*63*40*33*0*33*0*61*28*51*56*58*55*51*12*37*16*0*13*25*33*63*13*25*33*5*61*28*16*33*5*55*16*33*22*5*16*28*12*56*5*61*22*55*51*12*25*51*61*0*55*16*25*";
		String mfileid = getFielID(fileid, seed);
		String mkey = genKey(key1, key2);
		String msid = genSid();
		// 130086939328910582812_00
		// 03000201004D8858360BD1047C4F5FF471CDD7-C742-8D74-3EED-90A9EF54EEC1
		// de1515a31372faac182698bc
		String url = "http://f.youku.com/player/getFlvPath/sid/" + msid
				+ "/st/flv/fileid/" + mfileid + "?K=" + mkey;
		Log.e("edwin", url);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// finish();getParent();
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				if (mIsFirstIn) {
					mIsFirstIn = false;
					Intent intent = new Intent(MainActivity.this,
							BaiduMapActivity.class);
					startActivity(intent);
				}
			}
		}, 2000);
	}

	/**
	 * gene sid
	 * 
	 * @return
	 */
	private String genSid() {
		int i1 = (int) (100 + Math.floor(Math.random() * 999));
		int i2 = (int) (100 + Math.floor(Math.random() * 9000));
		return System.currentTimeMillis() + "" + i1 + "" + i2;
	}

	/**
	 * gene fileid
	 * 
	 * @param fileid
	 * @param seed
	 * @return
	 */
	private String getFielID(String fileid, double seed) {
		String mixed = getFileIDMixString(seed);
		String[] ids = fileid.split("\\*");
		StringBuilder realId = new StringBuilder();
		int idx;
		for (int i = 0; i < ids.length; i++) {
			idx = Integer.parseInt(ids[i]);
			realId.append(mixed.charAt(idx));
		}
		return realId.toString();
	}

	private String getFileIDMixString(double seed) {
		StringBuilder mixed = new StringBuilder();
		StringBuilder source = new StringBuilder(
				"abcdefghijklmnopqrstuvwxyzABCEEFGHIJKLMNOPQRSTUVWXYZ/\\:._-1234567890");
		int index, len = source.length();
		for (int i = 0; i < len; ++i) {
			seed = (seed * 211 + 30031) % 65536;
			index = (int) Math.floor(seed / 65536 * source.length());
			mixed.append(source.charAt(index));
			source.deleteCharAt(index);
		}
		return mixed.toString();
	}

	private String genKey(String key1, String key2) {
		int key = Long.valueOf(key1, 16).intValue();
		key ^= 0xA55AA5A5;
		return key2 + Long.toHexString(key);
	}

}

