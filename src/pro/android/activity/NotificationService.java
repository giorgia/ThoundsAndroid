package pro.android.activity;

import java.util.Vector;

import org.thounds.thoundsapi.IllegalThoundsObjectException;
import org.thounds.thoundsapi.NotificationPair;
import org.thounds.thoundsapi.NotificationsWrapper;
import org.thounds.thoundsapi.ThoundWrapper;
import org.thounds.thoundsapi.Thounds;
import org.thounds.thoundsapi.ThoundsConnectionException;
import org.thounds.thoundsapi.ThoundsNotAuthenticatedexception;
import org.thounds.thoundsapi.UserWrapper;

import pro.android.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;


public class NotificationService extends Service {

	private NotificationManager notificationMgr;
	private Notification notification;
	private PendingIntent pendingIntent;

	static  private boolean runnable=true;
	private Vector<Integer> friendshipNotifications;
	private Vector<Integer> newLineNotifications;
	private int num=1;
	private NotificationsWrapper notificationWrapper;
	@Override
	public void onCreate() {
		super.onCreate();

		friendshipNotifications = new Vector<Integer>();
		newLineNotifications = new Vector<Integer>();
		notificationMgr = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		notification = new Notification();
		Context context = getApplicationContext();
		notification.defaults |= Notification.DEFAULT_SOUND; //Suona
		notification.defaults |= Notification.DEFAULT_LIGHTS; //LED
		notification.defaults |= Notification.DEFAULT_VIBRATE; //Vibra
		pendingIntent = PendingIntent.getActivity(context, num ,new Intent(this, NotificationsActivity.class), Intent.FLAG_ACTIVITY_NEW_TASK);

		Thread thr = new Thread(null, new ServiceWorker(), "NotificationService");
		thr.start();
	}


	class ServiceWorker implements Runnable
	{
		public void run() {
			while(runnable) {				
				boolean haveNewNotification=false;
				try {
					//richiesta notifica
					notificationWrapper = Thounds.loadNotifications();

					int newFriends = numberOfNewFriendshipNotification();
					if ( newFriends > 0) {
						showFriendshipNotification(newFriends);
						haveNewNotification=true;
					}

					int newLines = numberOfNewLineNotifications();
					if(newLines > 0) {
						showNewLineNotification(newLines);
						haveNewNotification=true;
					}

					if(haveNewNotification)
						addNewNotification();

					Thread.sleep(1800000);

				}catch (InterruptedException e) {
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
				} catch (ThoundsNotAuthenticatedexception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private int numberOfNewFriendshipNotification() {

		int bandRequest = notificationWrapper.getBandRequestListLength();
		int newRequestNumber = 0;
		try {
			if(bandRequest > 0) {
				NotificationPair<UserWrapper>[] userWrapper = notificationWrapper.getBandRequestList();

				for(int i = 0; i < userWrapper.length; i++) {

					if( friendshipNotifications.contains(userWrapper[i].getNotificationObject().getId()) == false) {
						newRequestNumber++;
					}
				}
			}
		} catch (IllegalThoundsObjectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return newRequestNumber;
	}


	private int numberOfNewLineNotifications() {

		int newThounds = notificationWrapper.getNewThoundsListLength();
		int newRequestNumber = 0;

		try {
			if(newThounds > 0) {
				ThoundWrapper[] thounds = notificationWrapper.getNewThoundsList();

				for(int i = 0; i < thounds.length; i++) {
					if( newLineNotifications.contains(thounds[i].getId()) == false) {
						newRequestNumber++;
					}
				}
			}
		} catch (IllegalThoundsObjectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return newRequestNumber;

	}



	private void addNewNotification() {

		NotificationPair<UserWrapper>[] userWrapper;
		ThoundWrapper [] thounds;
		try {
			userWrapper = notificationWrapper.getBandRequestList();

			for(int i=0;i< userWrapper.length;i++) {
				friendshipNotifications.add(userWrapper[i].getNotificationObject().getId());
			}


			thounds = notificationWrapper.getNewThoundsList()	;
			for(int i=0;i< thounds.length;i++)
			{
				newLineNotifications.add(thounds[i].getId());
			}
		} catch (IllegalThoundsObjectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
	}
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private void showFriendshipNotification (int numRequest) {

		notification.icon = R.drawable.tab_icons_contacts_selected;
		notification.tickerText = "New Friendship Request";
		notification.when = System.currentTimeMillis();
		notification.setLatestEventInfo(this, "You have "+numRequest+" new friendship request"+(numRequest>1?"s":""),"", pendingIntent);
		notificationMgr.notify(num, notification);
		num++;

	}

	private void showNewLineNotification (int numRequest) {

		notification.icon = R.drawable.tab_icons_library_selected;
		notification.tickerText = "New Thound Line";
		notification.when = System.currentTimeMillis();
		notification.setLatestEventInfo(this, "You have "+numRequest+" new line"+(numRequest>1?"s":""),"", pendingIntent);
		notificationMgr.notify(num, notification);
		num++;
	}

	static	public void stop() {
		runnable=false;
	}
}

