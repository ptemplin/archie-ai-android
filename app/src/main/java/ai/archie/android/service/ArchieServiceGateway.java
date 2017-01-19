package ai.archie.android.service;

import java.io.ByteArrayOutputStream;

/**
 * Exposes the API of the ArchieService. Requests are handled by starting the corresponding
 * AsycTask.
 */
public class ArchieServiceGateway {

    private static final String DEFAULT_HOSTNAME = "http://archie-ai-web.herokuapp.com";

    private final String hostName;

    /**
     * Sets up the gateway using the default hostname.
     */
    public ArchieServiceGateway() {
        this(DEFAULT_HOSTNAME);
    }

    /**
     * Set a custom hostname to use
     * eg. http://0.0.0.0:9000
     *
     * @param hostName the hostname string to use
     */
    public ArchieServiceGateway(String hostName) {
        this.hostName = hostName;
    }

    /**
     * Starts an async task to transcribe the speech and extract an action if possible.
     *
     * @param speechStream the stream of raw wav speech data to upload
     * @param listener will be notified upon the completion of the async task
     */
    public void getActionFromSpeech(ByteArrayOutputStream speechStream,
                                    GetActionFromSpeechTask.OnActionReceivedListener listener) {
        GetActionFromSpeechTask task = new GetActionFromSpeechTask(hostName, listener);
        task.execute(speechStream);
    }

    /**
     * Starts an async task to play the given text as speech from the ArchieService.
     *
     * @param text string to play as speech
     */
    public void playTextAsSpeech(String text) {
        new PlayTextAsSpeechTask(hostName).execute(text);
    }

}
