package carparking.features.evcharging;

import carparking.annotations.Entity;

@Entity
public class EvChargingBay {
    private final String id;
    private final String bayCode;
    private boolean inUse;

    public EvChargingBay(String id, String bayCode, boolean inUse) {
        this.id = id;
        this.bayCode = bayCode;
        this.inUse = inUse;
    }

    public String getId() {
        return id;
    }

    public String getBayCode() {
        return bayCode;
    }

    public boolean isInUse() {
        return inUse;
    }

    public void setInUse(boolean inUse) {
        this.inUse = inUse;
    }
}
