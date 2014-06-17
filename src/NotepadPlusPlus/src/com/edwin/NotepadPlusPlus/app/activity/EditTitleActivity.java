package com.edwin.NotepadPlusPlus.app.activity;

import com.edwin.NotepadPlusPlus.R;
import com.edwin.NotepadPlusPlus.R.id;
import com.edwin.NotepadPlusPlus.R.layout;
import com.edwin.NotepadPlusPlus.app.model.Note;
import com.edwin.NotepadPlusPlus.database.NoteDataBase;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class EditTitleActivity extends Activity implements View.OnClickListener {

	// The index of the title column
	private final int COLUMN_INDEX_TITLE = 1;

	public final static String PASS_NOTE_ID = "com.edwin.NotepadPlusPlus.EditTitle.note_Id";

	private long mNoteId;
	private NoteDataBase mNoteDB;
	private EditText mNoteTitle;
	private Button mButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.title_editor);
		mNoteTitle = (EditText) findViewById(R.id.edit_title);
		mButton = (Button) findViewById(R.id.save_title);
		Bundle bundle = this.getIntent().getExtras();
		mNoteId = bundle.getLong(PASS_NOTE_ID);
		if (mNoteId != 0) {
			mNoteDB = new NoteDataBase(this);
			Note note = mNoteDB.selectNoteById(mNoteId);
			if (note != null) {
				mNoteTitle.setText(note.getmTitle());
			}
		}
		mButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (v.getId() == R.id.save_title) {
					finish();
				}

			}
		});
	}

	@Override
	public void onPause() {
		super.onPause();
		String title = mNoteTitle.getText().toString();
		if (title.equals("")) {
			return;
		}
		if (mNoteId != 0) {
			mNoteDB = new NoteDataBase(this);
			mNoteDB.updateTitleById(mNoteId, title);
		}
	}

	@Override
	public void onClick(View v) {
		// Close this activity
		finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mNoteDB != null) {
			mNoteDB.close();
		}
	}
}
