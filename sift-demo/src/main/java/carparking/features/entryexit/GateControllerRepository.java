package carparking.features.entryexit;

import carparking.annotations.Repository;

@Repository
public interface GateControllerRepository {
    boolean open(String laneId);
}
