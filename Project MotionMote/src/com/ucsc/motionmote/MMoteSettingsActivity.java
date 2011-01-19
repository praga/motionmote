package com.ucsc.motionmote;


import android.os.Bundle;
import android.preference.PreferenceActivity;


public class MMoteSettingsActivity extends PreferenceActivity {
    /** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    addPreferencesFromResource(R.xml.settings);
	    
      /*  Preference wifiSettings = (Preference) findPreference("wifisettings");
        wifiSettings.setOnPreferenceClickListener(new OnPreferenceClickListener() {
        	public boolean onPreferenceClick(Preference preference) {
        		Intent i = new Intent(Settings.ACTION_WIFI_SETTINGS);
        		return true;
        		}
        	});*/
	    
    }	
}