package com.emerson.smsmonitor;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class SMSBroadcast extends BroadcastReceiver {

	private final String	TAG						= SMSBroadcast.class.getName();
	private final String	SMS_RECEIVED			= "android.provider.Telephony.SMS_RECEIVED";
	private final String	SMS_SEND				= "android.provider.Telephony.SEND_SMS";
	private final String	SENT_ACTION_SMS			= "SENT_SMS";
	private final String	SMS_ACTION_DELIVERED	= "DELIVERED_SMS";

	String					textMessage;
	String					phoneNumber;

	@Override
	public void onReceive(Context c, Intent in) {
		String MSG_TYPE = in.getAction();
		Bundle bundle = in.getExtras();
		Log.v(TAG, "MSG_TYPE = " + MSG_TYPE);
		if (MSG_TYPE.equals(SMS_RECEIVED)) {
			Log.v(TAG, "SMS_RECEIVED ");
			if (bundle != null) {
				Object[] pdu = (Object[]) bundle.get("pdus");
				SmsMessage[] sms = new SmsMessage[pdu.length];

				for (int i = 0; i < pdu.length; i++) {
					sms[i] = SmsMessage.createFromPdu((byte[]) pdu[i]);
				}

				for (SmsMessage msg : sms) {
					if (msg.getMessageBody().contains("Sms:") || msg.getMessageBody().contains("Send:")) {
						// abortBroadcast();
					}
					textMessage = msg.getMessageBody();
					// Get the phone number of the sender if we want to use
					phoneNumber = msg.getOriginatingAddress();
					Log.v(TAG, "phoneNumber = " + phoneNumber);
					Log.v(TAG, "Message = " + textMessage);

					Util.showMessage(c, textMessage, phoneNumber);

				}
				// After this we need to analyze the text of our message to
				// check if starts with "sms:" and then perform an action
				analyzeBody();
			}

		}
		else if (MSG_TYPE.equals(SMS_SEND)) {
			Toast.makeText(c, "SMS SENT: " + MSG_TYPE, Toast.LENGTH_LONG).show();
			Log.v(TAG, "SEND_SMS ResultCode = " + getResultCode());

		}
		else if (MSG_TYPE.equals(SENT_ACTION_SMS)) {
			Log.v(TAG, "SENT_ACTION_SMS ResultCode = " + getResultCode());
			String info = "Send information: ";
			switch (getResultCode()) {
			case Activity.RESULT_OK:
				Toast.makeText(c, "SMS was sent!", Toast.LENGTH_SHORT).show();
				break;
			case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                info += "send failed, generic failure";
                break;
			case SmsManager.RESULT_ERROR_NO_SERVICE:
				info += "send failed, no service";
				break;
			case SmsManager.RESULT_ERROR_NULL_PDU:
				info += "send failed, null pdu";
				break;
			case SmsManager.RESULT_ERROR_RADIO_OFF:
				info += "send failed, radio is off";
				break;
			default:
				Toast.makeText(c, "SMS was not sent!", Toast.LENGTH_SHORT).show();
				break;
			}
			Log.v(TAG, "SENT_ACTION_SMS info = " + info);
		}
		else if (MSG_TYPE.equals(SMS_ACTION_DELIVERED)) {
			Log.v(TAG, "SMS_ACTION_DELIVERED  ResultCode = " + getResultCode());
			 String info = "Delivery information: ";
			switch (getResultCode()) {
			case Activity.RESULT_OK:
				Toast.makeText(c, "SMS was delivered!", Toast.LENGTH_SHORT).show();
                info += "delivered";
                break;
			case Activity.RESULT_CANCELED:
				info += "not delivered";
				break;
			default:
				Toast.makeText(c, "SMS was not delivered!", Toast.LENGTH_SHORT).show();
				break;
			}
			Log.v(TAG, "SMS_ACTION_DELIVERED info = " + info);
		}
		else {
			Log.e(TAG, "Received unexpected intent " + in.toString());
		}
	}

	/**
	 * Method that will analyze the body of the SMS to perform some checks. If
	 * the SMS doesn't match the pre-requesites it won't do anything
	 */
	private void analyzeBody() {

	};
}
