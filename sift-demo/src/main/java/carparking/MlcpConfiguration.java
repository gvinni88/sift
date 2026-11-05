package carparking;

/**
 * Describes a configured multi-level car park deployment.
 *
 * <p>This configuration allows the solution to represent different operating modes such as
 * smart, premium, EV-friendly or accessible installations. Feature-specific behavior is then
 * selected through the {@link MlcpType} enum.</p>
 */
public class MlcpConfiguration {
    private final MlcpType mlcpType;
    private final int floors;
    private final int spacesPerFloor;
    private final boolean useValet;

    public MlcpConfiguration(MlcpType mlcpType, int floors, int spacesPerFloor, boolean useValet) {
        this.mlcpType = mlcpType;
        this.floors = floors;
        this.spacesPerFloor = spacesPerFloor;
        this.useValet = useValet;
    }

    public MlcpType getMlcpType() {
        return mlcpType;
    }

    public int getFloors() {
        return floors;
    }

    public int getSpacesPerFloor() {
        return spacesPerFloor;
    }

    public boolean isUseValet() {
        return useValet;
    }
}
