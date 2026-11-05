package carparking.features.clickandcollect;

import carparking.annotations.Service;

/**
 * Manages reserved collection bays for customers collecting online purchases.
 *
 * <p>In scope: reserving an available click-and-collect bay for a specific order. Out of scope:
 * payment processing and order fulfillment tracking.</p>
 *
 * @implSpec A spot is reserved only when the bay code matches an available, unreserved slot.
 */
@Service
public class ClickAndCollectReservationService {
    private final ClickAndCollectSpotRepository clickAndCollectSpotRepository;

    public ClickAndCollectReservationService(ClickAndCollectSpotRepository clickAndCollectSpotRepository) {
        this.clickAndCollectSpotRepository = clickAndCollectSpotRepository;
    }

    /**
     * Reserves an available click-and-collect bay.
     *
     * @param bayCode the bay identifier requested by the customer
     * @return true when a bay was successfully reserved, otherwise false
     */
    public boolean reserveSpot(String bayCode) {
        for (ClickAndCollectSpot spot : clickAndCollectSpotRepository.findAll()) {
            if (spot.getBayCode().equals(bayCode) && !spot.isReserved()) {
                spot.setReserved(true);
                return true;
            }
        }
        return false;
    }
}
