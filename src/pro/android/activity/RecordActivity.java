package pro.android.activity;

import java.io.IOException;
import java.text.SimpleDateFormat;

import org.thounds.thoundsapi.RequestWrapper;

import pro.android.R;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class RecordActivity extends CommonActivity {

	public static String PATH= Environment.getExternalStorageDirectory().getAbsolutePath() +"/thounds/test.3gp";
	private MediaRecorder recorder;

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
	private Toast t;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.record);
		currentActivity = R.id.record;

		t = Toast.makeText(this, "", Toast.LENGTH_SHORT);

		countdown = (TextView) findViewById(R.id.txt_countdown);
		lTimer = (RelativeLayout) findViewById(R.id.ProgressBarLayout);
		lMetronome = (RelativeLayout) findViewById(R.id.MetronomeLayout);
		lMedia = (LinearLayout) findViewById(R.id.MediaLayout);
		lRec = (LinearLayout) findViewById(R.id.RecLayout);

		timer = (TextView) findViewById(R.id.txt_timer);
		led = (ImageView) findViewById(R.id.img_led_rec);

		//RECORD
		final Button btnRec = (Button) this.findViewById(R.id.btnRec);
		btnRec.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				if(!isRecording){
					isRecording = true;
					btnRec.setBackgroundResource(R.drawable.btn_stop);
					countDown();

				}else{
					isRecording = false;
					lRec.setVisibility(View.INVISIBLE);
					lMedia.setVisibility(View.VISIBLE);
					stopRecord();
				}


			}


		});

		//PLAY - PAUSE
		final ImageButton play = (ImageButton) findViewById(R.id.btnPlay);
		play.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v) {

				MediaPlayer mp = new MediaPlayer();
				try {
					mp.setDataSource(PATH);
					mp.prepare();
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

				mp.start();

			}
		});



		ImageButton recAgain = (ImageButton) findViewById(R.id.btnRecAgain);
		recAgain.setOnClickListener(new OnClickListener()

		{
			public void onClick(View v) {
			
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
					startActivity(nextIntent);
					finish();				}
			});

		ImageButton delete = (ImageButton) findViewById(R.id.btnDelete);
		delete.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v) {

				nextIntent = new Intent(v.getContext(), HomeActivity.class);
				startActivity(nextIntent);
				finish();
			}
		});
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
		recorder.start();   // Recording is now started

	}

	public void stopRecord(){
		recorder.stop();
		recorder.reset();   // You can reuse the object by going back to setAudioSource() step
		recorder.release(); // Now the object cannot be reused

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