package pro.android.utils;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;

import android.util.Log;
import android.widget.ProgressBar;

public class Recorder implements Runnable {
	public static final int DEFAULT_SAMPLE_RATE = 11025;
	private static final int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
	private File fileName;
	int bufferSize;
	int capacity =0;
	int idxBuffer;
	ProgressBar levelLine;
	boolean isRecording = false;
	int level = 0;
	private final Handler handler = new Handler();

	int retVal = 0;
	int volume = 0;
	AudioRecord arec;
	AudioManager audioManager ;

	byte[] buffer;


	public Recorder() {
		super();
	}

	public Recorder(File filename) {
		super();
		this.fileName = filename;
	}

	public void run() {

		// Open output stream...
		Log.d(this.getClass().getName(), "run");


		if (this.fileName == null) {
			throw new IllegalStateException("fileName is null");
		}

		BufferedOutputStream bufferedStreamInstance = null;

		if (fileName.exists()) {
			fileName.delete();
		}

		try {
			fileName.createNewFile();
		} catch (IOException e) {
			throw new IllegalStateException("Cannot create file: "
					+ fileName.toString());
		}
		try {
			bufferedStreamInstance = new BufferedOutputStream(
					new FileOutputStream(this.fileName));
			Log.d(this.getClass().getName(), "bufferedStreamInstance");


		} catch (FileNotFoundException e) {
			throw new IllegalStateException("Cannot Open File", e);
		}
		DataOutputStream dataOutputStreamInstance = new DataOutputStream(
				bufferedStreamInstance);

		// We're important...
		//android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);

		// Allocate Recorder and Start Recording...



		int actualBufferSize = 4096*16;

		bufferSize =  AudioTrack.getMinBufferSize(DEFAULT_SAMPLE_RATE,
				AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_PCM_16BIT);

		capacity = AudioRecord.getMinBufferSize(DEFAULT_SAMPLE_RATE,
				AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_PCM_16BIT);

		buffer = new  byte[bufferSize];

		arec = new AudioRecord(MediaRecorder.AudioSource.MIC,
				DEFAULT_SAMPLE_RATE,
				AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_PCM_16BIT,
				actualBufferSize);

		audioManager.setSpeakerphoneOn(true);
		audioManager.setMicrophoneMute(false);
		arec.startRecording();

		if (audioManager.isSpeakerphoneOn()) {
			isRecording = true;
			Log.d(this.getClass().getName(), "isSpeakerphoneOn");

		}

		while(isRecording)
		{
			int readSize = arec.read(buffer, 0, 320);

			if (readSize == AudioRecord.ERROR_INVALID_OPERATION) {
				throw new IllegalStateException(
				"read() returned AudioRecord.ERROR_INVALID_OPERATION");
			} else if (readSize == AudioRecord.ERROR_BAD_VALUE) {
				throw new IllegalStateException(
				"read() returned AudioRecord.ERROR_BAD_VALUE");
			} else if (readSize == AudioRecord.ERROR_INVALID_OPERATION) {
				throw new IllegalStateException(
				"read() returned AudioRecord.ERROR_INVALID_OPERATION");
			}
			try {

				dataOutputStreamInstance.write(buffer, 0, 320);


				handler.post(new Runnable() {
					public void run() {
						volume = buffer[319];
						levelLine.setProgress(volume);

					}
				});

			} catch (IOException e) {
				throw new IllegalStateException(
						"dataOutputStreamInstance.write(buffer, 0, readSize)");
			}

		}

		arec.stop();
		arec.release();
		

		try {
			bufferedStreamInstance.close();
		} catch (IOException e) {
			throw new IllegalStateException("Cannot close buffered writer.");
		}
	}

	public void setFileName(File fileName) {
		this.fileName = fileName;
	}

	public File getFileName() {
		return fileName;
	}

	/**
	 * @param isRecording
	 *            the isRecording to set
	 */
	public void setRecording(boolean isRecording) {
		this.isRecording = isRecording;
	}

	/**
	 * @return the isRecording
	 */
	public boolean isRecording() {
		return isRecording;
	}


	/**
	 * @return the audioEncoding
	 */
	public int getAudioEncoding() {
		return audioEncoding;
	}

	public ProgressBar getLevelLine() {
		return levelLine;
	}

	public void setLevelLine(ProgressBar levelLine) {
		this.levelLine = levelLine;
	}

	public AudioManager getAudioManager() {
		return audioManager;
	}

	public void setAudioManager(AudioManager audioManager) {
		this.audioManager = audioManager;
	}

}