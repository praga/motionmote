package com.ucsc.motionmote;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import android.util.Log;

final class MMoteConnection implements Runnable {
	
	private String ipaddress = "";
	private int port = 8675; // port no
	private ServerSocket ss;
	private Socket s;
	private OutputStream os;
	private InputStream is;
	private InputStreamReader isr;
	private long interval = 20;
	private boolean stopping = false;
	private MMoteService parent;
	
	public MMoteConnection(MMoteService activity1, int Port, String ipAddress, int interval2)
	{
		parent = activity1;
		port = Port;
		ipaddress= ipAddress;
		Log.v("MotionMote", "Info recieved (initiated)");
		interval = (long) (float) ((1 / (float) interval2) * 1000);
	}
	
	public void run() {
		Log.d("MotionMote", "Thread started. Waiting for connection...");
		client();
		Log.d("MotionMote", "Connected to a Server");
		
		float[] AccValues; //Accelerometer Values
		boolean[] ButtonValues; //Button Values
		String msg = "";
		
		while(!stopping)
		{
			if(s != null)
			{
				try
				{
					AccValues = parent.getAccValues();
					ButtonValues = parent.getButtonValues();
					msg = "[" +","+ -AccValues[0] + "," + -AccValues[1] + "," + AccValues[2] + "," + ButtonValues[0] + "," + ButtonValues[1]+ "," + ButtonValues[2] + "," + ButtonValues[3] +","+ "]\n"; //[] for easy string view
					os.write(msg.getBytes());
					os.flush();
					try {
						Thread.sleep(interval);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if(isr.ready())
					{
						char[] ch = new char[1024];
						isr.read(ch);
						String st = new String(ch);
						if(st.startsWith("<STOP>"))
						{
							client();
						}
					}
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (IOException e) {
					if(!stopping)
					{
						Log.d("MotionMote", "Thread started");
						client();
						Log.d("MotionMote", "Connected to a Server");
					}
				}
			}
		}
	}
	
	public final synchronized void killThread()
	{
		Log.d("MotionMote", "Thread dying...");
		try {
            os.write(("Client Stopped\n").getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("MotionMote", "MM: Error sending info to PC");
        }
		if(!stopping)
		{
			stopping = true;
			if(isr != null)
			{
				try {
					isr.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			if(is != null)
			{
				try {
					is.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			if(os != null)
			{
				try {
					os.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			if(s != null)
			{
				try {
					s.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			if(ss != null)
			{
				try {
					ss.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		Log.d("MotionMote", "Thread dead!");
	}
	
	//Client Methods
    private void client()
    {
        clientInit();
        if(stopping) return; // Escape method
        //clientMainLoop();
        if(!stopping)
        {
            try {
                s.setTcpNoDelay(true);
            }
            catch (SocketException e) {
                e.printStackTrace();
            }
            try {
                s.setKeepAlive(true);
            } catch (SocketException e) {
                e.printStackTrace();
            }
            clientSetup();
        }
    }
    private void clientInit()
    {
        if(!stopping)
        {
            while(s == null)
            {
                try {
                    s = new Socket(ipaddress,port);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("MotionMote", "Couldn't initiate Socket, perhaps not connected to network...");
                    try
                    {
                        Thread.sleep(500);
                    } catch (InterruptedException e1)
                    {
                        e1.printStackTrace();
                    }
                }
                if(stopping) return;
            }
            try
            {
                s.setSoTimeout(2000);
            } catch (SocketException e)
            {
                e.printStackTrace();
                Log.w("MotionMote", "Couldn't set timeout");
            }
        }
    }

    private void clientSetup()
    {
    	Log.d("MotionMote", "Client Setup Started");
        try {
            os = s.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("MotionMote", "Couldn't create output stream");
        }
        try {
            is = s.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("MotionMote", "Couldn't create input stream");
        }
        isr = new InputStreamReader(is);

        /*
        try {
            os.write(("<MODE></MODE>\n").getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("MotionMote", "Error sending info to Server");
        }
        */
        Log.d("MotionMote", "Client Setup ended");
    }
}