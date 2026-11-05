package carparking.features.accessibility;

import carparking.annotations.Service;

/**
 * Allocates accessible parking spaces for eligible users.
 *
 * <p>In scope: reserving a free accessible bay in the designated zone. Out of scope: user
 * eligibility checks and payment processing.</p>
 *
 * @implSpec Access reservations are granted only when the requested zone has an available slot.
 */
@Service
public class AccessibilityService {
    private final AccessibleParkingSpaceRepository accessibleParkingSpaceRepository;

    public AccessibilityService(AccessibleParkingSpaceRepository accessibleParkingSpaceRepository) {
        this.accessibleParkingSpaceRepository = accessibleParkingSpaceRepository;
    }

    /**
     * Reserves a currently free accessible space in the selected zone.
     *
     * @param zoneCode the accessible zone requested by the user
     * @return true when a reservation was granted, otherwise false
     */
    public boolean reserveAccessibleSpace(String zoneCode) {
        for (AccessibleParkingSpace space : accessibleParkingSpaceRepository.findAll()) {
            if (space.getZoneCode().equals(zoneCode) && !space.isReserved()) {
                space.setReserved(true);
                return true;
            }
        }
        return false;
    }
}
