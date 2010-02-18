package pro.android;

import java.io.File;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaRecorder.AudioSource;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings.System;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

public class ThoundsActivity extends Activity {
	private ProgressBar progressBar1;
	private ProgressBar progressBar2;
	Player player1;
	Player player2;
	
	public static final int DEFAULT_SAMPLE_RATE = 8000;
	private static final int DEFAULT_BUFFER_SIZE = 4096;
	private static final int CALLBACK_PERIOD = 4000;
	private static final int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;

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
				Intent logIntent = new Intent(v.getContext(), LogIn.class);
				startActivity(logIntent);
			}
		});

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


		Button btnRec = (Button)this.findViewById(R.id.btnRec);
		btnRec.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v) {
				try {
					
					if(isRecording){
						Log.d(this.getClass().getSimpleName(), "STOP REC");
						stopRecording();
						isRecording= false;
					}
					else{
						Log.d(this.getClass().getSimpleName(), "REC");
						beginRecording();
						
					}

				} catch (Exception e) { e.printStackTrace();
				}
			}
		});


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
	}

	//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$

	private void beginRecording() throws Exception {
		levelLine = (ProgressBar)this.findViewById(R.id.ProgressBar03);

		isRecording = true;
		 am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

		int actualBufferSize = 4096*8;
		int capacity = 0;

		int bufferSize =      AudioTrack.getMinBufferSize(DEFAULT_SAMPLE_RATE,
				AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_PCM_16BIT);

				atrack = new AudioTrack( AudioManager.STREAM_MUSIC,
				DEFAULT_SAMPLE_RATE,
				AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_PCM_16BIT,
				actualBufferSize,
				AudioTrack.MODE_STREAM);

		capacity = AudioRecord.getMinBufferSize(DEFAULT_SAMPLE_RATE,
				AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_PCM_16BIT);

		  buffer = new byte[actualBufferSize];

				arec = new AudioRecord(MediaRecorder.AudioSource.MIC,
				DEFAULT_SAMPLE_RATE,
				AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_PCM_16BIT,
				actualBufferSize);

		am.setSpeakerphoneOn(true);
		am.setMicrophoneMute(false);

		Log.d("SPEAKERPHONE", "Is speakerphoneon? : " +
				am.isSpeakerphoneOn());

		atrack.setPlaybackRate(DEFAULT_SAMPLE_RATE);
		arec.startRecording();
		//atrack.play();
		if (am.isSpeakerphoneOn()) {

			new Thread(new Runnable() {
				public void run() {
					while(isRecording)
					{
						int readSize = arec.read(buffer, 0, 320);
						Log.v("Number of bytes read is ", " " + readSize);
						
						retVal = atrack.write(buffer, 0, readSize);
						volume = buffer[319];
					Log.v("volume is ", "  "+ volume);
						
						
						handler.post(new Runnable() {
							public void run() {
								levelLine.setProgress(volume);
							}
						});

					}
				}


			}).start();
		}

		//arec.stop();
		//atrack.stop();
	} 


	private void stopRecording() throws Exception {
		
			arec.stop();
			arec.release();
			atrack.stop();
			atrack.release();
	}

	//=======MENU==================================

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//call the base class to include system menus
		super.onCreateOptionsMenu(menu);
		menu.add(0,1,0,"item1");
		menu.add(0,2,1,"item2");
		menu.add(0,3,2,"item3");
		return true;
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
