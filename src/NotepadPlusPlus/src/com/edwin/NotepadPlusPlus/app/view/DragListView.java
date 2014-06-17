package com.edwin.NotepadPlusPlus.app.view;

import com.edwin.NotepadPlusPlus.R;
import com.edwin.NotepadPlusPlus.R.id;
import com.edwin.NotepadPlusPlus.app.adapter.NoteListAdapter;
import com.edwin.NotepadPlusPlus.database.NoteDataBase;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

public class DragListView extends ListView {

	private int mScaledTouchSlop;

	public DragListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mScaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
	}

	private ImageView mDragImageView;
	private int mDragSrcPosition;
	private int mDragPosition;

	private int mDragPoint;
	private int mDragOffset;

	private WindowManager mWindowManager;
	private WindowManager.LayoutParams mWindowParams;

	private int mUpScrollBounce;
	private int mDownScrollBounce;
	
	private int mX;
	private int mY;
	private ViewGroup mItemView ;
	private int mGetVisiablePosition;
	private static long mNoteId;
	private NoteDataBase mNoteDB=new NoteDataBase(getContext());
	NoteListAdapter mAdapter;

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			mX = (int) ev.getX();
			mY = (int) ev.getY();
			//absolute position
			mDragSrcPosition = mDragPosition = pointToPosition(mX, mY);
			mNoteId=((NoteListAdapter)getAdapter()).getItemId(mDragPosition);
			
			if (mDragPosition == AdapterView.INVALID_POSITION) {
				return super.onInterceptTouchEvent(ev);
			}
			mDragOffset = (int) (ev.getRawY() - mY);
			//the first shown Item's current position (getFirstVisiblePosition())
			mItemView = (ViewGroup) getChildAt(mDragPosition
					- getFirstVisiblePosition());
			Log.i("DragView", "mDragSrcPosition:"+mDragSrcPosition);
			return false;
		}
		return super.onInterceptTouchEvent(ev);
	}

	public void confirmStartDrag(){
		
		mDragPoint = mY - mItemView.getTop();
		View dragger = mItemView.findViewById(R.id.rv_list_item);
		if (dragger != null && mX > dragger.getLeft() - 20) {
			mUpScrollBounce = Math.min(mY - mScaledTouchSlop, getHeight() / 3);
			mDownScrollBounce = Math.max(mY + mScaledTouchSlop,
					getHeight() * 2 / 3);
			mItemView.setDrawingCacheEnabled(true);
			Bitmap bm = Bitmap.createBitmap(mItemView.getDrawingCache());
			startDrag(bm, mY);
		}
	}
	
	public void startDrag(Bitmap bm, int y) {
		stopDrag();

		mWindowParams = new WindowManager.LayoutParams();
		mWindowParams.gravity = Gravity.TOP;
		mWindowParams.x = 0;
		mWindowParams.y = y - mDragPoint + mDragOffset;
		mWindowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		mWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		mWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
		mWindowParams.format = PixelFormat.TRANSLUCENT;
		mWindowParams.windowAnimations = 0;
		ImageView imageView = new ImageView(getContext());
		imageView.setImageBitmap(bm);
		mWindowManager = (WindowManager) getContext().getSystemService("window");
		mWindowManager.addView(imageView, mWindowParams);
		mDragImageView = imageView;
	}

	public void stopDrag() {
		if (mDragImageView != null) {
			mWindowManager.removeView(mDragImageView);
			mDragImageView = null;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (mDragImageView != null && mDragPosition != INVALID_POSITION) {
			int action = ev.getAction();
			switch (action) {
			case MotionEvent.ACTION_UP:
				int upY = (int) ev.getY();
				stopDrag();
				onDrop(upY);
				break;
			case MotionEvent.ACTION_MOVE:
				int moveY = (int) ev.getY();
				onDrag(moveY);
				break;
			default:
				break;
			}
			return true;
		}
		return super.onTouchEvent(ev);
	}

	public void onDrag(int y) {
		if (mDragImageView != null) {
			mWindowParams.alpha = 0.8f;
			mWindowParams.y = y - mDragPoint + mDragOffset;
			mWindowManager.updateViewLayout(mDragImageView, mWindowParams);
		}
		int tempPosition = pointToPosition(0, y);
		if (tempPosition != INVALID_POSITION) {
			mDragPosition = tempPosition;
		}
		int scrollHeight = 0;
		if (y < mUpScrollBounce) {
			scrollHeight = 8;
		} else if (y > mDownScrollBounce) {
			scrollHeight = -8;
		}
//		if (scrollHeight != 0) {
//			setSelectionFromTop(mDragPosition,
//					getChildAt(mDragPosition - getFirstVisiblePosition())
//							.getTop() + scrollHeight);
//		}	
		Log.i("dragView","drag is called");
	}
	
	public void onDrop(int y) {
		Log.i("DragView_drop","onDrop is called");
		mDragImageView = null;
		int tempPosition = pointToPosition(0, y);
		if (tempPosition != INVALID_POSITION) {
			mDragPosition = tempPosition;
		}
		if (y < getChildAt(0).getTop()) {
			mDragPosition = 0;
		} else if (y > getChildAt(getChildCount() - 1).getBottom()) {
			mDragPosition = getAdapter().getCount() - 1;
		}

		Log.i("DragView_drop", "mDragPosition:"+mDragPosition);
		Log.i("DragView_drop", "getAdapter().getCount():"+getAdapter().getCount());
		
		if (mDragPosition > -1 && mDragPosition < getAdapter().getCount()) {
			//NoteListAdapter adapter= (NoteListAdapter) getAdapter();
			//Cursor cursor=mAdapter.getCursor();
			//cursor.moveToPosition(dragPosition);
			long folderId=((NoteListAdapter) getAdapter()).getItemFolderId(mDragPosition);
			Log.i("DragView","mFolderId is:"+String.valueOf(folderId));
			Log.i("DragView","mNoteId is:"+String.valueOf(mNoteId));
			
			if(folderId==NoteDataBase.DEFAULT_FOLDERID){

				//return;
			}else{
				(new NoteDataBase(getContext())).setFolderId(mNoteId,folderId);
				
			}
			((NoteListAdapter) getAdapter()).refreshListView();
			
		}
	}

}