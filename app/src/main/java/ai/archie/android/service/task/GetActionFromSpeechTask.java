package ai.archie.android.service.task;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import ai.archie.android.service.model.ClientAction;

/**
 * Uploads a speech stream to the Archie Service and parses the ClientAction response if an action
 * can be retrieved from the speech.
 */
public class GetActionFromSpeechTask extends AsyncTask<ByteArrayOutputStream, String, ClientAction> {

    private static final String LOG_TAG = GetActionFromSpeechTask.class.getSimpleName();

    private static final String GET_ACTION_FROM_SPEECH_PATH = "/api/getActionFromSpeech";
    private static final String POST_REQUEST_METHOD = "POST";
    private static final String CONTENT_TYPE_KEY = "Content-Type";
    private static final String CONTENT_TYPE_VALUE = "application/octet-stream";

    private static final String DISPLAY_ACTION_KEY = "display";
    private static final String SPEAK_ACTION_KEY = "speak";

    private final String hostName;
    private final OnActionReceivedListener listener;

    /**
     * @param hostName to send requests to
     * @param listener to notify when a response is received
     */
    public GetActionFromSpeechTask(String hostName, OnActionReceivedListener listener) {
        this.hostName = hostName;
        this.listener = listener;
    }

    @Override
    protected ClientAction doInBackground(ByteArrayOutputStream[] baos) {
        String result = null;
        try {
            final URL url = new URL(hostName + GET_ACTION_FROM_SPEECH_PATH);
            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(POST_REQUEST_METHOD);
            connection.setRequestProperty(CONTENT_TYPE_KEY, CONTENT_TYPE_VALUE);
            connection.setDoOutput(true);
            connection.setDoInput(true);

            OutputStream os = connection.getOutputStream();
            baos[0].writeTo(os);
            os.close();

            Log.d(LOG_TAG, "Response status: " + connection.getResponseCode());

            final InputStream is = connection.getInputStream();
            final BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            final StringBuilder builder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            reader.close();
            result = builder.toString();

            Log.d(LOG_TAG, "Response: " + result);
        } catch (IOException ex) {
            Log.e(LOG_TAG, "Couldn't send data", ex);
        }
        return parseActionResult(result);
    }

    /**
     * Parses the JSON result string received from the server into a ClientAction.
     * If the result is null or cannot be parsed as JSON, returns the default ERROR_ACTION.
     *
     * @param result JSON string specifying a ClientAction
     * @return the ClientAction specified by the result string, ERROR_ACTION on error
     */
    private ClientAction parseActionResult(String result) {
        if (result == null) {
            return ClientAction.ERROR_ACTION;
        }
        try {
            JSONObject obj = new JSONObject(result);
            String toSpeak = (String) obj.get(SPEAK_ACTION_KEY);
            String toDisplay = (String) obj.get(DISPLAY_ACTION_KEY);
            return new ClientAction(toDisplay, toSpeak);
        } catch (JSONException ex) {
            Log.e(LOG_TAG, "Couldn't parse ClientAction JSON", ex);
            return ClientAction.ERROR_ACTION;
        }
    }

    /**
     * Notifies the listener of the received action.
     */
    @Override
    public void onPostExecute(ClientAction action) {
        listener.onActionReceived(action);
    }

    public interface OnActionReceivedListener {
        void onActionReceived(ClientAction action);
    }
}
