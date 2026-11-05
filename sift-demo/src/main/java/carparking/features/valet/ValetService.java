package carparking.features.valet;

import carparking.annotations.Service;

/**
 * Issues parking valet tickets and keeps the service interface simple for front-end consumers.
 *
 * <p>In scope: capturing a ticket reference for a vehicle. Out of scope: vehicle inspection or
 * payment collection.</p>
 *
 * @implSpec Ticket generation relies on the vehicle plate and the current timestamp to avoid ambiguity.
 */
@Service
public class ValetService {
    private final ValetTicketRepository valetTicketRepository;

    public ValetService(ValetTicketRepository valetTicketRepository) {
        this.valetTicketRepository = valetTicketRepository;
    }

    /**
     * Creates a new valet ticket reference for a vehicle.
     *
     * @param vehiclePlate the license plate being checked in
     * @return a generated valet ticket identifier
     */
    public String issueTicket(String vehiclePlate) {
        return "VT-" + vehiclePlate + "-" + System.currentTimeMillis();
    }
}
