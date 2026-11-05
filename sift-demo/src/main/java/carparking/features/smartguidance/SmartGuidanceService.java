package carparking.features.smartguidance;

import carparking.annotations.Service;

/**
 * Suggests the most suitable route from the current entry point to an available bay.
 *
 * <p>In scope: recommending a route in response to current occupancy conditions. Out of scope:
 * payment logic or physical navigation hardware.</p>
 *
 * @implSpec Route recommendations are computed through the repository and should be treated as
 * guidance rather than a guaranteed enforcement mechanism.
 */
@Service
public class SmartGuidanceService {
    private final SmartGuidanceRouteRepository smartGuidanceRouteRepository;

    public SmartGuidanceService(SmartGuidanceRouteRepository smartGuidanceRouteRepository) {
        this.smartGuidanceRouteRepository = smartGuidanceRouteRepository;
    }

    /**
     * Suggests the recommended route to the driver from a named entry point.
     *
     * @param entryPoint the inbound access point used to start the journey
     * @return the computed route description
     */
    public String suggestRoute(String entryPoint) {
        return smartGuidanceRouteRepository.findRoute(entryPoint);
    }
}
