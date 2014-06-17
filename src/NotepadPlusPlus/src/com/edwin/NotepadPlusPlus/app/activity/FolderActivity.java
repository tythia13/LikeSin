package com.edwin.NotepadPlusPlus.app.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.edwin.NotepadPlusPlus.R;
import com.edwin.NotepadPlusPlus.R.drawable;
import com.edwin.NotepadPlusPlus.R.id;
import com.edwin.NotepadPlusPlus.R.layout;
import com.edwin.NotepadPlusPlus.R.string;
import com.edwin.NotepadPlusPlus.app.adapter.NoteListAdapter;
import com.edwin.NotepadPlusPlus.app.model.Note;
import com.edwin.NotepadPlusPlus.app.model.ViewHolder;
import com.edwin.NotepadPlusPlus.app.view.DragListView;
import com.edwin.NotepadPlusPlus.database.NoteDataBase;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;

public class FolderActivity extends Activity implements
		AdapterView.OnItemClickListener, OnClickListener,
		AdapterView.OnItemLongClickListener, AdapterView.OnItemSelectedListener {

	public final static int[] mBackgroundResource = new int[] {
			R.drawable.biaoqian_huang, R.drawable.biaoqian_fen,
			R.drawable.biaoqian_lan, R.drawable.biaoqian_lu,
			R.drawable.biaoqian_hui };
	// Menu
	protected final static int MENU_ADD_NOTE = Menu.FIRST;
	protected final static int MENU_EDIT_FOLDER_TITLE = Menu.FIRST + 1;
	protected final static int MENU_DELETE = Menu.FIRST + 2;
	protected final static int MENU_MOVEOUTOFFOLDER = Menu.FIRST + 3;
	protected final static int MENU_ITEM_ADDSHORTTOHOME = Menu.FIRST + 4;

	private static NoteDataBase mNoteDB;
	private DragListView mNoteList;
	private Button mButtonAdd;
	private static long mNoteId = 0;
	// Save the state of the current activity.
	private static int mState = 0;
	// Whether back from editor
	private static boolean mIsbackFormEdit = false;
	// Keep the status of Map
	private static boolean mIsMapCleared = false;

	private static Button mDeleteOrMoveButton;
	private static Button mCancelDeleteOrMoveButton;
	private static Button mSaveFolderTitle;
	private static Button mCancelSaveFolderTitle;
	private static EditText mFolderName;
	// Keep the state whether the item is selected
	public static Map<Integer, Boolean> mIsSelected = new HashMap<Integer, Boolean>();
	// Keep the ids
	public static Map<Integer, Long> mIds = new HashMap<Integer, Long>();
	private boolean mIsAddActive = true;

	// Show title lenth
	private final int SHOW_TITLE_LENGTH_PORT = 18;
	private final int SHOW_TITLE_LENGTH_LAND = 40;
	public static int mShowtitleLength;
	private static long mFolderId = NoteDataBase.DEFAULT_FOLDERID;
	private static String mFolderTitle;

	public static String PASS_FOLDERID = "PassFolderId";
	public static String PASS_FOLDERTITLE = "PassFolderTitle";
	private final String ACTION_ADD_SHORTCUT = "com.android.launcher.action.INSTALL_SHORTCUT";
	private TextView mListTitle;
	// The matched layout with delete operation.
	private static LinearLayout mDeleteOrMoveLayout;
	private static LinearLayout mNewTitleLayout;
	private static String MOVEITEM = "OK";
	private static String DELETE = "Delete";
	// 0 do nothing,1 delete,2 move,3 new folder name.
	private static int[] operations = new int[] { 0, 1, 2, 3 };
	private static int operation = operations[0];

	private ArrayList<Note> mNoteArrayList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.folder_layout);
		setLayout();
		mNoteDB = new NoteDataBase(this);
		Bundle bundle = this.getIntent().getExtras();
		mFolderId = bundle.getLong(PASS_FOLDERID);
		mFolderTitle = bundle.getString(PASS_FOLDERTITLE);

		mNoteList = (DragListView) findViewById(R.id.folder_notelist);
		mListTitle = (TextView) findViewById(R.id.folder_list_title);
		mDeleteOrMoveLayout = (LinearLayout) findViewById(R.id.folder_lv_delete_layout);
		mNewTitleLayout = (LinearLayout) findViewById(R.id.folder_lv_newfolder_layout);
		mButtonAdd = (Button) findViewById(R.id.button_add);
		mFolderName = (EditText) findViewById(R.id.et_newfolder_name);

		mDeleteOrMoveButton = (Button) findViewById(R.id.btn_delete_or_move_item);
		mCancelDeleteOrMoveButton = (Button) findViewById(R.id.btn_cancel_delete_or_move_item);

		mSaveFolderTitle = (Button) findViewById(R.id.btn_save_folder_title);
		mCancelSaveFolderTitle = (Button) findViewById(R.id.btn_cancel_folder_title);

		// Set event listeners
		mNoteList.setOnItemClickListener(this);
		mNoteList.setOnItemSelectedListener(this);
		mNoteList.setOnItemLongClickListener(this);
		mNoteList.setOnCreateContextMenuListener(this);
		mButtonAdd.setOnClickListener(this);
		mDeleteOrMoveButton.setOnClickListener(this);
		mCancelDeleteOrMoveButton.setOnClickListener(this);
		mCancelSaveFolderTitle.setOnClickListener(this);
		mSaveFolderTitle.setOnClickListener(this);

		// Hide the layouts
		mDeleteOrMoveLayout.setVisibility(View.GONE);
		mNewTitleLayout.setVisibility(View.GONE);

		if (mFolderTitle.length() > 15) {
			mFolderTitle = mFolderTitle.substring(0, 14) + "…";
		}
		mListTitle.setText(mFolderTitle);
		if (mFolderId != NoteDataBase.DEFAULT_FOLDERID) {
			refreshList(true);
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.button_add:
			if (mNewTitleLayout.getVisibility() == View.VISIBLE) {
				return;
			}
			if (mIsAddActive) {
				mButtonAdd.setBackgroundResource(R.drawable.tianjia_2);
				addNote();
			}
			break;
		case R.id.btn_delete_or_move_item:
			String btnText = mDeleteOrMoveButton.getText().toString();
			int j = -1;
			long[] ids = new long[mIds.size()];
			for (int i = 0; i < mNoteList.getCount(); i++) {
				if (mIds.containsKey(i)) {
					++j;
					ids[j] = mIds.get(i);
				}
			}
			if (ids.length > 0) {
				if (btnText.equals(MOVEITEM)) {// Move out of folder
					moveOutOfFolder(ids);
				} else if (btnText.equals(DELETE)) { // Delete operation
					mNoteDB.delete(ids);
				}
				ids = null;
			}
			mDeleteOrMoveLayout.setVisibility(View.GONE);
			operation = operations[0];

			mIsAddActive = true;
			mIsMapCleared = false;
			mIsSelected.clear();
			mIds.clear();
			refreshList(true);
			break;
		case R.id.btn_cancel_delete_or_move_item:
			if (mDeleteOrMoveButton.getText().toString().equals(MOVEITEM)) {
				// move items
			} else {// delete items
			}
			mDeleteOrMoveLayout.setVisibility(View.GONE);
			operation = operations[0];
			mIsAddActive = true;
			mIsMapCleared = false;
			mIsSelected.clear();
			refreshList(true);
			break;
		case R.id.btn_save_folder_title:
			if (mFolderName.getText().toString() == "") {
				return;
			} else {
				saveFolderTitle();
			}
			mIsAddActive = true;
			operation = operations[0];
			closeSoftKeyBoard();
			mNewTitleLayout.setVisibility(View.GONE);
			break;
		case R.id.btn_cancel_folder_title:
			closeSoftKeyBoard();
			operation = operations[0];
			mIsAddActive = true;
			mNewTitleLayout.setVisibility(View.GONE);
			break;
		}
	}

	private void moveOutOfFolder(long[] ids) {
		mNoteDB.setDefaultParentId(ids);
	}

	private void closeSoftKeyBoard() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm.isActive()) {
			imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	/**
	 * Generate menu to be shown
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(Menu.FIRST, MENU_ADD_NOTE, 0, R.string.add).setIcon(
				R.drawable.caidan_tianjia_hui);
		menu.add(Menu.FIRST, MENU_EDIT_FOLDER_TITLE, 0,
				R.string.new_folder_title).setIcon(
				R.drawable.caidan_bianjifenzu_hui);
		menu.add(Menu.FIRST, MENU_MOVEOUTOFFOLDER, 0,
				R.string.move_out_of_folder).setIcon(
				R.drawable.caidan_yidong_hui);
		menu.add(Menu.FIRST, MENU_DELETE, 0, R.string.delete_note).setIcon(
				R.drawable.caidan_shanchu_hui);

		menu.add(Menu.FIRST, MENU_ITEM_ADDSHORTTOHOME, 0,
				R.string.add_shortcut_home).setIcon(
				R.drawable.caidan_zhuomian_hui);
		return true;
	}

	/**
	 * Show menu to response to clicking menu event
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		return true;
	}

	/**
	 * Response to the event activated by the selected item of the menu
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case MENU_ADD_NOTE:
			addNote();
			break;
		case MENU_ITEM_ADDSHORTTOHOME:
			addShortToHome();
			break;
		case MENU_DELETE:
			refreshList(false);
			mIsAddActive = false;
			operation = operations[1];
			mDeleteOrMoveLayout.setVisibility(View.VISIBLE);
			mDeleteOrMoveButton.setText(DELETE);
			break;
		case MENU_MOVEOUTOFFOLDER:
			refreshList(false);
			mIsAddActive = false;
			operation = operations[2];
			mDeleteOrMoveLayout.setVisibility(View.VISIBLE);
			mDeleteOrMoveButton.setText(MOVEITEM);
			break;
		case MENU_EDIT_FOLDER_TITLE:
			// refreshList(true);///////////////////////////////////////////////
			operation = operations[3];
			mIsAddActive = false;
			mNewTitleLayout.setVisibility(View.VISIBLE);
			break;
		}
		return true;
	}

	private void addShortToHome() {
		Intent intent2 = new Intent(Intent.ACTION_MAIN);
		intent2.addCategory(Intent.CATEGORY_LAUNCHER);
		intent2.setComponent(new ComponentName(this.getPackageName(), this
				.getPackageName() + ".FolderActivity"));
		// intent2.putExtra(CreateNote.KEY_ID, mNotes[0].getId());
		intent2.putExtra(PASS_FOLDERID, mFolderId);
		intent2.putExtra(PASS_FOLDERTITLE, mFolderTitle);

		Intent intent = new Intent(ACTION_ADD_SHORTCUT);
		intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, mFolderTitle);
		intent.putExtra("duplicate", false);
		intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent2);
		intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
				Intent.ShortcutIconResource.fromContext(this,
						R.drawable.icon_one));
		sendBroadcast(intent);
	}

	private void saveFolderTitle() {
		String folderTitle = mFolderName.getText().toString();
		mNoteDB.updateFolderTitle(mFolderId, folderTitle);
		if (folderTitle.length() > 15) {
			folderTitle = folderTitle.substring(0, 14) + "…";
		}
		mListTitle.setText(folderTitle);
	}

	/**
	 * Jump to add note activity
	 */
	private void addNote() {
		Note note = new Note();
		note.setmAlertDate(NoteDataBase.DEFAULT_ALERTDATE);
		note.setmColor(mNoteDB.getConfigColor());
		note.setmDate(System.currentTimeMillis());
		note.setmFolderId(NoteDataBase.DEFAULT_FOLDERID);
		note.setmIsFolder(NoteDataBase.ISFOLDER_NO);
		note.setmParentId(mFolderId);
		mNoteId = mNoteDB.insert(note);
		if (mNoteId == 0) {
			return;
		}
		mNoteDB.close();
		Intent intent = new Intent(this, EditNoteActivity.class);
		intent.putExtra(EditNoteActivity.PASS_NOTE_ID, mNoteId);
		intent.putExtra(EditNoteActivity.OPERATION, EditNoteActivity.OPERATION_ADD);
		startActivity(intent);
	}

	/**
	 * Jump to edit note activity
	 */
	public void editNote() {
		if (mNoteId == 0) {
			return;
		}
		Intent intent = new Intent(this, EditNoteActivity.class);
		intent.putExtra(EditNoteActivity.PASS_NOTE_ID, mNoteId);
		intent.putExtra(EditNoteActivity.OPERATION, EditNoteActivity.OPERATION_EDIT);
		startActivity(intent);
	}

	/**
	 * If isShow is true,show items, otherwise show delete or move items.
	 * 
	 * @param isShow
	 */
	public void refreshList(boolean isShow) {
		if (mFolderId != NoteDataBase.DEFAULT_FOLDERID) {
			mNoteArrayList = mNoteDB.selectNoteByFolderId(mFolderId);
		} else {
			Toast.makeText(this, "The folder is not found.", Toast.LENGTH_SHORT);
			finish();
		}
		if (isShow) {
			mNoteList.setAdapter(new NoteListAdapter(this, mNoteArrayList,
					mIsSelected, true));
		} else {
			mNoteList.setAdapter(new NoteListAdapter(this, mNoteArrayList,
					mIsSelected, false));
		}
	}

	/**
	 * Get the values of the selected item,and jump to edit note activity.
	 */
	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position,
			long id) {
		Note note = mNoteArrayList.get(position);
		mNoteId = note.getmId();
		if (operation == operations[1] || operation == operations[2]) {
			// if delete or move, try to selete items
			if (!mIsMapCleared) {
				for (int i = 0; i < mNoteList.getCount(); i++) {
					mIsSelected.put(i, false);
				}
				mIsMapCleared = true;
			}
			if (mIds.containsValue(mNoteId)) {
				mIds.remove(position);
			} else {
				mIds.put(position, mNoteId);
			}
			ViewHolder vHolder = (ViewHolder) view.getTag();
			vHolder.checkboxItem.toggle();
			if (mIsSelected.containsKey(position)) {
				mIsSelected.remove(position);
				mIsSelected.put(position, vHolder.checkboxItem.isChecked());
			} else {
				mIsSelected.put(position, vHolder.checkboxItem.isChecked());
			}
		} else if (operation == operations[3]) {
			return;
		} else if (operation == operations[0]) {
			mFolderId = note.getmFolderId();
			if (mNoteId != 0 && mFolderId != NoteDataBase.DEFAULT_FOLDERID) {
				Intent intent = new Intent(this, EditNoteActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString(EditNoteActivity.OPERATION, EditNoteActivity.OPERATION_EDIT);
				bundle.putLong(EditNoteActivity.PASS_NOTE_ID, mNoteId);
				bundle.putLong(EditNoteActivity.PASS_NOTE_PARENTID, mFolderId);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		}
	}

	/**
	 * Pass the id of the selected item to global variable mNoteId when the
	 * selected item is changed.
	 */
	@Override
	public void onItemSelected(AdapterView<?> arg0, View view, int position,
			long id) {
		Note note = mNoteArrayList.get(position);
		mNoteId = note.getmId();
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && operation != operations[0]) {
			mDeleteOrMoveLayout.setVisibility(View.GONE);
			mNewTitleLayout.setVisibility(View.GONE);
			operation = operations[0];
			mIsAddActive = true;
			mIsMapCleared = false;
			mIsSelected.clear();
			refreshList(true);
		} else if ((keyCode == KeyEvent.KEYCODE_HOME || keyCode == KeyEvent.KEYCODE_MENU)
				&& operation != operations[0]) {
			// mIsAddActive = true;
			// mIsDeleteClicked = false;
		} else {
			return super.onKeyDown(keyCode, event);
		}
		return true;
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onRestart() {
		super.onRestart();
		mButtonAdd.setBackgroundResource(R.drawable.tianjia);
		refreshList(true);
	}

	/**
	 * Update list view when this activity is restarted
	 */
	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mNoteDB.close();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setLayout();
	}

	private void setLayout() {
		if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			mShowtitleLength = SHOW_TITLE_LENGTH_LAND;
		} else {
			mShowtitleLength = SHOW_TITLE_LENGTH_PORT;
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {

		return false;
	}

}