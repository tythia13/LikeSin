package com.edwin.NotepadPlusPlus.app.activity;

import java.io.File;
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
import com.edwin.NotepadPlusPlus.database.BackupDatabase;
import com.edwin.NotepadPlusPlus.database.Helper;
import com.edwin.NotepadPlusPlus.database.NoteDataBase;
import com.edwin.NotepadPlusPlus.util.ExternalXML;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

public class NotepadPlusPlusActivity extends Activity implements
		AdapterView.OnItemClickListener, OnClickListener,
		AdapterView.OnItemLongClickListener, AdapterView.OnItemSelectedListener {

	public final static int[] mBackgroundResource = new int[] {
			R.drawable.biaoqian_huang, R.drawable.biaoqian_fen,
			R.drawable.biaoqian_lan, R.drawable.biaoqian_lu,
			R.drawable.biaoqian_hui };
	// Menu
	protected final static int MENU_ADD = Menu.FIRST;
	protected final static int MENU_EDIT_NOTE = Menu.FIRST + 1;
	protected final static int MENU_EDIT_TITLE = Menu.FIRST + 2;
	protected final static int MENU_ITEM_DELETE = Menu.FIRST + 3;
	protected final static int MENU_ITEM_BACKUPDATA = Menu.FIRST + 4;
	protected final static int MENU_ITEM_RESTOREDATA = Menu.FIRST + 5;
	protected final static int MENU_ITEM_EXPORTTOTXTFILE = Menu.FIRST + 6;
	protected final static int MENU_ITEM_ADDFOLDER = Menu.FIRST + 7;
	protected final static int MENU_ITEM_MOVEINTOFOLDER = Menu.FIRST + 8;
	protected final static int MENU_ITEM_SETPASSWORD = Menu.FIRST + 9;
	protected final static int MENU_ITEM_GETMORE = Menu.FIRST + 10;
	protected final static int MENU_ITEM_ABOUT = Menu.FIRST + 11;

	private static NoteDataBase mNoteDB;
	// private Cursor mCursor;
	private ArrayList<Note> mNoteArrayList;
	private DragListView mNoteList;
	private Button mButtonAdd;
	private static long mNoteId = 0;
	// Save the state of the current activity.
	private static int mState = 0;
	// Whether back from editor
	private static boolean mIsbackFormEdit = false;
	// Keep if the delete button is clicked
	// private static boolean mIsDeleteClicked = false;
	// Keep the status of Map
	private static boolean mIsMapCleared = false;
	// The matched layout with delete operation.
	private static LinearLayout mDeleteLayout;
	private static LinearLayout mNewFolderLayout;
	private static Button mDeleteButton;
	private static Button mCancelDeleteButton;
	private static Button mSaveFolder;
	private static Button mCancelSaveFolder;
	private static EditText mFolderName;
	// Choose operation title when long press the list item.
	private final String CHOOSE_OPERATION = "Please choose your operation!";
	private final String TOST_BACKUP_SUCCESS = "Buckup successful";
	private final String EXPORT_SUCCESS = "Export successful";
	private final String TOSDCARD = "To SD Card";
	private final String TOEMAIL = "To Email";
	private final String TIPRESTORDATA = "Current data will be overwritten";
	private final String OK = "Ok";
	private final String CANCEL = "Cancel";
	// Keep the state whether the item is selected
	public static Map<Integer, Boolean> mIsSelected = new HashMap<Integer, Boolean>();
	// Keep the ids
	public static Map<Integer, Long> mIds = new HashMap<Integer, Long>();
	// Keep the item click event
	public static ImageView mTempImageView = null;
	private boolean mIsAddActive = true;

	// Show title lenth
	private final int SHOW_TITLE_LENGTH_PORT = 18;
	private final int SHOW_TITLE_LENGTH_LAND = 40;
	public static int mShowtitleLength;
	private static long mFolderId = NoteDataBase.DEFAULT_FOLDERID;
	private static long mParentId = NoteDataBase.DEFAULT_PARENTID;
	private static int mInputpasswordTime = 0;
	private AlertDialog mGetPasswodDialog;

	// 0 nothing,1 delete,2 move,3 new folder name.
	private static int[] operations = new int[] { 0, 1, 2, 3 };
	private static int operation = operations[0];

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		setLayout();
		// Helper.getDate(System.currentTimeMillis());
		mNoteList = (DragListView) findViewById(R.id.notelist);
		mButtonAdd = (Button) findViewById(R.id.button_add);
		mDeleteLayout = (LinearLayout) findViewById(R.id.lv_delete_layout);
		mNewFolderLayout = (LinearLayout) findViewById(R.id.lv_newfolder_layout);
		mDeleteButton = (Button) findViewById(R.id.btn_delete_item);
		mCancelDeleteButton = (Button) findViewById(R.id.btn_cancel_delete_item);
		mSaveFolder = (Button) findViewById(R.id.btn_save_folder);
		mCancelSaveFolder = (Button) findViewById(R.id.btn_cancel_folder);
		mFolderName = (EditText) findViewById(R.id.et_newfolder_name);
		// Hide the delete layout
		mDeleteLayout.setVisibility(View.GONE);
		mNewFolderLayout.setVisibility(View.GONE);
		// Set event listeners
		mNoteList.setOnItemClickListener(this);
		mNoteList.setOnItemSelectedListener(this);
		mNoteList.setOnItemLongClickListener(this);
		mNoteList.setOnCreateContextMenuListener(this);
		mButtonAdd.setOnClickListener(this);
		mDeleteButton.setOnClickListener(this);
		mCancelDeleteButton.setOnClickListener(this);
		mSaveFolder.setOnClickListener(this);
		mCancelSaveFolder.setOnClickListener(this);
		mNoteDB = new NoteDataBase(this);
		mNoteDB.setConfig();
		if (!mNoteDB.getPassword().equals(NoteDataBase.DEFAULT_CONFIG_PASSWORD)) {
			getPassword();
		} else {
			refreshList(true);
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.button_add:
			if (mIsAddActive) {
				mButtonAdd.setBackgroundResource(R.drawable.tianjia_2);
				addNote();
			}
			break;
		case R.id.btn_delete_item:
			// operation
			int j = -1;
			long[] ids = new long[mIds.size()];
			for (int i = 0; i < mNoteList.getCount(); i++) {
				if (mIds.containsKey(i)) {
					++j;
					ids[j] = mIds.get(i);
				}
			}
			if (ids.length > 0) {
				mNoteDB.delete(ids);
				ids = null;
			}
			mDeleteLayout.setVisibility(View.GONE);
			operation = operations[0];
			mIsAddActive = true;
			mIsMapCleared = false;
			mIsSelected.clear();
			mIds.clear();
			refreshList(true);
			break;
		case R.id.btn_cancel_delete_item:
			// operation
			mDeleteLayout.setVisibility(View.GONE);
			operation = operations[0];
			mIsAddActive = true;
			mIsMapCleared = false;
			refreshList(true);
			break;
		case R.id.btn_save_folder:
			if (mFolderName.getText().toString() == "") {
				return;
			} else {
				newFolder();
			}
			closeSoftKeyBoard();
			operation = operations[0];
			mNewFolderLayout.setVisibility(View.GONE);
			break;
		case R.id.btn_cancel_folder:
			closeSoftKeyBoard();
			operation = operations[0];
			mNewFolderLayout.setVisibility(View.GONE);
			break;
		}
	}

	private void closeSoftKeyBoard() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm.isActive()) {
			imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	/**
	 * Add a folder
	 */
	private void newFolder() {
		Note note = new Note();
		note.setmContent(mFolderName.getText().toString());
		note.setmAlertDate(NoteDataBase.DEFAULT_ALERTDATE);
		note.setmColor(NoteDataBase.DEFAULT_COLOR);
		note.setmDate(System.currentTimeMillis());
		note.setmIsFolder(NoteDataBase.ISFOLDER_YES);
		note.setmTitle(mFolderName.getText().toString());
		long noteId = mNoteDB.insert(note);
		if (noteId > 0) {
			refreshList(true);
		}
		// TODO set tags;
	}

	/**
	 * Generate menu to be shown
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(Menu.FIRST, MENU_ADD, 0, R.string.add).setIcon(
				R.drawable.caidan_tianjia_hui);
		menu.add(Menu.FIRST, MENU_ITEM_ADDFOLDER, 0, R.string.new_folder)
				.setIcon(R.drawable.caidan_xinjianfenzu_hui);
		menu.add(Menu.FIRST, MENU_ITEM_MOVEINTOFOLDER, 0,
				R.string.move_into_folder)
				.setIcon(R.drawable.caidan_yidong_hui);
		menu.add(Menu.FIRST, MENU_EDIT_NOTE, 0, R.string.edit_note).setIcon(
				R.drawable.app_notes);
		menu.add(Menu.FIRST, MENU_EDIT_TITLE, 0, R.string.edit_title).setIcon(
				R.drawable.app_notes);
		menu.add(Menu.FIRST, MENU_ITEM_DELETE, 0, R.string.delete_note)
				.setIcon(R.drawable.caidan_shanchu_hui);
		menu.add(Menu.FIRST, MENU_ITEM_EXPORTTOTXTFILE, 0,
				R.string.export_to_sdcard)
				.setIcon(R.drawable.caidan_shuchu_hui);
		menu.add(Menu.FIRST, MENU_ITEM_BACKUPDATA, 0, R.string.backup_data);
		menu.add(Menu.FIRST, MENU_ITEM_RESTOREDATA, 0, R.string.restore_data);

		menu.add(Menu.FIRST, MENU_ITEM_SETPASSWORD, 0, R.string.set_password);
		menu.add(Menu.FIRST, MENU_ITEM_GETMORE, 0, R.string.get_more);
		menu.add(Menu.FIRST, MENU_ITEM_ABOUT, 0, R.string.about);
		return true;
	}

	/**
	 * Show menu to response to clicking menu event
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		menu.findItem(MENU_EDIT_NOTE).setVisible(false);
		menu.findItem(MENU_EDIT_TITLE).setVisible(false);
		return true;
	}

	/**
	 * Response to the event activated by the selected item of the menu
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case MENU_ADD:
			addNote();
			break;
		case MENU_EDIT_NOTE:
			editNote();
			break;
		case MENU_EDIT_TITLE:
			editTitle();
			break;
		case MENU_ITEM_DELETE:
			showDeleleItems();
			break;
		case MENU_ITEM_BACKUPDATA:
			backupData();
			break;
		case MENU_ITEM_RESTOREDATA:
			restoreData();
			break;
		case MENU_ITEM_EXPORTTOTXTFILE:
			exportToSDCard();
			break;
		case MENU_ITEM_ADDFOLDER:
			operation = operations[3];
			mNewFolderLayout.setVisibility(View.VISIBLE);
			break;
		case MENU_ITEM_MOVEINTOFOLDER:
			operation = operations[2];
			// TODO tasks
			break;
		case MENU_ITEM_SETPASSWORD:
			setOrChangePassword();
			break;
		case MENU_ITEM_GETMORE:
			// TODO getmore
			break;
		case MENU_ITEM_ABOUT:
			// TODO about
			break;
		}
		return true;
	}

	private void setOrChangePassword() {
		if (!mNoteDB.getPassword().equals(NoteDataBase.DEFAULT_CONFIG_PASSWORD)) {
			new AlertDialog.Builder(this).setItems(
					new String[] { "Change password", "Clear password" },
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							switch (which) {
							case 0:
								setPassword();
								break;
							case 1:
								mNoteDB.setPassword(NoteDataBase.DEFAULT_CONFIG_PASSWORD);
								return;
							}
						}
					}).show();
		} else {
			setPassword();
		}
	}

	private void setPassword() {
		LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(R.layout.set_password_dialog,
				(ViewGroup) findViewById(R.id.password_dialog));

		final EditText passwordView = (EditText) layout
				.findViewById(R.id.et_password);
		final EditText confirmPasswordView = (EditText) layout
				.findViewById(R.id.et_confirm_password);
		new AlertDialog.Builder(this)
				.setTitle("Set password")
				.setView(layout)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String password = passwordView.getText().toString();
						String confirmPassword = confirmPasswordView.getText()
								.toString();
						if (password.equals(confirmPassword)) {
							boolean isSet = mNoteDB.setPassword(password);
						} else {
							setPassword();
						}
					}
				})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
							}
						}).show();

	}

	private void getPassword() {
		final EditText passwordView = new EditText(this);
		mGetPasswodDialog = new AlertDialog.Builder(this)
				.setTitle("Input password")
				.setView(passwordView)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String password = mNoteDB.getPassword();
						if (password.equals(passwordView.getText().toString())) {
							refreshList(true);
							return;
						} else {
							mInputpasswordTime += 1;
							getPassword();
							Toast.makeText(
									NotepadPlusPlusActivity.this,
									"You have entered "
											+ mInputpasswordTime
											+ ", total:4. You can get help at http://www.xiaomi.com.",
									Toast.LENGTH_SHORT).show();
						}
						if (mInputpasswordTime > 3) {
							mInputpasswordTime = 0;
							finish();
						}
					}
				})
				.setNegativeButton("Quit",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								finish();
							}
						}).setCancelable(false).show();
	}

	/**
	 * Export database to a XML file in SD card
	 */
	private void exportToSDCard() {
		new AlertDialog.Builder(this).setItems(
				new String[] { TOSDCARD, TOEMAIL },
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if (which == 0) {
							ExternalXML.exportXML(mNoteDB.exportXML());
							Toast.makeText(NotepadPlusPlusActivity.this,
									EXPORT_SUCCESS, Toast.LENGTH_SHORT).show();
						} else {
							String filePath = Helper.backupAsTxt(mNoteDB
									.exportXML());
							sendEmail(filePath);
						}
					}
				}).show();
	}

	/**
	 * Recover database
	 */
	public void restoreData() {
		new AlertDialog.Builder(NotepadPlusPlusActivity.this)
				.setIcon(null)
				.setTitle(TIPRESTORDATA)
				.setPositiveButton(OK,
						new android.content.DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								new BackupDatabase(NotepadPlusPlusActivity.this)
										.execute(BackupDatabase.COMMAND_RESTORE);
								refreshList(true);
							}
						})
				.setNegativeButton(CANCEL,
						new android.content.DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
							}
						}).show();
	}

	/**
	 * Backup database
	 */
	private void backupData() {
		new AlertDialog.Builder(this).setItems(
				new String[] { TOSDCARD, TOEMAIL },
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if (which == 0) {
							new BackupDatabase(NotepadPlusPlusActivity.this)
									.execute(BackupDatabase.COMMAND_BACKUP);
							Toast.makeText(NotepadPlusPlusActivity.this,
									TOST_BACKUP_SUCCESS, Toast.LENGTH_SHORT)
									.show();
						} else {
							new BackupDatabase(NotepadPlusPlusActivity.this)
									.execute(BackupDatabase.COMMAND_BACKUP);
							String filePath = ExternalXML.exportXML(mNoteDB
									.exportXML());
							sendEmail(filePath);
						}
					}
				}).show();
	}

	private void sendEmail(String filePath) {
		if (filePath != null && filePath != "") {
			File file = new File(filePath);
			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.putExtra("subject", file.getName());
			intent.putExtra("body", "Android - email sender");
			intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
			if (file.getName().endsWith(".gz")) {
				intent.setType("application/x-gzip");
			} else if (file.getName().endsWith(".txt")
					|| file.getName().endsWith(".xml")) {
				intent.setType("text/plain");
			} else {
				intent.setType("application/octet-stream");
			}
			startActivity(intent);
		}
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
		note.setmParentId(NoteDataBase.DEFAULT_FOLDERID);
		mNoteId = mNoteDB.insert(note);
		if (mNoteId == 0) {
			return;
		}
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
	 * Jump to edit title activity
	 */
	public void editTitle() {
		if (mNoteId == 0) {
			return;
		}
		Intent intent = new Intent(this, EditTitleActivity.class);
		intent.putExtra(EditTitleActivity.PASS_NOTE_ID, mNoteId);
		startActivity(intent);
	}

	/**
	 * show delete note items
	 */
	private void showDeleleItems() {
		if (mNoteList.getCount() < 1) {
			return;
		}
		mIsAddActive = false;
		mDeleteLayout.setVisibility(View.VISIBLE);
		operation = operations[1];
		mIsSelected.clear();
		mIds.clear();
		refreshList(false);
	}

	/**
	 * If isShow is true,show items, otherwise show delete or move items.
	 * 
	 * @param isShow
	 */
	public void refreshList(boolean isShow) {
		mNoteArrayList = mNoteDB.select();
		if (isShow) {// show list
			mNoteList.setAdapter(new NoteListAdapter(this, mNoteArrayList,
					mIsSelected, true));
		} else {// show list delete
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
		String isFolder = note.getmIsFolder();
		if (operation == operations[1] || operation == operations[2]) {
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
		} else if (operation == operations[0]) {
			if (mNoteId == 0) {
				return;
			}
			if (isFolder.equals(NoteDataBase.ISFOLDER_YES)) {
				mFolderId = note.getmFolderId();
				String folderTitle = note.getmTitle();
				Intent intent = new Intent(this, FolderActivity.class);
				intent.putExtra(FolderActivity.PASS_FOLDERID, mFolderId);
				intent.putExtra(FolderActivity.PASS_FOLDERTITLE, folderTitle);
				startActivity(intent);
			} else {
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
	}

	/**
	 * Reset the mNoteId
	 */
	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}

	@Override
	public void onPause() {
		super.onPause();
		// if note-editor activity is activated set mState=1
		mState = 1;
	}

	@Override
	public void onRestart() {
		super.onRestart();
		Log.i("PlusPlus", "onRestart");
		mIsbackFormEdit = true;
		// Reset mButton's background image.
		mIsAddActive = true;
		mButtonAdd.setBackgroundResource(R.drawable.tianjia);
	}

	/**
	 * Update list view when this activity is restarted
	 */
	@Override
	public void onResume() {
		super.onResume();
		Log.i("PlusPlus", "onResume");
		// mState=1 means the app jump back from one of its sub-activities.
		if (mState == 1) {
			mNoteId = 0;
			mState = 0;
			refreshList(true);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mState = 0;
		if (mGetPasswodDialog != null) {
			mGetPasswodDialog.dismiss();
		}
		mNoteDB.close();
	}

	/**
	 * Cope the key event
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.i("ListActivity", "KeyEvent:" + keyCode + "=="
				+ KeyEvent.KEYCODE_BACK);
		if (keyCode == KeyEvent.KEYCODE_BACK && operation != operations[0]) {
			mDeleteLayout.setVisibility(View.GONE);
			mNewFolderLayout.setVisibility(View.GONE);
			operation = operations[0];
			mIsAddActive = true;
			mIsMapCleared = false;
			refreshList(true);
		} else if ((keyCode == KeyEvent.KEYCODE_HOME || keyCode == KeyEvent.KEYCODE_MENU)
				&& operation != operations[0]) {
			// mIsAddActive = true;
			// mIsDeleteClicked = false;
		} else if (keyCode == KeyEvent.KEYCODE_BACK
				&& mFolderId != NoteDataBase.DEFAULT_FOLDERID) {
			mFolderId = NoteDataBase.DEFAULT_FOLDERID;
			mParentId = NoteDataBase.DEFAULT_PARENTID;
			refreshList(true);
		} else {
			return super.onKeyDown(keyCode, event);
		}
		return true;
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
		//Note note=(Note)parent.getAdapter().getItem(position);
		Note note=mNoteDB.selectNoteById(id);
		if(note==null){
			return false;
		}
		String isFolder = note.getmIsFolder();
		Log.i("Idfolder",""+isFolder);
		if (isFolder.equals(NoteDataBase.ISFOLDER_YES)) {
			return false;
		} else {

			long noteId = note.getmId();
			mNoteArrayList = mNoteDB.selectNoteId(noteId);
			mNoteList.setAdapter(new NoteListAdapter(this, mNoteArrayList,
					mIsSelected, true));
			mNoteList.confirmStartDrag();
		}
		return false;
	}

}