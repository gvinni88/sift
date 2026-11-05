package carparking;

import carparking.annotations.Repository;

import java.util.List;

@Repository
interface ParkingSpaceRepository {
    List<ParkingSpace> findAll();
}
