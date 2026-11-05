package carparking;

import carparking.annotations.Controller;

/**
 * Provides the dashboard-facing entry point for the parking application.
 *
 * <p>This controller intentionally remains narrow: it exposes the current availability view
 * without containing parking rules or persistence logic. The actual calculations are delegated
 * to {@link ParkingAvailabilityService}.</p>
 *
 * @implSpec The controller should remain read-only by default and should not mutate parking state.
 */
@Controller
class ParkingDashboardController {
    private final ParkingAvailabilityService parkingAvailabilityService;

    public ParkingDashboardController(ParkingAvailabilityService parkingAvailabilityService) {
        this.parkingAvailabilityService = parkingAvailabilityService;
    }

    /**
     * Returns the latest parking availability snapshot for the configured MLCP installation.
     *
     * @return a dashboard model containing floor-level capacity and utilization data
     */
    public ParkingDashboard getDashboard() {
        return parkingAvailabilityService.getDashboard();
    }
}
