package com.emerson.smsmonitor;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MonitorSMS extends Activity {

	Button	startMonitor;
	Button	stopMonitor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.monitorsms);

		startMonitor = (Button) findViewById(R.id.monitorStart);
		stopMonitor = (Button) findViewById(R.id.monitorStop);

		/**
		 * Button listeners for each of the button. It's simple: if we click
		 * startMonitor we start the service If we click stopMonitor the service
		 * should be stopped
		 */
		startMonitor.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {

				try {
					Intent start = new Intent(MonitorSMS.this, SMSMonitorService.class);
					startService(start);
					Toast.makeText(getApplicationContext(), "SMS Monitor started..", Toast.LENGTH_SHORT).show();
					MonitorSMS.this.finish();
				} catch (Exception ex) {
					Log.e("startMonitor", "An error occurred while trying to start the Monitor service...");
					ex.getCause();
				}
			}
		});

		stopMonitor.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {

				try {
					Intent stop = new Intent(MonitorSMS.this, SMSMonitorService.class);
					stopService(stop);
					Toast.makeText(getApplicationContext(), "SMS Monitor stopped..", Toast.LENGTH_SHORT).show();
					
				} catch (Exception ex) {
					Log.e("stopMonitor", "An error occurred while trying to stop the Monitor service...");
					ex.getCause();
				}
			}
		});
	}
}
