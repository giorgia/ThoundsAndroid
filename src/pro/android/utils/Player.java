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

	private int bufferPercent;
	private int countSolo = 0;
	private int countMute = 0;
	public boolean[] mute;
	public boolean[] solo;


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
		mute = solo = new boolean[size];
		for(int i=0; i< size; i++){
			mute[i] = solo[i] = false;
		}
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

	}

	public Runnable bufferedAudio= new Runnable() {
		public void run() {
			for(int i = 0; i<mediaPlayers.length; i++){
				try {
					mediaPlayers[i].setDataSource(audioUrls[i]);
					mediaPlayers[i].setAudioStreamType(AudioManager.STREAM_MUSIC);
					mediaPlayers[i].prepare();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}
	};

	public Runnable getBufferedAudio() {
		return bufferedAudio;
	}

	public void setBufferedAudio(Runnable bufferedAudio) {
		this.bufferedAudio = bufferedAudio;
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

	public void muteBtnAudio(float volume, int index) {
		if(solo[index]) {
			countSolo--;
			
			if(countSolo == 0) {
				for(int i = 0; i< mediaPlayers.length; i++){
					if(i != index && !mute[i]){
						unmuteAudio(volume, i);
					}
				}
			}
		}
		countMute++;
		mute[index] = true;
		
		muteAudio(index);
	}
	public void unmuteBtnAudio(float volume, int index) {
		countMute--;
		mute[index] = false;
		
		if(countSolo == 0) {
			unmuteAudio(volume, index);
		}
	}

	public void soloAudio(float volume, int index) {
		if(mute[index])
			countMute--;
		countSolo++;
		mute[index] = false;
		solo[index] = true;

		if(!(countSolo > 1)) {
			for(int i = 0; i< mediaPlayers.length; i++){
				if(i != index && !mute[i]){
					muteAudio(i);
				}
			}
		}
		
		unmuteAudio(volume, index);
	}

	public void unsoloAudio(float volume, int index) {
		countSolo--;
		solo[index] = false;

		if(countSolo > 0) {
			muteAudio(index);
		}
		else {
			for(int i = 0; i< mediaPlayers.length; i++){
				if(i != index && !mute[i]){
					unmuteAudio(volume, i);
				}
			}
		}
	}
	public int getBufferPercentage() {
		return bufferPercent;
	}

	public int getCurrentPosition() {
		return mediaPlayers[0].getCurrentPosition();
	}

	public int getDuration() {
		return mediaPlayers[0].getDuration();
	}

	public boolean isPlaying() {
		return mediaPlayers[0].isPlaying();
	}

	public boolean isPlaying(int index) {
		return mediaPlayers[index].isPlaying();
	}

	public void pause() {
		mediaPlayers[0].pause();
	}

	public void pause(int index) {
		mediaPlayers[index].pause();
	}

	public void seekTo(int pos) {
		mediaPlayers[0].seekTo(pos);
	}

	public void seekTo(int pos, int index) {
		mediaPlayers[index].seekTo(pos);
	}

	public void start() {
		mediaPlayers[0].start();
	}

	public void start(int index) {
		mediaPlayers[index].start();
	}

	public void setBufferPercent(int percent) {
		bufferPercent = percent;
	}

	public MediaPlayer getDefaulMediaPlayer() {
		return mediaPlayers[0];
	}

	public MediaPlayer getMediaPlayer(int i) {
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

	public String getAudioUrl() {
		return audioUrls[0];
	}

	public void setAudioUrl(String audioUrl) {
		this.audioUrls[0] = audioUrl;
	}

	public int getOffset() {
		return this.offsets[0];
	}

	public boolean onError(MediaPlayer mp, int what, int extra) {
		Toast.makeText(null, "Errore", 4);
		Log.d("Player","error player"+what);
		return false;
	}

	public int size() {
		return mediaPlayers.length;
	}



}
