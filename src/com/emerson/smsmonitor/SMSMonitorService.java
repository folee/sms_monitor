package com.emerson.smsmonitor;

import android.app.AlertDialog;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.WindowManager;

public class SMSMonitorService extends Service {
	private final String	TAG						= SMSMonitorService.class.getName();
	private Context			context;
	private final String	SMS_RECEIVED			= "android.provider.Telephony.SMS_RECEIVED";
	private final String	SMS_SEND				= "android.provider.Telephony.SEND_SMS";
	private final String	SENT_ACTION_SMS			= "SENT_SMS";
	private final String	SMS_ACTION_DELIVERED	= "DELIVERED_SMS";
	ContentResolver			resolver;
	ContentObserver			observer;

	SMSBroadcast			smsBroadcast;

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(TAG, "--> onCreate");
		context = this;
		IntentFilter filter = new IntentFilter(SMS_RECEIVED);
		filter.addAction(SMS_SEND);
		filter.addAction(SENT_ACTION_SMS);
		filter.addAction(SMS_ACTION_DELIVERED);
		
		// Give the highest priority so the user will not be able to read the
		// setHost message
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		smsBroadcast = new SMSBroadcast();
		registerReceiver(smsBroadcast, filter);

		resolver = this.getContentResolver();
		observer = new OutgoingSMS(new Handler(), this);
		resolver.registerContentObserver(Uri.parse("content://sms/"), true, observer);

	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		Log.i(TAG, "--> onStart");

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(context.getString(R.string.monitorStart)).setPositiveButton(
				context.getString(R.string.ok), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {}
				});
		AlertDialog ad = builder.create();
		// ad.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_DIALOG);
		ad.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		ad.setCanceledOnTouchOutside(false);
		ad.show();

	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "--> onDestroy");
		unregisterReceiver(smsBroadcast);
		this.getContentResolver().unregisterContentObserver(observer);
	}

}