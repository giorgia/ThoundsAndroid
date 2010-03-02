package pro.android;

import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;
import android.widget.ProgressBar;

public class Player {

	private MediaPlayer mediaPlayer;
	private final Handler handler = new Handler();
	private ProgressBar progressBar = null;
	private int progress = 0;

	private static String AUDIO_PATH = "/sdcard/thounds/";
	private String audioName;


	public Player(String audioName) {
		super();		
		this.audioName = audioName;
	}

	public Player(ProgressBar progressBar, String audioName) {
		super();		
		this.progressBar = progressBar;
		this.audioName = audioName;
	}

	public void playAudio() throws Exception {

		killMediaPlayer();

		mediaPlayer = new MediaPlayer();
		mediaPlayer.setDataSource(AUDIO_PATH+audioName);
		mediaPlayer.prepare();
		mediaPlayer.start();

		if(progressBar != null)
			progressUpdater();
		
	}

	private void progressUpdater() {

		if (mediaPlayer.isPlaying()) {

			new Thread(new Runnable() {
				public void run() {
					while (progress < 100) {
						progress = doWork();

						// Update the progress bar
						handler.post(new Runnable() {
							public void run() {
								progressBar.setProgress(progress);
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


}
