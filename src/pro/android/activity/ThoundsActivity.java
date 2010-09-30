package pro.android.activity;

import org.thounds.thoundsapi.ThoundsConnectionException;

import pro.android.R;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Looper;
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

				if (!wifiInfo.isConnected() && !mobileInfo.isConnected()) {
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
							try{
								if (username != null && password!= null && login(username, password)){	
									nextIntent = new Intent(v.getContext(), HomeActivity.class);
								} else {
									nextIntent = new Intent(v.getContext(), LoginActivity.class);
								}
							} catch (ThoundsConnectionException e) {
								// TODO Auto-generated catch block
								connectionError = true;
								e.printStackTrace();
							}
							runOnUiThread(checkAutentication);
						}			
					};
					Thread thread =  new Thread(null, run, "LoginMain");
					thread.start();
					showDialog(DIALOG_LOADING);
				}

			}
		});
	}
	private Runnable checkAutentication = new Runnable() {
		public void run() {
			dismissDialog(DIALOG_LOADING);
			if(connectionError)
				showDialog(DIALOG_ALERT_CONNECTION);
			else
				startActivity(nextIntent);
		}

	};

}
