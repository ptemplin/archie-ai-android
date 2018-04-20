package ai.archie.android.service;

import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;
import java.net.URLEncoder;

/**
 * Custom implementation of the TextToSpeech service.
 */
public class ArchieTextToSpeechService {

    private static final String GET_SPEECH_PLAYBACK_PATH = "/api/getSpeechPlayback";
    private static final String TEXT_GET_PARAMETER_KEY = "?text=";

    private final String hostName;

    public ArchieTextToSpeechService(String hostName) {
        this.hostName = hostName;
    }

    public void speak(String text) {
        try {
            MediaPlayer player = new MediaPlayer();
            String paramText = URLEncoder.encode(text, "UTF-8");
            player.setDataSource(hostName + GET_SPEECH_PLAYBACK_PATH
                    + TEXT_GET_PARAMETER_KEY + paramText);
            player.prepare();
            player.start();
        } catch (IOException | IllegalArgumentException ex) {
            Log.e(getClass().getSimpleName(), "Couldn't playback speech data", ex);
        }
    }

}
