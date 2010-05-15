package pro.android.utils;

import java.io.IOException;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.SeekBar;

public class Player implements SeekBar.OnSeekBarChangeListener , OnCompletionListener{
	
	private MediaPlayer mediaPlayer;
	private final Handler handler = new Handler();
	private SeekBar seekBar = null;
	private int progress = 0;
	private int offset = 0;
	private boolean isBuffered= false;
	private String audioUrl;

	public static final int STATE_STOPPED = 0;
	public static final int STATE_BUFFERING = 1;
	public static final int STATE_PLAYING = 2;
	public static final int STATE_PAUSED = 3;
	public static final int STATE_NODATA = 4;
	public static final int STATE_ERROR = -1;
	private int currentState = STATE_STOPPED;
	private boolean mDoHasWiFi = false;
	private int bufferPercent;


	public Player(String audioUrl) {
		this.audioUrl = audioUrl;
		mediaPlayer = new MediaPlayer();
	}

	public Player(String audioUrl, int offset) {	
		this.offset = offset;
		this.audioUrl = audioUrl;
		mediaPlayer = new MediaPlayer();
	}

	public Player(String audioUrl, SeekBar seekBar) {		
		this.seekBar = seekBar;
		this.audioUrl = audioUrl;
		mediaPlayer = new MediaPlayer();
	}

	public Player(String audioUrl, SeekBar seekBar, int offset) {		
		this.offset = offset;
		this.seekBar = seekBar;
		this.audioUrl = audioUrl;
		mediaPlayer = new MediaPlayer();

	}

	public void bufferedAudio()throws Exception{
		Log.d("Player", "bufferedAudio()");
		mediaPlayer.setDataSource(audioUrl);
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		mediaPlayer.prepare();
		isBuffered = true;
	}
	public void playAudio() throws Exception  {
		//checkConnection();
		if(currentState == STATE_STOPPED){	

			if(!isBuffered){
				bufferedAudio();
				seekBar.setProgress(0);
			}else{

				if(offset != 0){
					Log.d("Player","offset  "+offset);
					mediaPlayer.seekTo(offset);
				}
				mediaPlayer.start();
				currentState = STATE_PLAYING;
				if(seekBar != null){
					seekBar.setProgress(0);
					progressUpdater();
				}
			}

		}else{
			mediaPlayer.seekTo((mediaPlayer.getDuration())*(progress)/100);
			Log.d("Player" , "CurrPos ="+mediaPlayer.getCurrentPosition()+"  durata = "+mediaPlayer.getDuration());
			mediaPlayer.start();
			currentState = STATE_PLAYING;
			progressUpdater();

		}

	}

	public void stopAudio()  throws Exception {
		currentState = STATE_STOPPED;
		seekBar.setProgress(0);
		mediaPlayer.stop();
		killMediaPlayer();
	}

	public void pauseAudio() {
		currentState = STATE_PAUSED;
		mediaPlayer.pause();

	}

	public void muteAudio() {

		mediaPlayer.setVolume(0, 0);

	}
	public void unmuteAudio(float volume) {

		mediaPlayer.setVolume(volume, volume);

	}

	public void progressUpdater() {

		if (currentState == STATE_PLAYING) {

			new Thread(new Runnable() {
				public void run() {
					while ((currentState != STATE_STOPPED && currentState != STATE_PAUSED) && progress < 100) {
						progress = ((mediaPlayer.getCurrentPosition()/1000)*100/(mediaPlayer.getDuration()/1000));
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


	private void killMediaPlayer() {
		if (mediaPlayer != null) {
			try {
				mediaPlayer.release();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void checkConnection(){
		BroadcastReceiver connectivityListener = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				NetworkInfo ni = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);

				if (ni.getState() == NetworkInfo.State.DISCONNECTED) {
					if (currentState != STATE_STOPPED) {
						// Ignore disconnections that don't change our WiFi / cell
						// state
						if ((ni.getType() == ConnectivityManager.TYPE_WIFI) != mDoHasWiFi ) {
							return;
						}

						// We just lost the WiFi connection so update our state
						if (ni.getType() == ConnectivityManager.TYPE_WIFI)
							mDoHasWiFi = false;

						Log.d("PLAYER","Data connection lost! Type: " + ni.getTypeName() + " Subtype: " + ni.getSubtypeName() + "Extra Info: " + ni.getExtraInfo()
								+ " Reason: " + ni.getReason());
						if (mediaPlayer != null && bufferPercent < 100) {
							try {
								mediaPlayer.stop();
							} catch (Exception e) {
								e.printStackTrace();
							}

							currentState = STATE_NODATA;

						}

					}
				} else if (ni.getState() == NetworkInfo.State.CONNECTED && currentState != STATE_STOPPED && currentState != STATE_PAUSED) {
					if (currentState == STATE_NODATA || ni.isFailover() || ni.getType() == ConnectivityManager.TYPE_WIFI) {
						if (ni.getType() == ConnectivityManager.TYPE_WIFI) {
							if (!mDoHasWiFi)
								mDoHasWiFi = true;
							else
								return;
						}
						Log.d("PLAYER","New data connection attached! Type: " + ni.getTypeName() + " Subtype: " + ni.getSubtypeName() + "Extra Info: "
								+ ni.getExtraInfo() + " Reason: " + ni.getReason());
						//currentState = STATE_TUNING;
					}
				}
			}


		};
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

	public SeekBar getSeekBar() {
		return seekBar;
	}

	public void setSeekBar(SeekBar seekBar) {
		this.seekBar = seekBar;
	}

	public int getCurrentState() {
		return currentState;
	}

	public void setCurrentState(int currentState) {
		this.currentState = currentState;
	}

	public String getAudioUrl() {
		return audioUrl;
	}

	public void setAudioUrl(String audioUrl) {
		this.audioUrl = audioUrl;
	}

	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
		if(fromTouch){
			Log.d("HOME", "fromTouch"); 
			this.progress = progress;
		}
	}

	public void onStartTrackingTouch(SeekBar seekBar) {
		Log.d("HOME", "onStart"); 
		pauseAudio();
	}

	public void onStopTrackingTouch(SeekBar seekBar) {
		try {
			Log.d("HOME", "onStop"); 
			playAudio();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void onCompletion(MediaPlayer mp) {
		progress = 0;
		seekBar.setProgress(0);
		mediaPlayer.seekTo(0);
		currentState = STATE_STOPPED;
	}

	public int getOffset() {
		// TODO Auto-generated method stub
		return this.offset;
	}
	
	


}
