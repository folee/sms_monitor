package com.emerson.smsmonitor;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;


public class Util {
	private static final String TAG = Util.class.getName();
	
	/**
	 * Display the SMS
	 * @param ctx
	 * @param msg
	 * @param phoneNum
	 */
	public static void showMessage(Context ctx, String msg, String phoneNum) {
		String title = "";
		String name = getContactName(ctx, phoneNum);
		if (!TextUtils.isEmpty(name)) {
			title = name + ctx.getString(R.string.contact).replaceFirst("%s", "" + phoneNum);
		}
		else {
			title = phoneNum;
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		builder.setTitle(title);
		builder.setMessage(msg).setPositiveButton(ctx.getString(R.string.ok), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {}
		});
		AlertDialog ad = builder.create();
		// ad.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_DIALOG);
		ad.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		ad.setCanceledOnTouchOutside(false);
		ad.show();
	}

	/**
	 * Query the contact's name by phone number
	 * @param context
	 * @param num
	 * @return
	 */
	public static String getContactName(Context context, String num) {
		String name = "";
		ContentResolver resolver = context.getContentResolver();
		Uri uri = Uri.parse("content://com.android.contacts/data/phones/filter/" + num);
		Cursor c = resolver.query(uri, new String[] { "display_name" }, null, null, null);
		while (c.moveToNext()) {
			name = c.getString(0);
			Log.d(TAG, "ContactName = " + name);
		}
		c.close();
		return name;
	}
}