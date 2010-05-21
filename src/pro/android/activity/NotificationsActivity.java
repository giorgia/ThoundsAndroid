package pro.android.activity;

import pro.android.R;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;



	public class NotificationsActivity extends CommonActivity {// implements OnClickListener {
	       /* private static final int NOTIFICATION_ID = 1;
	        NotificationManager mNotificationManager;
	        @Override
	        public void onCreate(Bundle savedInstanceState) {
	                super.onCreate(savedInstanceState);
	                setContentView(R.layout.main);

	                // Inizializzo i click listeners per tutti i pulsanti
	                View btnSendNotify = findViewById(R.id.btnSendNotify);
	                btnSendNotify.setOnClickListener(this);
	                View btnEraseNotify = findViewById(R.id.btnEraseNotify);
	                btnEraseNotify.setOnClickListener(this);
	        }
	        @Override
	        public void onClick(View v) {
	                switch (v.getId()) {
	                case R.id.btnSendNotify:
	                        String ns = Context.NOTIFICATION_SERVICE;
	                        mNotificationManager = (NotificationManager) getSystemService(ns);
	                       
	                        int icon = android.R.drawable.stat_notify_chat;
	                        CharSequence tickerText = "Questo è il tickerText";
	                        long when = System.currentTimeMillis();
	                        Notification notification = new Notification(icon, tickerText, when);
	                       
	                        Context context = getApplicationContext();implements OnClickListener
	                        CharSequence contentTitle = "Titolo della mia notifica";
	                        CharSequence contentText = "Testo della mia notifica";
	                        Intent notificationIntent = new Intent(this, TutorialNotification.class);
	                        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
	                        notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
	                       
	                        notification.defaults |= Notification.DEFAULT_SOUND;    //Suona
	                        notification.defaults |= Notification.DEFAULT_LIGHTS;   //LED
	                        notification.defaults |= Notification.DEFAULT_VIBRATE;  //Vibra


	                        mNotificationManager.notify(NOTIFICATION_ID, notification);

	                        break;
	                case R.id.btnEraseNotify:
	                        if(mNotificationManager != null){
	                                mNotificationManager.cancel(NOTIFICATION_ID);
	                        }
	                        break;
	                }
	        }
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notifications);
	}
*/
}
