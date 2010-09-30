package pro.android.activity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

import org.thounds.thoundsapi.IllegalThoundsObjectException;
import org.thounds.thoundsapi.Thounds;
import org.thounds.thoundsapi.ThoundWrapper;
import org.thounds.thoundsapi.ThoundsConnectionException;
import org.thounds.thoundsapi.ThoundsNotAuthenticatedexception;

import pro.android.R;
import pro.android.utils.Player;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class RecordActivity extends CommonActivity {

	public static String PATH= Environment.getExternalStorageDirectory().getAbsolutePath() +"/thounds/test.3gp";
	public static int METRO_ON = 1;
	public static int METRO_OFF = 2;
	public static int METRO_MUTE = 3;
	private MediaRecorder recorder;
	private MediaPlayer playerRec;
	private MediaPlayer playerMetr;
	private Player playerTh;
	private int thoundId = -1;
	private boolean isThoundDownloaded = false;
	private boolean isDialogShow = false;
	private ThoundWrapper track;
	private ImageButton btnRec;
	private RelativeLayout lRecorder;
	private RelativeLayout lSeek;
	private RelativeLayout lMetronome;
	private LinearLayout lMedia; 
	private LinearLayout lRec;
	private TextView countdown;
	private TextView timer;
	private TextView txtBpm;
	private TextView txtTime60;
	private TextView txtTime0;
	private String time;
	private SeekBar seekBar;
	private boolean isRecording = false;
	private int mestronomeState = METRO_OFF;
	private Thread th;
	private long mStartTime = 0L;
	private int bpm = 90;
	private long currentTime = 0L;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.record);
		currentActivity = R.id.record;
		
		//==================================================================

		lRecorder = (RelativeLayout) findViewById(R.id.lRecorder);
		countdown = (TextView) findViewById(R.id.txt_countdown);
		lSeek = (RelativeLayout) findViewById(R.id.SeekLayout);
		lMetronome = (RelativeLayout) findViewById(R.id.MetronomeLayout);
		lMedia = (LinearLayout) findViewById(R.id.MediaLayout);
		lRec = (LinearLayout) findViewById(R.id.RecLayout);
		seekBar = (SeekBar) findViewById(R.id.RecSeekBar);
		timer = (TextView) findViewById(R.id.txt_timer);
		txtBpm = (TextView) findViewById(R.id.txtBpm);
		txtTime60 = (TextView) findViewById(R.id.txt_time60);
		txtTime0 = (TextView) findViewById(R.id.txt_time0);

		thoundId = getIntent().getExtras()!=null?getIntent().getExtras().getInt("thoundId"):-1;
		if(thoundId != -1){
			try {
				track = Thounds.loadThounds(thoundId);
				bpm = track.getBmp();
				lRecorder.setVisibility(View.VISIBLE);
			} catch (ThoundsConnectionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalThoundsObjectException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ThoundsNotAuthenticatedexception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		//========================= Setup Recorder ======================================
		try {
			File f = new File(PATH).getParentFile();
			if(!f.exists() && !f.mkdirs())
				throw new IOException("Path to file could not be created");
			recorder = new MediaRecorder();
			recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			recorder.setOutputFile(PATH);

			recorder.prepare();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//========================== Sutup Metronome =======================================

		final String[] items = new String[] {"No Metronome", "Metronome Mute", "Metronome"};
		Spinner spinner = (Spinner) findViewById(R.id.spn_metronome);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, items);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener(){

			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				if(items[arg2].equals("No Metronome")){
					mestronomeState = METRO_OFF;
					bpm = 0;
					txtBpm.setText(bpm+"  BPM");
					stopMetronome();
				}else if(items[arg2].equals("Metronome Mute")){
					mestronomeState = METRO_MUTE;
					stopMetronome();		
				}else if(items[arg2].equals("Metronome")){
					mestronomeState = METRO_ON;
					bpm = 90;
					changeBpm(bpm);					
				}
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}});


		ImageButton plus = (ImageButton) findViewById(R.id.btn_plus);
		plus.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				if(bpm < 150 && mestronomeState != METRO_OFF){
					if(bpm == 0 )
						bpm = 80;
					stopMetronome();
					bpm+=10;
					changeBpm(bpm);
				}
			}
		});
		ImageButton minus = (ImageButton) findViewById(R.id.btn_minus);
		minus.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				if(bpm > 80 && mestronomeState != METRO_OFF){
					if(bpm == 0)
						bpm = 150;
					stopMetronome();
					bpm-=10;
					changeBpm(bpm);
				}
			}
		});

