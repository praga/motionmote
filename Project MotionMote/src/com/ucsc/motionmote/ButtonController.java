package com.ucsc.motionmote;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.util.Log;

public final class ButtonController {

	public static final int TOTAL_BUTTONS = 4;
	public static boolean a=false,b=false,c=false,d=false;

	public interface Listener {
		void buttonStateChanged(int index);
		void multipleButtonStateChanged();
	}
	
	private boolean[] buttonPressMap = new boolean[TOTAL_BUTTONS];
	private List<Listener> listeners = new ArrayList<Listener>();
	
	public ButtonController(){
	}
	
	public ButtonController(Context context) {
		for (int i = 0; i < TOTAL_BUTTONS; ++i) {
			buttonPressMap[i] = false;
		}
	}
	
	public void pressButton(int index) {
		if (index >= 0 && index < TOTAL_BUTTONS) {
			if (buttonPressMap[index] == false) {
				buttonPressMap[index] = true;
							
				if(index==0)
				{
					a=true;
					Log.d("TESTMULTITOUCH","A =1");
				}
				
				if(index==1)
				{
					b=true;
					Log.d("TESTMULTITOUCH","B =1");
				}
				if(index==2)
				{
					c=true;
					Log.d("TESTMULTITOUCH","C =1");
				}
				if(index==3)
				{		
					d=true;
					Log.d("TESTMULTITOUCH","D =1");
				}
				for (Listener listener : listeners) {
					listener.buttonStateChanged(index);
				}
			}
		}
	}
	
	public void releaseButton(int index) {
		if (index >= 0 && index < TOTAL_BUTTONS) {
			if (buttonPressMap[index] == true) {
				buttonPressMap[index] = false;
						
				if(index==0)
				{
					a=false;
					Log.d("TESTMULTITOUCH","A =0");
				}
				
				if(index==1)
				{
					b=false;
					Log.d("TESTMULTITOUCH","B =0");
				}
				if(index==2)
				{
					c=false;
					Log.d("TESTMULTITOUCH","C =0");
				}
				if(index==3)
				{		
					d=false;
					Log.d("TESTMULTITOUCH","D =0");
				}

				for (Listener listener : listeners) {
					listener.buttonStateChanged(index);
				}
			}
		}
	}
	
	public boolean isButtonPressed(int index) {
		if (index < 0 || index > TOTAL_BUTTONS) {
			return false;
		} else {
			return buttonPressMap[index];
		}
	}
	
	public void addListener(Listener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(Listener listener) {
		listeners.remove(listener);
	}

	public void releaseAllButtons() {
		for (int i = 0; i < buttonPressMap.length; ++i) {
			buttonPressMap[i] = false;
		}
		for (Listener listener : listeners) {
			listener.multipleButtonStateChanged();
		}
	}
	public void dispose() {
	}
}
