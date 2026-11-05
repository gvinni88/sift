package carparking.features.evcharging;

import carparking.annotations.Service;

/**
 * Manages the lifecycle of EV charging bays.
 *
 * <p>In scope: identifying an available charging bay and marking a charging session active.
 * Out of scope: billing, energy balancing, and charger hardware monitoring.</p>
 *
 * @implSpec Charging state changes only when the requested bay code resolves to a free charger.
 */
@Service
public class EvChargingService {
    private final EvChargingBayRepository evChargingBayRepository;

    public EvChargingService(EvChargingBayRepository evChargingBayRepository) {
        this.evChargingBayRepository = evChargingBayRepository;
    }

    /**
     * Starts a charging session for a bay when it is currently free.
     *
     * @param bayCode the bay identifier to activate
     * @return true when the bay transitioned to an active session, otherwise false
     */
    public boolean startSession(String bayCode) {
        for (EvChargingBay bay : evChargingBayRepository.findAll()) {
            if (bay.getBayCode().equals(bayCode) && !bay.isInUse()) {
                bay.setInUse(true);
                return true;
            }
        }
        return false;
    }
}
