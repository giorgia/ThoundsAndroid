package pro.android.utils;

import java.io.File;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

public class RecordPlayer{

	int SAMPLE_RATE = 11025;
	String path;
	AudioTrack audioTrack;
	AudioTrackLoader loader;
	int minBufferSize;
	File file;
	
	public void prepare() {
		// Get the file we want to playback.
		file = new File(path);

		// Get the length of the audio stored in the file (16 bit so 2 bytes per short)
		// and create a short array to store the recorded audio.
		
		minBufferSize = AudioTrack.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT);

		// Create a new AudioTrack object using the same parameters as the AudioRecord
		// object used to create the file.
		audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
				SAMPLE_RATE,
				AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_PCM_16BIT,
				minBufferSize,
				AudioTrack.MODE_STREAM);
		
		loader = new AudioTrackLoader(file, audioTrack, minBufferSize);
		
		
	}

	public void pause(){
		Log.d("Player", "PAUSE");

		audioTrack.pause();
	}

	public void play() {
		
		Log.d("Player", "PLAY");
		Thread th = new Thread(loader);	
		th.start();
		audioTrack.play();
	}

	public void stop() {
		audioTrack.stop();
		Log.d("Player", "STOP");
		audioTrack.release();
	}


	public int getState(){
		return audioTrack.getPlayState();
	}
	public void setData(String path) {
		this.path = path;
		
	}

}
