package carparking.features.accessibility;

import carparking.annotations.Repository;

import java.util.List;

@Repository
public interface AccessibleParkingSpaceRepository {
    List<AccessibleParkingSpace> findAll();
}
