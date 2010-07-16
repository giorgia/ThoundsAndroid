package pro.android.utils;

import java.io.IOException;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.util.Log;
import android.widget.Toast;
import android.widget.MediaController.MediaPlayerControl;

public class Player implements MediaPlayerControl, OnErrorListener{

	private MediaPlayer[] mediaPlayers = new MediaPlayer[1];;

	private int progress = 0;
	private int[] offsets = new int[1];
	private String[] audioUrls = new String[1];
	
	public static final int STATE_STOPPED = 0;
	public static final int STATE_BUFFERING = 1;
	public static final int STATE_PLAYING = 2;
	public static final int STATE_PAUSED = 3;
	public static final int STATE_NODATA = 4;
	public static final int STATE_ERROR = -1;
	private int currentState = STATE_STOPPED;

	private int bufferPercent;


	public Player(String audioUrl) {
		this.audioUrls[0] = audioUrl;	
		mediaPlayers[0] = new MediaPlayer();
	}

	public Player(String audioUrl, int offset) {	
		this.offsets[0] = offset;
		this.audioUrls[0] = audioUrl;
		mediaPlayers[0] = new MediaPlayer();
	}

	public Player(int size) {
		this.mediaPlayers = new MediaPlayer[size];
	}
	
	public Player(String[] audioUrls, int[] offsets) {	
		this.offsets = offsets;
		this.audioUrls = audioUrls;
		for(int i=0; i< audioUrls.length; i++){
			mediaPlayers[i] = new MediaPlayer();
		}
	}
	
	public void setData(String uri, int offset, int index) {
		mediaPlayers[index] = new MediaPlayer();
		try {
			mediaPlayers[index].setDataSource(uri);
			mediaPlayers[index].setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayers[index].prepare();
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
		//this.audioUrls[index] = uri;
		//this.offsets[index] = offset;
	}
	
	public void bufferedAudio()throws Exception{
		for(int i = 0; i<mediaPlayers.length; i++){
			mediaPlayers[i].setDataSource(audioUrls[i]);
			mediaPlayers[i].setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayers[i].prepare();
		}
	}

	public void bufferedAudio(int index)throws Exception{
		
			mediaPlayers[index].setDataSource(audioUrls[index]);
			mediaPlayers[index].setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayers[index].prepare();
		
	}
	public void muteAudio(int index) {

		mediaPlayers[index].setVolume(0, 0);

	}
	public void unmuteAudio(float volume, int index) {

		mediaPlayers[index].setVolume(volume, volume);

	}


	public int getBufferPercentage() {
		// TODO Auto-generated method stub
		return bufferPercent;
	}

	public int getCurrentPosition() {
		// TODO Auto-generated method stub
		return mediaPlayers[0].getCurrentPosition();
	}

	public int getDuration() {
		// TODO Auto-generated method stub
		return mediaPlayers[0].getDuration();
	}

	public boolean isPlaying() {
		// TODO Auto-generated method stub
		return mediaPlayers[0].isPlaying();
	}

	public boolean isPlaying(int index) {
		// TODO Auto-generated method stub
		return mediaPlayers[index].isPlaying();
	}

	public void pause() {
		// TODO Auto-generated method stub
		currentState = STATE_PAUSED;
		mediaPlayers[0].pause();
	}
	
	public void pause(int index) {
		// TODO Auto-generated method stub
		currentState = STATE_PAUSED;
		mediaPlayers[index].pause();
	}
	
	public void seekTo(int pos) {
		// TODO Auto-generated method stub
		mediaPlayers[0].seekTo(pos);
	}
	
	public void seekTo(int pos, int index) {
		// TODO Auto-generated method stub
		mediaPlayers[index].seekTo(pos);
	}
	
	public void start() {
		// TODO Auto-generated method stub
		mediaPlayers[0].start();

	}

	public void start(int index) {
		// TODO Auto-generated method stub
		mediaPlayers[index].start();

	}
	
	public void setBufferPercent(int percent) {
		// TODO Auto-generated method stub
		bufferPercent = percent;
	}

	public MediaPlayer getDefaulMediaPlayer() {
		return mediaPlayers[0];
	}

	public MediaPlayer getMediaPlayer(int i) {
		// TODO Auto-generated method stub
		return mediaPlayers[i];
	}
	
	public void setDefaultMediaPlayer(MediaPlayer mediaPlayer) {
		this.mediaPlayers[0] = mediaPlayer;
	}
	

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	public int getCurrentState() {
		return currentState;
	}

	public void setCurrentState(int currentState) {
		this.currentState = currentState;
	}

	public String getAudioUrl() {
		return audioUrls[0];
	}

	public void setAudioUrl(String audioUrl) {
		this.audioUrls[0] = audioUrl;
	}



	public int getOffset() {
		// TODO Auto-generated method stub
		return this.offsets[0];
	}

	public boolean onError(MediaPlayer mp, int what, int extra) {
		// TODO Auto-generated method stub
		Toast.makeText(null, "Errore", 4);
		Log.d("Player","error player"+what);
		return false;
	}

	public int size() {
		// TODO Auto-generated method stub
		return mediaPlayers.length;
	}

	
	
}
