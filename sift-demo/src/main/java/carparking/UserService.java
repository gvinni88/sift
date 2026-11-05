package carparking;

import carparking.annotations.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Calculates the realtime, floor-by-floor state of the parking environment.
 *
 * <p>The service is intentionally scoped to availability calculations and status updates for
 * parking spaces. It does not include pricing, ticket issuance, access control, or customer
 * account logic.</p>
 *
 * @implSpec The service aggregates by floor number and derives available/occupied totals from the
 * current repository state.
 */
@Service
class ParkingAvailabilityService {
    private final ParkingSpaceRepository parkingSpaceRepository;

    public ParkingAvailabilityService(ParkingSpaceRepository parkingSpaceRepository) {
        this.parkingSpaceRepository = parkingSpaceRepository;
    }

    /**
     * Builds the dashboard summary from all known spaces in the MLCP.
     *
     * @return a dashboard snapshot with per-floor availability and total occupancy information
     */
    public ParkingDashboard getDashboard() {
        List<ParkingSpace> spaces = parkingSpaceRepository.findAll();
        Map<Integer, List<ParkingSpace>> byFloor = new LinkedHashMap<>();
        for (ParkingSpace space : spaces) {
            byFloor.computeIfAbsent(space.getFloorNumber(), key -> new ArrayList<>()).add(space);
        }

        List<ParkingFloor> floors = new ArrayList<>();
        int totalAvailable = 0;
        int totalOccupied = 0;
        for (Map.Entry<Integer, List<ParkingSpace>> entry : byFloor.entrySet()) {
            List<ParkingSpace> floorSpaces = entry.getValue();
            int available = 0;
            int occupied = 0;
            for (ParkingSpace space : floorSpaces) {
                if (space.isOccupied()) {
                    occupied++;
                } else {
                    available++;
                }
            }
            totalAvailable += available;
            totalOccupied += occupied;
            floors.add(new ParkingFloor("F" + entry.getKey(), entry.getKey(), available, occupied));
        }
        floors.sort(Comparator.comparingInt(ParkingFloor::getLevel));

        return new ParkingDashboard(MlcpType.SMART, floors, totalAvailable, totalOccupied, totalAvailable + totalOccupied);
    }

    /**
     * Updates the occupied flag for a single parking space.
     *
     * @param spaceId the space identifier to be updated
     * @param occupied the new occupied state
     */
    public void updateSpaceStatus(String spaceId, boolean occupied) {
        for (ParkingSpace space : parkingSpaceRepository.findAll()) {
            if (space.getId().equals(spaceId)) {
                space.setOccupied(occupied);
                return;
            }
        }
    }
}
