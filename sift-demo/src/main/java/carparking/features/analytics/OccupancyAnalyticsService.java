package carparking.features.analytics;

import carparking.annotations.Service;

/**
 * Produces utilization metrics used by dashboards and management reports.
 *
 * <p>In scope: calculating occupancy percentages for a specific zone. Out of scope: storing
 * audit history or pricing logic.</p>
 *
 * @implSpec The service returns the most recent utilization value from the repository for the zone.
 */
@Service
public class OccupancyAnalyticsService {
    private final OccupancyMetricRepository occupancyMetricRepository;

    public OccupancyAnalyticsService(OccupancyMetricRepository occupancyMetricRepository) {
        this.occupancyMetricRepository = occupancyMetricRepository;
    }

    /**
     * Retrieves the latest utilization percentage for a named zone.
     *
     * @param zoneCode the zone being measured
     * @return the current occupancy percentage as an integer value
     */
    public int computeUtilization(String zoneCode) {
        return occupancyMetricRepository.getCurrentUtilization(zoneCode);
    }
}
