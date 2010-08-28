package pro.android.activity;

import java.util.Vector;

import org.json.JSONException;
import org.thounds.thoundsapi.IllegalThoundsObjectException;


import org.thounds.thoundsapi.BandWrapper;
import org.thounds.thoundsapi.NotificationPair;
import org.thounds.thoundsapi.NotificationsWrapper;
import org.thounds.thoundsapi.RequestWrapper;
import org.thounds.thoundsapi.ThoundWrapper;
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
	static  private boolean runnable=true;
	private Vector<Integer> idUserNotification;
	private Vector<Integer> idThoundsNotification;
	private int num=1;
	private NotificationsWrapper nw;
	@Override
	public void onCreate() {
		super.onCreate();

		idUserNotification=new Vector<Integer>();
		idThoundsNotification=new Vector<Integer>();
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
				boolean trov=false;
				try {
					Log.e("NOtification", "dentro il metodo run");

					//richiesta notifica

					nw= RequestWrapper.loadNotifications();


					//vedere richieste amicizia
					int bandRequest=-1;

					bandRequest = nw.getBandRequestListLength();


					if(bandRequest >0)
					{
						//recupero
						int newusers=-1;

						//newusers = nw.getBandRequestListLength();

						newusers= isInVectorUser();
						Log.e("Notification","Il numero di utenti e"+newusers);
						if(newusers>0)
						{


							displayUserRequest(newusers);

							NotificationPair<UserWrapper>[]	userWrapper= nw.getBandRequestList();
							trov=true;
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

					newThounds = isInVectorThounds();
					Log.e("Notification","Il numero di thounds e"+newThounds);
					if(newThounds>0)
					{
						displayNewThounds(newThounds);
						trov=true;
					}

					if(trov)
						AddVector();

					Thread.sleep(500000);
					//Thread.sleep(60000);
					// RequestWrapper.getNotifications();
					// displayNotificationMessage("ciao mondo");
				}


				catch (InterruptedException e) {
					// TODO Auto-generated catch block
					//	e.printStackTrace();

				} catch (ThoundsConnectionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalThoundsObjectException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					//showDialog(DIALOG_ILLEGAL_THOUNDS_OBJECT);
					stop();
				}


			}
		}
	}

	static private boolean isInVector(Vector<Integer> v,int element)
	{
		for(int i=0;i<v.size();i++)
		{
			Log.e("Notification thounds",String.valueOf(v.get(i).intValue())+ " element:"+element);
			if(v.get(i).intValue()==element)
				return true;


		}

		return false;

	}


	private int isInVectorUser()
	{

		int bandRequest = nw.getBandRequestListLength();
		int equalsid=0;
		try
		{
			if(bandRequest >0)
			{
				//recupero
				int newusers=-1;

				newusers = nw.getBandRequestListLength();

				if(newusers>0)
				{
					NotificationPair<UserWrapper>[]	userWrapper= nw.getBandRequestList();
					//
					for(int i=0;i< userWrapper.length;i++)
					{
						/*for(int i=0;i< userWrapper.length;i++)
						   {
							 int id=	userWrapper[i].getNotificationObject().getId();
							 int userid=idUserNotification.get(j).intValue();
						     if(id== userid)
						     {
						       	 trov=true;
						     }
					  	     }
						 */

						if( isInVector(idUserNotification,userWrapper[i].getNotificationObject().getId() ) ==false)
						{
							equalsid++;

						}
					}
				}
			}
			Log.e("Notification","li elementi uguali sono:"+String.valueOf(equalsid)+" gli elementi dell'array sono:"+idUserNotification.size() +" e la differenza è:"+ String.valueOf(equalsid));
			//return equalsid-idUserNotification.size();
			return equalsid;
		}
		catch(Exception e)
		{

		}

		return 0;
	}


	private int isInVectorThounds()
	{

		int equalsid=0;

		try
		{
			int newThounds = nw.getNewThoundsListLength();

			if(newThounds>0)
			{
				ThoundWrapper [] thounds=nw.getNewThoundsList();
				for(int i=0;i< thounds.length;i++)
				{
					/*for(int j=0;j<idThoundsNotification.size();j++)
					{
					   int idThounds =thounds[i].getId();
					   int idPresentThounds= c.get(j).intValue();
					   if(idThounds==idPresentThounds)
					   {
						   equalsid++;

					   }
					}*/

					if(isInVector(idThoundsNotification, thounds[i].getId())==false)
					{
						Log.e("Notification thounds","id"+String.valueOf(thounds[i].getId()));
						equalsid++;
						Log.e("Notification thounds","id"+String.valueOf(equalsid));

					}
				}

			}
			Log.e("Notification","li elementi uguali sono:"+String.valueOf(equalsid)+ " e la differenza è:"+ String.valueOf(equalsid));
			//return equalsid-idThoundsNotification.size();
			return equalsid;
		}
		catch(Exception e)
		{

		}

		return 0;
	}



	private void AddVector()
	{
		int bandRequest = nw.getBandRequestListLength();

		try
		{
			if(bandRequest >0)
			{
				//recupero
				int newusers=-1;

				newusers = nw.getBandRequestListLength();

				if(newusers>0)
				{
					NotificationPair<UserWrapper>[]	userWrapper= nw.getBandRequestList();
					for(int i=0;i< userWrapper.length;i++)
					{
						idUserNotification.add(userWrapper[i].getNotificationObject().getId());
						Log.e("Notification","Aggiunto al vettore utente id"+String.valueOf(userWrapper[i].getNotificationObject().getId()));
					}
				}
			}

			int newThounds = nw.getNewThoundsListLength();

			if(newThounds>0)
			{
				ThoundWrapper [] thounds=	nw.getNewThoundsList()	;
				for(int i=0;i< thounds.length;i++)
				{
					idThoundsNotification.add(thounds[i].getId());
					Log.e("Notification","Aggiunto al vettore id"+String.valueOf(thounds[i].getId()));
				}
			}


		}
		catch(Exception e )
		{

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
			PendingIntent pendingIntent = PendingIntent.getActivity(context, num ,new Intent(this, NotificationsActivity.class), Intent.FLAG_ACTIVITY_NEW_TASK);

			Log.d("Invio notification","Vado su profile");
			notification.setLatestEventInfo(this, "Hai "+String.valueOf(numRequest)+" nuove richieste di amicizia","Scopri chi vuole diventare tuo amico", pendingIntent);
			notificationMgr.notify(num, notification);
			num++;
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
			notificationMgr.notify(num, notification);
			num++;
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

	static	public void stop()
	{
		runnable=false;
	}


}

