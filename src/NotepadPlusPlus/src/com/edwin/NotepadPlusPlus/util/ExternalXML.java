package com.edwin.NotepadPlusPlus.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import com.edwin.NotepadPlusPlus.app.model.Note;
import com.edwin.NotepadPlusPlus.database.NoteDataBase;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.util.Xml;

public class ExternalXML {
	private static Context mContext;

	public ExternalXML(Context context) {
		mContext = context;
	}

	private final static String folderpath = Environment
			.getExternalStorageDirectory() + File.separator + "Edwin";
	private final static String filepath = folderpath + File.separator
			+ "backup.xml";

	/**
	 * Export database as a XML file
	 * @param list
	 * @return Return the file path
	 */
	public static String exportXML(ArrayList<Note> list) {
		// Create a folder
		File dir = new File(folderpath);
		if (dir.mkdirs()) {
			Log.i("ExternalXML", "Create folder success");
		} else {
			Log.i("ExternalXML", "Create folder failure");
		}
		
		// Create a file
		File file = new File(filepath);
		try {
			if (file.createNewFile())
				Log.i("ExternalXML", "Create file success");
			else
				Log.i("ExternalXML", "Create file failure");
		} catch (IOException e) {
			e.printStackTrace();
			Log.i("Create file failure", e.getMessage());
		}
		file.setReadOnly();
		// Bind the new file with a FileOutputStream
		FileOutputStream fileos = null;
		try {
			fileos = new FileOutputStream(filepath);
		} catch (FileNotFoundException e) {
			Log.e("FileNotFoundException", "Can't create FileOutputStream");
		}
		// Create a XmlSerializer in order to write xml data
		XmlSerializer serializer = Xml.newSerializer();
		try {
			// Set the FileOutputStream as output for the serializer, using
			// UTF-8 encoding
			serializer.setOutput(fileos, "UTF-8");
			// Write <?xml declaration with encoding (if encoding not null) and
			// standalone flag (if standalone not null)
			serializer.startDocument(null, Boolean.valueOf(true));
			// Set indentation option
			serializer.setFeature(
					"http://xmlpull.org/v1/doc/features.html#indent-output",
					true);
			// Start a tag called "root"
			serializer.startTag(null, "notes");
			for (Note note : list) {
				serializer.startTag(null, "note");
				serializer.startTag(null, "title");
				serializer.text(showString(note.getmTitle()));
				// Set an attribute called "attribute" with a "value" for
				// <child2>
				// Serializer.attribute(null, "attribute", "value");
				serializer.endTag(null, "title");
				serializer.startTag(null, "content");
				// Write some text inside <text>
				serializer.text(showString(note.getmContent()));
				serializer.endTag(null, "content");
				serializer.startTag(null, "date");
				// Write some date inside <date>
				serializer.text(showString(String.valueOf(note.getmDate())));
				serializer.endTag(null, "date");
				serializer.startTag(null, "color");
				// Write some color inside <color>
				serializer.text(showString(note.getmColor()));
				serializer.endTag(null, "color");
				serializer.endTag(null, "note");
			}
			serializer.endTag(null, "notes");
			serializer.endDocument();
			// Write xml data into the FileOutputStream
			serializer.flush();
			// Finally we close the file stream
			fileos.close();
			return filepath;
		} catch (Exception e) {
			// "Error occurred while creating xml file"
			Log.e("Exception", e.getMessage());
			return "";
		}
	}

	/**
	 * Dispose a array of String
	 * 
	 * @param str
	 * @return String
	 */
	public static String showString(String str) {
		if (str == null | str == "") {
			return " ";
		}
		return str;
	}

	public static ArrayList<Note> importXML() {
		InputStream inputStream = null;
		XmlPullParser parser = Xml.newPullParser();
		File f = new File(filepath);
		String path = f.getAbsolutePath();
		File myFile = new File(path);
		if (!myFile.exists()) {
			return null;
		}
		try {
			inputStream = new FileInputStream(path);
			parser.setInput(inputStream, "UTF-8");
			int eventType = parser.getEventType();

			Note currentNote = null;
			ArrayList<Note> notes = null;
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					notes = new ArrayList<Note>();
					break;
				case XmlPullParser.START_TAG:
					String name = parser.getName();
					if (name.equalsIgnoreCase("note")) {
						currentNote = new Note();
					} else if (currentNote != null) {
						if (name.equalsIgnoreCase("title")) {
							currentNote.setmTitle(parser.nextText());
						} else if (name.equalsIgnoreCase("content")) {
							currentNote.setmContent(parser.nextText());
						} else if (name.equalsIgnoreCase("date")) {
							currentNote
									.setmDate(Long.valueOf(parser.nextText()));
						} else if (name.equalsIgnoreCase("color")) {
							currentNote.setmColor(parser.nextText());
						}
					}
					break;
				case XmlPullParser.END_TAG:
					if (parser.getName().equalsIgnoreCase("note")
							&& currentNote != null) {
						notes.add(currentNote);
						currentNote = null;
					}
					break;
				}
				eventType = parser.next();
			}
			inputStream.close();
			return notes;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static boolean RestoreData() {
		ArrayList<Note> list = importXML();
		if (list != null) {
			boolean isSuccess = new NoteDataBase(mContext)
					.restoreDatabase(list);
			return isSuccess;
		}
		return false;
	}

}
