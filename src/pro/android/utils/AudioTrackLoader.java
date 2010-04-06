package pro.android.utils;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import android.media.AudioTrack;
import android.util.Log;

public class AudioTrackLoader  implements Runnable{
	File file;
	AudioTrack audioTrack;
	int minBufferSize;

	public AudioTrackLoader(File file, AudioTrack audioTrack, int minBufferSize){
		this.file = file;
		this.audioTrack = audioTrack;
		this.minBufferSize = minBufferSize;
	}
	public void run() {

		// Get the length of the audio stored in the file (16 bit so 2 bytes per short)
		// and create a short array to store the recorded audio.
		int musicLength = (int)(file.length());
		byte[] music = new byte[musicLength];


		try {
			// Create a DataInputStream to read the audio data back from the saved file.
			InputStream is = new FileInputStream(file);
			BufferedInputStream bis = new BufferedInputStream(is);
			DataInputStream dis = new DataInputStream(bis);

			// Read the file into the music array.
			while (dis.available() > 0) {
				dis.read(music, 0 , minBufferSize);

				// Write the music buffer to the AudioTrack object
				audioTrack.write(music, 0 , minBufferSize);

			}
			Log.d("Player", "fuori while");

			// Close the input streams.
			dis.close();    

		} catch (Throwable t) {
			Log.e("AudioTrack","Playback Failed");
		}
	}
}
