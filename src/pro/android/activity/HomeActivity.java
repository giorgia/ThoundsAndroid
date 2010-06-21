package pro.android.activity;

import org.thounds.thoundsapi.HomeWrapper;
import org.thounds.thoundsapi.IllegalThoundsObjectException;
import org.thounds.thoundsapi.RequestWrapper;
import org.thounds.thoundsapi.ThoundWrapper;
import org.thounds.thoundsapi.ThoundsConnectionException;

import java.io.IOException;
import java.net.SocketException;
import java.net.URISyntaxException;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import pro.android.R;
import pro.android.utils.ThoundsList;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;


public class HomeActivity extends CommonActivity{

	ThoundsList list;
	HomeWrapper home;
	ThoundWrapper thounds;

	JSONObject thound;
	JSONObject thounds_collection;
	JSONObject json;
	private boolean runningNotificationService=false; 


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		currentActivity = R.id.home;
	
		Button logout = (Button) findViewById(R.id.btnLogout);
		logout.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				logout();
			}
		});

		ImageButton reload = (ImageButton) findViewById(R.id.btnReload);
		reload.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

			}
		});


		
		list = new ThoundsList(this);

		Runnable run = new Runnable(){
			public void run() {
				retrievedData();     	
			}			
		};
		Thread thread =  new Thread(run, "retrievedData");
		thread.start();
		
		StartNotification();

	}
	
	//Start Notification Service
	public synchronized void StartNotification()
	{
		  Log.e("notification", "qui arriva e poi eccezione");
		if(runningNotificationService==false)
		{
	    Log.e("notification", "start service su Thounds ACtivity");
	    runningNotificationService=true;
		startService(new Intent(HomeActivity.this, NotificationService.class));
		
		}
	}

	private synchronized void retrievedData() {
		
			try {
				home = RequestWrapper.loadHome(1, 20);
				list.setThound(home.getThoundsCollection().getThoundsList());
			} catch (ThoundsConnectionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalThoundsObjectException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			

		

		runOnUiThread(list.getReturnRes());

	}
	public void onClickArrow(View v){
		list.onClickArrow(v);
	}
	public void onItemClick(View v){
		list.onItemClick(v);
	}

}

