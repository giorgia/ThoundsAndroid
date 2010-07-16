package pro.android.activity;

import java.io.IOException;
import java.text.SimpleDateFormat;

import org.thounds.thoundsapi.IllegalThoundsObjectException;
import org.thounds.thoundsapi.RequestWrapper;
import org.thounds.thoundsapi.ThoundsConnectionException;

import pro.android.R;
import pro.android.utils.Player;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class RecordActivity extends CommonActivity {
	
	public static String PATH= Environment.getExternalStorageDirectory().getAbsolutePath() +"/thounds/test.3gp";
	private MediaRecorder recorder;
	private MediaPlayer playerRec;
	private MediaPlayer playerMetr;
	private Player playerTh;
	private int thoundId;
	private boolean isThoundDownloaded = false;
	private boolean isDialogShow = false;

	private RelativeLayout lTimer;
	private RelativeLayout lMetronome;
	private LinearLayout lMedia; 
	private LinearLayout lRec;
	private TextView countdown;
	private TextView timer;
	private ImageView led;
	private int ledOnOff=0;
	private boolean isRecording = false;
	private Thread th;
	private long mStartTime = 0L;
	private int bpm = 90;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.record);
		currentActivity = R.id.record;

		final String[] items = new String[] {"No Menotronome", "Metronome Mute", "Metronome"};
				//"80 bpm", "90 bpm", "100 bpm", "110 bpm", "120 bpm", "130 bpm", "140 bpm", "150 bpm"};
		Spinner spinner = (Spinner) findViewById(R.id.spn_metronome);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
		            android.R.layout.simple_spinner_item, items);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener(){

			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				if(items[arg2].equals("No Metronome")){
					if(playerMetr!=null){
						playerMetr.stop();
					}
				}else if(items[arg2].equals("Metronome")){
						bpm = 90;
						changeBpb(bpm);					
					}
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}});
		ImageButton plus = (ImageButton) findViewById(R.id.btn_plus);
		plus.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				if(bpm < 150){
					playerMetr.stop();
					bpm+=10;
					changeBpb(bpm);
				}
			}
});
		ImageButton minus = (ImageButton) findViewById(R.id.btn_minus);
		minus.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				if(bpm > 80){
					playerMetr.stop();
					bpm-=10;
					changeBpb(bpm);
				}
			}
});
		countdown = (TextView) findViewById(R.id.txt_countdown);
		lTimer = (RelativeLayout) findViewById(R.id.ProgressBarLayout);
		lMetronome = (RelativeLayout) findViewById(R.id.MetronomeLayout);
		lMedia = (LinearLayout) findViewById(R.id.MediaLayout);
		lRec = (LinearLayout) findViewById(R.id.RecLayout);

		timer = (TextView) findViewById(R.id.txt_timer);
		led = (ImageView) findViewById(R.id.img_led_rec);

		//RECORD
		final ImageButton btnRec = (ImageButton) this.findViewById(R.id.btnRec);
		btnRec.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				if(!isRecording){
					btnRec.setImageResource(R.drawable.btn_square_overlay_disabled);
					if(isThoundDownloaded){
						isRecording = true;
						countDown();
					} else {
						isDialogShow = true;
						showDialog(DIALOG_RETRIEVING_TRACKS);

					}

				}else{
					isRecording = false;
					lRec.setVisibility(View.INVISIBLE);
					lMedia.setVisibility(View.VISIBLE);
					stopRecord();
					setMediaPlayer();
				}


			}


		});

		thoundId = getIntent().getExtras()!=null?getIntent().getExtras().getInt("thoundId"):-1;
		if(thoundId!=-1){
			runOnUiThread(
					new Runnable(){

						public void run() {
							try {
								playerTh = new Player(RequestWrapper.loadThounds(thoundId).getMixUrl());
								playerTh.bufferedAudio();
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

								}


								);

							} catch (ThoundsConnectionException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IllegalThoundsObjectException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

					});

		}
		//PLAY - PAUSE
		final ImageButton play = (ImageButton) findViewById(R.id.btnPlay);
		play.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v) {
				playerTh.start();
				playerRec.start();

			}
		});



		ImageButton recAgain = (ImageButton) findViewById(R.id.btnRecAgain);
		recAgain.setOnClickListener(new OnClickListener()

		{
			public void onClick(View v) {
				if(playerTh!=null)
					playerTh.pause();
				nextIntent = new Intent(v.getContext(), RecordActivity.class);
				startActivity(nextIntent);
				finish();
			}
		});

		ImageButton save = (ImageButton) findViewById(R.id.btnSave);
		save.setOnClickListener(new OnClickListener()

		{
			public void onClick(View v) {
				nextIntent = new Intent(v.getContext(), SaveActivity.class);
				nextIntent.putExtra("duration", playerRec.getDuration());
				startActivity(nextIntent);
				finish();				}
		});

		ImageButton delete = (ImageButton) findViewById(R.id.btnDelete);
		delete.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v) {
				if(playerTh!=null)
					playerTh.pause();
				nextIntent = new Intent(v.getContext(), HomeActivity.class);
				startActivity(nextIntent);
				finish();
			}
		});
	}

	private void changeBpb(int bpm) {
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
		playerMetr.setLooping(true);
		playerMetr.start();
	}
	public void startRecord(){

		recorder = new MediaRecorder();
		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		recorder.setOutputFile(PATH);
		try {
			recorder.prepare();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		playerTh.start();
		recorder.start();   // Recording is now started

	}

	public void stopRecord(){
		if(playerTh!=null)
			playerTh.pause();
		recorder.stop();
		recorder.reset();   // You can reuse the object by going back to setAudioSource() step
		recorder.release(); // Now the object cannot be reused

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
	private void countDown(){
		new CountDownTimer(4000, 1000) {

			public void onTick(long millisUntilFinished) {

				countdown.setVisibility(View.VISIBLE);
				countdown.setText(""+millisUntilFinished/ 1000);
			}

			public void onFinish() {
				countdown.setVisibility(View.INVISIBLE);
				if (mStartTime == 0L) {
					mStartTime = System.currentTimeMillis();
					th = new Thread(null, updateTime, "updateTime");
					th.start();
				}
				startRecord();
			}
		}.start();

	}

	private Runnable updateTime = new Runnable() {

		public void run() {

			while(true) {

				if(isRecording)
					runOnUiThread(mUpdateTimeText);

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
			ledOnOff= (ledOnOff==0? 1: 0);
			led.setImageResource(ledOnOff==1?R.drawable.red_on_32:R.drawable.red_off_32);
			SimpleDateFormat formatter = new SimpleDateFormat("mm:ss:SSS");
			timer.setText(formatter.format(System.currentTimeMillis() - mStartTime));
		}
	};

}