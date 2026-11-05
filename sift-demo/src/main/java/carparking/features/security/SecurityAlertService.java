package carparking.features.security;

import carparking.annotations.Service;

/**
 * Raises an alert when a monitored camera indicates abnormal conditions.
 *
 * <p>In scope: checking camera health and signaling incidents. Out of scope: dispatching guards
 * or physically opening gates.</p>
 *
 * @implSpec The method acts as a gateway signal and does not execute any safety-critical control.
 */
@Service
public class SecurityAlertService {
    private final SecurityCameraRepository securityCameraRepository;

    public SecurityAlertService(SecurityCameraRepository securityCameraRepository) {
        this.securityCameraRepository = securityCameraRepository;
    }

    /**
     * Checks whether the named camera is online and can notify security staff.
     *
     * @param cameraId the camera identifier to inspect
     * @return true if the camera is online, otherwise false
     */
    public boolean triggerAlert(String cameraId) {
        return securityCameraRepository.isCameraOnline(cameraId);
    }
}
