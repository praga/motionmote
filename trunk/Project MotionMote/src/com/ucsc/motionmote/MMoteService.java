package com.ucsc.motionmote;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class MMoteService extends Service {
	
	/* Variable Declaration */
	private SensorManager sm;
	public float x = 0,y = 0,z = 0; //Accelerometer Values
	private MMoteConnection apc;
	private Thread th;
	public int port;
	public int interval;
	public String ipaddress;
	private ButtonController bc = new ButtonController();   
	
	// NORMAL SERVICE
	@Override
	public void onCreate() {
		super.onCreate();
		Log.d("MotionMote", "Service Created");
		
		sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		try
			{
				sm.registerListener(asl, sm.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0), SensorManager.SENSOR_DELAY_GAME);
			}
		catch(IndexOutOfBoundsException e)
			{
				Toast.makeText(this, "ERROR: Accelerometer not found!", Toast.LENGTH_SHORT).show();
			}
			
			Log.v("MotionMote", " Sensors initiated.");		
	}
	
	@Override
	public void onStart(Intent intent, int startId)
	{
		super.onStart(intent, startId);
		Log.d("MotionMote", "Service Started");
		
		interval = intent.getExtras().getInt("interval", 20);
		port = intent.getExtras().getInt("port", 3141);
		ipaddress = intent.getExtras().getString("ipaddress");
		
		apc = new MMoteConnection(this, port, ipaddress, interval);
		Log.v("MotionMote", "MotionMote connection initiated");
        th = new Thread(apc);
		Log.v("MotionMote", "MotionMote connection thread initiated");
        th.start();
		Log.v("MotionMote", "MotionMote connection thread started!");
	}
	
	@Override
	public void onDestroy() {
		if(asl != null)
			sm.unregisterListener(asl);
		Log.v("MotionMote", "Stopping MotionMote thread...");
		apc.killThread();
		try {
			th.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Log.v("MotionMote", "MotionMote thread down!");
		Log.d("MotionMote", "Service destroyed & prefs saved.");
		super.onDestroy();
	}
	
	 // BIND
	@Override
	public IBinder onBind(Intent arg0) {
		return mBinder;
	}
	
    public class LocalBinder extends Binder {
        MMoteService getService() {
            return MMoteService.this;
        }
    }
    private final IBinder mBinder = new LocalBinder();
    // END BIND
	
	private SensorEventListener asl = new SensorEventListener() {
		//@Override
		public void onAccuracyChanged(Sensor arg0, int arg1) {
		}

		//@Override
		public void onSensorChanged(SensorEvent event) {
				x = -event.values[1];
				y = -event.values[0];
				z = event.values[2];
		}
    };  
    
	/*Method for passing accelerometer values to other Activity */
	public synchronized float[] getAccValues() 
    {
    	return new float[] {x, y, z};
    }	
	
	@SuppressWarnings("static-access")
	public synchronized boolean[] getButtonValues()
    {
    	return new boolean[] {bc.a, bc.b, bc.c ,bc.d};
    }
    
	}