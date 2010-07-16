package pro.android.activity;

import org.json.JSONObject;
import org.thounds.thoundsapi.HomeWrapper;
import org.thounds.thoundsapi.IllegalThoundsObjectException;
import org.thounds.thoundsapi.RequestWrapper;
import org.thounds.thoundsapi.ThoundWrapper;
import org.thounds.thoundsapi.ThoundsConnectionException;

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


	private boolean runningNotificationService=false; 

	Thread thread;
	Runnable run;

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
				 reload();
			}
		});

		
		 run = new Runnable(){

			public void run() {
				retrievedData();     	
			}			
		};

		
		StartNotification();
		reload();
		
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
	public void reload(){
		list = new ThoundsList(this);
		thread =  new Thread(run, "retrievedData");
		thread.start();
	}
	public void onClickArrow(View v){
		list.onClickArrow(v);
	}
	public void onItemClick(View v){
		list.onItemClick(v);
	}

	@Override
	public void onRestart(){
		super.onRestart();
		reload();
		Log.d("HOME","RESTART");
	}

	@Override
	public void onResume(){
		super.onResume();
		//reload();
		Log.d("HOME","RESUME");
	}
	@Override
	public void onStart(){
		super.onStart();
		//reload();
		Log.d("HOME","START");
	}
	@Override
	public void onStop(){
		super.onStop();
		//reload();
		Log.d("HOME","STOP");
	}
}

