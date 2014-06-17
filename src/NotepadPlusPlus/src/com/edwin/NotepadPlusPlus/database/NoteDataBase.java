package com.edwin.NotepadPlusPlus.database;

import java.util.ArrayList;
import java.util.List;

import com.edwin.NotepadPlusPlus.app.model.Note;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class NoteDataBase extends SQLiteOpenHelper {
	public final static String CONFIG_ID = "config_id";
	public final static String CONFIG_COLOR = "config_color";
	public final static String CONFIG_FONTSIZE = "config_fontSize";
	public final static String CONFIG_PASSWORD = "config_password";
	public final static String NOTE_ID = "_id";
	public final static String NOTE_TITLE = "note_title";
	public final static String NOTE_CONTENT = "note_content";
	public final static String NOTE_DATE = "note_date";
	public final static String NOTE_COLOR = "note_color";
	public final static String NOTE_ALERTDATE = "note_alertDate";
	public final static String NOTE_FOLDERID = "note_folderId";
	public final static String NOTE_ISFOLDER = "is_folder";
	public final static String NOTE_PARENTID = "note_parentId";
	// Initial or default data
	public final static String DEFAULT_COLOR = "0";
	public final static long DEFAULT_DATE = System.currentTimeMillis();
	public final static long DEFAULT_ALERTDATE = 0;
	public final static long DEFAULT_FOLDERID = -1;
	public final static String ISFOLDER_NO = "no";
	public final static String ISFOLDER_YES = "yes";
	public final static long DEFAULT_PARENTID = -1;

	// Column index
	public final static int COLUMNINDEX_NOTEID = 0;
	public final static int COLUMNINDEX_NOTETITLE = 1;
	public final static int COLUMNINDEX_NOTECONTENT = 2;
	public final static int COLUMNINDEX_NOTEDATE = 3;
	public final static int COLUMNINDEX_NOTECOLOR = 4;
	public final static int COLUMNIDEX_NOTEALERTDATE = 5;
	public final static int COLUMNINDEX_NOTEISFOLDER = 6;
	public final static int COLUMNINDEX_NOTEFODERID = 7;
	public final static int COLUMNINDEX_NOTEPARENTID = 8;

	private final String DEFAULT_CONFIG_COLOR = "0";
	private final int DEFAULT_CONFIG_FONTSIZE = 10;
	public final static String DEFAULT_CONFIG_PASSWORD="8888";
	private final static String DATABASE_NAME = "Note.db";
	private final static int DATABASE_VERSION = 8;
	private final static String TABLE_NAME_NOTE = "note_table";
	private final static String TABLE_NAME_CONFIG = "config_table";
	private final static String ORDERBY_FOLDERID = NOTE_FOLDERID + " desc,"
			+ NOTE_DATE + " desc";

	/**
	 * Create an database
	 */
	public NoteDataBase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/**
	 * Create data table
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql_note = "CREATE TABLE " + TABLE_NAME_NOTE + "(" + NOTE_ID
				+ " INTEGER primary key autoincrement, " + NOTE_TITLE
				+ " text, " + NOTE_CONTENT + " text, " + NOTE_DATE + " long, "
				+ NOTE_COLOR + " text," + NOTE_ALERTDATE + " long,"
				+ NOTE_ISFOLDER + " text," + NOTE_FOLDERID + " long,"
				+ NOTE_PARENTID + " long);";
		String sql_config = "CREATE TABLE " + TABLE_NAME_CONFIG + "("
				+ CONFIG_ID + " INTEGER primary key autoincrement, "
				+ CONFIG_COLOR + " text, " + CONFIG_FONTSIZE + " text,"+
				CONFIG_PASSWORD+" text);";
		db.execSQL(sql_note);
		db.execSQL(sql_config);
	}

	/**
	 * Update database when the version is changed. (Drop the current database
	 * and recreate a new one)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String sql_note = "DROP TABLE IF EXISTS " + TABLE_NAME_NOTE;
		String sql_config = "DROP TABLE IF EXISTS " + TABLE_NAME_CONFIG;
		db.execSQL(sql_note);
		db.execSQL(sql_config);
		onCreate(db);
	}

	/**
	 * Get note all
	 */
	public ArrayList<Note> select() {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME_NOTE, null, NOTE_PARENTID+"= ?", 
				new String[]{String.valueOf(DEFAULT_PARENTID)}, null, null,
				ORDERBY_FOLDERID);
		ArrayList<Note> list=null;
		Note note;
		if(cursor.getCount()>0){
			list=new ArrayList<Note>();
			cursor.moveToFirst();
			while(!cursor.isAfterLast()){
				note=new Note();
				note.setmId(cursor.getLong(COLUMNINDEX_NOTEID));
				note.setmTitle(cursor.getString(COLUMNINDEX_NOTETITLE));
				note.setmContent(cursor.getString(COLUMNINDEX_NOTECONTENT));
				note.setmDate(cursor.getLong(COLUMNINDEX_NOTEDATE));
				note.setmColor(cursor.getString(COLUMNINDEX_NOTECOLOR));
				note.setmAlertDate(cursor.getLong(COLUMNIDEX_NOTEALERTDATE));
				note.setmIsFolder(cursor.getString(COLUMNINDEX_NOTEISFOLDER));
				note.setmFolderId(cursor.getLong(COLUMNINDEX_NOTEFODERID));
				note.setmParentId(cursor.getLong(COLUMNINDEX_NOTEPARENTID));
				list.add(note);
				cursor.moveToNext();
			}
		}
		closeCursor(cursor);
		db.close();
		return list;
	}

	public ArrayList<Note> selectNoteId(long noteId) {
		String sql = "select * from " + TABLE_NAME_NOTE + " where "
				+ NOTE_PARENTID + "=" + DEFAULT_PARENTID + " " + "and "
				+ NOTE_ID + " <> " + noteId + "  order by  " + ORDERBY_FOLDERID;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(sql, null);
		ArrayList<Note> list=null;
		Note note;
		if(cursor.getCount()>0){
			cursor.moveToFirst();
			list=new ArrayList<Note>();
			while(!cursor.isAfterLast()){
				note=new Note();
				note.setmAlertDate(cursor.getLong(COLUMNIDEX_NOTEALERTDATE));
				note.setmColor(cursor.getString(COLUMNINDEX_NOTECOLOR));
				note.setmContent(cursor.getString(COLUMNINDEX_NOTECONTENT));
				note.setmDate(cursor.getLong(COLUMNINDEX_NOTEDATE));
				note.setmFolderId(cursor.getLong(COLUMNINDEX_NOTEFODERID));
				note.setmId(cursor.getLong(COLUMNINDEX_NOTEID));
				note.setmIsFolder(cursor.getString(COLUMNINDEX_NOTEISFOLDER));
				note.setmParentId(cursor.getLong(COLUMNINDEX_NOTEPARENTID));
				note.setmTitle(cursor.getString(COLUMNINDEX_NOTETITLE));
				list.add(note);
				cursor.moveToNext();
			}
		}
		closeCursor(cursor);
		db.close();
		return list;
	}

	public ArrayList<Note> exportXML() {
		Note note;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME_NOTE, null, null, null, null, null,
				ORDERBY_FOLDERID);
		ArrayList<Note> list = null;
		if (cursor.getCount() > 0) {
			list=new ArrayList<Note>();
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				note = new Note();
				note.setmId(cursor.getLong(0));
				note.setmTitle(cursor.getString(1));
				note.setmContent(cursor.getString(2));
				note.setmDate(cursor.getLong(3));
				note.setmColor(cursor.getString(4));
				list.add(note);
				cursor.moveToNext();
			}
		}
		closeCursor(cursor);
		db.close();
		return list;
	}

	public boolean restoreDatabase(ArrayList<Note> noteList) {
		if (noteList != null) {
			SQLiteDatabase db = this.getWritableDatabase();
			// SQLite transaction
			db.beginTransaction();
			try {
				for (int i = 0; i < noteList.size(); i++) {
					insert(noteList.get(i), db);
				}
				db.setTransactionSuccessful();
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			} finally {
				db.endTransaction();
			}
			db.close();
		}
		return true;
	}

	/**
	 * Get note by note id
	 */
	public Note selectNoteById(long noteId) {
		SQLiteDatabase db = this.getWritableDatabase();
		Note note=null;
		Cursor cursor = db.query(TABLE_NAME_NOTE, null, NOTE_ID
				+ "=" + noteId, null, null, null, ORDERBY_FOLDERID);
		if(cursor.getCount()>0){
			note=new Note();
			cursor.moveToFirst();
			note.setmId(cursor.getLong(COLUMNINDEX_NOTEID));
			note.setmAlertDate(cursor.getLong(COLUMNIDEX_NOTEALERTDATE));
			note.setmColor(cursor.getString(COLUMNINDEX_NOTECOLOR));
			note.setmContent(cursor.getString(COLUMNINDEX_NOTECONTENT));
			note.setmDate(cursor.getLong(COLUMNINDEX_NOTEDATE));
			note.setmFolderId(cursor.getLong(COLUMNINDEX_NOTEFODERID));
			note.setmIsFolder(cursor.getString(COLUMNINDEX_NOTEISFOLDER));
			note.setmParentId(cursor.getLong(COLUMNINDEX_NOTEPARENTID));
			note.setmTitle(cursor.getString(COLUMNINDEX_NOTETITLE));
		}
		closeCursor(cursor);
		db.close();
		return note;
	}

	/*
	 * Get notes whose parent id equals folderId
	 */
	public ArrayList<Note> selectNoteByFolderId(long folderId) {
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.query(TABLE_NAME_NOTE, null, NOTE_PARENTID + "="
				+ folderId, null, null, null, ORDERBY_FOLDERID);
		ArrayList<Note> list=null;
		Note note;
		if(cursor.getCount()>0){
			cursor.moveToFirst();
			list=new ArrayList<Note>();
			while(!cursor.isAfterLast()){
				note=new Note();
				note.setmAlertDate(cursor.getLong(COLUMNIDEX_NOTEALERTDATE));
				note.setmColor(cursor.getString(COLUMNINDEX_NOTECOLOR));
				note.setmContent(cursor.getString(COLUMNINDEX_NOTECONTENT));
				note.setmDate(cursor.getLong(COLUMNINDEX_NOTEDATE));
				note.setmFolderId(cursor.getLong(COLUMNINDEX_NOTEFODERID));
				note.setmId(cursor.getLong(COLUMNINDEX_NOTEID));
				note.setmIsFolder(cursor.getString(COLUMNINDEX_NOTEISFOLDER));
				note.setmParentId(cursor.getLong(COLUMNINDEX_NOTEPARENTID));
				note.setmTitle(cursor.getString(COLUMNINDEX_NOTETITLE));
				list.add(note);
				cursor.moveToNext();
			}
		}
		closeCursor(cursor);
		db.close();
		return list;
	}

	/**
	 * Insert operation
	 */
	public long insert(String noteTitle, String noteContent, long noteDate) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put(NOTE_TITLE, noteTitle);
		cv.put(NOTE_CONTENT, noteContent);
		cv.put(NOTE_DATE, noteDate);
		long row = db.insert(TABLE_NAME_NOTE, "N/A", cv);
		db.close();
		return row;
	}

	public void deleteAll() {
		SQLiteDatabase db = this.getWritableDatabase();

		db.delete(TABLE_NAME_NOTE, null, null);
		db.close();
	}

	
	/**
	 * Delete operation
	 */
	public void delete(long id) {
		SQLiteDatabase db = this.getWritableDatabase();
		String where = NOTE_ID + " = ?";
		String[] whereValue = { String.valueOf(id) };
		db.delete(TABLE_NAME_NOTE, where, whereValue);
		db.close();
	}

	public void delete(long[] ids) {
		SQLiteDatabase db = this.getWritableDatabase();
		String where;
		for (int i = 0; i < ids.length; i++) {
			where = NOTE_ID + " = ?";
			String[] whereValue = { String.valueOf(ids[i]) };
			db.delete(TABLE_NAME_NOTE, where, whereValue);
		}
		db.close();
	}

	/**
	 * Update operation
	 */
	public void update(long id, String noteTitle, String noteContent,
			long note_date) {
		SQLiteDatabase db = this.getWritableDatabase();
		String where = NOTE_ID + " = ?";
		String[] whereValue = { String.valueOf(id) };

		ContentValues cv = new ContentValues();
		cv.put(NOTE_TITLE, noteTitle);
		cv.put(NOTE_CONTENT, noteContent);
		cv.put(NOTE_DATE, note_date);
		db.update(TABLE_NAME_NOTE, cv, where, whereValue);
		db.close();
	}

	/**
	 * Update content by id
	 */
	public void updateContentById(long id, String content) {
		SQLiteDatabase db = this.getWritableDatabase();
		String where = NOTE_ID + "=?";
		String[] whereValue = { String.valueOf(id) };
		long curDate = System.currentTimeMillis();
		ContentValues cv = new ContentValues();
		cv.put(NOTE_CONTENT, content);
		cv.put(NOTE_DATE, curDate);
		db.update(TABLE_NAME_NOTE, cv, where, whereValue);
		db.close();
	}

	/**
	 * Update title by id
	 * 
	 */
	public void updateTitleById(long id, String title) {
		SQLiteDatabase db = this.getWritableDatabase();
		String where = NOTE_ID + "=?";
		String[] whereValue = { String.valueOf(id) };

		long curDate = System.currentTimeMillis();

		ContentValues cv = new ContentValues();
		cv.put(NOTE_TITLE, title);
		cv.put(NOTE_DATE, curDate);
		db.update(TABLE_NAME_NOTE, cv, where, whereValue);
		db.close();
	}

	public void updateColorById(long id, String color) {
		SQLiteDatabase db = this.getWritableDatabase();
		String where = NOTE_ID + "=?";
		String[] whereValue = { String.valueOf(id) };
		ContentValues cv = new ContentValues();
		cv.put(NOTE_COLOR, color);
		db.update(TABLE_NAME_NOTE, cv, where, whereValue);
		cv.clear();

		cv.put(CONFIG_COLOR, color);
		db.update(TABLE_NAME_CONFIG, cv, CONFIG_ID + "=?", new String[] { "1" });
		db.close();
	}

	public String getConfigColor() {
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.query(TABLE_NAME_CONFIG,
				new String[] { CONFIG_COLOR }, null, null, null, null, null);
		String color ;
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			color= cursor.getString(0);
			closeCursor(cursor);
		} else {
			closeCursor(cursor);
			ContentValues cv = new ContentValues();
			cv.put(CONFIG_COLOR, DEFAULT_CONFIG_COLOR);
			db.insert(TABLE_NAME_CONFIG, CONFIG_COLOR, cv);
			color= DEFAULT_CONFIG_COLOR;
		}
		db.close();
		return color;
	}

	public int getConfigFontSize() {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME_CONFIG,
				new String[] { CONFIG_FONTSIZE }, null, null, null, null, null);
		if (cursor.getCount() < 1) {
			ContentValues cv = new ContentValues();
			cv.put(CONFIG_FONTSIZE, DEFAULT_CONFIG_FONTSIZE);
			db.insert(TABLE_NAME_CONFIG, CONFIG_FONTSIZE, cv);
			db.close();
			return DEFAULT_CONFIG_FONTSIZE;
		}
		cursor.moveToFirst();
		int fontSize = cursor.getInt(0);
		closeCursor(cursor);
		db.close();
		return fontSize;
	}
	

	public void setConfig() {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME_CONFIG,null, null, null, null, null, null);
		if (cursor.getCount() < 1) {
			ContentValues cv = new ContentValues();
			cv.put(CONFIG_PASSWORD, DEFAULT_CONFIG_PASSWORD);
			cv.put(CONFIG_FONTSIZE, DEFAULT_CONFIG_FONTSIZE);
			cv.put(CONFIG_COLOR, DEFAULT_CONFIG_COLOR);
			db.insert(TABLE_NAME_CONFIG, null, cv);
		}closeCursor(cursor);
		db.close();
	}
	
	public String getPassword() {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME_CONFIG,
				new String[] { CONFIG_PASSWORD }, null, null, null, null, null);
		if (cursor.getCount() < 1) {
			ContentValues cv = new ContentValues();
			cv.put(CONFIG_PASSWORD, DEFAULT_CONFIG_PASSWORD);
			db.insert(TABLE_NAME_CONFIG, CONFIG_PASSWORD, cv);
			db.close();
			return DEFAULT_CONFIG_PASSWORD;
		}
		cursor.moveToFirst();
		String password = cursor.getString(0);
		closeCursor(cursor);
		db.close();
		return password;
	}
	
	

	public boolean setPassword(String password) {
		SQLiteDatabase db = this.getWritableDatabase();
		try {
			Cursor cursor = db.query(TABLE_NAME_CONFIG,
					new String[] { CONFIG_ID }, null, null, null, null, null);
			ContentValues cv = new ContentValues();
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				int id = cursor.getInt(0);
				cv.put(CONFIG_PASSWORD, password);
				db.update(TABLE_NAME_CONFIG, cv, CONFIG_ID + "=?",
						new String[] { String.valueOf(id) });
			} else {
				cv.put(CONFIG_PASSWORD, password);
				db.insert(TABLE_NAME_CONFIG, CONFIG_PASSWORD, cv);
			}
			closeCursor(cursor);
			db.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			db.close();
			return false;
		}
	}

	public boolean setConfigFontSize(String size) {
		SQLiteDatabase db = this.getWritableDatabase();
		try {
			Cursor cursor = db.query(TABLE_NAME_CONFIG,
					new String[] { CONFIG_ID }, null, null, null, null, null);
			ContentValues cv = new ContentValues();
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				int id = cursor.getInt(0);
				cv.put(CONFIG_FONTSIZE, size);
				db.update(TABLE_NAME_CONFIG, cv, CONFIG_ID + "=?",
						new String[] { String.valueOf(id) });
			} else {
				cv.put(CONFIG_FONTSIZE, size);
				db.insert(TABLE_NAME_CONFIG, CONFIG_FONTSIZE, cv);
			}
			closeCursor(cursor);
			db.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			db.close();
			return false;
		}
	}

	private void insert(Note note, SQLiteDatabase db) {
		ContentValues cv = new ContentValues();
		cv.put(NOTE_TITLE, note.getmTitle() == " " ? null : note.getmTitle());
		cv.put(NOTE_CONTENT, note.getmContent());
		cv.put(NOTE_DATE, note.getmDate());
		cv.put(NOTE_COLOR, note.getmColor());
		db.insert(TABLE_NAME_NOTE, "N/A", cv);
		db.close();
	}

	/**
	 * insert note
	 * 
	 * @param note
	 * @return if success return true, otherwise false
	 */
	public long insert(Note note) {
		long noteId = 0;
		SQLiteDatabase db = getWritableDatabase();
		try {
			ContentValues cv = new ContentValues();
			cv.put(NOTE_TITLE, note.getmTitle());
			cv.put(NOTE_CONTENT, note.getmContent());
			cv.put(NOTE_DATE, note.getmDate());
			cv.put(NOTE_COLOR, note.getmColor());
			cv.put(NOTE_ALERTDATE, note.getmAlertDate());
			cv.put(NOTE_ISFOLDER, note.getmIsFolder());
			cv.put(NOTE_PARENTID, note.getmParentId());
			if (note.getmIsFolder().equals(ISFOLDER_YES)) {
				// Search the max folderId in the note table,
				// and get the value plus 1 to serve as the next folderId.
				String sql = "select max(" + NOTE_FOLDERID + ")" + " from "
						+ TABLE_NAME_NOTE;
				Cursor cursor = db.rawQuery(sql, null);
				long folderId;
				if (cursor.getCount() > 0) {
					cursor.moveToFirst();
					folderId = cursor.getLong(0);
					closeCursor(cursor);
				} else {
					folderId = 0;
				}
				note.setmFolderId(folderId + 1);
			} else {
				note.setmFolderId(DEFAULT_FOLDERID);
			}
			cv.put(NOTE_FOLDERID, note.getmFolderId());
			noteId = db.insert(TABLE_NAME_NOTE, null, cv);
		} catch (Exception e) {
			Log.e("NoteDataBase", "Insert note failed.");
		}
		db.close();
		return noteId;
	}

	/**
	 * close cursor
	 * 
	 * @param cursor
	 */
	private void closeCursor(Cursor cursor) {
		if (cursor != null) {
			if (!cursor.isClosed()) {
				cursor.close();
				cursor=null;
			}
		}
	}

	public boolean setFolderId(long noteId, long folderId) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put(NOTE_PARENTID, folderId);
		try {
			db.update(TABLE_NAME_NOTE, cv, NOTE_ID + "=?",
					new String[] { String.valueOf(noteId) });
			db.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			db.close();
			return false;
		}
	}

	public void updateFolderTitle(long folderId, String folderTitle) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put(NOTE_CONTENT, folderTitle);
		cv.put(NOTE_TITLE, folderTitle);
		db.update(TABLE_NAME_NOTE, cv, NOTE_FOLDERID + "=?",
				new String[] { String.valueOf(folderId) });
		db.close();
	}

	public void setDefaultParentId(long[] ids) {
		SQLiteDatabase db = this.getWritableDatabase();
		String where = NOTE_ID + " = ?";
		ContentValues cv = new ContentValues();
		cv.put(NOTE_PARENTID, DEFAULT_PARENTID);
		for (int i = 0; i < ids.length; i++) {
			String[] whereValue = { String.valueOf(ids[i]) };
			db.update(TABLE_NAME_NOTE, cv, where, whereValue);
		}
		db.close();
	}

}
