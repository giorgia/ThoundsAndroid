package pro.android.activity;

import java.io.File;

import pro.android.R;
import pro.android.utils.RecordPlayer;
import pro.android.utils.Recorder;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

public class RecordActivity_02 extends CommonActivity {

	public static final int DEFAULT_SAMPLE_RATE = 8000;
	Recorder rec = new Recorder();
	RecordPlayer player = new RecordPlayer();
	Recorder recorderInstance =null;
	ProgressBar levelLine;
	private boolean isRecording = false;
	private boolean isPlaying = false;

	Intent nextIntent;
	int level = 100;
	private final Handler handler = new Handler();
	int retVal = 0;
	int volume = 0;
	AudioRecord arec;
	AudioTrack atrack;
	AudioManager am;
	byte[] buffer;
	Thread th = null;
	int offset = 0;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.record);
		currentActivity = R.id.record;
		// Bottone Rec - utilizza la classe Recorder (scrittura su file da
		// sistemare)

		levelLine = (ProgressBar) this.findViewById(R.id.LevelLineBar);
		final LinearLayout mediaLayout = (LinearLayout) this.findViewById(R.id.MediaLayout);
		final LinearLayout recLayout = (LinearLayout) this.findViewById(R.id.RecLayout);
		
		//DEBUG
		//recLayout.setVisibility(View.INVISIBLE);
		//mediaLayout.setVisibility(View.VISIBLE);

		//RECORD
		final Button btnRec = (Button) this.findViewById(R.id.btnRec);
		btnRec.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				try {
					if (isRecording) {
						recLayout.setVisibility(View.INVISIBLE);
						mediaLayout.setVisibility(View.VISIBLE);

						Log.d(this.getClass().getSimpleName(), "STOP REC");
						rec.setRecording(false);
						isRecording = false;

					} else {
						btnRec.setBackgroundResource(R.drawable.btn_stop);
		
						Log.d(this.getClass().getSimpleName(), "REC");
						
						rec.setFileName(new File(Environment.getExternalStorageDirectory().getAbsolutePath() +"/thounds/test.pcm"));

						rec.setLevelLine(levelLine);
						rec.setAudioManager((AudioManager) getSystemService(Context.AUDIO_SERVICE));
						isRecording = true;
						new Thread(rec).start();
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		//PLAY - PAUSE
		final ImageButton play = (ImageButton) findViewById(R.id.btnPlay);
		play.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v) {
			
				if(!isPlaying){
					isPlaying = true;
					play.setImageResource(android.R.drawable.ic_media_pause);
					player.setData(Environment.getExternalStorageDirectory().getAbsolutePath() +"/thounds/test.pcm");
					player.prepare();
					player.play();
				}else{
					if(player.getState() == AudioTrack.PLAYSTATE_PAUSED){
						play.setImageResource(android.R.drawable.ic_media_pause);
						player.play();
					}else if (player.getState() == AudioTrack.PLAYSTATE_PLAYING){
						play.setImageResource(android.R.drawable.ic_media_play);
						player.pause();
						
					}
				}
			}
		});



		ImageButton recAgain = (ImageButton) findViewById(R.id.btnRecAgain);
		recAgain.setOnClickListener(new OnClickListener()

		{
			public void onClick(View v) {
				if(isPlaying)
					player.stop();
				nextIntent = new Intent(v.getContext(), RecordActivity_02.class);
				startActivity(nextIntent);
				finish();
			}
		});

		//	ImageButton save = (ImageButton) findViewById(R.id.btnSave);

		ImageButton delete = (ImageButton) findViewById(R.id.btnDelete);
		delete.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v) {
				if(isPlaying)
					player.stop();
				nextIntent = new Intent(v.getContext(), HomeActivity.class);
				startActivity(nextIntent);
				finish();
			}
		});
	}
}