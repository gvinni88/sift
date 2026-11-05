package carparking.features.smartguidance;

import carparking.annotations.Repository;

@Repository
public interface SmartGuidanceRouteRepository {
    String findRoute(String entryPoint);
}
