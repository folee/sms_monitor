/**
 * As you might know, Android doesn't provide an Intent that you can launch when you send a message
 * So the solution is simple. Use a ContentObserver to notify you about changes in the outgoing messages
 */
package com.emerson.smsmonitor;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

public class OutgoingSMS extends ContentObserver {
	private final String	TAG					= OutgoingSMS.class.getName();
	public Context			ctx;
	// Variable used to hold the text of the sent SMS
	String					textSent;
	// Variable used to hold the number of the receiver
	String					numberSent;
	Uri						SMS_STATUS			= Uri.parse("content://sms");
	// Should be 2
	int						type;

	// Variables used to send the message to the host
	String					SENT_ACTION			= "SENT_SMS";
	String					DELIVERED_ACTION	= "DELIVERED_SMS";
	String					hostNumber;
	String					protocol;
	
	SharedPreferences			preferences;
	public static final String	PREFS_NAME			= "PREFS";
	Boolean						hasHost;

	public OutgoingSMS(Handler handler, Context c) {
		super(handler);
		ctx = c;
	}

	@Override
	public boolean deliverSelfNotifications() {
		return false;
	}

	@Override
	public void onChange(boolean selfChange) {
		super.onChange(selfChange);

		Cursor cursor = ctx.getContentResolver().query(SMS_STATUS, null, null, null, null);

		// Move the cursor to the most recent message(last row)
		cursor.moveToNext();

		// Get the type of the message
		// The type should be 2
		type = cursor.getInt(cursor.getColumnIndex("type"));
		Log.v(TAG, "type = " + type);
		// Get the protocol of the message
		protocol = cursor.getString(cursor.getColumnIndex("protocol"));

		if (protocol == null) {
			numberSent = cursor.getString(cursor.getColumnIndex("address"));
			textSent = cursor.getString(cursor.getColumnIndex("body"));
			Log.v(TAG, "numberSent = " + numberSent);
			Log.v(TAG, "textSent = " + textSent);

			switch (type) {
			// MESSAGE_TYPE_QUEUED = 6 , for messages to send later
			case 6:
				doNothing();
				break;
			// MESSAGE_TYPE_OUTBOX = 4
			case 4:
				preferences = ctx.getSharedPreferences(PREFS_NAME, 0);
				hostNumber = preferences.getString("hostNumber", "0");
				sendMessage();
				Util.showMessage(ctx, textSent, numberSent);

				break;
			// MESSAGE_TYPE_SENT = 2
			case 2:
				doNothing();
				break;
			}
		}
	}

	public void sendMessage() {
		String message = "Message sent to: " + numberSent + "\n[MESSAGE]" + textSent;
		SmsManager manager = SmsManager.getDefault();
		Intent i = new Intent(SENT_ACTION);
		PendingIntent pin = PendingIntent.getBroadcast(ctx, 0, i, 0);
		Intent o = new Intent(DELIVERED_ACTION);
		PendingIntent pout = PendingIntent.getBroadcast(ctx, 0, o, 0);

		ArrayList<String> messages = manager.divideMessage(message);
		ArrayList<PendingIntent> sentPI = new ArrayList<PendingIntent>();
		ArrayList<PendingIntent> deliveredPI = new ArrayList<PendingIntent>();

		for (int j = 0; j < messages.size(); j++) {
			sentPI.add(pin);
			deliveredPI.add(pout);
		}

		manager.sendMultipartTextMessage(hostNumber, null, messages, sentPI, deliveredPI);
	}
	
	/**
	 * Method used to check if we have any host in the preferences
	 */
	public boolean checkHost() {
		preferences = ctx.getSharedPreferences(PREFS_NAME, 0);
		hasHost = preferences.getBoolean("setHost", false);

		if (hasHost)
			return true;
		else
			return false;
	}

	public void doNothing() {}
}
