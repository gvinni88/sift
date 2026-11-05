package carparking.features.maintenance;

import carparking.annotations.Repository;

@Repository
public interface MaintenanceTicketRepository {
    String createTicket(String facilityId);
}
