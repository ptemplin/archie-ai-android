package ai.archie.android.settings;

import android.content.Context;
import android.content.SharedPreferences;

import ai.archie.android.R;

/**
 * Manages retrieval and updates to device-local settings.
 */
public class DeviceSettingsManager {

    private static final String LOCAL_ARCHIE_SERVICE_HOSTNAME_DEFAULT = "";
    private static final boolean USE_PROD_SERVICES_DEFAULT = true;
    private static final boolean USE_CUSTOM_ASR_DEFAULT = true;
    private static final boolean USE_CUSTOM_TTS_DEFAULT = true;

    private final Context context;

    public DeviceSettingsManager(Context context) {
        this.context = context;
    }

    public DeviceSettings getDeviceSettings() {
        final SharedPreferences settings = getDeviceSettingsPrefs();

        // Build DeviceSettings from prefs
        final String localArchieServiceHostname = settings.getString(
                context.getString(R.string.local_archie_service_hostname_key),
                LOCAL_ARCHIE_SERVICE_HOSTNAME_DEFAULT);
        final boolean useProdServices = settings.getBoolean(
                context.getString(R.string.use_production_services_key),
                USE_PROD_SERVICES_DEFAULT);
        final boolean useCustomASR = settings.getBoolean(
                context.getString(R.string.use_custom_asr_key),
                USE_CUSTOM_ASR_DEFAULT);
        final boolean useCustomTTS = settings.getBoolean(
                context.getString(R.string.use_custom_tts_key),
                USE_CUSTOM_TTS_DEFAULT);

        return new DeviceSettings(localArchieServiceHostname,
                useProdServices,
                useCustomASR,
                useCustomTTS);
    }

    public void save(DeviceSettings settings) {
        getDeviceSettingsPrefs()
                .edit()
                .putString(context.getString(R.string.local_archie_service_hostname_key),
                        settings.getLocalArchieServiceHostname())
                .putBoolean(context.getString(R.string.use_production_services_key),
                        settings.shouldUseProductionServices())
                .putBoolean(context.getString(R.string.use_custom_asr_key),
                        settings.shouldUseCustomASR())
                .putBoolean(context.getString(R.string.use_custom_tts_key),
                        settings.shouldUseCustomTTS())
                .apply();
    }

    public void setLocalArchieServiceHostname(String localArchieServiceHostname) {
        getDeviceSettingsPrefs()
                .edit()
                .putString(context.getString(R.string.local_archie_service_hostname_key),
                        localArchieServiceHostname)
                .apply();
    }

    public void setUseProdServices(boolean useProdServices) {
        getDeviceSettingsPrefs()
                .edit()
                .putBoolean(context.getString(R.string.use_production_services_key),
                        useProdServices)
                .apply();
    }

    public void setUseCustomASR(boolean useCustomASR) {
        getDeviceSettingsPrefs()
                .edit()
                .putBoolean(context.getString(R.string.use_custom_asr_key),
                        useCustomASR)
                .apply();
    }

    public void setUseCustomTTS(boolean useCustomTTS) {
        getDeviceSettingsPrefs()
                .edit()
                .putBoolean(context.getString(R.string.use_custom_tts_key),
                        useCustomTTS)
                .apply();
    }

    private SharedPreferences getDeviceSettingsPrefs() {
        return context.getSharedPreferences(
                context.getString(R.string.device_settings_file), Context.MODE_PRIVATE);
    }

}
