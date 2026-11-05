package carparking.features.membership;

import carparking.annotations.Service;

/**
 * Checks whether a driver account is currently eligible for member benefits.
 *
 * <p>In scope: validating active membership status. Out of scope: billing or entitlement rules
 * beyond the repository response.</p>
 *
 * @implSpec Membership status is represented as a boolean active indicator returned by the repository.
 */
@Service
public class MembershipService {
    private final MembershipRepository membershipRepository;

    public MembershipService(MembershipRepository membershipRepository) {
        this.membershipRepository = membershipRepository;
    }

    /**
     * Determines if the account is currently active for premium benefits.
     *
     * @param accountId the user or account identifier to validate
     * @return true when the account is active, otherwise false
     */
    public boolean isMemberEligible(String accountId) {
        return membershipRepository.isActive(accountId);
    }
}
