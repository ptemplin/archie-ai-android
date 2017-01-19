package ai.archie.android.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import ai.archie.android.R;
import ai.archie.android.service.ArchieServiceGateway;

/**
 * Direct interface to Archie's TTS for debugging purposes.
 */
public class TTSDemoActivity extends AppCompatActivity {

    private EditText mTTSEditText = null;
    private Button mTTSSubmitButton = null;

    private ArchieServiceGateway mArchieService = null;

    /**
     * Handles "Speak" button press events by playing the submitted text as speech.
     */
    private void onSpeak() {
        String text = mTTSEditText.getText().toString();
        mTTSEditText.setText("");
        mArchieService.playTextAsSpeech(text);
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_tts_demo);

        mTTSEditText = (EditText) findViewById(R.id.ttsEditText);

        mTTSSubmitButton = (Button) findViewById(R.id.ttsSubmitButton);
        mTTSSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSpeak();
            }
        });

        mArchieService = new ArchieServiceGateway();
    }
}