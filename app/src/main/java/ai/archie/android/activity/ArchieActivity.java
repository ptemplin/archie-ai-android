package ai.archie.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;

import ai.archie.android.R;
import ai.archie.android.audio.VoiceRecorder;
import ai.archie.android.service.ArchieService;
import ai.archie.android.service.task.GetActionFromSpeechTask;
import ai.archie.android.service.model.ClientAction;
import ai.archie.android.settings.DeviceSettingsManager;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Main interface for interacting with the Archie AI Service. Contains UI elements allowing user to
 * record voice and display response from Archie.
 */
public class ArchieActivity extends AppCompatActivity implements ArchieService.ArchieListener {

    private static final String LOG_TAG = ArchieActivity.class.getSimpleName();

    @BindView(R.id.recordButton)
    Button recordButton;
    @BindView(R.id.progressSpinner)
    ProgressBar progressSpinner;
    @BindView(R.id.resultDisplay)
    TextView resultDisplay;

    private DeviceSettingsManager deviceSettingsManager;
    private ArchieService archieService;

    private boolean recording = false;

    /**
     * Setup UI references and gateway to the Archie Service.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archie);
        ButterKnife.bind(this);

        deviceSettingsManager = new DeviceSettingsManager(this);

        archieService = new ArchieService();
        archieService.registerListener(this);

        recordButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        onRecordPressed();
                        break;
                    case MotionEvent.ACTION_UP:
                        onRecordReleased();
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        archieService.initialize(deviceSettingsManager.getDeviceSettings());
    }

    @Override
    public void onPause() {
        super.onPause();
        archieService.release();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        archieService.unregisterListener(this);
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

    @Override
    public void onArchieInitialized() {
        resultDisplay.setText("Ready to listen");
    }

    /**
     * Receives the callback from GetActionFromSpeechTask when response is received from
     * ArchieService.
     *
     * @param action ClientAction to perform
     */
    @Override
    public void onActionResult(ClientAction action) {
        // update the views
        progressSpinner.setVisibility(View.GONE);
        recordButton.setVisibility(View.VISIBLE);
        resultDisplay.setText(action.getDisplayText());
    }

    @Override
    public void onSpeechPlaybackStarted(String transcription) {

    }

    /**
     * Handles record button press events by starting/stopping recording and manipulating UI
     * elements accordingly.
     */
    private void onRecordPressed() {
        resultDisplay.setText("");
        archieService.startRecording();
        Log.d(LOG_TAG, "Started recording");
    }

    private void onRecordReleased() {
        recordButton.setVisibility(View.GONE);
        progressSpinner.setVisibility(View.VISIBLE);
        archieService.stopRecording();
        Log.d(LOG_TAG, "Stopped recording");
    }
}
