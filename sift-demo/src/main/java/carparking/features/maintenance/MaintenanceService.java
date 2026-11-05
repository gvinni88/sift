package carparking.features.maintenance;

import carparking.annotations.Service;

/**
 * Creates maintenance work tickets for facilities or equipment that need attention.
 *
 * <p>In scope: opening a repair or maintenance request for a facility identifier. Out of scope:
 * technician assignment and vendor scheduling.</p>
 *
 * @implSpec Ticket creation delegates to the repository and returns the generated reference.
 */
@Service
public class MaintenanceService {
    private final MaintenanceTicketRepository maintenanceTicketRepository;

    public MaintenanceService(MaintenanceTicketRepository maintenanceTicketRepository) {
        this.maintenanceTicketRepository = maintenanceTicketRepository;
    }

    /**
     * Opens a ticket for the nominated facility or asset.
     *
     * @param facilityId the facility or equipment identifier affected by the issue
     * @return the generated work ticket reference
     */
    public String openTicket(String facilityId) {
        return maintenanceTicketRepository.createTicket(facilityId);
    }
}
