package com.ucsc.motionmote;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

public class MMoteMenuActivity extends Activity {
	//Global Variables
	
    /** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE); //to remove title bar
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);
    }
	
	 public void onClickGameController(View v) {
		 startActivity(new Intent(MMoteMenuActivity.this,
				 MMoteGameControllerActivity.class));
	 }
	 
	 public void onClickAbout(View v) {
		 final AlertDialog.Builder aboutalertbox = new AlertDialog.Builder(this);
         aboutalertbox.setTitle("About MotionMote");
         aboutalertbox.setMessage("MotionMote turns your Android phone into a remote control for your computer, allowing you to control games at a distance. \n\nDeveloped by: \n• A.C Udugala \n• I.M. Irham \n• R. Kreshan \n• S. Pragalathan \n• U. Rajeeva \n\nfor the 3rd year group project(UCSC)");
         aboutalertbox.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
             public void onClick(DialogInterface arg0, int arg1) {     	 
             }});
         aboutalertbox.show();
	 }
	 
	 public void onClickSettings(View v) {
		 startActivity(new Intent(MMoteMenuActivity.this,
				 MMoteSettingsActivity.class));
	 }
	 
	 public void onClickInstructions(View v) {
		 
		 final AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
         alertbox.setTitle("Step 1");
         alertbox.setMessage("Start the windows MotionMote Server and note down the port and the ip address displayed in the application window");
    	 final AlertDialog.Builder alertbox2 = new AlertDialog.Builder(this);
         alertbox2.setTitle("Step 2");
         alertbox2.setMessage("Go to the settings in the android application and update the ip address and port field with the applications values");
    	 final AlertDialog.Builder alertbox3 = new AlertDialog.Builder(this);
         alertbox3.setTitle("Step 3");
         alertbox3.setMessage("In the windows server application press the start button to listen");
    	 final AlertDialog.Builder alertbox4 = new AlertDialog.Builder(this);
         alertbox4.setTitle("Step 4");
         alertbox4.setMessage("Go to the game Controller button in the android application and press menu button and select start/stop button to start and stop the server");
         
         alertbox.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
             public void onClick(DialogInterface arg0, int arg1) {     	 
             }});
      
         alertbox.setNegativeButton("Next", new DialogInterface.OnClickListener() {
        	 public void onClick(DialogInterface arg0, int arg1) {
            	 alertbox2.show();
             }});
         alertbox2.setPositiveButton("Back", new DialogInterface.OnClickListener() {
             public void onClick(DialogInterface arg0, int arg1) {     	 
            	 alertbox.show();
             }});
      
         alertbox2.setNegativeButton("Next", new DialogInterface.OnClickListener() {
        	 public void onClick(DialogInterface arg0, int arg1) {
            	 alertbox3.show();
             }});
         alertbox3.setPositiveButton("Back", new DialogInterface.OnClickListener() {
             public void onClick(DialogInterface arg0, int arg1) {     	 
            	 alertbox2.show();
             }});
      
         alertbox3.setNegativeButton("Next", new DialogInterface.OnClickListener() {
        	 public void onClick(DialogInterface arg0, int arg1) {
            	 alertbox4.show();
             }});
         alertbox4.setPositiveButton("Back", new DialogInterface.OnClickListener() {
             public void onClick(DialogInterface arg0, int arg1) {     	 
            	 alertbox3.show();
             }});
      
         alertbox4.setNegativeButton("Finish", new DialogInterface.OnClickListener() {
        	 public void onClick(DialogInterface arg0, int arg1) {
             }});     
         alertbox.show();       
	 }
}