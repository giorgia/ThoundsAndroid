package pro.android.activity;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import pro.android.R;
import pro.android.R.drawable;
import pro.android.R.id;
import pro.android.R.layout;
import pro.android.R.menu;
import pro.android.utils.Player;
import pro.android.utils.Recorder;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SlidingDrawer;

public class ThoundsActivity extends CommonActivity {

	NetworkInfo wifiInfo, mobileInfo;

	String username, password;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		ImageView img = (ImageView) this.findViewById(R.id.ImageView01);
		img.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				
				// ========= CHECK CONNECTION ===================
				ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

				wifiInfo = connectivity
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
				mobileInfo = connectivity
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

				//Aggiungere controllo  UNKNOWN
				
				if (!wifiInfo.isConnectedOrConnecting() && !mobileInfo.isConnectedOrConnecting()) {
					showDialog(DIALOG_ALERT_CONNECTION);				
				} else {
					showDialog(DIALOG_LOADING);
				}
				// =========CHECK IS LOGGED===================
				// Restore preferences
				SharedPreferences settings = getSharedPreferences(
						PREFS_NAME, 0);
				username = settings.getString("silentUsername", username);
				password = settings.getString("silentPassword", password);

				//if (isLogged) {
					//nextIntent = new Intent(v.getContext(), HomeActivity.class);

			//	} else
					if (username != null && password!= null && login(username, password, "http://thounds.com/profile")){

					nextIntent = new Intent(v.getContext(), HomeActivity.class);

				} else {
					nextIntent = new Intent(v.getContext(), LoginActivity.class);
				}
				
				// Waiting 2 sec for ProgressDialog displayed
				new Handler().postDelayed(new Runnable() {

					public void run() {
						dismissDialog(DIALOG_LOADING);
					}
				}, 4000);

			}

		});
		
	}



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
