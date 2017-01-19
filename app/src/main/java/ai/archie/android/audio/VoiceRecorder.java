package ai.archie.android.audio;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

/**
 * Interface for recording voice data stream from user in raw format.
 */
public class VoiceRecorder
{
    // audio parameters
    private static final short CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_DEFAULT;
    private static final int SAMPLE_RATE = 16000;
    private static final int SAMPLE_SIZE = 16;
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    private static final int AUDIO_SOURCE = MediaRecorder.AudioSource.MIC;

    // interval in which the recorded samples are output
    private static final int TIMER_INTERVAL = 120;

    private static final int FRAME_PERIOD = SAMPLE_RATE * TIMER_INTERVAL / 1000;
    private static final int BUFFER_SIZE = FRAME_PERIOD * 2 * SAMPLE_SIZE / 8;

    private ByteArrayOutputStream byteStream;
    private AudioRecord audioRecorder;
    // Buffer for output
    private byte[] buffer;
    private boolean recording;

    /**
     * Initializes the VoiceRecorder, initializing a new underlying AudioRecorder.
     */
    public VoiceRecorder() {
        recording = false;
        byteStream = new ByteArrayOutputStream();
        buffer = new byte[BUFFER_SIZE];
        try {
            audioRecorder = new AudioRecord(
                    AUDIO_SOURCE, SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT, BUFFER_SIZE);

            if (audioRecorder.getState() != AudioRecord.STATE_INITIALIZED)
                throw new Exception("AudioRecord initialization failed");
            audioRecorder.setPositionNotificationPeriod(FRAME_PERIOD);
            audioRecorder.setRecordPositionUpdateListener(updateListener);

        } catch (Exception e) {
            Log.e(VoiceRecorder.class.getName(), e.getMessage());
        }
    }

    /**
     * Starts the recording.
     */
    public void start()
    {
        audioRecorder.startRecording();
        recording = true;
        audioRecorder.read(buffer, 0, buffer.length);
    }

    /**
     * Stops the recording.
     * In case of further usage, a reset is needed.
     */
    public ByteArrayOutputStream stop()
    {
        audioRecorder.stop();
        recording = false;
        return byteStream;
    }

    /**
     * @return true if the recorder is recording, false otherwise
     */
    public boolean isRecording() {
        return recording;
    }

    /**
     *  Releases the AudioRecorder if it has been initialized.
     */
    public void release() {
        if (audioRecorder != null) {
            audioRecorder.release();
        }
    }

    /**
     * Resets the recorder to the INITIALIZING state, as if it was just created.
     * In case the class was in RECORDING state, the recording is stopped.
     */
    public void reset() {
        release();
        byteStream = new ByteArrayOutputStream();
        audioRecorder = new AudioRecord(
                AUDIO_SOURCE, SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT, BUFFER_SIZE);
    }

    /*
    * Method used for recording.
    */
    private AudioRecord.OnRecordPositionUpdateListener updateListener = new AudioRecord.OnRecordPositionUpdateListener()
    {
        public void onPeriodicNotification(AudioRecord recorder)
        {
            audioRecorder.read(buffer, 0, buffer.length); // Fill buffer
            try {
                byteStream.write(buffer);
            } catch (IOException ex) {
                Log.e(VoiceRecorder.class.getName(), "Couldn't write to byte stream", ex);
            }
        }

        public void onMarkerReached(AudioRecord recorder) { }
    };

}