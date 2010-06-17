package pro.android.activity;

import java.net.UnknownHostException;

import org.thounds.thoundsapi.RequestWrapper;
import org.thounds.thoundsapi.ThoundsConnectionException;

import pro.android.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class ThoundsActivity extends CommonActivity {

	NetworkInfo wifiInfo, mobileInfo;

	String username, password;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		ImageView img = (ImageView) this.findViewById(R.id.ImageView01);
		img.setOnClickListener(new OnClickListener() {

			public void onClick(final View v) {

				// ========= CHECK CONNECTION ===================
				ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

				wifiInfo = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
				mobileInfo = connectivity.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

				if (!wifiInfo.isConnectedOrConnecting() && !mobileInfo.isConnectedOrConnecting()) {
					showDialog(DIALOG_ALERT_CONNECTION);				
				} else {

					Runnable run = new Runnable(){
						public void run() {
							Looper.prepare();
							// =========CHECK IS LOGGED===================
							// Restore preferences
							SharedPreferences settings = getSharedPreferences(
									PREFS_NAME, 0);
							username = settings.getString("silentUsername", username);
							password = settings.getString("silentPassword", password);

							
							try {
								if (username != null && password!= null && RequestWrapper.login(username, password)){	
									isLogged = true;
									nextIntent = new Intent(v.getContext(), HomeActivity.class);
								} else {
									nextIntent = new Intent(v.getContext(), LoginActivity.class);
								}
							} catch (ThoundsConnectionException e) {
								isLogged = false;
								dismissDialog(DIALOG_LOADING);
								
								e.printStackTrace();
							}
							

							runOnUiThread(returnRes);
						}			
					};
					Thread thread =  new Thread(null, run, "LoginMain");
					thread.start();
					showDialog(DIALOG_LOADING);
				}

			}
		});
	}

	private Runnable returnRes = new Runnable() {
		public void run() {
			dismissDialog(DIALOG_LOADING);
			if(isLogged)
				startActivity(nextIntent);
			else
				showDialog(DIALOG_ALERT_CONNECTION);
		}

	};

	// =======NOTIFICHE==================================
	public void notification() {

		NotificationManager not = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		int icon = R.drawable.icon;
		CharSequence tickerText = "Thounds!";
		long when = java.lang.System.currentTimeMillis();
		Notification notification = new Notification(icon, tickerText, when);
		Context context = getApplicationContext();
		CharSequence contentTitle = "New Thounds!";
		CharSequence contentText = "Bellaaaa!";
		Intent notificationIntent = new Intent(this, this.getClass());
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, 0);

		notification.setLatestEventInfo(context, contentTitle, contentText,
				contentIntent);
		notification.defaults |= Notification.DEFAULT_SOUND;
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		notification.ledARGB = 0xff00ff00;
		notification.ledOnMS = 300;
		notification.ledOffMS = 1000;
		notification.flags |= Notification.FLAG_SHOW_LIGHTS;
		try {
			not.notify(1, notification);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
