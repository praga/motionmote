package com.ucsc.motionmote;

import java.util.List;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MMoteGameControllerActivity extends Activity {
	
	private SharedPreferences prefs;
	private Intent i;
	private ActivityManager am;
	private boolean running;
	private MMoteService apsbound = null;
	private WakeLock wakeLock;
	private PowerManager pm;
	private ButtonController model;

	
    /** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("MotionMote", "Hello!");
        this.requestWindowFeature(Window.FEATURE_NO_TITLE); //to remove title bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //remove status bar
        //setContentView(R.layout.gamecontroller);  
        model = new ButtonController(this);
        
        LinearLayout drumMachineLayout 
        	= (LinearLayout) LayoutInflater.from(this).inflate(R.layout.gamecontroller, null);
        setContentView(drumMachineLayout);
        
        ButtonGridView grid = (ButtonGridView) drumMachineLayout.findViewById(R.id.button_grid);
        grid.setDrumMachineModel(model);
        
        
        prefs = PreferenceManager.getDefaultSharedPreferences(this);        
        i = new Intent(MMoteGameControllerActivity.this,MMoteService.class);
        am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        
    	pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
    	wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"MyWakeLock");// wake lock initialization     
        
        SensorManager sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if(sm.getSensorList(Sensor.TYPE_ACCELEROMETER).isEmpty())
        {
        	Toast.makeText(MMoteGameControllerActivity.this, "No Accelerometer Sensor", Toast.LENGTH_SHORT).show();
			Log.i("MotionMote", "No accelerometer!");
        }
        
        if(isRunning("MMoteService"))
        {
			Log.v("MotionMote", "Already running");
	        bind();
	        running = true;
        }
        else
        {
	        running = false;
        }
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	super.onCreateOptionsMenu(menu);
	menu.add(0,0,0, "Start / Stop");
	return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		// Find which menu item has been selected
		switch (item.getItemId()) {
		// Check for each known menu item
		case 0:{
						if(!running) //If client is not started
						{
							Log.i("MotionMote", "Starting Game Controller");
							i = new Intent(MMoteGameControllerActivity.this, MMoteService.class);
							try
							{
								i.putExtra("interval", Integer.parseInt(prefs.getString("interval", "20"))); //passing values to that activity
							}
							catch (NumberFormatException e)
							{
								Toast.makeText(getBaseContext(), "In Preferences, \"Update Interval\" is not a number!", Toast.LENGTH_LONG).show();
								Log.w("MotionMote", "Number format error for interval");
								return false;
							}
							
							try
							{
								i.putExtra("port", Integer.parseInt(prefs.getString("portno", "3141")));
							}
							catch (NumberFormatException e)
							{
								Toast.makeText(getBaseContext(), "In Preferences, \"Port\" is not a number!", Toast.LENGTH_LONG).show();
								Log.w("MotionMote", "Number format error");
								return false;
							}
							
							i.putExtra("ipaddress", prefs.getString("ipaddress", "192.168.1.2"));
							
							Log.v("MotionMote", "Starting service");
							startService(i); //starting mmoteservice
							Log.v("MotionMote", "Service started! (will start when loop is free)");
							
							bind(); //binding to the service
							Log.v("MotionMote", "Bound to service");
							wakeLock.acquire(); //starting wakelock
							running = true;
						}
				        else
				        {
				        	Toast.makeText(getBaseContext(), "Stopping Game Controller", Toast.LENGTH_LONG).show();
							Log.i("MotionMote", "Stopping MotionMote Service");
				        	unbind();
				        	i = new Intent(MMoteGameControllerActivity.this, MMoteService.class);
				        	stopService(i); //stopping mmoteservice
							Log.v("MotionMote", "Sucessfully killed");
							wakeLock.release(); //stopping wakelock
							running = false;
				        }
				}
		}
		return false;
	}
	
	/* To change menu name dynamically 
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
	super.onPrepareOptionsMenu(menu);
	if(running)
	menu.findItem(0).setTitle("Stop");
	
	return true;
	}
	*/
    
		// PRIVATE COMMANDS
	    private void bind()
	    {
	    	i = new Intent(MMoteGameControllerActivity.this, MMoteService.class);
	        bindService(i,mConnection,0);
	    }
	    private void unbind()
	    {
	    	if(apsbound != null)
	    	{
	    		unbindService(mConnection);
	    	}
	    }
	    
	    private boolean isRunning(String Activity)
	    {
	        List<ActivityManager.RunningServiceInfo> sl = am.getRunningServices(Integer.MAX_VALUE);
	        for(int i = 0;i < sl.size();i++)
	        {
	        	if(sl.get(i).service.getClassName().equals("com.ucsc.motionmote." + Activity))
	        	{
	        		return true;
	        	}
	        }
	        return false;
	    }
	    // END PRIVATE COMMANDS	    
    
 // BINDER
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            apsbound = ((MMoteService.LocalBinder)service).getService();
            //sendParent();
        }

        public void onServiceDisconnected(ComponentName className) {
            apsbound = null;
        }
    };    
}