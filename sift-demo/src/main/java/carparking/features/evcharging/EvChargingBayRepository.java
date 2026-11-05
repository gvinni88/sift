package carparking.features.evcharging;

import carparking.annotations.Repository;

import java.util.List;

@Repository
public interface EvChargingBayRepository {
    List<EvChargingBay> findAll();
}
