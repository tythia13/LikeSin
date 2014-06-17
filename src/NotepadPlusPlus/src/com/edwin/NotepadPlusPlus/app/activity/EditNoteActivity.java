package com.edwin.NotepadPlusPlus.app.activity;

import java.util.Calendar;

import com.edwin.NotepadPlusPlus.R;
import com.edwin.NotepadPlusPlus.R.drawable;
import com.edwin.NotepadPlusPlus.R.id;
import com.edwin.NotepadPlusPlus.R.layout;
import com.edwin.NotepadPlusPlus.R.string;
import com.edwin.NotepadPlusPlus.app.broadcast.AlarmReceiver;
import com.edwin.NotepadPlusPlus.app.model.Note;
import com.edwin.NotepadPlusPlus.app.view.LineEditText;
import com.edwin.NotepadPlusPlus.database.Helper;
import com.edwin.NotepadPlusPlus.database.NoteDataBase;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class EditNoteActivity extends Activity implements SeekBar.OnSeekBarChangeListener{

	// Sender arguments
	public final static String OPERATION = "com.edwin.NotepadPlusPlus.EditNote.operation";
	public final static String OPERATION_ADD = "com.edwin.NotepadPlusPlus.EditNote.add";
	public final static String OPERATION_EDIT = "com.edwin.NotepadPlusPlus.EditNote.edit";
	public final static String PASS_NOTE_ID = "com.edwin.NotepadPlusPlus.EditNote.note_Id";
	public final static String PASS_NOTE_PARENTID = "com.edwin.NotepadPlusPlus.EditNote.parentId";

	// menu
	protected final static int MENU_REVERT = Menu.FIRST;
	protected final static int MENU_DELETE_NOTE = Menu.FIRST + 1;
	protected final static int MENU_EDIT_TITLE = Menu.FIRST + 2;
	protected final static int MENU_CHANGE_FONTSIZE = Menu.FIRST + 3;
	protected final static int MENU_SET_ALARM = Menu.FIRST + 4;
	protected final static int MENU_ADD_SHORTCUT = Menu.FIRST + 5;
	

	private int mYear;// 提醒时间的年份
	private int mMonth;// 提醒时间的月份
	private int mDay;// 提醒时间的日(dayOfMonth)
	private int mHour;// 提醒时间的小时
	private int mMinute;// 提醒时间的分钟
	private boolean hasSetAlartTime = false;// 用于标识用户是否设置Alarm
	private final String ACTION_ADD_SHORTCUT = "com.android.launcher.action.INSTALL_SHORTCUT";

	private EditText mNoteContent;
	private TextView mShowDate;
	private SeekBar mSeekBar;
	private static NoteDataBase mNoteDB;
	private static long mNoteId = 0;
	private static long mParentId=NoteDataBase.DEFAULT_PARENTID;
	private static Button mButtonChangeColor;
	private static LinearLayout mLinearLayout;
	// Set the header color
	private static RelativeLayout mRelativeLayout;
	// Set the footer color. And the body color will be set in mNoteContent.
	private static TextView mFooterColor;
	// Create a array to mark each button to set color.
	// 0~4 represent yellow,pink,blue,green and gray.
	private static Button[] mButtonColor = new Button[5];
	// Is back to home
	private boolean mIsBack = false;
	// Save the state of this activity.
	private int mState = 0;
	// Get get current configured color in database.
	private static String mConfigColor=NoteDataBase.DEFAULT_COLOR;
	// Keep the current note content
	private static String mContent = "";
	// Receiver arguments
	private String RECEIVED_OPERATION = "operation";

	// Total colors
	public final static int COLORNUMBER = 5;
	private String[] mColorsHeader = new String[] { "#EFDF6B", "#FFD3D6",
			"#ADDFFF", "#D6DF8C", "#D6D3D6" };
	private final String[] mColorsBody = new String[] { "#FFF3A5", "#FFEFEF",
			"#D6EFFF", "#EFF7BD", "#FFFBFF" };
	private final static int[] mBackgroundResource = new int[] {
			R.drawable.notes_bg_yellow, R.drawable.notes_bg_pink,
			R.drawable.notes_bg_blue, R.drawable.notes_bg_green,
			R.drawable.notes_bg_gray };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.note_editor);
		// Edit Text View
		mNoteContent = (LineEditText) findViewById(R.id.edit_content);
		// The header layout
		mRelativeLayout = (RelativeLayout) findViewById(R.id.rv_header);
		// The footer view for change background color.
		mFooterColor = (TextView) findViewById(R.id.tv_footer);
		mShowDate = (TextView) findViewById(R.id.show_date);
		mSeekBar=(SeekBar)findViewById(R.id.seekbar_fontsize);
		mButtonChangeColor = (Button) findViewById(R.id.btn_change_color);
		mLinearLayout = (LinearLayout) findViewById(R.id.lv_layout);
		mButtonColor[0] = (Button) findViewById(R.id.btn_show_yellow);
		mButtonColor[1] = (Button) findViewById(R.id.btn_show_pink);
		mButtonColor[2] = (Button) findViewById(R.id.btn_show_blue);
		mButtonColor[3] = (Button) findViewById(R.id.btn_show_green);
		mButtonColor[4] = (Button) findViewById(R.id.btn_show_gray);

		mNoteContent.setText("");
		Bundle bundle = this.getIntent().getExtras();
		RECEIVED_OPERATION = bundle.getString(OPERATION);
		mNoteId = bundle.getLong(PASS_NOTE_ID);
		mParentId=bundle.getLong(PASS_NOTE_PARENTID);

		mNoteDB = new NoteDataBase(this);
		if (RECEIVED_OPERATION.equals(OPERATION_ADD)) {
			// Get current configured color.
			mConfigColor = mNoteDB.getConfigColor();
			mShowDate.setText("");
		}
		if (RECEIVED_OPERATION.equals(OPERATION_EDIT)) {
			Note note=mNoteDB.selectNoteById(mNoteId);
			//mCursor = mNoteDB.selectNoteById(mNoteId);
			if(note!=null){
				mContent=note.getmContent();
				mNoteContent.setText(mContent);
				mShowDate.setText(Helper.getDate(note.getmDate()));
				mConfigColor=note.getmColor();
			}
		}
		mButtonChangeColor.setOnClickListener(mHandler);
		for (int i = 0; i < 5; i++) {
			mButtonColor[i].setOnClickListener(mHandler);
		}
		mNoteContent.setOnClickListener(mHandler);
		mSeekBar.setOnSeekBarChangeListener(this);
		mSeekBar.setVisibility(View.INVISIBLE);
		setBackground(Integer.valueOf(mConfigColor));
		mNoteContent.setTextSize(mNoteDB.getConfigFontSize());
	}

	public View.OnClickListener mHandler = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_change_color:
				if (mLinearLayout.isShown()) {
					mLinearLayout.setVisibility(View.INVISIBLE);
				} else {
					mLinearLayout.setVisibility(View.VISIBLE);
				}
				break;
			case R.id.lv_layout:
				mLinearLayout.setVisibility(View.INVISIBLE);
				break;
			case R.id.btn_show_yellow:
				setBackground(0);// operation yellow;
				// updateColor();
				break;
			case R.id.btn_show_pink:
				setBackground(1);// operation pink;
				// updateColor();
				break;
			case R.id.btn_show_blue:
				setBackground(2);// operation blue;
				// updateColor();
				break;
			case R.id.btn_show_green:
				setBackground(3);// operation green;
				// updateColor();
				break;
			case R.id.btn_show_gray:
				setBackground(4);// operation gray;
				// updateColor();
				break;
			case R.id.edit_content:
				mLinearLayout.setVisibility(View.INVISIBLE);
				mSeekBar.setVisibility(View.INVISIBLE);
			}
		}
	};

	// Update the color of current note and configured color.
	public void updateColor() {
		mNoteDB.updateColorById(mNoteId, mConfigColor);
	}

	public void setBackground(int index) {
		if (index > -1 && index < COLORNUMBER) {
			mConfigColor = String.valueOf(index);
			mRelativeLayout.setBackgroundColor(Color
					.parseColor(mColorsHeader[index]));
			mNoteContent.setBackgroundColor(Color
					.parseColor(mColorsBody[index]));
			mFooterColor.setBackgroundResource(mBackgroundResource[index]);
			mLinearLayout.setVisibility(View.INVISIBLE);
			for (int i = 0; i < COLORNUMBER; i++) {
				mButtonColor[i]
						.setBackgroundResource(R.drawable.zhengwen_xuanyanse_dangqian_bai);
			}
			mButtonColor[index]
					.setBackgroundResource(R.drawable.zhengwen_xuanyanse_dangqian);
		}
	}

	/**
	 * Show menu to response to clicking menu event
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		if (mNoteId == 0) {
			menu.add(Menu.FIRST, MENU_REVERT, 0, R.string.revert).setIcon(
					android.R.drawable.ic_menu_revert);
			return true;
		} else {
			menu.add(Menu.FIRST, MENU_REVERT, 0, R.string.revert).setIcon(
					android.R.drawable.ic_menu_revert);
			menu.add(Menu.FIRST, MENU_DELETE_NOTE, 0, R.string.delete_note)
					.setIcon(android.R.drawable.ic_menu_delete);
			menu.add(Menu.FIRST, MENU_EDIT_TITLE, 0, R.string.edit_title)
			.setIcon(R.drawable.app_notes);
			menu.add(Menu.FIRST, MENU_CHANGE_FONTSIZE, 0, R.string.change_font_size)
			.setIcon(R.drawable.zitidaxiao_hui);
			menu.add(Menu.FIRST, MENU_SET_ALARM, 0, R.string.set_alarm)
			.setIcon(R.drawable.caidan_tixing_hui);
			menu.add(Menu.FIRST, MENU_ADD_SHORTCUT, 0, R.string.add_shortcut_home)
			.setIcon(R.drawable.caidan_zhuomian_hui);
		}
		return true;
	}

	/**
	 * Dispose the event according to selected item of menu
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case MENU_REVERT:
			revert();
			break;
		case MENU_DELETE_NOTE:
			deleteNote();
			break;
		case MENU_EDIT_TITLE:
			editTitle();
			break;
		case MENU_CHANGE_FONTSIZE:
			changeFontSize();
			break;
		case MENU_SET_ALARM:
			setAlarm();
			break;
		case MENU_ADD_SHORTCUT:
			addShortCut();
			break;
		}
		return true;
	}

	private void addShortCut() {

		Intent intent2 = new Intent(Intent.ACTION_MAIN);
		intent2.addCategory(Intent.CATEGORY_LAUNCHER);
		intent2.setComponent(new ComponentName(this.getPackageName(), this
				.getPackageName() + ".EditNote"));
		//intent2.putExtra(CreateNote.KEY_ID, mNotes[0].getId());
		intent2.putExtra(OPERATION, RECEIVED_OPERATION);
		intent2.putExtra(PASS_NOTE_ID, mNoteId);
		intent2.putExtra(PASS_NOTE_PARENTID, mParentId);
		
		
		Intent intent = new Intent(ACTION_ADD_SHORTCUT);
		intent.putExtra(Intent.EXTRA_SHORTCUT_NAME,mNoteContent.getText().toString());
		intent.putExtra("duplicate", false);
		intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent2);
		intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
				Intent.ShortcutIconResource.fromContext(this,
						R.drawable.icon_one));
		//intent.putExtra(PASS_FOLDERID, mFolderId);
		//intent.putExtra(PASS_FOLDERTITLE, mFolderTitle); 
		sendBroadcast(intent);
		
	}

	// 设置便签的提醒时间
	private void setAlarm() {
		//Log.d(MainActivity.TAG, "NoteActivity==>Set Alarm");
		// 获得AlarmManager
		final AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		final Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);
		mHour = c.get(Calendar.HOUR_OF_DAY);
		mMinute = c.get(Calendar.MINUTE);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		//builder.setTitle(R.string.alarm_time);
		builder.setTitle("Set Alarm");
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.alarm, null);
		builder.setView(view);
		// 点击设置闹钟日期
		final Button btnAlarmDate = (Button) view
				.findViewById(R.id.alarm_date);
		btnAlarmDate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DatePickerDialog dpd = new DatePickerDialog(EditNoteActivity.this,
						new DatePickerDialog.OnDateSetListener() {
							@Override
							public void onDateSet(DatePicker view, int year,
									int monthOfYear, int dayOfMonth) {
								mYear = year;
								mMonth = monthOfYear;
								mDay = dayOfMonth;
								String alarmDate = mYear + "-" + (mMonth+1) + "-"
										+ mDay;
								btnAlarmDate.setText(alarmDate);
								Log.d("EditNote",
										"NoteActivity==>设置的闹钟日期: " + alarmDate);
							}
						}, mYear, mMonth, mDay);
				dpd.show();
			}
		});
		// 点击设置闹钟时间
		final Button btnAlarmTime = (Button) view
				.findViewById(R.id.alarm_time);
		btnAlarmTime.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				TimePickerDialog tpd = new TimePickerDialog(EditNoteActivity.this,
						new TimePickerDialog.OnTimeSetListener() {
							@Override
							public void onTimeSet(TimePicker view,
									int hourOfDay, int minute) {
								mHour = hourOfDay;
								mMinute = minute;
								String alarmTime = hourOfDay + ":" + minute;
								btnAlarmTime.setText(alarmTime);
								Log.i("EditNote",
										"NoteActivity==>设置的闹钟时间" + alarmTime);
							}
						}, mHour, mMinute, true);
				tpd.show();
			}
		});
		builder.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 检测时间是否合理,如:不能早于现在
						if (checkAlarmTime(am)) {
							hasSetAlartTime = true;
							dialog.dismiss();
							Toast.makeText(getApplicationContext(), "设置提醒时间成功",
									Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(getApplicationContext(), "设置提醒时间失败",
									Toast.LENGTH_SHORT).show();
						}
					}
				});
		builder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						hasSetAlartTime = false;
						cancelAlarm(am);
						dialog.dismiss();
					}
				});
		builder.create().show();
	}
	private boolean checkAlarmTime(AlarmManager am) {
		Log.i("EditNote", "NoteActivity==>checkAlarmTime()");
		Calendar alarmCalendar = Calendar.getInstance();
		alarmCalendar.set(mYear, mMonth, mDay, mHour, mMinute, 0);
		// 使用传递过来的intent,因为它包含了打开EditNote所需的一切参数
		Intent intent = new Intent();
		intent.setClass(EditNoteActivity.this, AlarmReceiver.class);
		//intent.putExtra(OPERATION, RECEIVED_OPERATION);
		intent.putExtra(PASS_NOTE_ID, mNoteId);
		//intent.putExtra(PASS_NOTE_PARENTID, mParentId);
		
		PendingIntent pi = PendingIntent.getBroadcast(EditNoteActivity.this, (int) mNoteId,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		if (!alarmCalendar.before(Calendar.getInstance())) {// 判断时间设置是否合理
			am.set(AlarmManager.RTC_WAKEUP, alarmCalendar.getTimeInMillis(), pi);
			return true;
		}
		return false;
	}


	private void cancelAlarm(AlarmManager am) {
		Intent intent = new Intent();
		intent.setClass(EditNoteActivity.this, AlarmReceiver.class);
		//intent.putExtra(OPERATION, RECEIVED_OPERATION);
		intent.putExtra(PASS_NOTE_ID, mNoteId);
		//intent.putExtra(PASS_NOTE_PARENTID, mParentId);
		
		PendingIntent pi = PendingIntent.getBroadcast(EditNoteActivity.this, (int) mNoteId,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		am.cancel(pi);
	}
	
	/*
	 * change font size
	 */
	private void changeFontSize() {
		if(mSeekBar.getVisibility()==View.VISIBLE){
			mSeekBar.setVisibility(View.GONE);
		} else{
			mSeekBar.setVisibility(View.VISIBLE);
			mSeekBar.setProgress((int)((mNoteDB.getConfigFontSize()-10)*2.5));
		}
	}

	/**
	 * Back to list page
	 */
	private void revert() {
		if (RECEIVED_OPERATION.equals(OPERATION_ADD) && mNoteId != 0) {
			mNoteDB = new NoteDataBase(this);
			mNoteDB.delete(mNoteId);
			mNoteId = 0;
		}
		finish();
	}

	/**
	 * Delete this note
	 */
	private void deleteNote() {
		if (mNoteId == 0) {
			return;
		}
		mNoteDB = new NoteDataBase(this);
		mNoteDB.delete(mNoteId);
		mNoteDB.close();
		mNoteId = 0;
		finish();
	}

	/**
	 * Jump to the title editor page
	 */
	private void editTitle() {
		if (mNoteId == 0) {
			return;
		}
		// If the activity "EditNote" will be activated,
		// the method onPause() will do operations according to "mState"
		mState = 1;
		Intent intent = new Intent(this, EditTitleActivity.class);
		intent.putExtra(EditTitleActivity.PASS_NOTE_ID, mNoteId);
		startActivity(intent);
	}

	@Override
	public void onRestart() {
		super.onRestart();
		mState = 0;
	}

	@Override
	public void onPause() {
		super.onPause();
		String content = mNoteContent.getText().toString();

		// mState=1 means the activity is jumping to "EditNote",
		// the current activity will not save data into database.
		if (mState == 1) {
			return;
		}
		if (mIsBack) {
			mIsBack = false;
			if (RECEIVED_OPERATION.equals(OPERATION_EDIT) && mNoteId != 0
					&& content.length() > 0) {
				if(mContent.equals(content)) {
					updateColor();
					return;
				}
				mNoteDB = new NoteDataBase(this);
				mNoteDB.updateContentById(mNoteId, content);
				updateColor();
				mNoteDB.close();
			} else if (RECEIVED_OPERATION.equals(OPERATION_ADD) && mNoteId != 0) {
				if (content.length() == 0) {
					mNoteDB = new NoteDataBase(this);
					mNoteDB.delete(mNoteId);
					mNoteDB.close();
					//mNoteDB = null;
					mNoteId = 0;
				} else {
					mNoteDB = new NoteDataBase(this);
					// Get current time
					long curDate = System.currentTimeMillis();
					mNoteDB.update(mNoteId, null, content, curDate);
					updateColor();
					mNoteDB.close();
					//mNoteDB = null;
				}
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		String content = mNoteContent.getText().toString();

		if (RECEIVED_OPERATION.equals(OPERATION_ADD) && mNoteId != 0
				&& content.equals("")) {
			mNoteDB = new NoteDataBase(this);
			mNoteDB.delete(mNoteId);
			mNoteDB.close();
			mNoteDB = null;
		}
	}

	/**
	 * Save the key "back" event
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			mIsBack = true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * A custom EditText that draws lines between each line of text that is
	 * displayed.
	 */
	public static class LinedEditText extends EditText {
		private Rect mRect;
		private Paint mPaint;

		// we need this constructor for LayoutInflater
		public LinedEditText(Context context, AttributeSet attrs) {
			super(context, attrs);
			mRect = new Rect();
			mPaint = new Paint();
			mPaint.setStyle(Paint.Style.STROKE);
			mPaint.setColor(0x800000FF);
		}

		@Override
		protected void onDraw(Canvas canvas) {
			int count = getLineCount();
			Rect r = mRect;
			Paint paint = mPaint;

			for (int i = 0; i < count; i++) {
				int baseline = getLineBounds(i, r);

				canvas.drawLine(r.left, baseline + 1, r.right, baseline + 1,
						paint);
			}

			super.onDraw(canvas);
		}
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		mNoteContent.setTextSize((float) (0.4*progress+10));
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		String fontSize=String.valueOf(0.4*seekBar.getProgress()+10);
		mNoteDB.setConfigFontSize(fontSize);
	}

}
