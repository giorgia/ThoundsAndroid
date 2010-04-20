package pro.android.utils;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.SeekBar;

public class Player{

	private MediaPlayer mediaPlayer;
	private final Handler handler = new Handler();
	private SeekBar seekBar = null;
	private int progress = 0;
	private boolean isStopped = false;
	private boolean isPlaying = false;

	private static String AUDIO_PATH = "/sdcard/thounds/";
	private String audioUrl;


	public Player(String audioUrl) {
		super();		
		this.audioUrl = audioUrl;
		mediaPlayer = new MediaPlayer();
	}

	public Player(SeekBar seekBar, String audioUrl) {
		super();		
		this.seekBar = seekBar;
		this.audioUrl = audioUrl;
		mediaPlayer = new MediaPlayer();
	}

	public void playAudio() throws Exception {
		if(!isStopped){
			isPlaying = true;
			mediaPlayer.setDataSource(audioUrl);
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.prepare();
			mediaPlayer.start();
		}else{
			mediaPlayer.seekTo((mediaPlayer.getDuration())*(progress)/100);
			Log.d("Player" , "CurrPos ="+mediaPlayer.getCurrentPosition()+"  durata = "+mediaPlayer.getDuration());
			mediaPlayer.start();
		}
		if(seekBar != null)
			progressUpdater();

	}

	public void stopAudio()  throws Exception {
		isStopped=true;
		mediaPlayer.stop();
		killMediaPlayer();
	}

	public void pauseAudio() {
		isStopped=true;
		mediaPlayer.pause();

	}

	private void progressUpdater() {

		if (mediaPlayer.isPlaying()) {

			new Thread(new Runnable() {


				public void run() {
					while (!isStopped && progress < 100) {
						progress = doWork();

						// Update the progress bar
						handler.post(new Runnable() {
							public void run() {
								seekBar.setProgress(progress);
							}
						});
					}
				}


			}).start();
		}
	}

	private int doWork() {
		return ((mediaPlayer.getCurrentPosition()/1000)*100/(mediaPlayer.getDuration()/1000));
	}

	private void killMediaPlayer() {
		if (mediaPlayer != null) {
			try {
				mediaPlayer.release();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


	public MediaPlayer getMediaPlayer() {
		return mediaPlayer;
	}

	public void setMediaPlayer(MediaPlayer mediaPlayer) {
		this.mediaPlayer = mediaPlayer;
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}



}
