package carparking.features.entryexit;

import carparking.annotations.Entity;

@Entity
public class AccessLog {
    private final String id;
    private final String laneId;
    private final String vehiclePlate;

    public AccessLog(String id, String laneId, String vehiclePlate) {
        this.id = id;
        this.laneId = laneId;
        this.vehiclePlate = vehiclePlate;
    }

    public String getId() {
        return id;
    }

    public String getLaneId() {
        return laneId;
    }

    public String getVehiclePlate() {
        return vehiclePlate;
    }
}
