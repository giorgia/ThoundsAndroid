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
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
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

// Classe iniziale di prova che include Player - Recorder - Menu e Notifiche

public class ThoundsActivity extends CommonActivity {
	
	private ProgressBar progressBar1;
	private ProgressBar progressBar2;
	Player player1;
	Player player2;

	public static final int DEFAULT_SAMPLE_RATE = 8000;
	Recorder rec = new Recorder();
	ProgressBar levelLine;
	boolean isRecording = false;

	int level = 100;
	private final Handler handler = new Handler();
	int retVal = 0;
	int volume = 0;
	AudioRecord arec;
	AudioTrack atrack;
	AudioManager am ;
	byte[] buffer;
	/** Called when the activity is first created. */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);


		ImageView img = (ImageView) this.findViewById(R.id.ImageView01);
		img.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent logIntent = new Intent(v.getContext(), LoginActivity.class);
				startActivity(logIntent);
			}
		});

		//Bottone Play - test del play di due mp3 contemporaneamente
		progressBar1 = (ProgressBar)this.findViewById(R.id.ProgressBar01);
		progressBar2 = (ProgressBar)this.findViewById(R.id.ProgressBar02);

		Button btnPlay = (Button)this.findViewById(R.id.btnPlay);
		btnPlay.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v) {
				try { 
					player1 = new Player(progressBar1, "robots3humans0.mp3");

					player2 = new Player(progressBar2,"dethroned.mp3");

					player1.playAudio();
					player2.playAudio();
				} catch (Exception e) { e.printStackTrace();
				}
			}
		});
		//Bottone che mette in pausa il primo player
		Button btnPause1 = (Button)this.findViewById(R.id.btnPause1);
		btnPause1.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v) {
				try {
					if(player1.getMediaPlayer().isPlaying())
						player1.getMediaPlayer().pause();
					else
						player1.getMediaPlayer().start();
				} catch (Exception e) { e.printStackTrace();
				}
			}
		});
		//Bottone che mette in pausa il secondo player
		Button btnPause2 = (Button)this.findViewById(R.id.btnPause2);

		btnPause2.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v) {
				try {
					if(player2.getMediaPlayer().isPlaying())
						player2.getMediaPlayer().pause();
					else
						player2.getMediaPlayer().start();
				} catch (Exception e) { e.printStackTrace();
				}
			}
		});

		//Bottone Rec - utilizza la classe Recorder (scrittura su file da sistemare)
		Button btnRec = (Button)this.findViewById(R.id.btnRec);
		levelLine = (ProgressBar)this.findViewById(R.id.ProgressBar03);

		btnRec.setOnClickListener(new OnClickListener()

		{
			public void onClick(View v) {
				try {

					if(isRecording){
						Log.d(this.getClass().getSimpleName(), "STOP REC");
						rec.setRecording(false);
						isRecording= false;

					}
					else{

						Log.d(this.getClass().getSimpleName(), "REC");
						rec.setFileName(new File("/sdcard/thounds/test.pcm")); 
						rec.setLevelLine(levelLine);
						am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
						rec.setAudioManager(am);
						isRecording = true;
						new Thread(rec).start();

						//beginRecording();

					}

				} catch (Exception e) { e.printStackTrace();
				}
			}
		});

		ImageButton playRec = (ImageButton)findViewById(R.id.ImageButton01);
		playRec.setOnClickListener(new OnClickListener()

		{
			public void onClick(View v) {
				try {
					Player play = new Player("test.pcm");
					play.playAudio();
				} catch (Exception e) { e.printStackTrace();
				}
			}
		});	



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
