package com.edwin.NotepadPlusPlus.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

public class BackupDatabase extends AsyncTask<String, Void, Integer> {
	public static final String COMMAND_BACKUP = "backupDatabase";
	public static final String COMMAND_RESTORE = "restroeDatabase";
	private static final String FILE_PATH="/data/data/com.edwin.NotepadPlusPlus/databases/Note.db";
	
	private Context mContext;

	public BackupDatabase(Context context) {
		this.mContext = context;
	}

	@Override
	protected Integer doInBackground(String... params) {
		File dbFile=new File(FILE_PATH);
		File exportDir = new File(Environment.getExternalStorageDirectory(),
				"noteBackup");

		if (!exportDir.exists()) {
			exportDir.mkdirs();
		}

		File backup = new File(exportDir, dbFile.getName());
		String command = params[0];
		if (command.equals(COMMAND_BACKUP)) {
			try {
				backup.createNewFile();
				fileCopy(dbFile, backup);
				return Log.d("backup", "ok");
			} catch (Exception e) {
				e.printStackTrace();
				return Log.d("backup", "fail");
			}
		} else if (command.equals(COMMAND_RESTORE)) {
			try {
				fileCopy(backup, dbFile);
				return Log.d("restore", "success");
			} catch (Exception e) {
				e.printStackTrace();
				return Log.d("restore", "fail");
			}
		} else {
			return null;
		}
	}

	private void fileCopy(File dbFile, File backup) throws IOException
	{
		FileChannel inChannel = new FileInputStream(dbFile).getChannel();
		FileChannel outChannel = new FileOutputStream(backup).getChannel();
		try {
		inChannel.transferTo(0, inChannel.size(), outChannel);
		} catch (IOException e) {
		e.printStackTrace();
		} finally{
			if(inChannel!=null){inChannel.close();}
			if(outChannel!=null){outChannel.close();}
		}
	}
}