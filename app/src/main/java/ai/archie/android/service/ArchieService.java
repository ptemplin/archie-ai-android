package ai.archie.android.service;

import android.os.AsyncTask;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import ai.archie.android.audio.VoiceRecorder;
import ai.archie.android.service.model.ClientAction;
import ai.archie.android.service.task.GetActionFromSpeechTask;
import ai.archie.android.service.task.PlayTextAsSpeechTask;
import ai.archie.android.settings.DeviceSettings;

/**
 * Exposes the API of the ArchieService. Requests are handled by starting the corresponding
 * AsycTask.
 */
public class ArchieService implements GetActionFromSpeechTask.OnActionReceivedListener {

    private List<ArchieListener> listeners = new ArrayList<>();

    private String archieServiceUrl;
    private boolean useCustomASR;
    private boolean useCustomTTS;

    private VoiceRecorder voiceRecorder = new VoiceRecorder();

    public void registerListener(ArchieListener archieListener) {
        listeners.add(archieListener);
    }

    public void unregisterListener(ArchieListener archieListener) {
        listeners.remove(archieListener);
    }

    /**
     * Initializes the service connection.
     */
    public void initialize(DeviceSettings deviceSettings) {
        archieServiceUrl = deviceSettings.getArchieServiceUrl();
        useCustomASR = deviceSettings.shouldUseCustomASR();
        useCustomTTS = deviceSettings.shouldUseCustomTTS();

        new InitializationTask().execute();
    }

    /**
     * Releases resources used by this service connection.
     */
    public void release() {

    }

    /**
     * Starts recording user speech, recognizing it and building an appropriate action in response.
     */
    public void startRecording() {
        voiceRecorder.start();
    }

    /**
     * Stops recording speech. Finish recognizing and processing response.
     */
    public void stopRecording() {
        ByteArrayOutputStream recording = voiceRecorder.stop();
        GetActionFromSpeechTask task = new GetActionFromSpeechTask(archieServiceUrl, this);
        task.execute(recording);
    }

    /**
     * Receives the callback from GetActionFromSpeechTask when response is received from
     * ArchieService.
     *
     * @param action ClientAction to perform
     */
    @Override
    public void onActionReceived(ClientAction action) {
        // play the speech response
        playTextAsSpeech(action.getSpeakText());

        for (ArchieListener listener : listeners) {
            listener.onActionResult(action);
        }

        new InitializationTask().execute();
    }

    /**
     * Starts an async task to play the given text as speech from the ArchieService.
     *
     * @param text string to play as speech
     */
    private void playTextAsSpeech(String text) {
        new PlayTextAsSpeechTask(archieServiceUrl).execute(text);
    }

    public class InitializationTask extends AsyncTask<Void, Void, Void> {

        @Override
        public Void doInBackground(Void... params) {
            voiceRecorder.reset();
            return null;
        }

        @Override
        public void onPostExecute(Void params) {
            for (ArchieListener listener : listeners) {
                listener.onArchieInitialized();
            }
        }

    }

    public interface ArchieListener {

        void onArchieInitialized();

        void onActionResult(ClientAction action);

        void onSpeechPlaybackStarted(String transcription);
    }

}
