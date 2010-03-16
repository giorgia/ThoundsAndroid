package pro.android.activity;

import java.io.File;

import pro.android.R;
import pro.android.utils.Player;
import pro.android.utils.Recorder;
import android.content.Context;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

public class RecordActivity extends CommonActivity {

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
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.record);
		
		//Bottone Rec - utilizza la classe Recorder (scrittura su file da sistemare)
		final Button btnRec = (Button)this.findViewById(R.id.btnRec);
		levelLine = (ProgressBar)this.findViewById(R.id.LevelLineBar);
		final LinearLayout mediaLayout = (LinearLayout) this.findViewById(R.id.MediaLayout);
		final LinearLayout recLayout = (LinearLayout) this.findViewById(R.id.RecLayout);

		btnRec.setOnClickListener(new OnClickListener()

		{
			public void onClick(View v) {
				try {

					if(isRecording){
						recLayout.setVisibility(View.INVISIBLE);
						mediaLayout.setVisibility(View.VISIBLE);
					
						
						Log.d(this.getClass().getSimpleName(), "STOP REC");
						rec.setRecording(false);
						isRecording= false;

					}
					else{
						btnRec.setBackgroundResource(R.drawable.btn_stop);
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

		ImageButton play = (ImageButton)findViewById(R.id.btnPlay);
		play.setOnClickListener(new OnClickListener()

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
	

}
