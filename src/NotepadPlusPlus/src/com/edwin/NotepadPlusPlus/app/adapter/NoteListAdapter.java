package com.edwin.NotepadPlusPlus.app.adapter;

import java.util.ArrayList;
import java.util.Map;

import com.edwin.NotepadPlusPlus.R;
import com.edwin.NotepadPlusPlus.R.drawable;
import com.edwin.NotepadPlusPlus.R.id;
import com.edwin.NotepadPlusPlus.R.layout;
import com.edwin.NotepadPlusPlus.app.activity.NotepadPlusPlusActivity;
import com.edwin.NotepadPlusPlus.app.model.Note;
import com.edwin.NotepadPlusPlus.app.model.ViewHolder;
import com.edwin.NotepadPlusPlus.database.Helper;
import com.edwin.NotepadPlusPlus.database.NoteDataBase;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

/**
 * Show, move or delete items
 */
public class NoteListAdapter extends BaseAdapter {
	private NoteDataBase mNoteDB;
	private LayoutInflater mInflater;
	private boolean mIsShow = true;
	// Keep the state whether the item is selected
	public static Map<Integer, Boolean> mIsSelected;
	private Context mContext;

	private ArrayList<Note> mNoteArrayList;

	public NoteListAdapter(Context _context, ArrayList<Note> _list,
			Map<Integer, Boolean> isSelected, boolean isShow) {
		mContext = _context;
		mIsShow = isShow;
		mNoteArrayList = _list;
		mIsSelected = isSelected;
		mNoteDB = new NoteDataBase(mContext);
		mInflater = LayoutInflater.from(mContext);
	}

	public NoteListAdapter(Context _context, Cursor _cursor,
			Map<Integer, Boolean> isSelected, boolean isShow) {
		mContext = _context;
		mIsShow = isShow;
		mIsSelected = isSelected;
		mNoteDB = new NoteDataBase(mContext);
		mInflater = LayoutInflater.from(mContext);
	}

	@Override
	public int getCount() {
		if (mNoteArrayList != null) {
			return mNoteArrayList.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		if (mNoteArrayList != null) {
			if (position < 0 || position > mNoteArrayList.size() - 1) {
				return 0;
			}
			Note note = mNoteArrayList.get(position);// mCursor.moveToPosition(position);
			return note;
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		if (mNoteArrayList != null) {
			if (position < 0 || position > mNoteArrayList.size() - 1) {
				return 0;
			}
			Note note = mNoteArrayList.get(position);// mCursor.moveToPosition(position);
			return note.getmId();
		}
		return 0;
	}

	public long getItemFolderId(int position) {
		if (mNoteArrayList != null) {
			if (position < 0 || position > mNoteArrayList.size() - 1) {
				return NoteDataBase.DEFAULT_FOLDERID;
			}
			Note note = mNoteArrayList.get(position);
			return note.getmFolderId();
		}
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (mNoteArrayList == null) {
			return null;
		}
		// Keep the content from database
		String title, content, color, isFolder;
		long date, parentId;
		Note note = mNoteArrayList.get(position);
		title = note.getmTitle();
		content = note.getmContent();
		date = note.getmDate();
		color = note.getmColor();
		isFolder = note.getmIsFolder();
		parentId = note.getmParentId();
		if (color == null || color == "") {
			color = "0";
		}
		String showTitle = getTitle(title, content);

		ViewHolder holder = null;
		// If convertView is null,initialize it.
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.sub_main, null);

			holder.showTitle = (TextView) convertView
					.findViewById(R.id.list_showtitle);
			holder.showDate = (TextView) convertView
					.findViewById(R.id.list_showdate);
			holder.checkboxItem = (CheckBox) convertView
					.findViewById(R.id.ch_note_item);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (isFolder.equals(NoteDataBase.ISFOLDER_YES)) {
			convertView.setBackgroundResource(R.drawable.listfolderbg);
		} else {
			switch (Integer.valueOf(color)) {
			case 0:
				convertView.setBackgroundResource(R.drawable.itembg_yellow);
				break;
			case 1:
				convertView.setBackgroundResource(R.drawable.itembg_pink);
				break;
			case 2:
				convertView.setBackgroundResource(R.drawable.itembg_blue);
				break;
			case 3:
				convertView.setBackgroundResource(R.drawable.itembg_green);
				break;
			case 4:
				convertView.setBackgroundResource(R.drawable.itembg_gray);
				break;
			default:
				convertView.setBackgroundResource(R.drawable.itembg_yellow);
				break;
			}

		}
		Log.v("NoteListAdapter",
				"Position:" + position + " " + convertView.toString());
		Log.v("NoteListAdapter", "getHeight:" + convertView.getHeight());
		Log.v("NoteListAdapter", "getWidth:" + convertView.getWidth());
		holder.showTitle.setText(showTitle);
		holder.showDate.setText(Helper.getDate(date));
		if (!mIsShow) {// delete or move items
			holder.showDate.setVisibility(View.INVISIBLE);
			if (!mIsSelected.isEmpty()) {
				if (mIsSelected.containsKey(position)) {
					holder.checkboxItem.setChecked(mIsSelected.get(position));
				}
			}
		} else {// Show items
			holder.checkboxItem.setVisibility(View.INVISIBLE);
		}
		Log.i("NoteListAdapter", "position:isFolder:parentId:" + position + " "
				+ isFolder + " " + parentId);
		return convertView;
	}

	private String getTitle(String title, String content) {
		String showTitle;
		if (title == null || title == "") {
			if (content != null && content != "") {
				showTitle = getShow(content);
			} else {
				showTitle = "";
			}
		} else {
			showTitle = getShow(title);
		}
		return showTitle;
	}

	/**
	 * Get the title to be shown in the ListView;
	 */
	private String getShow(String show) {
		if (show != null && show != "") {
			if (show.length() > NotepadPlusPlusActivity.mShowtitleLength) {
				show = show.substring(0,
						NotepadPlusPlusActivity.mShowtitleLength - 1) + "...";
				return show;
			} else {
				return show;
			}
		}
		return null;
	}

	public void refreshListView() {
		mNoteArrayList = mNoteDB.select();
		notifyDataSetChanged();
		Log.i("ListAdapter", "refreshListView() is called.");
	}

}