package carparking.features.security;

import carparking.annotations.Repository;

@Repository
public interface SecurityCameraRepository {
    boolean isCameraOnline(String cameraId);
}
