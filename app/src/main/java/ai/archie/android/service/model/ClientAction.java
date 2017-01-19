package ai.archie.android.service.model;

/**
 * Represents an actionable item on the client-side which is extracted from a stream of speech.
 */
public class ClientAction {

    // the default action in case of errors
    private static final String ERROR_DISPLAY = "Something went wrong";
    private static final String ERROR_SPEAK = "Something went wrong";
    public static final ClientAction ERROR_ACTION = new ClientAction(ERROR_DISPLAY, ERROR_SPEAK);

    private final String displayText;
    private final String speakText;

    /**
     * Simple client action to display a text string on screen and play text as speech.
     *
     * @param displayText the text to display
     * @param speakText the text to speak
     */
    public ClientAction(String displayText, String speakText) {
        this.displayText = displayText;
        this.speakText = speakText;
    }

    public String getDisplayText() {
        return displayText;
    }

    public String getSpeakText() {
        return speakText;
    }

}
