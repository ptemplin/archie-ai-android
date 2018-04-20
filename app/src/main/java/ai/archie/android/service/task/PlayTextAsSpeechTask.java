package ai.archie.android.service.task;

import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.URLEncoder;

/**
 * Retrieves the configured text as speech from the Archie Service, using a MediaPlayer to play it
 * back to the user.
 */
public class PlayTextAsSpeechTask extends AsyncTask<String, Void, Void> {

    private static final String GET_SPEECH_PLAYBACK_PATH = "/api/getSpeechPlayback";
    private static final String TEXT_GET_PARAMETER_KEY = "?text=";

    private final String hostName;

    public PlayTextAsSpeechTask(String hostName) {
        this.hostName = hostName;
    }

    /**
     * Plays the text as speech retrieved from the ArchieService.
     * @param text first element is sentence to play as speech
     */
    @Override
    protected Void doInBackground(String... text) {
        try {
            MediaPlayer player = new MediaPlayer();
            String paramText = URLEncoder.encode(text[0], "UTF-8");
            player.setDataSource(hostName + GET_SPEECH_PLAYBACK_PATH
                    + TEXT_GET_PARAMETER_KEY + paramText);
            player.prepare();
            player.start();
        } catch (IOException ex) {
            Log.e(PlayTextAsSpeechTask.class.getName(), "Can't play speech data", ex);
        }
        return null;
    }
}
