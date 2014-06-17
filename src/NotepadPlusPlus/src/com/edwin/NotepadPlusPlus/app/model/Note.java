package com.edwin.NotepadPlusPlus.app.model;

import com.edwin.NotepadPlusPlus.database.NoteDataBase;

public class Note {
	private  long mId;
	private  String mTitle;
	private  String mContent;
	private  long mDate;
	private  long mFolderId=NoteDataBase.DEFAULT_FOLDERID;
	private  long mAlertDate=NoteDataBase.DEFAULT_ALERTDATE;
	// "0" refer to the default color
	private  String mColor = NoteDataBase.DEFAULT_COLOR;
	private  String mIsFolder=NoteDataBase.DEFAULT_COLOR;
	private long mParentId=NoteDataBase.DEFAULT_PARENTID;
	
	/**
	 * @return the mId
	 */
	public long getmId() {
		return mId;
	}
	/**
	 * @param mId the mId to set
	 */
	public void setmId(long mId) {
		this.mId = mId;
	}
	/**
	 * @return the mTitle
	 */
	public String getmTitle() {
		return mTitle;
	}
	/**
	 * @param mTitle the mTitle to set
	 */
	public void setmTitle(String mTitle) {
		this.mTitle = mTitle;
	}
	/**
	 * @return the mContent
	 */
	public String getmContent() {
		return mContent;
	}
	/**
	 * @param mContent the mContent to set
	 */
	public void setmContent(String mContent) {
		this.mContent = mContent;
	}
	/**
	 * @return the mDate
	 */
	public long getmDate() {
		return mDate;
	}
	/**
	 * @param mDate the mDate to set
	 */
	public void setmDate(long mDate) {
		this.mDate = mDate;
	}
	/**
	 * @return the mFolderId
	 */
	public long getmFolderId() {
		return mFolderId;
	}
	/**
	 * @param mFolderId the mFolderId to set
	 */
	public void setmFolderId(long mFolderId) {
		this.mFolderId = mFolderId;
	}
	/**
	 * @return the mAlertDate
	 */
	public long getmAlertDate() {
		return mAlertDate;
	}
	/**
	 * @param mAlertDate the mAlertDate to set
	 */
	public void setmAlertDate(long mAlertDate) {
		this.mAlertDate = mAlertDate;
	}
	/**
	 * @return the mColor
	 */
	public String getmColor() {
		return mColor;
	}
	/**
	 * @param mColor the mColor to set
	 */
	public void setmColor(String mColor) {
		this.mColor = mColor;
	}
	/**
	 * @return the mIsFolder
	 */
	public String getmIsFolder() {
		return mIsFolder;
	}
	/**
	 * @param mIsFolder the mIsFolder to set
	 */
	public void setmIsFolder(String mIsFolder) {
		this.mIsFolder = mIsFolder;
	}
	/**
	 * @return the mParentId
	 */
	public long getmParentId() {
		return mParentId;
	}
	/**
	 * @param mParentId the mParentId to set
	 */
	public void setmParentId(long mParentId) {
		this.mParentId = mParentId;
	}
	
}
