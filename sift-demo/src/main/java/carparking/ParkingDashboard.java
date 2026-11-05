package carparking;

import java.util.ArrayList;
import java.util.List;

/**
 * Immutable snapshot of the parking system state as it should appear on a dashboard.
 *
 * <p>This object describes the active MLCP configuration, floor-by-floor breakdown, and totals
 * for available and occupied spaces. It keeps the presentation layer simple and prevents callers
 * from mutating the computed summary.</p>
 */
public class ParkingDashboard {
    private final MlcpType mlcpType;
    private final List<ParkingFloor> floors;
    private final int availableSpaces;
    private final int occupiedSpaces;
    private final int totalSpaces;

    public ParkingDashboard(MlcpType mlcpType,
                            List<ParkingFloor> floors,
                            int availableSpaces,
                            int occupiedSpaces,
                            int totalSpaces) {
        this.mlcpType = mlcpType;
        this.floors = new ArrayList<>(floors);
        this.availableSpaces = availableSpaces;
        this.occupiedSpaces = occupiedSpaces;
        this.totalSpaces = totalSpaces;
    }

    public MlcpType getMlcpType() {
        return mlcpType;
    }

    public List<ParkingFloor> getFloors() {
        return floors;
    }

    public int getAvailableSpaces() {
        return availableSpaces;
    }

    public int getOccupiedSpaces() {
        return occupiedSpaces;
    }

    public int getTotalSpaces() {
        return totalSpaces;
    }
}
