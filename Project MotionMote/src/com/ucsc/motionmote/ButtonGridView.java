package com.ucsc.motionmote;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class ButtonGridView extends View implements ButtonController.Listener {

	// measurements	
	private static final int BUTTON_GRID_SIZE = 2;
	private static final float BUTTON_PADDING = 0.01f;
	private float buttonCellSize;
	private float scale,scale2;
	private static final int INVALID_POINTER_ID = -1;
	private int mActivePointerId = INVALID_POINTER_ID;
	private int mSecondPointerId = INVALID_POINTER_ID;
	private int buttonValue1 =-1;
	private int buttonValue2 =-1;
		
	Paint paint = new Paint(); 
	
	// drawing tools
	
	private Bitmap buttonIdleBitmap;	
	private Bitmap buttonPressedBitmap;
	
	// model
	
	private ButtonController model;

	public ButtonGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public ButtonGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ButtonGridView(Context context) {
		super(context);
		init();
	}

	private void init() {
		initDrawingInstruments();
	}
	
	private void initDrawingInstruments() {
		Resources resources = getContext().getResources();
		
		buttonIdleBitmap = BitmapFactory.decodeResource(resources, R.drawable.button_idle);
		buttonPressedBitmap = BitmapFactory.decodeResource(resources, R.drawable.button_pressed);
	}	
	
	public void setDrumMachineModel(ButtonController model) {
		if (model != null) {
			model.removeListener(this);
		}
		this.model = model;
		if (model != null) {
			model.addListener(this);
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		scale = canvas.getClipBounds().height();
		scale2 = canvas.getClipBounds().width();	
		
		paint.setColor(Color.BLACK); 
		paint.setTextSize(60); 
			
		canvas.save(Canvas.MATRIX_SAVE_FLAG);
		canvas.scale(scale2, scale);
		
		drawButtons(canvas);
		canvas.restore();
	}
	
	private void drawButtons(Canvas canvas) {
		for (int row = 0; row < BUTTON_GRID_SIZE; ++row) {
			for (int col = 0; col < BUTTON_GRID_SIZE; ++col) {
				drawButton(canvas, row, col);
			}
		}
	}
	
	private void drawButton(Canvas canvas, int row, int col) {
		
		buttonCellSize = 1f / BUTTON_GRID_SIZE;
		float textLeft,textTop;
		
		float buttonCellTop = row * buttonCellSize;
		float buttonCellLeft = col * buttonCellSize;
		
		float buttonTop = buttonCellTop + BUTTON_PADDING;
		float buttonLeft = buttonCellLeft + BUTTON_PADDING;
		
		float buttonSize = (buttonCellSize - BUTTON_PADDING * 2);
		
		Bitmap bitmap = getBitmapForButton(row, col);
		float pixelSizeWidth = canvas.getClipBounds().width();
		float pixelSizeHeight = canvas.getClipBounds().height();
		float bitmapScaleX = (pixelSizeWidth / bitmap.getWidth()) * buttonSize;
		float bitmapScaleY = (pixelSizeHeight / bitmap.getHeight()) * buttonSize;
		
		canvas.save(Canvas.MATRIX_SAVE_FLAG);
		canvas.scale(bitmapScaleX, bitmapScaleY);
		canvas.drawBitmap(bitmap, buttonLeft / bitmapScaleX, buttonTop / bitmapScaleY, null);
		
		textLeft = (buttonLeft / bitmapScaleX)+50;
		textTop = (buttonTop / bitmapScaleY) +160;
		
		canvas.drawText(getButtonName(row,col), textLeft, textTop, paint);
	
		canvas.restore();
	}

	private Bitmap getBitmapForButton(int row, int col) {
		return model.isButtonPressed(getButtonIndex(row, col)) ?
				buttonPressedBitmap : buttonIdleBitmap;
	}

	private int getButtonIndex(int row, int col) {
		return row * BUTTON_GRID_SIZE + col;
	}
	
	private String getButtonName(int row, int col) {
		int no= row * BUTTON_GRID_SIZE + col;
		String name = "A";
		switch(no)
		{
		case 0:
			name = "A";
			return name;
		case 1:
			name = "B";
			return name;
		case 2:
			name = "C";
			return name;
		case 3:
			name = "D";
			return name;
		}
		return name;
		}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
				
		int chosenWidth = chooseDimension(widthMode, widthSize);
		int chosenHeight = chooseDimension(heightMode, heightSize);
		
		setMeasuredDimension(chosenWidth,chosenHeight);
	}
	
	private int chooseDimension(int mode, int size) {
		if (mode == MeasureSpec.AT_MOST || mode == MeasureSpec.EXACTLY) {
			return size;
		} else { // (mode == MeasureSpec.UNSPECIFIED)
			return getPreferredSize();
		} 
	}
	
	private int getPreferredSize() {
		return 300;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		int buttonIndex = -1;
		int buttonIndex2 = -1;
		
		final int action = ev.getAction();
		switch (action & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN: {
			mActivePointerId = ev.getPointerId(0);
			final int pointerIndex = ev.findPointerIndex(mActivePointerId);	
			buttonIndex = getButtonByCoords(ev.getX(pointerIndex), ev.getY(pointerIndex));
			buttonValue1=buttonIndex;
			if (buttonIndex != -1) {
				model.pressButton(buttonIndex);
			}
			Log.d("TESTMULTITOUCH","ACTION_DOWN ");
			break;
		}
		case MotionEvent.ACTION_POINTER_DOWN: {
			final int pointerIndex = ev.findPointerIndex(mActivePointerId);
			final int pointer2Index = (action & MotionEvent.ACTION_POINTER_ID_MASK) >> MotionEvent.ACTION_POINTER_ID_SHIFT;
			mSecondPointerId = ev.getPointerId(pointer2Index);
			
			buttonIndex = getButtonByCoords(ev.getX(pointerIndex), ev.getY(pointerIndex));
			buttonValue1=buttonIndex;
			if (buttonIndex != -1) {
				model.pressButton(buttonIndex);
			}	
			buttonIndex2 = getButtonByCoords(ev.getX(pointer2Index), ev.getY(pointer2Index));
			buttonValue2=buttonIndex2;
			if (buttonIndex2 != -1) {
				model.pressButton(buttonIndex2);
			}
			Log.d("TESTMULTITOUCH","ACTION_POINTER_DOWN ");
			break;
		}
		
		case MotionEvent.ACTION_MOVE: {
			final int pointerIndex = ev.findPointerIndex(mActivePointerId);
			buttonIndex = getButtonByCoords(ev.getX(pointerIndex), ev.getY(pointerIndex));
			final float x = ev.getX(pointerIndex);
			final float y = ev.getY(pointerIndex);
			
			if (mSecondPointerId == INVALID_POINTER_ID) {
				Log.d("TESTMULTITOUCH","ACTION_MOVE (SINGLE) AT "+x+","+y);
				if(buttonValue1 != buttonIndex) {
					if (buttonValue1 != -1) {
						model.releaseButton(buttonValue1);
						model.pressButton(buttonIndex);
						buttonValue1=buttonIndex;
					}
				}
			}
			
			else
			{
				final int pointer2Index = ev.findPointerIndex(mSecondPointerId);
				buttonIndex2 = getButtonByCoords(ev.getX(pointer2Index), ev.getY(pointer2Index));
				//final float x2 = ev.getX(pointer2Index);
				//final float y2 = ev.getY(pointer2Index);
				//Log.d("TESTMULTITOUCH","ACTION_MOVE (DOUBLE) AT "+x+","+y+" AND "+x2+","+y2);
				Log.d("TESTMULTITOUCH","ACTION_MOVE (DOUBLE) AT "+ buttonValue1 + " " + buttonValue2);
				
				if(buttonValue1 != buttonIndex) {
					if (buttonValue1 != -1) {
						model.releaseButton(buttonValue1);
						model.pressButton(buttonIndex);
						buttonValue1=buttonIndex;
					}
				}
				
				if(buttonValue2 != buttonIndex2) {
					if (buttonValue2 != -1) {
						model.releaseButton(buttonValue2);
						model.pressButton(buttonIndex2);
						buttonValue2=buttonIndex2;
					}
				}		
			}
			break;
		}
		
		case MotionEvent.ACTION_POINTER_UP: {
			final int pointerIndex = (action & MotionEvent.ACTION_POINTER_ID_MASK) >> MotionEvent.ACTION_POINTER_ID_SHIFT;
			final int pointerId = ev.getPointerId(pointerIndex);
			if (pointerId == mActivePointerId) {
				final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
				mActivePointerId = ev.getPointerId(newPointerIndex);
				buttonIndex = getButtonByCoords(ev.getX(pointerIndex), ev.getY(pointerIndex));
				if (buttonIndex != -1) {
					model.releaseButton(buttonIndex);
				}
				mSecondPointerId = INVALID_POINTER_ID;
				
				
				Log.d("TESTMULTITOUCH","ACTION_POINTER_UP ON ActivePointer");
			}
			else if (pointerId == mSecondPointerId)
			{
				mSecondPointerId = INVALID_POINTER_ID;
				buttonIndex2 = getButtonByCoords(ev.getX(pointerIndex), ev.getY(pointerIndex));
				if (buttonIndex2 != -1) {
					model.releaseButton(buttonIndex2);
				}
				Log.d("TESTMULTITOUCH","ACTION_POINTER_UP ON SecondPointer");
			}
			else
				Log.d("TESTMULTITOUCH","ACTION_POINTER_UP ON NONE!");
			break;
		}
		case MotionEvent.ACTION_UP: {
			mActivePointerId = ev.getPointerId(0);
			final int pointerIndex = ev.findPointerIndex(mActivePointerId);	
			buttonIndex = getButtonByCoords(ev.getX(pointerIndex), ev.getY(pointerIndex));
			if (buttonIndex != -1) {
				model.releaseButton(buttonIndex);
			}
			model.releaseAllButtons();
			Log.d("TESTMULTITOUCH","ACTION__UP");
			mActivePointerId = INVALID_POINTER_ID;
			break;
		}
		case MotionEvent.ACTION_CANCEL: {
			Log.d("TESTMULTITOUCH","ACTION__CANCEL");
			mActivePointerId = INVALID_POINTER_ID;
			mSecondPointerId = INVALID_POINTER_ID;
			break;
		}
		}
		return true;
	}

	private int getButtonByCoords(float x, float y) {
		float scaledX = x / scale2;
		float scaledY = y / scale;
		
		float buttonCellX = FloatMath.floor(scaledX / buttonCellSize); 
		float buttonCellY = FloatMath.floor(scaledY / buttonCellSize);
		
		return getButtonIndex((int) buttonCellY, (int) buttonCellX);
	}

	//@Override
	public void buttonStateChanged(int index) {
		invalidate();
	}

	//@Override
	public void multipleButtonStateChanged() {
		invalidate();
	}
}
