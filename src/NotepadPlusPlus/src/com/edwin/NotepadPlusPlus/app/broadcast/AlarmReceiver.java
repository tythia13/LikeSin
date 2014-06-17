package com.edwin.NotepadPlusPlus.app.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i("AlarmReceiver", "called", null);
		 Toast.makeText(context, "你设置的闹铃时间到了", Toast.LENGTH_LONG).show();
	}

}
