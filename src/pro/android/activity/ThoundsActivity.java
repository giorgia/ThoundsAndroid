package pro.android.activity;

import java.io.File;

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

	private static final int DIALOG1_KEY = 0;
	private static final int DIALOG2_KEY = 1;

	String username;
	String password;
	/** Called when the activity is first created. */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		ImageView img = (ImageView) this.findViewById(R.id.ImageView01);
		img.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				//========= CHECK CONNECTION ===================
				ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

				wifiInfo = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
				mobileInfo = connectivity.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

				if(wifiInfo.getState().name().equals("DISCONNECTED") && mobileInfo.getState().name().equals("DISCONNECTED")){

					showDialog(DIALOG2_KEY);

				}else{

					showDialog(DIALOG1_KEY);

					//=========CHECK IS LOGGED===================
					// Restore preferences
					SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
					 username = settings.getString("silentUsername", username);
					 password = settings.getString("silentPassword", password);	
					
					if(isLogged ){	
						nextIntent = new Intent(v.getContext(), HomeActivity.class);
					}else if(username != null && password != null){
						login(username, password, "http://thounds.com/home");
						nextIntent = new Intent(v.getContext(), HomeActivity.class);
					}			
					else{
						nextIntent = new Intent(v.getContext(), LoginActivity.class);
					}
					// Waiting 2 sec for ProgressDialog displayed
					new Handler().postDelayed(new Runnable() {

						public void run() {
							dismissDialog(DIALOG1_KEY);		
						}
					}, 4000);


				}

			}
		});
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG1_KEY: {
			ProgressDialog mDialog1 = new ProgressDialog(this);
			mDialog1.setMessage("Loading. Please wait...");
			mDialog1.setIndeterminate(true);
			mDialog1.setCancelable(true);
			mDialog1.setOnDismissListener(new DialogInterface.OnDismissListener() {	
				public void onDismiss(DialogInterface dialog) {
					startActivity(nextIntent);
				}
			});
			return mDialog1;
		}
		case DIALOG2_KEY: {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Alert")
			.setMessage("No network connection!")
			.setCancelable(false)
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			});

			return builder.create();
		}
		}
		return null;
	}



	//=======NOTIFICHE==================================
	public void notification() {

		NotificationManager not = (NotificationManager)
		getSystemService(NOTIFICATION_SERVICE);
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
		} catch (Exception e) { e.printStackTrace();
		}

	}



}
