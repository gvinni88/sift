package carparking.features.clickandcollect;

import carparking.annotations.Entity;

@Entity
public class ClickAndCollectSpot {
    private final String id;
    private final String bayCode;
    private boolean reserved;

    public ClickAndCollectSpot(String id, String bayCode, boolean reserved) {
        this.id = id;
        this.bayCode = bayCode;
        this.reserved = reserved;
    }

    public String getId() {
        return id;
    }

    public String getBayCode() {
        return bayCode;
    }

    public boolean isReserved() {
        return reserved;
    }

    public void setReserved(boolean reserved) {
        this.reserved = reserved;
    }
}
