package carparking.features.entryexit;

import carparking.annotations.Service;

/**
 * Opens a gate for a lane when the proper status is determined by the higher-level workflow.
 *
 * <p>In scope: issuing a gate-open signal for a lane. Out of scope: payment or driver identity
 * validation beyond the gate controller integration.</p>
 *
 * @implSpec Gate open requests are delegated to the repository and should be treated as a control action.
 */
@Service
public class EntryExitGateService {
    private final GateControllerRepository gateControllerRepository;

    public EntryExitGateService(GateControllerRepository gateControllerRepository) {
        this.gateControllerRepository = gateControllerRepository;
    }

    /**
     * Requests the gate controller to open a lane.
     *
     * @param laneId the lane identifier to open
     * @return true if the gate controller accepted the request, otherwise false
     */
    public boolean openGate(String laneId) {
        return gateControllerRepository.open(laneId);
    }
}
