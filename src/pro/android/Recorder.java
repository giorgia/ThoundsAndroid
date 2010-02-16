package pro.android;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

public class Recorder implements Runnable {
	private int frequency;
	private int channelConfiguration;
	private volatile boolean isPaused;
	private File fileName;
	private volatile boolean isRecording;
	private final Object mutex = new Object();

	// Changing the sample resolution changes sample type. byte vs. short.
	private static final int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
	private static final int CALLBACK_PERIOD = 4000;

	/**
	 *
	 */
	public Recorder() {
		super();
		this.setFrequency(11025);
		this.setChannelConfiguration(AudioFormat.CHANNEL_CONFIGURATION_MONO);
		this.setPaused(false);
	}

	public void run() {
		// Wait until we're recording...
		synchronized (mutex) {
			while (!this.isRecording) {
				try {
					mutex.wait();
				} catch (InterruptedException e) {
					throw new IllegalStateException("Wait() interrupted!", e);
				}
			}
		}

		// Open output stream...
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
		} catch (FileNotFoundException e) {
			throw new IllegalStateException("Cannot Open File", e);
		}
		DataOutputStream dataOutputStreamInstance = new DataOutputStream(
				bufferedStreamInstance);

		// We're important...
		android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);

		// Allocate Recorder and Start Recording...
		int bufferRead = 0;
		int bufferSize = AudioRecord.getMinBufferSize(this.getFrequency(), 
				this.getChannelConfiguration(), this.getAudioEncoding());
		AudioRecord recordInstance = new AudioRecord(
				MediaRecorder.AudioSource.MIC, this.getFrequency(), 
				this.getChannelConfiguration(), this.getAudioEncoding(),
				bufferSize);


		recordInstance.setPositionNotificationPeriod(CALLBACK_PERIOD);
		recordInstance.setRecordPositionUpdateListener(new
				AudioRecord.OnRecordPositionUpdateListener() {
			public void onMarkerReached(AudioRecord recorder) {
				Log.e(this.getClass().getSimpleName(), "onMarkerReached Called");
			}


			public void onPeriodicNotification(AudioRecord recorder) {
				Log.e(this.getClass().getSimpleName(), "onPeriodicNotification Called");
			}
		}); 

		short[] tempBuffer = new short[bufferSize];
		recordInstance.startRecording();
		while (this.isRecording) {
			// Are we paused?
			synchronized (mutex) {
				if (this.isPaused) {
					try {
						mutex.wait(250);
					} catch (InterruptedException e) {
						throw new IllegalStateException("Wait() interrupted!",
								e);
					}
					continue;
				}
			}

			bufferRead = recordInstance.read(tempBuffer, 0, bufferSize);
			if (bufferRead == AudioRecord.ERROR_INVALID_OPERATION) {
				throw new IllegalStateException(
				"read() returned AudioRecord.ERROR_INVALID_OPERATION");
			} else if (bufferRead == AudioRecord.ERROR_BAD_VALUE) {
				throw new IllegalStateException(
				"read() returned AudioRecord.ERROR_BAD_VALUE");
			} else if (bufferRead == AudioRecord.ERROR_INVALID_OPERATION) {
				throw new IllegalStateException(
				"read() returned AudioRecord.ERROR_INVALID_OPERATION");
			}
			try {
				for (int idxBuffer = 0; idxBuffer < bufferRead; ++idxBuffer) {
					dataOutputStreamInstance.writeShort(tempBuffer[idxBuffer]);
				}
			} catch (IOException e) {
				throw new IllegalStateException(
				"dataOutputStreamInstance.writeShort(curVal)");
			}

		}

		// Close resources...
		recordInstance.stop();
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
		synchronized (mutex) {
			this.isRecording = isRecording;
			if (this.isRecording) {
				mutex.notify();
			}
		}
	}

	/**
	 * @return the isRecording
	 */
	public boolean isRecording() {
		synchronized (mutex) {
			return isRecording;
		}
	}

	/**
	 * @param frequency
	 *            the frequency to set
	 */
	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}

	/**
	 * @return the frequency
	 */
	public int getFrequency() {
		return frequency;
	}

	/**
	 * @param channelConfiguration
	 *            the channelConfiguration to set
	 */
	public void setChannelConfiguration(int channelConfiguration) {
		this.channelConfiguration = channelConfiguration;
	}

	/**
	 * @return the channelConfiguration
	 */
	public int getChannelConfiguration() {
		return channelConfiguration;
	}

	/**
	 * @return the audioEncoding
	 */
	public int getAudioEncoding() {
		return audioEncoding;
	}

	/**
	 * @param isPaused
	 *            the isPaused to set
	 */
	public void setPaused(boolean isPaused) {
		synchronized (mutex) {
			this.isPaused = isPaused;
		}
	}

	/**
	 * @return the isPaused
	 */
	public boolean isPaused() {
		synchronized (mutex) {
			return isPaused;
		}
	}

}