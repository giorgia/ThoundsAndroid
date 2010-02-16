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
	boolean running = false;
	private AudioRecord audioRecorder;
	private ProgressBar progressBar1;
	private ProgressBar progressBar2;
	Player player1;
	Player player2;
	private MediaRecorder recorder;
	public static final int DEFAULT_SAMPLE_RATE = 8000;
	private static final int DEFAULT_BUFFER_SIZE = 4096;
	private static final int CALLBACK_PERIOD = 4000;
	private static final int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;

	ProgressBar levelLine;
	int level = 100;
	private final Handler handler = new Handler();


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
					Log.d(this.getClass().getSimpleName(), "rec clicked");
					if(running){
						stopRecording();
						running= false;
					}
					else{
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

		//================ Registratore Interno =======================
		//			Intent intt = new Intent("android.provider.MediaStore.RECORD_SOUND");
		//			startActivityForResult(intt, 0);

		// //================ Registratore Media Recorder
		// ========================
		// killMediaRecorder();
		// File outFile = new File(AUDIO_PATH);
		// if(outFile.exists()) {
		// outFile.delete();
		// }
		// recorder = new MediaRecorder();
		// recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		// recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		// recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		// recorder.setOutputFile(AUDIO_PATH);
		// recorder.prepare();
		// recorder.start();
		// }
		// //
		// //@Override
		// //protected void onActivityResult(int requestCode, int resultCode,
		// Intent data) {
		// // switch (requestCode) { case 0:
		// //
		// // if (resultCode == RESULT_OK)
		// // { AUDIO_PATH = data.getData().toString();
		// // int i=0;
		// // }
		// // }
		android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
		running= true;
		new Thread(new Runnable() {
			public void run() {
				int bufferSize = AudioRecord.getMinBufferSize(
						11025,
						AudioFormat.CHANNEL_CONFIGURATION_MONO, 
						audioEncoding);
			
				AudioRecord audioRecorder = new AudioRecord(
						MediaRecorder.AudioSource.MIC, 
						11025, 
						AudioFormat.CHANNEL_CONFIGURATION_MONO, audioEncoding,
						bufferSize
						);
				Log.d(this.getClass().getSimpleName(), "loop start");
				while(running){
					
					//audioRecorder.setPositionNotificationPeriod(CALLBACK_PERIOD);
					
					audioRecorder.setRecordPositionUpdateListener(new
							
							AudioRecord.OnRecordPositionUpdateListener() {
						
						public void onMarkerReached(AudioRecord recorder) {
							Log.d(this.getClass().getSimpleName(), "onMarkerReached Called");
						}


						public void onPeriodicNotification(AudioRecord recorder) {
							Log.d(this.getClass().getSimpleName(), "onPeriodicNotification Called");
						}
					}); 
					
				}
			}


		}).start();
	}

	private void stopRecording() throws Exception {
		if (recorder != null) {
			recorder.stop();
		}
	}

	private void killMediaRecorder() {
		if (recorder != null) {
			recorder.release();
		}
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
