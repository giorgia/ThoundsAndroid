package pro.android.activity;

import pro.android.R;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class HomeActivity extends CommonActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		//Inizializza Notifiche
		StartNotification();
		Button logout = (Button) findViewById(R.id.Button01);
		logout.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				logout();
				StopNotification();
			}
		});
		
		
		
	}
	
	public void StartNotification()
	{
		Log.d("notification", "start service su Thounds ACtivity");
		startService(new Intent(HomeActivity.this, NotificationService.class));

		
	}
	//Implementare il metodo stop
	public void StopNotification()
	{
		Log.d("notification", "stop service su Thounds ACtivity");
		startService(new Intent(HomeActivity.this, NotificationService.class));

		
	}

}
