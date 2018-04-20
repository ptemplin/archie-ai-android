package ai.archie.android.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import ai.archie.android.R;
import ai.archie.android.settings.DeviceSettings;
import ai.archie.android.settings.DeviceSettingsManager;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Contains configurable settings for the app. eg. Archie service hostname.
 */
public class SettingsActivity extends AppCompatActivity {

    @BindView(R.id.localArchieServiceHostnameInput)
    EditText localArchieServiceHostnameInput;
    @BindView(R.id.useProdServicesInput)
    CheckBox useProdServicesInput;
    @BindView(R.id.useCustomASRInput)
    CheckBox useCustomASRInput;
    @BindView(R.id.useCustomTTSInput)
    CheckBox useCustomTTSInput;

    private DeviceSettingsManager settingsManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);

        settingsManager = new DeviceSettingsManager(getApplicationContext());
        final DeviceSettings existingSettings = settingsManager.getDeviceSettings();

        localArchieServiceHostnameInput.setText(existingSettings.getLocalArchieServiceHostname());
        useProdServicesInput.setChecked(existingSettings.shouldUseProductionServices());
        useCustomASRInput.setChecked(existingSettings.shouldUseCustomASR());
        useCustomTTSInput.setChecked(existingSettings.shouldUseCustomTTS());
    }

    public void onSaveClicked(View view) {
        final String localArchieHostname = localArchieServiceHostnameInput.getText().toString();
        final boolean useProdServices = useProdServicesInput.isChecked();
        final boolean useCustomASR = useCustomASRInput.isChecked();
        final boolean useCustomTTS = useCustomTTSInput.isChecked();

        settingsManager.save(new DeviceSettings(localArchieHostname,
                useProdServices,
                useCustomASR,
                useCustomTTS));

        finish();
    }
}
