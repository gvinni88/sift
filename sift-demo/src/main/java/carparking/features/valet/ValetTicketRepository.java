package carparking.features.valet;

import carparking.annotations.Repository;

import java.util.List;

@Repository
public interface ValetTicketRepository {
    List<String> findAll();
}
