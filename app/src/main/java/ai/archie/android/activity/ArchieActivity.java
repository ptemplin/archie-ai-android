package ai.archie.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;

import ai.archie.android.R;
import ai.archie.android.audio.VoiceRecorder;
import ai.archie.android.service.ArchieServiceGateway;
import ai.archie.android.service.GetActionFromSpeechTask;
import ai.archie.android.service.model.ClientAction;

/**
 * Main interface for interacting with the Archie AI Service. Contains UI elements allowing user to
 * record voice and display response from Archie.
 */
public class ArchieActivity extends AppCompatActivity
        implements GetActionFromSpeechTask.OnActionReceivedListener {

    private static final String LOG_TAG = ArchieActivity.class.getSimpleName();

    private Button mRecordButton = null;
    private ProgressBar mProgressSpinner = null;
    private TextView mResultDisplay = null;

    private VoiceRecorder mRecorder = null;

    private ArchieServiceGateway mArchieService = null;

    /**
     * Setup UI references and gateway to the Archie Service.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_archie);

        mProgressSpinner = (ProgressBar) findViewById(R.id.progressSpinner);
        mResultDisplay = (TextView) findViewById(R.id.resultDisplay);

        mRecordButton = (Button) findViewById(R.id.recordButton);
        mRecordButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onRecordPressed();
            }
        });

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitle("");
        setSupportActionBar(myToolbar);

        mArchieService = new ArchieServiceGateway();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }
    }

    /**
     * Settings menu items launch additional activities.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_tts_demo:
                startActivity(new Intent(this, TTSDemoActivity.class));
                break;
            case R.id.action_asr_demo:
                startActivity(new Intent(this, SpeechRecognitionDemoActivity.class));
                break;
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_archie, menu);
        return true;
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
        mArchieService.playTextAsSpeech(action.getSpeakText());

        // update the views
        mProgressSpinner.setVisibility(View.GONE);
        mRecordButton.setVisibility(View.VISIBLE);
        mResultDisplay.setText(action.getDisplayText());
    }

    /**
     * Handles record button press events by starting/stopping recording and manipulating UI
     * elements accordingly.
     */
    private void onRecordPressed() {
        if (mRecorder == null || !mRecorder.isRecording()) {
            mRecordButton.setBackgroundResource(R.mipmap.ic_mic_on);
            mResultDisplay.setText("");
            mRecorder = new VoiceRecorder();
            mRecorder.start();
            Log.d(LOG_TAG, "Started recording");
        } else {
            // update the views
            mRecordButton.setVisibility(View.GONE);
            mProgressSpinner.setVisibility(View.VISIBLE);
            mRecordButton.setBackgroundResource(R.mipmap.ic_mic_off);

            // stop the recording and upload the speech
            ByteArrayOutputStream recording = mRecorder.stop();
            mArchieService.getActionFromSpeech(recording, this);
            mRecorder.release();
            mRecorder = null;
            Log.d(LOG_TAG, "Finished recording, uploading speech");
        }
    }
}