//===================== RECORD =========================================
	    btnRec = (ImageButton) this.findViewById(R.id.btnRec);
		btnRec.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				if(!isRecording){
					btnRec.setBackgroundResource(R.drawable.btn_record_stop);
					if(isThoundDownloaded || thoundId ==-1){
						isRecording = true;
						if(bpm != 0){
							stopMetronome();
							countDown(bpm);
						}else{
							startRecord();
						}
					} else {
						isDialogShow = true;
						showDialog(DIALOG_RETRIEVING_TRACKS);
					} 
				}else{
					isRecording = false;
					lRec.setVisibility(View.INVISIBLE);
					lMedia.setVisibility(View.VISIBLE);
					
					stopRecord();
					stopMetronome();
					setMediaPlayer();
				}
			}
		});

		if(thoundId!=-1){
			lMetronome.setVisibility(View.INVISIBLE);
			runOnUiThread(
					new Runnable(){

						public void run() {
							try {
								playerTh = new Player(track.getMixUrl());
								runOnUiThread(playerTh.getBufferedAudio());
								//playerTh.bufferedAudio();
								playerTh.getDefaulMediaPlayer().setOnBufferingUpdateListener(new OnBufferingUpdateListener(){
									public void onBufferingUpdate(MediaPlayer mp,
											int percent) {
										if(percent==100){
											isThoundDownloaded = true;
											if(isDialogShow){
												dismissDialog(DIALOG_RETRIEVING_TRACKS);
												btnRec.performClick();
											}
										}
									}
								});

							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					});
		}

		//=================================MEDIA CONTROLLER===========================================================
		//PLAY - PAUSE
		final ImageButton play = (ImageButton) findViewById(R.id.btnPlay);
		play.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v) {
				if(isThoundDownloaded)
					playerTh.start();
				playerRec.start();
				seekBar.setProgress(0);
				if(isThoundDownloaded) {
					seekBar.setMax((int) (track.getMixDuration()));
					txtTime60.setText(track.getMixDuration());
				}
				else {
					seekBar.setMax((int) (currentTime/1000));
					txtTime60.setText(time.substring(0, 5));
				}
				th = new Thread(null, updateTime, "updateProgress");
				th.start();
			}
		});
		//REC AGAIN
		ImageButton recAgain = (ImageButton) findViewById(R.id.btnRecAgain);
		recAgain.setOnClickListener(new OnClickListener()

		{
			public void onClick(View v) {
				stopAllPlayer();
				isRecording = false;
				btnRec.setPressed(false);
				lMedia.setVisibility(View.INVISIBLE);
				lRec.setVisibility(View.VISIBLE);
				seekBar.setProgress(0);
			}
		});
		//SAVE
		ImageButton save = (ImageButton) findViewById(R.id.btnSave);
		save.setOnClickListener(new OnClickListener()

		{
			public void onClick(View v) {
				stopAllPlayer();
				nextIntent = new Intent(v.getContext(), SaveActivity.class);
				nextIntent.putExtra("duration", currentTime);
				Log.d("Durationd", ""+playerRec.getDuration());
				startActivity(nextIntent);
				finish();				}
		});
		//CANCEL
		ImageButton delete = (ImageButton) findViewById(R.id.btnDelete);
		delete.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v) {
				stopAllPlayer();
				nextIntent = new Intent(v.getContext(), RecordActivity.class);
				startActivity(nextIntent);
				finish();
			}
		});
	}

	//=========================================================================================

	private void changeBpm(int bpm) {
		switch (bpm) {
		case 80:
			playerMetr = new MediaPlayer().create(getBaseContext(), R.raw.bpm_80);
			break;
		case 90:
			playerMetr = new MediaPlayer().create(getBaseContext(), R.raw.bpm_90);
			break;
		case 100:
			playerMetr = new MediaPlayer().create(getBaseContext(), R.raw.bpm_100);
			break;
		case 110:
			playerMetr = new MediaPlayer().create(getBaseContext(), R.raw.bpm_110);
			break;
		case 120:
			playerMetr = new MediaPlayer().create(getBaseContext(), R.raw.bpm_120);
			break;
		case 130:
			playerMetr = new MediaPlayer().create(getBaseContext(), R.raw.bpm_130);
			break;
		case 140:
			playerMetr = new MediaPlayer().create(getBaseContext(), R.raw.bpm_140);
			break;
		case 150:
			playerMetr = new MediaPlayer().create(getBaseContext(), R.raw.bpm_150);
			break;
		default:
			playerMetr = new MediaPlayer().create(getBaseContext(), R.raw.bpm_90);
		break;
		}
		txtBpm.setText(bpm+" BPM");
		if(mestronomeState == METRO_ON){
			playerMetr.setLooping(true);
			playerMetr.start();
		}
	}


	public void startRecord(){
		lMetronome.setVisibility(View.INVISIBLE);
		lSeek.setVisibility(View.VISIBLE);

		if(isThoundDownloaded)
			playerTh.start();
		if (mStartTime == 0L) {
			mStartTime = System.currentTimeMillis();
			th = new Thread(null, updateTime, "updateTime");
			th.start();
		}
		recorder.start();   // Recording is now started
	}

	public void stopRecord(){
		if(playerTh!=null)
			playerTh.pause();
		recorder.stop();
		recorder.reset();   // You can reuse the object by going back to setAudioSource() step
		recorder.release(); // Now the object cannot be reused
	}

	private void stopMetronome(){
		if(playerMetr!=null){
			playerMetr.pause();
		}
	}

	private void startMetronome(){
		if(playerMetr!=null){
			playerMetr.start();
		}
	}

	private void stopAllPlayer(){
		if(playerTh!=null){
			playerTh.pause();
		}
		if(playerRec!=null){
			playerRec.pause();
		}
		stopMetronome();
	}
	
	private void setMediaPlayer(){
		playerRec = new MediaPlayer();
		try {
			playerRec.setDataSource(PATH);
			playerRec.prepare();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//===================== Count Down =====================================
	private void countDown(int bpm){
		lRecorder.setVisibility(View.INVISIBLE);
		lSeek.setVisibility(View.VISIBLE);

		if(mestronomeState == METRO_ON)
			startMetronome();
		new MyCountDownTimer(5*(60000/bpm), 60000/bpm).start();
	}

	class MyCountDownTimer extends CountDownTimer{

		long myCountDownInterval;

		public MyCountDownTimer(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
			myCountDownInterval = countDownInterval;
			// TODO Auto-generated constructor stub
		}

		public void onTick(long millisUntilFinished) {
			countdown.setVisibility(View.VISIBLE);
			countdown.setText(""+(millisUntilFinished/myCountDownInterval));
		}

		public void onFinish() {
			countdown.setVisibility(View.INVISIBLE);
			lRecorder.setVisibility(View.VISIBLE);
			startRecord();
		}
	};

	private Runnable updateTime = new Runnable() {

		public void run() {

			while(true) {

				if(isRecording)
					runOnUiThread(mUpdateTimeText);
				if(playerRec != null && playerRec.isPlaying())
					runOnUiThread(mUpdatePlayProgress);
				try {
					th.sleep(150);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	};

	private Runnable mUpdateTimeText = new Runnable() {
		public void run() {
			SimpleDateFormat formatter = new SimpleDateFormat("mm:ss:SSS");
			currentTime = System.currentTimeMillis() - mStartTime;
			time = formatter.format(currentTime);
			seekBar.setProgress((int) (currentTime/1000));
			timer.setText(time);

			// end recording after 60 seconds
			if(currentTime/1000 > 60){
				btnRec.performClick();
				timer.setText("01:00:000");
			}
		}
	};
	private Runnable mUpdatePlayProgress = new Runnable() {
		public void run() {
			SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");
			currentTime = System.currentTimeMillis() - mStartTime;
			time = formatter.format(currentTime);
			seekBar.setProgress(playerRec.getCurrentPosition());
			txtTime0.setText(time);
		}
	};

}