package com.edwin.NotepadPlusPlus.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import com.edwin.NotepadPlusPlus.app.model.Note;

import android.content.Context;
import android.os.Environment;

public class Helper {
	private final static String[] mWeek = new String[] { "Sunday", "Monday",
			"Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };
	private final static String FORMATDATE = "MM-dd-yyyy";
	private final static String FORMATMONTH = "MM-dd";
	private final static String FORMATDAY = "hh:mm";
	private static Context mContext;

	public Helper(Context context) {
		mContext = context;
	}

	/**
	 * Get date in different styles.
	 */
	public static String getDate(long date) {

		SimpleDateFormat formatterReturnDate = new SimpleDateFormat(FORMATDATE);
		SimpleDateFormat formatterReturnMonth = new SimpleDateFormat(
				FORMATMONTH);
		SimpleDateFormat formatterReturnDay = new SimpleDateFormat(FORMATDAY);

		Calendar caGivenDate = Calendar.getInstance();
		Calendar caCurrentDate = Calendar.getInstance();

		Date givenDate = new Date(date);
		Date curDate = new Date(System.currentTimeMillis());

		caGivenDate.setTime(givenDate);
		caCurrentDate.setTime(curDate);

		String returnDate = formatterReturnDate.format(date);

		if (caGivenDate.get(Calendar.YEAR) == caCurrentDate.get(Calendar.YEAR)) {
			if (caGivenDate.get(Calendar.DAY_OF_YEAR) == caCurrentDate
					.get(Calendar.DAY_OF_YEAR)) {
				return formatterReturnDay.format(date);
			}
			if (caGivenDate.get(Calendar.WEEK_OF_YEAR) == caCurrentDate
					.get(Calendar.WEEK_OF_YEAR)) {
				return mWeek[caGivenDate.get(Calendar.DAY_OF_WEEK) - 1];
			}
			if (caGivenDate.get(Calendar.MONTH) == caCurrentDate
					.get(Calendar.MONTH)) {
				return formatterReturnMonth.format(date);
			}
			returnDate = formatterReturnMonth.format(date);
		} else if ((caGivenDate.get(Calendar.YEAR) - caCurrentDate
				.get(Calendar.YEAR)) == 1) {
			if (caGivenDate.get(Calendar.MONTH) == Calendar.JANUARY
					&& caCurrentDate.get(Calendar.MONTH) == Calendar.DECEMBER
					&& (caGivenDate.get(Calendar.DAY_OF_MONTH) + 31 - caCurrentDate
							.get(Calendar.DAY_OF_MONTH)) < 7) {
				int givenWeek = caGivenDate.get(Calendar.DAY_OF_WEEK);
				int currentWeek = caCurrentDate.get(Calendar.DAY_OF_WEEK);
				if (givenWeek < currentWeek) {
					returnDate = mWeek[caGivenDate.get(Calendar.DAY_OF_WEEK) - 1];
				}
			}
		} else if ((caCurrentDate.get(Calendar.YEAR) - caGivenDate
				.get(Calendar.YEAR)) == 1) {
			if (caCurrentDate.get(Calendar.MONTH) == Calendar.JANUARY
					&& caGivenDate.get(Calendar.MONTH) == Calendar.DECEMBER
					&& (caCurrentDate.get(Calendar.DAY_OF_MONTH) + 31 - caGivenDate
							.get(Calendar.DAY_OF_MONTH)) < 7) {
				int givenWeek = caGivenDate.get(Calendar.DAY_OF_WEEK);
				int currentWeek = caCurrentDate.get(Calendar.DAY_OF_WEEK);
				if (currentWeek < givenWeek) {
					returnDate = mWeek[caGivenDate.get(Calendar.DAY_OF_WEEK) - 1];
				}
			}

		}
		return returnDate;
	}

	/**
	 * Backup database in TXT file
	 * 
	 * @return if back up successful return file path,otherwise null.
	 */
	public static String backupAsTxt(ArrayList<Note> list) {
		FileOutputStream fileOutputStream = null;
		String folderPath = Environment.getExternalStorageDirectory().getPath()
				+ File.separator + "Edwin";
		String filePath = folderPath + File.separator + "notes.txt";
		File fileFolder = new File(folderPath);
		if (fileFolder.mkdirs()) {
		}
		File fileFile = new File(filePath);
		try {
			fileFile.createNewFile();

		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			fileOutputStream = new FileOutputStream(fileFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		byte[] content;
		for (int i = 0; i < list.size(); i++) {
			String a;
			if (i == list.size() - 1) {
				if (list.get(i).getmTitle() == null) {
					a = "";
				} else {
					a = list.get(i).getmTitle();
				}
				a += "@#" + list.get(i).getmContent() + "@#"
						+ list.get(i).getmDate() + "@#"
						+ list.get(i).getmColor();
			} else {
				a = list.get(i).getmTitle() + "@#" + list.get(i).getmContent()
						+ "@#" + list.get(i).getmDate() + "@#"
						+ list.get(i).getmColor() + "@#@";
			}
			content = a.getBytes();
			try {
				fileOutputStream.write(content);
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
		return filePath;
	}

	public static ArrayList<Note> restoreData(Context context) {
		backupAsTxt(new NoteDataBase(context).exportXML());
		ArrayList<Note> list = null;
		Note note;
		FileInputStream fileInputStream = null;
		String path = Environment.getExternalStorageDirectory().getPath()
				+ File.separator + "Edwin" + File.separator + "notes.txt";

		File file = new File(path);
		try {
			fileInputStream = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			byte[] buff = new byte[fileInputStream.available()];
			String temp;
			while (fileInputStream.read(buff) != -1) {
				// operations
				temp = new String(buff);
				String[] dataParent = temp.split("@#@");
				if (dataParent.length > 0) {
					list = new ArrayList<Note>();
					for (int i = 0; i < dataParent.length; i++) {
						String[] data = dataParent[i].split("@#");
						if (data.length == 4) {
							note = new Note();
							note.setmTitle(data[0]);
							note.setmContent(data[1]);
							note.setmDate(Long.valueOf(data[2]));
							note.setmColor(data[3]);
							list.add(note);
						} else {
							return null;
						}
					}
				}
			}
			fileInputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}

}
