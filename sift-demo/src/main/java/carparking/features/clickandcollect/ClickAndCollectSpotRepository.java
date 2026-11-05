package carparking.features.clickandcollect;

import carparking.annotations.Repository;

import java.util.List;

@Repository
public interface ClickAndCollectSpotRepository {
    List<ClickAndCollectSpot> findAll();
}
