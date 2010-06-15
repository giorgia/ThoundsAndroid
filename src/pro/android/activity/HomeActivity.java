package pro.android.activity;

import org.json.JSONException;
import org.thounds.thoundsapi.IllegalThoundsObjectException;
import org.thounds.thoundsapi.NotificationsWrapper;
import org.thounds.thoundsapi.RequestWrapper;
import org.thounds.thoundsapi.ThoundsConnectionException;
import org.thounds.thoundsapi.UserWrapper;

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
		/*try {
			  Log.e("NOtification", "dentro il metodo run");
			//Thread.sleep(10000);
		    //richiesta notifica
			
		NotificationsWrapper nw=	 RequestWrapper.loadNotifications();
	       //vedere richieste amicizia
		 int count=  nw.getBandRequestListLength();
		 if(count >0)
		 {
			 //recupero
			UserWrapper [] ur=  nw.getBandRequestList();
		for(int i=0;i< ur.length;i++)
			{
				Log.e("NOTIFICAAAAAAAAAAAAAa", "Ciao "+ur[i].getName());
				Log.e("NOtification", "prova");
				
			}
			//RequestWrapper.acceptFriendship(friendshipId);
			//RequestWrapper.refuseFriendship(friendshipId)
	
   Log.e("NOTIFICAAAAAAAAAAAAAa", "FINE");
		 }
	//		RequestWrapper.getNotifications();
		//	displayNotificationMessage("ciao mondo");
	//	} catch (InterruptedException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
		}
		catch (ThoundsConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalThoundsObjectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
	}
	//Implementare il metodo stop
	public void StopNotification()
	{
		Log.d("notification", "stop service su Thounds ACtivity");
		//startService(new Intent(HomeActivity.this, NotificationService.class));

		
	}

}
