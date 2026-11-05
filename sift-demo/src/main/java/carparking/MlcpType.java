package carparking;

public enum MlcpType {
    STANDARD(false, false, false),
    PREMIUM(true, false, true),
    EV_CHARGING(true, true, true),
    ACCESSIBLE(true, false, false),
    SMART(true, true, true);

    private final boolean valetSupport;
    private final boolean evCharging;
    private final boolean realTimeMonitoring;

    MlcpType(boolean valetSupport, boolean evCharging, boolean realTimeMonitoring) {
        this.valetSupport = valetSupport;
        this.evCharging = evCharging;
        this.realTimeMonitoring = realTimeMonitoring;
    }

    public boolean hasValetSupport() {
        return valetSupport;
    }

    public boolean hasEvCharging() {
        return evCharging;
    }

    public boolean hasRealTimeMonitoring() {
        return realTimeMonitoring;
    }
}
