package ai.archie.android.settings;

/**
 * Represents configured settings local to this device.
 */
public class DeviceSettings {

    private static final String PROD_ARCHIE_HOSTNAME = "https://archie-ai-web.herokuapp.com";

    private String localArchieServiceHostname;
    private boolean useProductionServices;
    private boolean useCustomASR;
    private boolean useCustomTTS;

    public DeviceSettings(String localArchieServiceHostname,
                          boolean useProductionServices,
                          boolean useCustomASR,
                          boolean useCustomTTS) {
        this.localArchieServiceHostname = localArchieServiceHostname;
        this.useProductionServices = useProductionServices;
        this.useCustomASR = useCustomASR;
        this.useCustomTTS = useCustomTTS;
    }

    public String getArchieServiceUrl() {
        if (useProductionServices) {
            return PROD_ARCHIE_HOSTNAME;
        } else {
            return localArchieServiceHostname;
        }
    }

    public String getLocalArchieServiceHostname() {
        return localArchieServiceHostname;
    }

    public boolean shouldUseProductionServices() {
        return useProductionServices;
    }

    public boolean shouldUseCustomASR() {
        return useCustomASR;
    }

    public boolean shouldUseCustomTTS() {
        return useCustomTTS;
    }

    public static class Builder {

        private String localArchieServiceHostname = "";
        private boolean useProductionServices = true;
        private boolean useCustomASR = true;
        private boolean useCustomTTS = true;

        public Builder localArchieServiceHostname(String hostname) {
            localArchieServiceHostname = hostname;
            return this;
        }

        public Builder useProductionServices(boolean useProductionServices) {
            this.useProductionServices = useProductionServices;
            return this;
        }

        public Builder useCustomASR(boolean useCustomASR) {
            this.useCustomASR = useCustomASR;
            return this;
        }

        public Builder useCustomTTS(boolean useCustomTTS) {
            this.useCustomTTS = useCustomTTS;
            return this;
        }

        public DeviceSettings build() {
            return new DeviceSettings(localArchieServiceHostname,
                    useProductionServices,
                    useCustomASR,
                    useCustomTTS);
        }

    }
}
