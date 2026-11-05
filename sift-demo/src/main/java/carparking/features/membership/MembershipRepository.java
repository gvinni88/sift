package carparking.features.membership;

import carparking.annotations.Repository;

@Repository
public interface MembershipRepository {
    boolean isActive(String accountId);
}
