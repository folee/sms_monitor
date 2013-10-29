package com.emerson.smsmonitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class LaunchOnBoot extends BroadcastReceiver {

	private final String TAG = LaunchOnBoot.class.getName();
	
	@Override
	public void onReceive(Context c, Intent in) {
		if(in.getAction().equals( Intent.ACTION_BOOT_COMPLETED )){
			Intent i = new Intent(c, SMSMonitorService.class);
			c.startService(i);
			Toast.makeText(c, "SMSMonitorService started...", Toast.LENGTH_LONG).show();
			Log.v(TAG, "SMS Monitor is Started~");
			
		}else{
			Log.e(TAG, "Received unexpected intent " + in.toString());
		}
	}
}
