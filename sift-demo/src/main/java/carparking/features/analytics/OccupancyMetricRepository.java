package carparking.features.analytics;

import carparking.annotations.Repository;

@Repository
public interface OccupancyMetricRepository {
    int getCurrentUtilization(String zoneCode);
}
