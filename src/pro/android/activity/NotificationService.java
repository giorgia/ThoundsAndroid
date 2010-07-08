package pro.android.activity;

import org.json.JSONException;
import org.thounds.thoundsapi.IllegalThoundsObjectException;


import org.thounds.thoundsapi.NotificationsWrapper;
import org.thounds.thoundsapi.RequestWrapper;
import org.thounds.thoundsapi.ThoundsConnectionException;
import org.thounds.thoundsapi.UserWrapper;

import pro.android.utils.ImageFromUrl;

import android.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.ImageView;


public class NotificationService extends Service{

	private NotificationManager notificationMgr;
	private static final int NOTIFICATION_ID = 1;
	private boolean runnable=true;
	@Override
	public void onCreate() {
		super.onCreate();
		notificationMgr =(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		Log.e("NOtification", "creato il serviio");
		// displayNotificationMessage("starting Background Service");
		Thread thr = new Thread(null, new ServiceWorker(), "NotificationService");
		thr.start();
	}



	class ServiceWorker implements Runnable
	{
		public void run() {
			// do background processing here...
			// stop the service when done...
			// BackgroundService.this.stopSelf();

			while(runnable)
			{
				try {
					Log.e("NOtification", "dentro il metodo run");

					//richiesta notifica

					NotificationsWrapper nw= RequestWrapper.loadNotifications();
					//vedere richieste amicizia
					int bandRequest=-1;
					
						bandRequest = nw.getBandRequestListLength();
					

					if(bandRequest >0)
					{
						//recupero
						int newusers=-1;
						
							newusers = nw.getBandRequestListLength();
						
						if(newusers>0)
						{
							displayUserRequest(newusers);

						}
						/*for(int i=0;i< ur.length;i++)
{
Log.e("NOTIFICAAAAAAAAAAAAAa", ur[i].getName());//.getMail());
Log.e("NOtification", ur[i].getName());
//displayUserRequest(ur[i].getName());
displayUserRequest(ur[i]);
//cover � una image text
//cover.setImageDrawable(new ImageFromUrl(this,jsTracks.getJSONObject(0).getString("cover"), ""+jsTracks.getJSONObject(0).getInt("id")).getDrawable());
//ImageFromUrl urlimage=new ImageFromUrl(ctx, url, saveFilename)
ur[i].getSiteUrl();
}*/
						//RequestWrapper.acceptFriendship(friendshipId);
						//RequestWrapper.refuseFriendship(friendshipId)

						Log.e("NOTIFICAAAAAAAAAAAAAa", "FINE");
					}
        int newThounds=-1;
	
			newThounds = nw.getNewThoundsListLength();
		
					if(newThounds>0)
					{
						displayNewThounds(newThounds);
					}


					Thread.sleep(10000);
					// RequestWrapper.getNotifications();
					// displayNotificationMessage("ciao mondo");
				}


				catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ThoundsConnectionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalThoundsObjectException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}


			}
		}
	}
	@Override
	public void onDestroy()
	{
		//displayNotificationMessage("stopping Background Service");
		super.onDestroy();
	}
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
	}
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	



	private void displayUserRequest (int numRequest)//(String message)
	{
		// ImageFromUrl ifu=new ImageFromUrl(this,uw.getSiteUrl(),uw.getName());
		//android.R.drawable.
		try
		{
			//Notification notification = new Notification(R.drawable.ic_dialog_alert, uw.getName(),System.currentTimeMillis());
			Notification notification = new Notification(R.drawable.ic_media_play, "Richiesta di amicizia",System.currentTimeMillis());
			Context context = getApplicationContext();
			CharSequence contentTitle = "Nuove Richiesta di amicizia";
			CharSequence contentText = "Hai "+String.valueOf(numRequest)+" nuove richieste di amicizia";
			notification.defaults |= Notification.DEFAULT_SOUND; //Suona
			notification.defaults |= Notification.DEFAULT_LIGHTS; //LED
			notification.defaults |= Notification.DEFAULT_VIBRATE; //Vibra
			ComponentName comp = new ComponentName(context.getPackageName(),
					getClass().getName());


			Intent contentIntent = new Intent().setComponent(comp);
			// PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,contentIntent, Intent.FLAG_ACTIVITY_NEW_TASK);
			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,new Intent(this, NotificationsActivity.class), Intent.FLAG_ACTIVITY_NEW_TASK);

			// notification.
			//PendingIntent contentIntent = PendingIntent.getActivity(this, 0,new Intent(this, LoginActivity.class), 0);
			Log.d("Invio notification","Vado su profile");
			notification.setLatestEventInfo(this, "Hai "+String.valueOf(numRequest)+" nuove richieste di amicizia","Scopri chi vuole diventare tuo amico", pendingIntent);//contentIntent);
			notificationMgr.notify(0, notification);
		}
		catch(Exception e)
		{
			Log.e("Eccezione notification", e.toString());

		}

	}

	private void displayNewThounds (int numRequest)//(String message)
	{
		// ImageFromUrl ifu=new ImageFromUrl(this,uw.getSiteUrl(),uw.getName());
		//android.R.drawable.
		try
		{
			//Notification notification = new Notification(R.drawable.ic_dialog_alert, uw.getName(),System.currentTimeMillis());
			Notification notification = new Notification(R.drawable.ic_media_play, "Aggiunto un nuovo thounds",System.currentTimeMillis());
			Context context = getApplicationContext();
			CharSequence contentTitle = "Nuove Richiesta di amicizia";
			CharSequence contentText = "Hai "+String.valueOf(numRequest)+" notifiche di aggiunta thounds";
			notification.defaults |= Notification.DEFAULT_SOUND; //Suona
			notification.defaults |= Notification.DEFAULT_LIGHTS; //LED
			notification.defaults |= Notification.DEFAULT_VIBRATE; //Vibra
			ComponentName comp = new ComponentName(context.getPackageName(),
					getClass().getName());


			Intent contentIntent = new Intent().setComponent(comp);
			// PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,contentIntent, Intent.FLAG_ACTIVITY_NEW_TASK);
			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,new Intent(this, NewThoundsNotificationActivity.class), Intent.FLAG_ACTIVITY_NEW_TASK);

			// notification.
			//PendingIntent contentIntent = PendingIntent.getActivity(this, 0,new Intent(this, LoginActivity.class), 0);
			Log.d("Invio notification","Vado su profile");
			notification.setLatestEventInfo(this, "Hai "+String.valueOf(numRequest)+" nuove thounds","Ascolta la traccia ", pendingIntent);//contentIntent);
			notificationMgr.notify(0, notification);
		}
		catch(Exception e)
		{
			Log.e("Eccezione notification", e.toString());

		}

	}


	/*private void displayNotificationMessage(String message)
	{
		Log.e("NOtification", "su send message entra");
		Notification notification = new Notification(R.drawable.ic_dialog_alert, message,System.currentTimeMillis());
		Context context = getApplicationContext();
		CharSequence contentTitle = "Titolo della mia notifica";
		CharSequence contentText = "Testo della mia notifica";
		notification.defaults |= Notification.DEFAULT_SOUND; //Suona
		notification.defaults |= Notification.DEFAULT_LIGHTS; //LED
		notification.defaults |= Notification.DEFAULT_VIBRATE; //Vibra
		ComponentName comp = new ComponentName(context.getPackageName(),
				getClass().getName());

		Intent contentIntent = new Intent().setComponent(comp);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,contentIntent, Intent.FLAG_ACTIVITY_NEW_TASK);

		//PendingIntent contentIntent = PendingIntent.getActivity(this, 0,new Intent(this, LoginActivity.class), 0);
		notification.setLatestEventInfo(this, "Background Service",message, pendingIntent);//contentIntent);
		notificationMgr.notify(0, notification);
	}*/

	public void stop()
	{
		runnable=false;
	}


}

